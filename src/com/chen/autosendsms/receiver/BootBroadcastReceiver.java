package com.chen.autosendsms.receiver;

import com.chen.autosendsms.sendsmsservice.SendSMSService;
import com.chen.autosendsms.utils.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		if(arg1.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			Intent intent=new Intent();
			intent.setClass(arg0, SendSMSService.class);
			arg0.startService(intent);
		}
		if(arg1.getAction().equals("SendSMSServiceReboot")){
			Utils.printLog(1,"RebootBroadCastReceiver", "RebootBroadCastReceiver received message to reboot");
			Intent intent=new Intent();
			intent.setClass(arg0, SendSMSService.class);
			arg0.startService(intent);
		}
	}

}
