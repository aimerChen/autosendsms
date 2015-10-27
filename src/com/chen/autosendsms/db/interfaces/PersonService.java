package com.chen.autosendsms.db.interfaces;

import java.util.List;
import java.util.Map;

import com.chen.autosendsms.db.entities.Person;

public interface PersonService {
	public List<Person> getPeopleByCondition(Map<String,Object> condition);
	public List<Person> getOneDayPerson(long ts);//ts以秒为单位
}
