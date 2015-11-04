package com.chen.autosendsms.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.chen.autosendsms.db.dao.TimeDao;
import com.chen.autosendsms.db.entities.MyTime;
import com.chen.autosendsms.db.entities.Person;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class Utils {

	@SuppressLint("SimpleDateFormat")
	public static String getTS() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return sdf.format(new Date());
	}

	/**
	 * print log on console
	 * 
	 * @param type
	 *            1:Log.i; 2:Log.e
	 * @param message
	 */
	public static void printLog(int type, String tag, String message) {
		if (!Parameters.DEBUG) {
			return;
		}
		if (type == 1) {
			Log.i(tag, message);
		} else {
			Log.e(tag, message);
		}

	}

	public static void WriteLog(String content) {
		if (!Parameters.DEBUG) {
			return;
		}
		if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
			File file = new File(Environment.getExternalStorageDirectory() + "AutoSendSMS.txt");
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
		TimeDao dao = new TimeDao(context);
		MyTime time = dao.queryForTheFirst();
		boolean isRightTime = false;
		if (time != null && time.getTime() < 24 && time.getTime() >= 0) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeZone(TimeZone.getDefault());
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			if (hour == time.getTime()) {
				isRightTime = true;
			}
		}
		dao = null;
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

	@SuppressLint("SimpleDateFormat")
	public static int[] getBirthdayArray(long ts) {
		int[] result = new int[3];
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(ts * 1000);
		result[0] = cal.get(Calendar.YEAR);
		result[1] = cal.get(Calendar.MONTH);
		result[2] = cal.get(Calendar.DAY_OF_MONTH);
		return result;
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
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
		int month = calendar1.get(Calendar.MONTH);// 0-11
		int day = calendar1.get(Calendar.DAY_OF_MONTH);

		calendar1.setTimeInMillis(date * 1000);
		int year1 = calendar1.get(Calendar.YEAR);
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

	/**
	 * set preference values
	 * 
	 * @param key
	 * @param value
	 * @param context
	 */
	public static boolean setPreferenceValue(String key, Object value, Context context) {
		SharedPreferences pre = context.getSharedPreferences(Parameters.APPLICATION_PREFERENCE_KEY,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pre.edit();
		if (value instanceof Boolean) {
			editor.putBoolean(key, (Boolean) value);
		} else if (value instanceof String) {
			editor.putString(key, (String) value);
		}
		return editor.commit();
	}

	/**
	 * set preference values
	 * 
	 * @param key
	 * @param value
	 * @param context
	 */
	public static boolean getPreferenceBoolean(String key, Context context) {
		SharedPreferences pre = context.getSharedPreferences(Parameters.APPLICATION_PREFERENCE_KEY,
				Context.MODE_PRIVATE);
		return pre.getBoolean(key, true);
	}

	/**
	 * 从xls文件中读取联系人
	 * 
	 * @param file
	 * @return
	 */
	public static List<Person> readContactsFromFile(File file) {
		String NAME = "name";
		String BIRTHDAY = "birthday";
		String PHONENUMBER = "phone";
		int name_order = 0;
		int birthday_order = 0;
		int phonenumber_order = 0;
		List<Person> list = new ArrayList<Person>();
		if (file.canRead()) {
			InputStream is = null;
			try {
				is = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
			Workbook book = null;
			try {
				book = Workbook.getWorkbook(is);
			} catch (BiffException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			int num = book.getNumberOfSheets();
			// 获得第一个工作表对象
			if (num > 0) {
				Sheet sheet = book.getSheet(0);
				int Rows = sheet.getRows();
				int Cols = sheet.getColumns();

				// 读取第一行，判断哪个一列是名字，那一列是生日，那一列是电话
				Cell[] cells = sheet.getRow(0);
				if (cells != null) {
					for (int i = 0; i < Cols; i++) {
						if (NAME.equalsIgnoreCase(cells[i].getContents())) {
							name_order = i;
						} else if (BIRTHDAY.equalsIgnoreCase(cells[i].getContents())) {
							birthday_order = i;
						} else if (PHONENUMBER.equalsIgnoreCase(cells[i].getContents())) {
							phonenumber_order = i;
						}
					}
				}
				Person person = null;
				String name = "";
				for (int i = 1; i < Rows; i++) {//
					person = new Person();
					for (int j = 0; j < Cols; j++) {
						if (name_order == j) {
							// name column
							name = sheet.getRow(i)[j].getContents();
							person.setLastName(name);
						} else if (birthday_order == j) {
							// birthday column
							person.setBirthday(Utils.getBirthdayTS(sheet.getRow(i)[j].getContents().replace(".", "-")));
						} else if (phonenumber_order == j) {
							// phone number column
							person.setPhoneNumber(sheet.getRow(i)[j].getContents());
						}
					}
					list.add(person);
				}
			}
			book.close();
		} else {
			Log.e("MainFragment", "can not read");
		}
		return list;
	}

	/**
	 * 拨打电话弹框
	 * 
	 * @param number
	 * @param context
	 */
	public static void dialDialog(final String number, final Context context) {
		if (context != null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setCancelable(true).setNegativeButton("取消", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).setPositiveButton("确定", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dial(number, context);
					dialog.dismiss();
				}
			}).setTitle(number);
			builder.create().show();
		}
	}

	/**
	 * 拨打电话
	 * 
	 * @param number
	 * @param context
	 */
	public static void dial(String number, Context context) {
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
		context.startActivity(intent);
	}

	/**
	 * 读取本地联系人
	 * 
	 * @return
	 */
	public static List<Person> readLocalContacts(Context context) {
		List<Person> list=new ArrayList<Person>();
		Person person=null;
		ContentResolver resolver = context.getContentResolver();
		Cursor phoneCursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				person=new Person();
				// 得到手机号码
				String id = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.Contacts._ID));
				String name = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				// 获得联系人手机号码
				Cursor phone = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);
				if (phone != null) {
					while (phone.moveToNext()) { // 取得电话号码(可能存在多个号码)
						int phoneFieldColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
						String phoneNumber = phone.getString(phoneFieldColumnIndex);
						person.setPhoneNumber(phoneNumber);
					}
					phone.close();
				}
				person.setLastName(name);
				list.add(person);
			}
			phoneCursor.close();
		}
		return list;
	}
}
