package com.chen.autosendsms.db.dao;

import java.sql.SQLException;

import com.chen.autosendsms.db.DatabaseHelper;
import com.chen.autosendsms.db.entities.Note;
import com.j256.ormlite.dao.Dao;

import android.content.Context;

public class NoteDao extends BaseDao<Note,Integer>{
	
	private Context mContext;
	public NoteDao(Context context){
		mContext=context;
	}

	@Override
	public Dao<Note, Integer> getDao() throws SQLException {
		return DatabaseHelper.getDatabaseHelper(mContext).getDao(Note.class);
	}

}

