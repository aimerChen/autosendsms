package com.chen.autosendsms.sendsmsservice;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.chen.autosendsms.db.dao.NoteDao;
import com.chen.autosendsms.db.dao.PersonDao;
import com.chen.autosendsms.db.entities.Note;
import com.chen.autosendsms.db.entities.Person;
import com.chen.autosendsms.utils.Parameters;
import com.chen.autosendsms.utils.Utils;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;

public class SendSMSService extends Service {

	private String TAG = "SendSMSService";
	private List<Person> mList;
	private PersonDao mPersonDao = null;
	private NoteDao mNoteDao = null;
	private TimerTask mTimerTask = null;
	private Timer mTimer;

	@Override
	public IBinder onBind(Intent arg0) {
//		return mBinder;
		return null;
	}
	
	/**
	 * 实现GuardServiceInterface中的接口;
	 * 可以在拥有此service对象并绑定了实现此接口的ServiceConnection的服务类中可以调用，显示夸进程调用
	 */
//	private final GuardServiceInterface.Stub mBinder = new GuardServiceInterface.Stub() {
//	    public int getPid(){
//	    	Utils.printLog(1, TAG, "getPid:"+0);
//	        return 0;
//	    }
//	    public void basicTypes(int anInt) {
//	    	Utils.printLog(1, TAG, "basicTypes:"+anInt);
//	    }
//	};

	
	@Override
	public void onCreate() {
		super.onCreate();
		Utils.printLog(1, TAG, "onCreate");
		if(initialDao()){
			startSchedule();
		}
	}
	
	/**
	 * initialize dao
	 * @return
	 */
	private boolean initialDao(){
		mPersonDao = new PersonDao(getApplicationContext());
		mNoteDao = new NoteDao(getApplicationContext());
		if (mPersonDao == null || mNoteDao == null) {
			Utils.printLog(2, TAG, "initialize dao failed");
			return false;
		}
		return true;
	}

	/**
	 * start schedule to scan database and then retrieve contacts whose birthday
	 * is today
	 */
	private void startSchedule() {
		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}
		if (mTimer != null) {
			// If there is a currently running task it is not affected
			mTimer.cancel();
			mTimer = null;
		}
		mTimerTask = new MyTimerTask();
		mTimer = new Timer();
		if(Parameters.DEBUG){
			mTimer.schedule(mTimerTask, 0, 5 * 1000);
		}else{
			mTimer.schedule(mTimerTask, 0, 1800 * 1000);
		}
	}

	private class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			Utils.printLog(1, TAG, "isSwitchOn:" + isSwitchOn());
			if (isSwitchOn()&&Utils.isRightTime(getApplicationContext())) {
				retrieveContactsFromDB();
				sendSMSToAll();
			}
		}
	}

	/**
	 * whether auto sending sms is allowed
	 * 
	 * @return
	 */
	private boolean isSwitchOn() {
		return Utils.getPreferenceBoolean(Parameters.AUTO_SEND_SMS_KEY, getApplicationContext());
	}

	/**
	 * retrieve contact from database
	 */
	private void retrieveContactsFromDB(){
		mList = mPersonDao.getOneDayPerson(System.currentTimeMillis() / 1000);
	}

	private void sendSMSToAll() {
		Utils.printLog(1,TAG,"sendAllSMS");
		mThreadSendSMS.run();
	}
	
	/**
	 * 发送sms线程
	 */
	private Runnable mThreadSendSMS=new Runnable(){

		@Override
		public void run() {
			if (mList != null && mList.size() > 0) {
				for (Person contact : mList) {
					sendSMSToContact(contact);
				}
			}
		}
	};
	

	/**
	 * Send sms
	 * 
	 * @param phoneNumber
	 */
	private void sendSMSToContact(Person contact) {
		// whether it has sent sms to contacts whose birthday is today
		long date = contact.getDateSendSMS();
		if (!Utils.hasSentSMSToday(date)) {
			Utils.printLog(1, TAG, "has not sent sms today");
			String content=getSendNote(contact);
			sendSMSToPhone(content,contact.getPhoneNumber());
			contact.setDateSendSMS(System.currentTimeMillis() / 1000);
			updatePerson(contact);
		} else {
			Utils.printLog(1, TAG, "has sent sms today");
		}

	}

	/**
	 * create note
	 * @param contact
	 * @return
	 */
	private String getSendNote(Person contact){
		StringBuilder content = new StringBuilder();
		content.append("亲爱的战友，" ).append(contact.getLastName())
			.append(contact.getFirstName()).append("：");
		Note note = mNoteDao.queryForTheFirst();
		if (note != null && note.getNote() != null) {
			content.append(note.getNote());
		}
		return content.toString();
	}

	/**
	 * send sms to phone number
	 * @param content
	 * @param phoneNumber
	 */
	private void sendSMSToPhone(String content,String phoneNumber){
		if(phoneNumber!=null&&!phoneNumber.equals("")&&
				phoneNumber.length()==Parameters.PHONE_NUMBER_LENGTH){
			SmsManager smsManager = SmsManager.getDefault();
			PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(), 0);
			if (content.length() > 70) {
				List<String> contents = smsManager.divideMessage(content);
				for (String str : contents) {
					smsManager.sendTextMessage(phoneNumber, null, str, pendingIntent, null);
				}
			} else {
				smsManager.sendTextMessage(phoneNumber, null, content, pendingIntent, null);
			}	
		}
	}
	
	/**
	 * update contact
	 * @param contact
	 */
	private void updatePerson(Person contact){
		if (mPersonDao != null) {
			mPersonDao.update(contact);
		}
	}
	
	@Override
	public void onDestroy() {
		Utils.printLog(1,TAG, "onDestroy");
		Intent intent = new Intent();
		intent.setAction("SendSMSServiceReboot");
		getApplicationContext().sendBroadcast(intent);
		super.onDestroy();
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Utils.printLog(1,TAG, "onStartCommand startId=" + startId);
		return super.onStartCommand(intent, flags, startId);	
	}
}
