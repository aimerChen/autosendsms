package com.chen.autosendsms.ui.contacts;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.chen.autosendsms.R;
import com.chen.autosendsms.db.dao.PersonDao;
import com.chen.autosendsms.db.entities.Person;
import com.chen.autosendsms.utils.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ImportContactsActivity extends Activity {

	private final static int DAO_NULL = 0x01;// dao为空
	private final static int CANNT_ADD = 0x02;// 不能添加
	private final static int CANNT_READ = 0x03;// 文件不可读
	private final static int UNSUPPORT_TYPE = 0x11;// 文件格式不正确
	private final static int NO_CARD = 0x12;// 没有sd card
	private final static int NON_EXIST = 0x13;// 文件不存在

	private MyHandler mHandler = null;
	private PersonDao mPersonDao = null;
	private ListView mListView;
	private MyFileAdapter mAdapter = null;
	private List<File> mList = null;
	private Runnable mRunnable = null;
	private File mCurrentFile = null;

//	private char[] l = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
//			'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', };
	private String mComparedString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	
	private String UNKNOWN_FILE="UNKNOWN";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import_contacts);

		initial();

		mHandler = new MyHandler(this);
		mPersonDao = new PersonDao(getApplicationContext());
	}

	private void initial() {
		mListView = (ListView) findViewById(R.id.filelist);
		mList = new ArrayList<File>();
		showExternalStorageFiles();
		mAdapter = new MyFileAdapter();
		mListView.setAdapter(mAdapter);
		mAdapter.setList(mList);
	}

	/**
	 * 获取sd card目录
	 */
	private void showExternalStorageFiles() {
		String sDStateString = Environment.getExternalStorageState();
		if (sDStateString.equals(Environment.MEDIA_MOUNTED)) {
			try {
				File SDFile = Environment.getExternalStorageDirectory();
				File sdPath = new File(SDFile.getAbsolutePath());
				changeDir(sdPath);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

	/**
	 * 按顺序排列
	 * 
	 * @param fList
	 */
	private void sortList(List<File> fList) {
		
		// 按顺序排列
		Collections.sort(fList, new Comparator<File>() {

			@Override
			public int compare(File lhs, File rhs) {
				String str1 = lhs.getName().toUpperCase(Locale.getDefault());
				String str2 = rhs.getName().toUpperCase(Locale.getDefault());
				if (str1.toCharArray()[0] > str2.toCharArray()[0]) {
					return 1;
				} else {
					return -1;
				}
			}
		});
	}

	/**
	 * ListView adapter
	 * 
	 * @author chen
	 *
	 */
	private class MyFileAdapter extends BaseAdapter {

		private List<File> myList;

		/**
		 * 切换数据源
		 * 
		 * @param list
		 */
		 public void setList(List<File> list){
			 myList=list;
			 this.notifyDataSetChanged();
		 }

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return myList == null ? 0 : myList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return myList == null ? null : myList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;

			final File mFile = myList.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_fileitem, null, false);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv_name = (TextView) convertView.findViewById(R.id.name);
			holder.img_logo = (ImageView) convertView.findViewById(R.id.logo);
			if (mFile.isDirectory()) {
				holder.img_logo.setImageResource(R.drawable.folder);
			} else {
				String exName=getExtensionName(mFile);
				if(exName.equalsIgnoreCase("xls")){
					holder.img_logo.setImageResource(R.drawable.xls);
				}else if(exName.equalsIgnoreCase(UNKNOWN_FILE)){
					//unkown
					holder.img_logo.setImageResource(R.drawable.txt_file);
				}else{
					holder.img_logo.setImageResource(R.drawable.txt_file);
				}
			}
			holder.tv_name.setText(mFile.getName());
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					changeDir(mFile);
				}

			});
			return convertView;
		}
	}

	private static class ViewHolder {
		private TextView tv_name;
		private ImageView img_logo;

	}

	/**
	 * 如果是文件夹，则显示文件夹下的文件；如果是文件，则判断是否为xls，如果是xls，就可以导入联系人
	 * 
	 * @param dirPath
	 */
	private void changeDir(final File dirPath) {
		if (dirPath == null) {
			return;
		}
		if (dirPath.isDirectory()) {
			mCurrentFile = dirPath;
			mList.clear();
			File[] files = dirPath.listFiles();
			if (files.length > 0) {
				for (File file : files) {
					if (mComparedString.contains(file.getName().substring(0, 1))) {
						mList.add(file);
					}
				}
			}
			sortList(mList);
			mAdapter.setList(mList);
		} else {
			String[] token = dirPath.getName().split(".");
			if (token.length >= 2 && token[1].equalsIgnoreCase("xls")) {

				// 是xls文件
				AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						toast("开始导入");
						importPeople(dirPath);
						dialog.dismiss();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			} else {
				toast("文件格式不对，不能导入");
			}
		}
	}
	
	private void toast(String message){
		Toast.makeText(getApplicationContext(), ""+message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 获取文件的扩展名
	 * @param file
	 * @return
	 */
	private String getExtensionName(File file){
		if(file==null){
			return "";
		}
		String[] name=file.getName().split(".");
		if(name.length>=2){
			return name[name.length-1];
		}else{
			return UNKNOWN_FILE;
		}
	}
	
	/**
	 * 将联系人读出
	 * @param file
	 */
	private void importPeople(final File file) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
		final AlertDialog dialog = builder.setView(new ProgressBar(getApplicationContext())).setCancelable(false)
				.create();
		dialog.show();
		mRunnable = new Runnable() {
			@Override
			public void run() {
				List<Person> list = Utils.readContactsFromFile(file);
				saveContacts(list);
			}
		};
		mRunnable.run();

	}

	/**
	 * 将联系人写入数据库
	 * @param list
	 */
	private void saveContacts(List<Person> list) {
		if (mPersonDao != null) {
			mPersonDao.saveList(list);
		} else {
			Log.e("", "mPersonDao ==null");
			mHandler.sendEmptyMessage(1);
		}
	}

	private static class MyHandler extends Handler {
		private WeakReference<Activity> mActivityReference;

		public MyHandler(Activity activity) {
			mActivityReference = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {

			final Context mContext = mActivityReference.get();
			switch (msg.what) {
			case DAO_NULL:
				Toast.makeText(mContext, "数据库内部错误", Toast.LENGTH_SHORT).show();
				break;
			case CANNT_ADD:
				Toast.makeText(mContext, "添加出错", Toast.LENGTH_SHORT).show();
				break;
			case CANNT_READ:
				Toast.makeText(mContext, "文件不能读取", Toast.LENGTH_SHORT).show();
				break;
			case UNSUPPORT_TYPE:
				Toast.makeText(mContext, "没有找到文件xls,只支持excel2003版", Toast.LENGTH_LONG).show();
				break;
			case NO_CARD:
				Toast.makeText(mContext, "没有内存卡", Toast.LENGTH_LONG).show();
				break;

			case NON_EXIST:
				Toast.makeText(mContext, "文件不存在", Toast.LENGTH_LONG).show();
				break;
			}
		}
	};

	/**
	 * 按下back键之后，不退出，而是显示上一个文件夹的文件;如果退到根目录，则直接退出此界面
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (mCurrentFile != null) {
				File file = mCurrentFile.getParentFile();
				if (mCurrentFile.getAbsolutePath()
						.equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
					return super.onKeyDown(keyCode, event);
				}
				changeDir(file);
			}
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
