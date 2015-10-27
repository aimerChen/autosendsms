package com.chen.autosendsms.sendsmsservice;

import com.chen.autosendsms.utils.Utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class GuardService extends Service {

	private static Thread mThread;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("SendSMSService", "onStartCommand startId=" + startId);
		startDaemon();
		return super.onStartCommand(intent, flags, startId);
	}

	private void startDaemon() {
		if (mThread == null || !mThread.isAlive()) {
			mThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						if (!Utils.isServiceAlive(GuardService.this,
								"com.chen.autosendsms.sendsmsservice。SendSMSService")) {
							System.out.println("检测到服务SendSMSService不存在.....");
							Intent intent=new Intent();
					    	intent.setClass(getApplicationContext(), SendSMSService.class);
							startService(intent);
						}
						try {
							Thread.sleep(1800 * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
			mThread.start();
		}
	}
}
