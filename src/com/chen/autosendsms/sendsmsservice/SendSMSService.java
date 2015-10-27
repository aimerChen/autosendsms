package com.chen.autosendsms.sendsmsservice;

import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.chen.autosendsms.db.dao.NoteDao;
import com.chen.autosendsms.db.dao.PersonDao;
import com.chen.autosendsms.db.entities.Note;
import com.chen.autosendsms.db.entities.Person;
import com.chen.autosendsms.utils.Utils;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

public class SendSMSService extends Service {

	private List<Person> mList;
	// private ServiceFactory mServiceFactory=null;
	private PersonDao mPersonDao = null;
	private NoteDao mNoteDao = null;
	private TimerTask mTimerTask = null;
	private Timer mTimer;
	private static Thread mThread;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	/**
	 * 开机启动服务，先知道今天有没有发送
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("SendSMSService", "SendSMSService onCreate");

		Utils.WriteLog("SendSMSService onCreate");
		mPersonDao = new PersonDao(getApplicationContext());
		mNoteDao = new NoteDao(getApplicationContext());
		if (mPersonDao == null || mNoteDao == null) {
			Utils.WriteLog("SendSMSService mPersonService初始化失败，服务退出");
			return;
		}
		// mServiceFactory=ServiceFactory.getServiceFactory(getApplicationContext());
		// mPersonDao=mServiceFactory.getPersonService();
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}
		mTimerTask = new TimerTask() {

			@Override
			public void run() {
				checkTime(getApplicationContext());
			}

		};
		mTimer = new Timer();
		mTimer.schedule(mTimerTask, 0, 1800 * 1000);

		Utils.WriteLog("SendSMSService mTimer启动");
	}

	private void checkTime(Context context) {

		Utils.WriteLog("SendSMSService checkTime");
		// mServiceFactory=ServiceFactory.getServiceFactory(getApplicationContext());
		// mPersonDao=mServiceFactory.getPersonService();
		Log.e("SendSMSService", "设置的时间＝" + Utils.isRightTime(context));
		if (Utils.isRightTime(context)) {// 是设置的时间点吗
			mList = mPersonDao.getOneDayPerson(System.currentTimeMillis() / 1000);
			new Thread() {
				@Override
				public void run() {

					Utils.WriteLog("SendSMSService sendAllSMS开始");
					sendAllSMS();
				}
			}.start();
			// Utils.setSendSMSToday(getApplicationContext(), true);//今天已经发送了
			Log.e("SendSMSService", "到点了，并且刚发送了");
			Utils.WriteLog("SendSMSService 到点了，并且刚发送了");
		}
		// else{
		// Log.e("SendSMSService","不是9点");
		// if(!Utils.hasSendSMSToday(getApplicationContext())){//今天没有发送短信//&&Utils.isRightTime()
		// Log.e("SendSMSService","不是9点，但是没有发送");
		// mList=mPersonDao.getOneDayPerson(System.currentTimeMillis()/1000);
		// new Thread(){
		// @Override
		// public void run(){
		//// sendAllSMS();
		// }
		// }.start();
		// Log.e("SendSMSService","不是9点，但是没有发送，刚发送了");
		// Utils.setSendSMSToday(getApplicationContext(), true);//今天已经发送了
		// }else{
		// Log.e("SendSMSService","不是9点，而且已经发送了");
		// }
		// }
	}

	private void sendAllSMS() {
		Log.i("SendSMSService", "SendSMSService sendAllSMS");
		if (mList != null && mList.size() > 0) {
			for (Person person : mList) {
				sendSMS(person);
			}
		}
	}

	/**
	 * 发送短信
	 * 
	 * @param phoneNumber
	 */
	private void sendSMS(Person person) {
		try {
			if (person.getPhoneNumber() == null || person.getPhoneNumber().length() <= 0) {
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// 判断今天是否发送过

		long date = person.getDateSendSMS();
		if (!Utils.hasSentSMSToday(date)) {// 今天已经发过了？没有

			Utils.WriteLog("SendSMSService 今天没有发送");
			Log.i("SendSMSService", "SendSMSService sendSMS " + person.getPhoneNumber());

			String content = "亲爱的战友，" + person.getLastName() + person.getFirstName() + "：";
			Note note=null;
			try {
				note = mNoteDao.queryForTheFirst();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (note != null && note.getNote() != null) {
				Log.e("SendSMSService", "mnote不为空");
				Utils.WriteLog("SendSMSService mnote不为空");
				content += note.getNote();
			}

			SmsManager smsManager = SmsManager.getDefault();
			PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(), 0);
			if (content.length() > 70) {
				List<String> contents = smsManager.divideMessage(content);
				for (String str : contents) {
					smsManager.sendTextMessage(person.getPhoneNumber(), null, str, pendingIntent, null);
				}
			} else {
				smsManager.sendTextMessage(person.getPhoneNumber(), null, content, pendingIntent, null);
			}
			person.setDateSendSMS(System.currentTimeMillis() / 1000);
			if (mPersonDao != null) {
				try {
					mPersonDao.update(person);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} else {
			Utils.WriteLog("SendSMSService 今天发送了");
			Log.e("SendSMSService", "今天已经发送了");
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("SendSMSService", "SendSMSService onDestroy");
		Log.i("SendSMSService", "SendSMSService 重新开启");
		Intent intent = new Intent();
		intent.setAction("SendSMSServiceReboot");
		getApplicationContext().sendBroadcast(intent);

		Utils.WriteLog("SendSMSService SendSMSService onDestroy");
		// Intent intent=new Intent();
		// intent.setClass(getApplicationContext(), SendSMSService.class);
		// startService(intent);
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
						if (!Utils.isServiceAlive(SendSMSService.this,
								"com.chen.autosendsms.sendsmsservice。GuardService")) {
							System.out.println("检测到服务GuardService不存在.....");
							Intent intent=new Intent();
					    	intent.setClass(getApplicationContext(), GuardService.class);
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
