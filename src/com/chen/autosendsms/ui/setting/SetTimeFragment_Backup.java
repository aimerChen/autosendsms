package com.chen.autosendsms.ui.setting;
//package com.chen.autosendsms.ui;
//
//import com.chen.autosendsms.R;
//import com.chen.autosendsms.db.dao.TimeDao;
//import com.chen.autosendsms.db.entities.MyTime;
//
//import android.app.Fragment;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//public class SetTimeFragment extends Fragment{
//
//	private Button btn_save;
//	private MyTime mMyTime=null;
//	private EditText mEditText;
//	private TimeDao mTimeDao=null;
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		ViewGroup view=(ViewGroup) inflater.inflate(R.layout.activity_changetime, container,false);
//		
//		mTimeDao=new TimeDao(getActivity());
//		intial(view);
//		return view;
//	}
//
//	private void intial(View view){
//		btn_save=(Button)view.findViewById(R.id.save);
//		btn_save.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View arg0) {
//				if(mTimeDao==null)
//					return;
////				if(mDaoFactory!=null){
//				if(mMyTime==null){
//					mMyTime=new MyTime();
//				}
//				int time=0;
//				try{
//					time=Integer.parseInt(mEditText.getText().toString().trim());
//				}catch(Exception e){
//					Toast.makeText(getActivity(), "时间只能是数字", Toast.LENGTH_SHORT).show();
//					return;
//				}
//				if(time>24||time<0){
//					Toast.makeText(getActivity(), "时间必须大于0且小于24", Toast.LENGTH_SHORT).show();
//					return;
//				}
//				mMyTime.setTime(time);
//				int saved=0;
//				if(mMyTime.getId()<=0){
//					saved=mTimeDao.save(mMyTime);
//				}else{
//					saved=mTimeDao.update(mMyTime);
//				}
//				if(saved>0){
//					Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
//				}
//			}
//
//		});
//
//		mEditText=(EditText)view.findViewById(R.id.time);
//		setTime();
//	}
//
//	private void setTime(){
//		if(mTimeDao==null){
//			Log.e("NoteActivity","mDaoFactory==null");
//		}else{
//			MyTime myTime=null;
//			myTime = mTimeDao.queryForTheFirst();
//			mMyTime=myTime==null?null:myTime;
//			if(mMyTime!=null){
//				mEditText.setText(mMyTime.getTime()+"");
//			}
//		}
//	}
//}
