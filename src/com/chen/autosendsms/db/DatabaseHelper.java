package com.chen.autosendsms.db;

import java.sql.SQLException;

import com.chen.autosendsms.db.entities.MyTime;
import com.chen.autosendsms.db.entities.Note;
import com.chen.autosendsms.db.entities.Person;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 方案1：使用第三方包
 * 
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application -- change to something appropriate for your app
	private static final String DATABASE_NAME = "audosendsmss.db";
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION =1;

	private static DatabaseHelper instance=null;
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	 /** 
     * 单例获取该Helper 
     *  
     * @param context 
     * @return 
     */  
	public static DatabaseHelper getDatabaseHelper(Context context){
		synchronized (DatabaseHelper.class){  
            if (instance == null){ 
                instance = new DatabaseHelper(context);  
            }
        }  
		return instance;
	}
	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, Person.class);
			TableUtils.createTable(connectionSource, Note.class);
			TableUtils.createTable(connectionSource, MyTime.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, Person.class, true);
			TableUtils.dropTable(connectionSource, Note.class,true);
			TableUtils.dropTable(connectionSource, MyTime.class,true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}
	
/*	public synchronized Dao getDao(Class clazz) throws SQLException{
	    Dao dao = null;  
        String className = clazz.getSimpleName();  
  
        if (daos.containsKey(className))  
        {  
            dao = daos.get(className);  
        }  
        if (dao == null)  
        {  
            dao = super.getDao(clazz);  
            daos.put(className, dao);  
        }  
        return dao;  
	}

	  
    *//** 
     * 释放资源 
     *//*  
    @Override  
    public void close()  
    {  
        super.close();  
        for (String key : daos.keySet())  
        {  
			Dao dao = daos.get(key);  
            dao = null;  
        }  
    }  */
}