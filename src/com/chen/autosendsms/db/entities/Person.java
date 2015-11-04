package com.chen.autosendsms.db.entities;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="person")
public class Person implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Person(){}

	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField(defaultValue = "")
	private String userName="";//用于登录
	
	@DatabaseField(canBeNull=false)
	private String phoneNumber="";
	
	@DatabaseField(defaultValue = "")
	private String nickName="";//昵称
	
	@DatabaseField(defaultValue = "")
	private String firstName="";
	
	@DatabaseField(defaultValue = "")
	private String lastName="";
	
	@DatabaseField(columnName="birthday")
	private long birthday=0;
	
	@DatabaseField
	private int age=0;
	
	@DatabaseField
	private int workAge=0;//工龄
	
	@DatabaseField(defaultValue = "")
	private String department="";//科室
	
	@DatabaseField(defaultValue = "")
	private String note="";//备注
	
	@DatabaseField
	private long dateSendSMS=0;//发送短信的日期
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public long getBirthday() {
		return birthday;
	}

	public void setBirthday(long birthday) {
		this.birthday = birthday;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getWorkAge() {
		return workAge;
	}

	public void setWorkAge(int workAge) {
		this.workAge = workAge;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public long getDateSendSMS() {
		return dateSendSMS;
	}

	public void setDateSendSMS(long dateSendSMS) {
		this.dateSendSMS = dateSendSMS;
	}

}
