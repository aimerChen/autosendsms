package com.chen.autosendsms.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.chen.autosendsms.db.DatabaseHelper;
import com.chen.autosendsms.db.entities.Person;
import com.chen.autosendsms.db.interfaces.PersonService;
import com.chen.autosendsms.utils.Utils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import android.content.Context;

public class PersonDao extends BaseDao<Person,Integer> implements PersonService{
	
	private Context mContext;
	public PersonDao(Context context) {
		super(context);
		mContext=context;
	}
	
	@Override
	public Dao<Person, Integer> getDao() throws SQLException {
		return DatabaseHelper.getDatabaseHelper(mContext).getDao(Person.class);
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
	public List<Person> searchContactsByName(String name){
		List<Person> mlist=new ArrayList<Person>();
    	try {
            QueryBuilder<Person, Integer> queryBuilder=getDao().queryBuilder();
	        queryBuilder.where().like("firstName", "%"+name+"%").or().like("lastName", "%"+name+"%");
	        PreparedQuery<Person> preparedQuery = queryBuilder.prepare();  
    		mlist=getDao().query(preparedQuery);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
    	return mlist;
	}
}
