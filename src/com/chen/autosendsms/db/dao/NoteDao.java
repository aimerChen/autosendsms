package com.chen.autosendsms.db.dao;

import java.sql.SQLException;

import com.chen.autosendsms.db.entities.Note;
import com.j256.ormlite.dao.Dao;

import android.content.Context;

public class NoteDao extends BaseDao<Note,Integer>{

	public NoteDao(Context context) {
		super(context);
	}

	@Override
	public Dao<Note, Integer> getDao() throws SQLException {
		return getHelper().getDao(Note.class);
	}
}
