package com.chen.autosendsms.db.interfaces;

import java.util.List;

import com.chen.autosendsms.db.entities.Person;

public interface PersonService {
	public List<Person> searchContactsByName(String name);
	public List<Person> getOneDayPerson(long ts);//ts以秒为单位
}
