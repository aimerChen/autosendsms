package com.chen.autosendsms.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.chen.autosendsms.db.dao.TimeDao;
import com.chen.autosendsms.db.entities.MyTime;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class Utils {

	private static boolean DEBUG = true;

	@SuppressLint("SimpleDateFormat")
	public static String getTS() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return sdf.format(new Date());
	}

	public static void WriteLog(String content) {
		if (!DEBUG) {
			return;
		}
		if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
			File file = new File(Environment.getExternalStorageDirectory() + "/AutoSendSMS.txt");
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (file.canWrite()) {
				OutputStream out = null;
				try {
					try {
						out = new BufferedOutputStream(new FileOutputStream(file));
						String ts = getTS();
						ts += "              ";
						ts += content;
						out.write(ts.getBytes());
						out.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}

		}
	}

	// 判断服务是否开启
	public static boolean isServiceAlive(Context context, String serviceClassName) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> running = manager.getRunningServices(50);
		for (int i = 0; i < running.size(); i++) {
			if (serviceClassName.equals(running.get(i).service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	@SuppressLint("SimpleDateFormat")
	public static long getDayFirstSecond(long ts) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String dateStr = sdf.format(new Date(ts * 1000));
		try {
			return sdf.parse(dateStr.split(" ")[0] + " 00:00:00").getTime() / 1000;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return ts;
		}
	}

	/**
	 * 现在是不是正确的时间
	 * 
	 * @return
	 */
	public static boolean isRightTime(Context context) {
		List<MyTime> list=null;
		MyTime time =null;
		list = new TimeDao(context).queryAll();
		if (list == null||list.size()==0) {
			return false;
		}else{
			time = list.get(0);
		}
		if (time.getTime() > 24 || time.getTime() < 0) {
			return false;
		}
		boolean isRightTime = false;
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		if (hour == time.getTime()) {
			isRightTime = true;
		}
		return isRightTime;
	}

	/**
	 * 生日转成ts
	 * 
	 * @param datestr
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static long getBirthdayTS(String datestr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			return sdf.parse(datestr + " 00:00:00").getTime() / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
			return System.currentTimeMillis() / 1000;
		}
	}

	/**
	 * ts转成生日
	 * 
	 * @param ts
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getBirthdayStr(long ts) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date(ts * 1000));
	}

	/**
	 * 生日的月和日
	 * 
	 * @param ts
	 * @return
	 */
	public static int[] getMonthAndDayofTs(long ts) {
		int[] daymonth = new int[2];
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.setTimeInMillis(ts * 1000);
		daymonth[0] = calendar.get(Calendar.MONTH);// 0-11
		daymonth[1] = calendar.get(Calendar.DAY_OF_MONTH);// 0-11

		return daymonth;
	}

	@SuppressLint("SimpleDateFormat")
	public static long stringToLong(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long ts = 0;
		try {
			ts = sdf.parse(date).getTime() / 1000;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		return ts;

	}

	/**
	 * 是否是今天
	 * 
	 * @param date
	 * @return
	 */
	public static boolean hasSentSMSToday(long date) {
		Calendar calendar1 = Calendar.getInstance();
		int year = calendar1.get(Calendar.YEAR);
		Log.e("hasTodaySendSMS", "year=" + year);
		int month = calendar1.get(Calendar.MONTH);// 0-11
		int day = calendar1.get(Calendar.DAY_OF_MONTH);

		calendar1.setTimeInMillis(date * 1000);
		int year1 = calendar1.get(Calendar.YEAR);
		Log.e("hasTodaySendSMS", "year=" + year1);
		int month1 = calendar1.get(Calendar.MONTH);// 0-11
		int day1 = calendar1.get(Calendar.DAY_OF_MONTH);
		if (year == year1 && month == month1 && day1 == day) {
			return true;
		} else {
			return false;
		}
	}

	public static void hiddenKeyBoard(Context context, EditText myEditText) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);

	}

	// /**
	// * 获取是否发送短信
	// * @param context
	// * @param hasSend
	// */
	// public static boolean hasSendSMSToday(Context context){
	// SharedPreferences preference=context.getSharedPreferences("hasSendSMS",
	// Context.MODE_PRIVATE);
	// return preference.getBoolean("sent", false);
	// }
	//
	// /**
	// * 设置是否发送短信
	// * @param context
	// * @param hasSend
	// */
	// public static void setSendSMSToday(Context context,boolean hasSend){
	// SharedPreferences preference=context.getSharedPreferences("hasSendSMS",
	// Context.MODE_PRIVATE);
	// Editor editor =preference.edit();
	// editor.putBoolean("sent", hasSend);
	// editor.commit();
	// }

}
