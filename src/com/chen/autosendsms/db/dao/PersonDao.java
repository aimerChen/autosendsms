package com.chen.autosendsms.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.chen.autosendsms.db.entities.Person;
import com.chen.autosendsms.db.interfaces.PersonService;
import com.chen.autosendsms.utils.Utils;
import com.j256.ormlite.dao.Dao;

import android.content.Context;

public class PersonDao  extends BaseDao<Person,Integer> implements PersonService{
	
	public PersonDao(Context context) {
		super(context);
	}
	
	@Override
	public Dao<Person, Integer> getDao() throws SQLException {
		return getHelper().getDao(Person.class);
	}
	
	@Override
	public List<Person> getOneDayPerson(long ts) {
		List<Person> mlist=new ArrayList<Person>();
		List<Person> list=new ArrayList<Person>();
		int[] monthAndDay=Utils.getMonthAndDayofTs(ts);
		int[] monthAndDay2=new int[2];
		try {
			list=getDao().queryForAll();
			for(Person person:list){
				monthAndDay2=Utils.getMonthAndDayofTs(person.getBirthday());
				if((monthAndDay[0]==monthAndDay2[0])&&(monthAndDay[1]==monthAndDay2[1])){
					mlist.add(person);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return mlist;
		} 
		return mlist;
	}

	@Override
	public List<Person> getPeopleByCondition(Map<String, Object> condition) {
		List<Person> mlist=new ArrayList<Person>();
    	try {
    		mlist=getDao().queryForFieldValuesArgs(condition);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
    	return mlist;
	}

}
