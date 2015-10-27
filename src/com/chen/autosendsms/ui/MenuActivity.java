package com.chen.autosendsms.ui;

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

import com.chen.autosendsms.R;
import com.chen.autosendsms.sendsmsservice.SendSMSService;
import com.chen.autosendsms.utils.Utils;

public class MenuActivity extends Activity{
	private Button mBtn_listfragment;
	private Button mBtn_searchfragment;
	private Button mBtn_notefragment;
	private Button mBtn_timefragment;
	private Button mButtons[];
	private Fragment[] mFragments;
	private FragmentManager mFragmentManager;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		initial();
		startService();
		changeFramgent(0);
	}
	
	private void initial(){
		mBtn_listfragment=(Button)findViewById(R.id.listfragment);
		mBtn_searchfragment=(Button)findViewById(R.id.searchfragment);
		mBtn_notefragment=(Button)findViewById(R.id.notefragment);
		mBtn_timefragment=(Button)findViewById(R.id.timefragment);
		
		mFragmentManager=getFragmentManager();
        //mFragments
        mFragments=new Fragment[4];
        mFragments[0]=new MainFragment();
        mFragments[1]=new SearchPersonFragment();
        mFragments[2]=new NoteFragment();
        mFragments[3]=new SetTimeFragment();
        
        mButtons=new Button[4];
        mButtons[0]=mBtn_listfragment;
		mButtons[1]=mBtn_searchfragment;
		mButtons[2]=mBtn_notefragment;
		mButtons[3]=mBtn_timefragment;
        		
       
        mBtn_listfragment.setOnClickListener(new OnClickListener(){

 			@Override
 			public void onClick(View v) {
 				changeFramgent(0);
 			}
         	
         });
        
        mBtn_searchfragment.setOnClickListener(new OnClickListener(){

 			@Override
 			public void onClick(View v) {
 				changeFramgent(1);
 			}
         	
         });
        
        mBtn_notefragment.setOnClickListener(new OnClickListener(){

 			@Override
 			public void onClick(View v) {
 				changeFramgent(2);
 			}
         	
         });        
        
        mBtn_timefragment.setOnClickListener(new OnClickListener(){

  			@Override
  			public void onClick(View v) {
  				changeFramgent(3);
  			}
          	
          });
	
	}
    
    private void startService(){
    	Utils.WriteLog("MenuActivity启动service");
    	Intent intent=new Intent();
    	intent.setClass(getApplicationContext(), SendSMSService.class);
    	startService(intent);
    }
	
	private void changeFramgent(int order){
		for(int i=0;i<4;i++){
			mButtons[i].setBackgroundColor(Color.parseColor("#ffffe0"));
			if(i==order){
				mButtons[i].setBackgroundColor(Color.parseColor("#eedd82"));
			}
		}
		
		//创建修改实例
		FragmentTransaction transaction =mFragmentManager.beginTransaction();
		// and add the transaction to the backstack
		transaction.replace(R.id.fragment_container,mFragments[order]);
		//提交修改
		transaction.commit();	
	}
}
