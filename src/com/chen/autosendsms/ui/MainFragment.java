package com.chen.autosendsms.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.chen.autosendsms.R;
import com.chen.autosendsms.db.dao.PersonDao;
import com.chen.autosendsms.db.entities.Person;
import com.chen.autosendsms.utils.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;


public class MainFragment extends Fragment {

//	private DaoFactory mDaoFactory=null;
	private PersonDao mPersonDao=null;
	private ListView mListView;
	private MyAdapter mAdapter;
	private List<Person> mList;

	private Button importPeople;

	private Button mImg_Btn_addPerson;
	//	private Button mImg_Btn_changeMessage;
	private AlertDialog dialog=null;
	private static String NAME="name";
	private static String BIRTHDAY="birthday";
	private static String PHONENUMBER="phone";
	
	private int name_order=0;
	private int birthday_order=0;
	private int phonenumber_order=0;
	
	//    @Override
	//    protected void onCreate(Bundle savedInstanceState) {
	//        super.onCreate(savedInstanceState);
	//        setContentView(R.layout.activity_main);
	//        inital();
	//        Intent intent=new Intent();
	//        intent.setClass(getApplicationContext(), SearchPersonActivity.class);
	//        startActivity(intent);
	////        addPerson();
	//        startDao();
	//    }
	private Context mContext=null;
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		mContext=activity.getApplicationContext();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup view=(ViewGroup) inflater.inflate(R.layout.activity_main, container,false);
		initial(view);
		return view;
	}

	private void initial(ViewGroup view){
//		mDaoFactory=DaoFactory.getDaoFactory(mContext);
//		if(mDaoFactory!=null){
//			mPersonDao=mDaoFactory.getPersonDao();
//		}
		mPersonDao=new PersonDao(getActivity());
		mListView=(ListView)view.findViewById(R.id.personlist);
		mImg_Btn_addPerson=(Button)view.findViewById(R.id.addPerson);
		mImg_Btn_addPerson.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.setClass(mContext, AddPersonActivity.class);
				startActivity(intent);
			}

		});

		importPeople=(Button)view.findViewById(R.id.importPeople);
		importPeople.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				importPeople();
			}

		});
		//        mImg_Btn_changeMessage=(Button)view.findViewById(R.id.changeMessage);
		//        mImg_Btn_changeMessage.setOnClickListener(new OnClickListener(){
		//
		//			@Override
		//			public void onClick(View v) {
		//				Intent intent=new Intent();
		//				intent.setClass(mContext, NoteActivity.class);
		//				startActivity(intent);
		//			}
		//        	
		//        });
		mList=new ArrayList<Person>();
		mAdapter=new MyAdapter();
		mListView.setAdapter(mAdapter);
	}

	private final int CAN_READ=0x10;
	private final int UNSUPPORT_TYPE=0x11;
	private final int NO_CARD=0x12;
	private final int NON_EXIST=0x13;
	
	private void importPeople(){
		AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
		final AlertDialog dialog=builder.setView(new ProgressBar(getActivity())).setCancelable(false).create();
		dialog.show();
		new Thread(){
			@Override
			public void run(){
				Log.e("MainFragment","state="+Environment.getExternalStorageState());
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					try {
						File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/save2.xls");
						if(file.exists()){
							Log.e("MainFragment","exsit");
						}else{
							handler.sendEmptyMessage(NON_EXIST);
							Log.e("MainFragment","not exsit");
						}

						if(file.canRead()){
							Log.e("MainFragment","can read");
						}else{
							handler.sendEmptyMessage(CAN_READ);
							Log.e("MainFragment","can not read");
						}
						
						InputStream is = new FileInputStream(file);  
						Workbook book = Workbook.getWorkbook(is);  
						int num = book.getNumberOfSheets();  
						Log.e("MainFragment","the num of sheets is " + num+ "\n");  
						// 获得第一个工作表对象  
						Sheet sheet = book.getSheet(0);  
						int Rows = sheet.getRows();  
						int Cols = sheet.getColumns();  
						StringBuilder txt=new StringBuilder();
						txt.append("the name of sheet is " + sheet.getName() + "\n");  
						txt.append("total rows is " + Rows + "\n");  
						txt.append("total cols is " + Cols + "\n");  

						//读取第一行，判断哪个一列是名字，那一列是生日，那一列是电话
						
						Cell[] cells=sheet.getRow(0);
						if(cells!=null){
							for(int i=0;i<Cols;i++){
								if(NAME.equalsIgnoreCase(cells[i].getContents())){
									name_order=i;
								}else if(BIRTHDAY.equalsIgnoreCase(cells[i].getContents())){
									birthday_order=i;
								}else if(PHONENUMBER.equalsIgnoreCase(cells[i].getContents())){
									phonenumber_order=i;
								}
							}
						}
						Person person;
						String name="";
						for (int i = 1; i < Rows; i++) {//
							person=new Person();
							for (int j = 0; j < Cols; j++) { 
								// getCell(Col,Row)获得单元格的值  
								txt.append("contents:" + sheet.getRow(i)[j].getContents() + "\n");  
								if(name_order==j){
									//name column
									name=sheet.getRow(i)[j].getContents();
									person.setLastName(name);
//									if(name.length()>=2){
//										person.setLastName(name.substring(0, 1));
//										person.setFirstName(name.substring(1, name.length()-1));
//									}else{
//										person.setLastName(name);
//									}
								}else if(birthday_order==j){
									//birthday column
									person.setBirthday(Utils.getBirthdayTS(sheet.getRow(i)[j].getContents().replace(".", "-")));
								}else if(phonenumber_order==j){
									//phone number column
									person.setPhoneNumber(sheet.getRow(i)[j].getContents());
								}
							} 
							addPerson(person);
						}  
						dialog.dismiss();
						Log.e("MainFragment","content:"+txt.toString());
						book.close();  
					} catch (Exception e) {

						handler.sendEmptyMessage(UNSUPPORT_TYPE);
						System.out.println(e);  
						dialog.dismiss();
					}  
				}else{
					handler.sendEmptyMessage(NO_CARD);
					dialog.dismiss();
				}

			}
		}.start();
		
	}



	@Override
	public void onResume(){
		super.onResume();
		Log.i("MainActivity","onResume");
		getDataFromDatabase();
		mAdapter.notifyDataSetChanged();
	}
	
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case 1:
				Toast.makeText(mContext, "mPersonDao==null", Toast.LENGTH_SHORT).show();
				break;
			case 2:
				Toast.makeText(mContext, "添加出错", Toast.LENGTH_SHORT).show();
				break;
			case CAN_READ:
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
	
	private void addPerson(Person person){
		if(mPersonDao!=null){
			mPersonDao.save(person);
		}else{
			Log.e("","mPersonDao ==null");
			handler.sendEmptyMessage(1);
		}
		//	    	mPersonDao.getPersonDao().createIfNotExists(arg2);
		//	    	mPersonDao.getPersonDao().createIfNotExists(arg3);
	}

	private void getDataFromDatabase(){
		if(mPersonDao==null){
			return;
		}
		mList=mPersonDao.getOneDayPerson(System.currentTimeMillis()/1000);
	}

	private void deleteAlert(final int position){
		if(mList.get(position)!=null){
			AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
			builder.setTitle("要删除这个联系人码？").setPositiveButton("确定", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(mPersonDao!=null){
						mPersonDao.delete(mList.get(position));
						mList.remove(position);
						mAdapter.notifyDataSetChanged();
					}
					dialog.dismiss();
				}

			}).setNegativeButton("取消", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}

			});
			dialog=builder.create();
			dialog.show();
		}
	}

	/**
	 * ListView adapter
	 * @author chen
	 *
	 */
	private class MyAdapter extends BaseAdapter{

		private Person mPerson=null;
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList==null?0:mList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mList==null?null:mList.get(position);
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
			ViewHolder holder=null;
			mPerson=mList.get(position);
			if(convertView==null){
				holder=new ViewHolder();
				convertView=LayoutInflater.from(mContext).inflate(R.layout.list_item, null, false);
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder)convertView.getTag();
			}
			holder.tv_birthday=(TextView)convertView.findViewById(R.id.birthday);
			holder.tv_department=(TextView)convertView.findViewById(R.id.department);
			holder.tv_name=(TextView)convertView.findViewById(R.id.name);
			holder.tv_phoneNumber=(TextView)convertView.findViewById(R.id.phonenumber);

			holder.tv_name.setText(mPerson.getLastName()+" "+mPerson.getFirstName());
			holder.tv_department.setText(mPerson.getDepartment());
			holder.tv_birthday.setText(Utils.getBirthdayStr(mPerson.getBirthday()));
			holder.tv_phoneNumber.setText(mPerson.getPhoneNumber());
			convertView.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(View v) {
					deleteAlert(position);
					return true;
				}

			});
			convertView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Intent intent=new Intent();
					intent.setClass(mContext, EditPersonActivity.class);
					intent.putExtra("person", mPerson);
					startActivity(intent);
				}

			});
			return convertView;
		}
	}

	private static class ViewHolder{
		private TextView tv_name;
		private TextView tv_phoneNumber;
		private TextView tv_birthday;
		private TextView tv_department;
	}



	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.i("MainActivity","onDestroy");
	}	
}
