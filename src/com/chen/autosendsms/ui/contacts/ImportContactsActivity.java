package com.chen.autosendsms.ui.contacts;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addperson);

		initial();

		mHandler = new MyHandler(this);
		mPersonDao = new PersonDao(getApplicationContext());
	}

	private void initial() {
		mListView = (ListView) findViewById(R.id.filelist);
		mList = new ArrayList<File>();
		showExternalStorageFiles();
		mAdapter = new MyFileAdapter(mList);
		mListView.setAdapter(mAdapter);
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
				mCurrentFile=sdPath;//当前文件夹
				if (sdPath.isDirectory()) {
					if (sdPath.listFiles().length > 0) {
						for (File file : sdPath.listFiles()) {
							mList.add(file);
						}
					}
				} else {
					mList.add(sdPath);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

	/**
	 * ListView adapter
	 * 
	 * @author chen
	 *
	 */
	private class MyFileAdapter extends BaseAdapter {

		private List<File> myList;
		private File mFile = null;

		public MyFileAdapter(List<File> list) {
			myList = list;
		}

		/**
		 * 切换数据源
		 * 
		 * @param list
		 */
		// public void setList(List<File> list){
		// myList=list;
		// this.notifyDataSetChanged();
		// }

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
			mFile = myList.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_fileitem, null, false);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv_name = (TextView) convertView.findViewById(R.id.name);

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
	}

	/**
	 * 如果是文件夹，则显示文件夹下的文件；如果是文件，则判断是否为xls，如果是xls，就可以导入联系人
	 * 
	 * @param dirPath
	 */
	private void changeDir(final File dirPath) {
		if (dirPath.isDirectory()) {
			mCurrentFile=dirPath;
			mList.clear();
			File sdPath = new File(dirPath.getAbsolutePath());
			if (sdPath.isDirectory()) {
				if (sdPath.listFiles().length > 0) {
					for (File file : sdPath.listFiles()) {
						mList.add(file);
					}
				}
			} else {
				mList.add(sdPath);
			}
			mAdapter.notifyDataSetChanged();
		} else {
			// 是文件
			AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					String[] token = dirPath.getName().split(".");
					if (token.length >= 2 && token[1].equalsIgnoreCase("xls")) {
						Toast.makeText(getApplicationContext(), "开始导入", Toast.LENGTH_SHORT).show();
						importPeople(dirPath);
					} else {
						Toast.makeText(getApplicationContext(), "文件格式不对，不能导入", Toast.LENGTH_SHORT).show();
					}
					dialog.dismiss();
				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
		}
	}

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
			if(mCurrentFile!=null){
				File file=mCurrentFile.getParentFile();
				if(!file.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())){
					changeDir(file);
					return false;
				}
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
