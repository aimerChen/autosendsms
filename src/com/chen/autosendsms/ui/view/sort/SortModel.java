package com.chen.autosendsms.ui.view.sort;

public class SortModel<T>{
	private T model;   
	private String sortLetters;  //显示数据拼音的首字母
	
	public T getModel() {
		return model;
	}
	public void setModel(T model) {
		this.model = model;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
}
