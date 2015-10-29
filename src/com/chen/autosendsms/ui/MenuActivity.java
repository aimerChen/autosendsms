package com.chen.autosendsms.ui;

import com.chen.autosendsms.R;
import com.chen.autosendsms.db.DatabaseHelper;
import com.chen.autosendsms.sendsmsservice.SendSMSService;
import com.chen.autosendsms.ui.contacts.ContactsFragment;
import com.chen.autosendsms.ui.list.ListFragment;
import com.chen.autosendsms.ui.setting.SettingFragment;
import com.chen.autosendsms.utils.Utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


/**
 * 关于数据库访问的方案1：在app启动时打开数据库，在app关闭时关闭数据库；其他时期不需要注意开关数据库
 * 
 * @author Administrator
 *
 */
public class MenuActivity extends Activity implements OnClickListener{
	private Button mBtn_listfragment;
	private Button mBtn_searchfragment;
	private Button mBtn_settingfragment;
	private Button mButtons[];
	private Fragment[] mFragments;
	private FragmentManager mFragmentManager;
	private int FRAGMENT_NUMBER=3;
	private int mCurrentFragment=0;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		DatabaseHelper.getDatabaseHelper(getApplicationContext()).getWritableDatabase();
		initial();
		changeFramgent();
		startService();
	}
	
	private void initial(){
		mBtn_listfragment=(Button)findViewById(R.id.listfragment);
		mBtn_searchfragment=(Button)findViewById(R.id.searchfragment);
		mBtn_settingfragment=(Button)findViewById(R.id.settingfragment);
		
		mFragmentManager=getFragmentManager();
        //mFragments
        mFragments=new Fragment[FRAGMENT_NUMBER];
        mFragments[0]=new ListFragment();
        mFragments[1]=new ContactsFragment();
        mFragments[2]=new SettingFragment();
        
        mButtons=new Button[FRAGMENT_NUMBER];
        mButtons[0]=mBtn_listfragment;
		mButtons[1]=mBtn_searchfragment;
		mButtons[2]=mBtn_settingfragment;
		for(int i=0;i<FRAGMENT_NUMBER;i++){
			 mButtons[i].setOnClickListener(this);
		}
	}
    
    private void startService(){
    	Utils.WriteLog("MenuActivity启动service");
    	Intent intent=new Intent();
    	intent.setClass(getApplicationContext(), SendSMSService.class);
    	startService(intent);
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.listfragment:
			mCurrentFragment=0;
			break;
		case R.id.searchfragment:
			mCurrentFragment=1;
			break;
		case R.id.settingfragment:
			mCurrentFragment=2;
			break;
		}
		changeFramgent();
	}
    
	private void changeFramgent(){
		for(int i=0;i<FRAGMENT_NUMBER;i++){
			mButtons[i].setBackgroundColor(Color.parseColor("#ffffe0"));
			if(i==mCurrentFragment){
				mButtons[i].setBackgroundColor(Color.parseColor("#eedd82"));
			}
		}
		
		//创建修改实例
		FragmentTransaction transaction =mFragmentManager.beginTransaction();
		// and add the transaction to the backstack
		transaction.replace(R.id.fragment_container,mFragments[mCurrentFragment]);
		//提交修改
		transaction.commit();	
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		DatabaseHelper.getDatabaseHelper(getApplicationContext()).close();
	}

}
