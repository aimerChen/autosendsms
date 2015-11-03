package com.chen.autosendsms.ui.setting;

import java.util.Calendar;

import com.chen.autosendsms.R;
import com.chen.autosendsms.db.dao.TimeDao;
import com.chen.autosendsms.db.entities.MyTime;
import com.chen.autosendsms.ui.view.CheckSwitchButton;
import com.chen.autosendsms.utils.Parameters;
import com.chen.autosendsms.utils.Utils;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

public class SettingFragment extends Fragment implements OnClickListener{
	
	private RelativeLayout mRel_SMS_content;
	private WheelView hourWheel;
	private CheckSwitchButton btn_Switch;
	private MyTime mMyTime = null;
	private TimeDao mTimeDao = null;
	private int mOldTime=0;
	private int mNewTime=0;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup view=(ViewGroup) inflater.inflate(R.layout.fragment_setting, container,false);
		mTimeDao=new TimeDao(getActivity());
		initial(view);
		setWheelTime();
		return view;
	}
	
	private void initial(View view){
		mRel_SMS_content=(RelativeLayout)view.findViewById(R.id.setting_sms_content_rel);
		mRel_SMS_content.setOnClickListener(this);
		hourWheel=(WheelView)view.findViewById(R.id.hourwheel);
		int curHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		hourWheel.setViewAdapter(new NumericWheelAdapter(getActivity(), 0, 23));
		hourWheel.setCurrentItem(curHour);
		OnWheelChangedListener listener=new OnWheelChangedListener() {
			
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				mNewTime=newValue;
			}
		};
		hourWheel.addChangingListener(listener);
		
		btn_Switch=(CheckSwitchButton)view.findViewById(R.id.setting_switch_btn);
		btn_Switch.setOnCheckedChangeListener(mOnCheckedChangeListener);
		btn_Switch.setChecked(Utils.getPreferenceBoolean(Parameters.AUTO_SEND_SMS_KEY, getActivity()));
	}
	
	private OnCheckedChangeListener mOnCheckedChangeListener =new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			Utils.setPreferenceValue(Parameters.AUTO_SEND_SMS_KEY, isChecked, getActivity());
		}
		
	};

	@Override
	public void onStop(){
		super.onStop();
		saveTime();
	}
	
	
	private void setWheelTime() {
		if (mTimeDao == null) {
			Log.e("NoteActivity", "mDaoFactory==null");
		} else {
			MyTime myTime = null;
			myTime = mTimeDao.queryForTheFirst();
			mMyTime = myTime == null ? null : myTime;
			if (mMyTime != null) {
				hourWheel.setCurrentItem(mMyTime.getTime());
				mOldTime=mMyTime.getTime();
			}
		}
	}

	private void saveTime(){
		if(mNewTime==mOldTime){
			return;
		}
		if (mTimeDao == null) {
			Toast.makeText(getActivity(), "保存失败", Toast.LENGTH_SHORT).show();
			return;
		}
		if (mMyTime == null) {
			mMyTime = new MyTime();
		}
		int time = 0;
		time = hourWheel.getCurrentItem();
		if (time < 0 || time > 23) {
			Toast.makeText(getActivity(), "时间超出范围", Toast.LENGTH_SHORT).show();
			return;
		}
		mMyTime.setTime(time);
		int saved = 0;
		if (mMyTime.getId() <= 0) {
			saved = mTimeDao.save(mMyTime);
		} else {
			saved = mTimeDao.update(mMyTime);
		}
		if (saved > 0) {
			Toast.makeText(getActivity(), "时间保存成功", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.setting_sms_content_rel:
			Intent intent=new Intent();
			intent.setClass(getActivity(), NoteActivity.class);
			startActivity(intent);
			break;
		}
	}
}
