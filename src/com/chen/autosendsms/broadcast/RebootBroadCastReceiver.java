package com.chen.autosendsms.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chen.autosendsms.sendsmsservice.SendSMSService;

public class RebootBroadCastReceiver  extends BroadcastReceiver{

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		if(arg1.getAction().equals("SendSMSServiceReboot")){
			Log.e("RebootBroadCastReceiver","收到广播，重新启动服务");
			Intent intent=new Intent();
			intent.setClass(arg0, SendSMSService.class);
			arg0.startService(intent);
		}
	}

}
