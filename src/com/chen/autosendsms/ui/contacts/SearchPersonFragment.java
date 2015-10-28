package com.chen.autosendsms.ui.contacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chen.autosendsms.R;
import com.chen.autosendsms.db.dao.PersonDao;
import com.chen.autosendsms.db.entities.Person;
import com.chen.autosendsms.utils.Utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SearchPersonFragment extends Fragment{
	
	private PersonDao mPersonDao=null;
	private EditText mEditTextSearch;
	private TextView txt_contactsNumber;
	private ListView mListView;
	private List<Person> mList;
	private MySearchAdapter mAdapter;
	private List<Person> list;

	private AlertDialog dialog=null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup view=(ViewGroup) inflater.inflate(R.layout.activity_searchperson, container,false);
		initial(view);
		return view;
	}
	
	
	@Override
	public void onResume(){
		super.onResume();
        getDataFromDatabase();
        setContactNumberText();
        mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		Utils.hiddenKeyBoard(getActivity(), mEditTextSearch);
	}

	private void initial(ViewGroup view){
		mPersonDao=new PersonDao(getActivity());
		mEditTextSearch=(EditText)view.findViewById(R.id.search);
		mEditTextSearch.addTextChangedListener(mTextWatcher); 
		txt_contactsNumber=(TextView)view.findViewById(R.id.contactsNumber);
		txt_contactsNumber.setText("0");
		mListView=(ListView)view.findViewById(R.id.searchList);
		mList=new ArrayList<Person>();
		list=new ArrayList<Person>();
		getDataFromDatabase();
		mAdapter=new MySearchAdapter();
		mListView.setAdapter(mAdapter);
		

		setContactNumberText();
	}
	
    private void getDataFromDatabase(){
    	if(mPersonDao==null){
    		return ;
    	}
    	mList=mPersonDao.queryAll();
    }
    
    private void setContactNumberText(){
    	if(mList!=null&&mList.size()>0){
        	txt_contactsNumber.setText(""+mList.size());
    	}
    }
    
    private void refreshListView(){
        mList.clear();
        mList.addAll(list);
        mAdapter.notifyDataSetChanged();
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
    
    private TextWatcher mTextWatcher = new TextWatcher() {  
        private CharSequence temp;
        @Override  
        public void onTextChanged(CharSequence s, int start, int before, int count) {  
            // TODO Auto-generated method stub  
             temp = s;  
        }  
          
        @Override  
        public void beforeTextChanged(CharSequence s, int start, int count,  
                int after) {  
        }  
          
        @Override  
        public void afterTextChanged(Editable s) {  
            // TODO Auto-generated method stub
        	if(temp==null||temp.length()==0){
        		list=mPersonDao.queryAll();
        		refreshListView();
        		return;
        	}
        	Map<String,Object> condition=new HashMap<String,Object>();
        	condition.put("firstName", temp.toString());
        	condition.put("lastName", temp.toString());
    		list=mPersonDao.getPeopleByCondition(condition);
        }  
    };  
	
	  
    /**
     * ListView adapter
     * @author chen
     *
     */
    private class MySearchAdapter extends BaseAdapter{

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
				convertView=LayoutInflater.from(getActivity()).inflate(R.layout.list_searchitem, null, false);
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
					intent.setClass(getActivity(), EditPersonActivity.class);
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
    
    
}
