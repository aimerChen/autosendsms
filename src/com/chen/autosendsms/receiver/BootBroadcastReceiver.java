package com.chen.autosendsms.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chen.autosendsms.sendsmsservice.SendSMSService;
import com.chen.autosendsms.utils.Utils;

public class BootBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		//日期改变了，今天没有发短信，所以修改成false
//		if(arg1.getAction().equals(Intent.ACTION_DATE_CHANGED)){
//			Utils.setSendSMSToday(arg0, false);
//		}
		//日期改变或者开机启动，就开启服务
		if(arg1.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){

	    	Utils.WriteLog("SendSMSService BootBroadcastReceiver");
			Intent intent=new Intent();
			intent.setClass(arg0, SendSMSService.class);
			arg0.startService(intent);
		}
	}

}
