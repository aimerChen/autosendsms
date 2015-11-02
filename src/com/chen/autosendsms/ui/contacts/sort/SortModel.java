package com.chen.autosendsms.ui.contacts.sort;

import com.chen.autosendsms.db.entities.Person;

public class SortModel{
	private Person model;   //显示的数据
	private String sortLetters;  //显示数据拼音的首字母
	
	public Person getModel() {
		return model;
	}
	public void setModel(Person model) {
		this.model = model;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
}
