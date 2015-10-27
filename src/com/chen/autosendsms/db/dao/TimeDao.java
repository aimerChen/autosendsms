package com.chen.autosendsms.db.dao;

import java.sql.SQLException;

import com.chen.autosendsms.db.entities.MyTime;
import com.j256.ormlite.dao.Dao;

import android.content.Context;

public class TimeDao extends BaseDao<MyTime,Integer>{

	public TimeDao(Context context) {
		super(context);
	}

	@Override
	public Dao<MyTime, Integer> getDao() throws SQLException {
		return getHelper().getDao(MyTime.class);
	}

}
