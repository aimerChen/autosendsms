package com.chen.autosendsms.db;

import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**方案2：不使用第三方包
 * 
 * 在Android平台上，如果你想在多线程环境下安全的使用数据库的话，那么你得确保所有的线程使用的都是同一个数据库连接。
 * 
 * @author Administrator
 *
 */
public class DBManager {
	private static DBManager instance;
	private static SQLiteOpenHelper mDatabaseHelper;
	private SQLiteDatabase mDatabase;
	private AtomicInteger mOpenCounter = new AtomicInteger();

//	public static synchronized void initialize(SQLiteOpenHelper helper) {
//		if (instance == null) {
//			instance = new DatabaseManager();
//			mDatabaseHelper = helper;
//		}
//	}
	
	public static synchronized DBManager getInstance(Context context) {
		if (instance == null) {
			instance = new DBManager();
			mDatabaseHelper = DatabaseHelper.getDatabaseHelper(context);
		}
		return instance;
	}
	
	//调用这个函数是要打开数据库连接；但是这个函数真正重新打开数据库连接是在特定的条件下
	public synchronized SQLiteDatabase openDatabase() {
		if (mOpenCounter.incrementAndGet() == 1) {
			// Opening new database
			mDatabase = mDatabaseHelper.getWritableDatabase();
		}
		return mDatabase;
	}

	//调用这个函数是要关闭数据库连接；但是这个函数真正关闭数据库连接是在特定的条件下
	public synchronized void closeDatabase() {
		if (mOpenCounter.decrementAndGet() == 0) {
			// Closing database
			mDatabase.close();
		}
	}

}
