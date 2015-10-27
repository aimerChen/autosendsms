package com.chen.autosendsms.db.entities;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="time")
public class MyTime implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MyTime(){}
	
	@DatabaseField(generatedId = true)
	public int id;

	@DatabaseField
	public int time;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}
}
