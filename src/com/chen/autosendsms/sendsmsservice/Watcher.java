package com.chen.autosendsms.sendsmsservice;

import java.util.ArrayList;

import com.chen.autosendsms.utils.Utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

/**
 * 监视器类，构造时将会在Native创建子进程来监视当前进程，
 */
public class Watcher {
	
	private final String TAG = "Watcher";
	private String mMonitoredService = "";
	private volatile boolean bHeartBreak = false;
	private Context mContext;
	private boolean mRunning = true;
	
	public Watcher(Context context) {
		mContext = context;
	}
	
	public void createAppMonitor(String userId) {
		if (!createWatcher(userId)) {
			Utils.printLog(2,TAG,"<<Monitor created failed>>");
		}
	}



	private int isServiceRunning() {
		ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) am.getRunningServices(1024);
		for (int i = 0; i < runningService.size(); ++i) {
			if (mMonitoredService.equals(runningService.get(i).service.getClassName().toString())) {
				return 1;
			}
		}
		return 0;
	}

	/**
	* Native方法，创建一个监视子进程.
	* @param userId 当前进程的用户ID,子进程重启当前进程时需要用到当前进程的用户ID.
	* @return 如果子进程创建成功返回true，否则返回false
	*/
	private native boolean createWatcher(String userId);
	
//	/**
//	* Native方法，让当前进程连接到监视进程.
//	* @return 连接成功返回true，否则返回false
//	*/
//	private native boolean connectToMonitor();
//	
//	/**
//	* Native方法，向监视进程发送任意信息
//	* @param 发给monitor的信息
//	* @return 实际发送的字节
//	*/
//	private native int sendMsgToMonitor(String msg);
//	
	
	static 
	{
		System.loadLibrary("monitor");
	}
}