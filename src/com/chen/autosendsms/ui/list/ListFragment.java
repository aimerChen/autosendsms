package com.chen.autosendsms.ui.list;

import java.util.ArrayList;
import java.util.List;

import com.chen.autosendsms.R;
import com.chen.autosendsms.db.dao.PersonDao;
import com.chen.autosendsms.db.entities.Person;
import com.chen.autosendsms.ui.contacts.AddAndEditContactActivity;
import com.chen.autosendsms.utils.Utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class ListFragment extends Fragment {

//	private DaoFactory mDaoFactory=null;
	private PersonDao mPersonDao=null;
	private ListView mListView;
	private MyAdapter mAdapter;
	private List<Person> mList;

//	private Button importPeople;

	private AlertDialog dialog=null;
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
		mList=new ArrayList<Person>();
		mAdapter=new MyAdapter();
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onResume(){
		super.onResume();
		Log.i("MainActivity","onResume");
		getDataFromDatabase();
		mAdapter.notifyDataSetChanged();
	}
	
	private void getDataFromDatabase(){
		if(mPersonDao==null){
			return;
		}
		mList=mPersonDao.getOneDayPerson(System.currentTimeMillis()/1000);
	}

	private void deleteAlert(final int position){
		if(mList.get(position)!=null){
			AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
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
				convertView=LayoutInflater.from(getActivity()).inflate(R.layout.list_item, null, false);
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
					intent.setClass(getActivity(), AddAndEditContactActivity.class);
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
