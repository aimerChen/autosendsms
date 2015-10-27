package com.chen.autosendsms.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.chen.autosendsms.utils.InvalidParamsException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.content.Context;

@SuppressWarnings("hiding")
public abstract class  BaseDao<T, Integer> {
	  
    protected Context mContext;  
  
    public BaseDao(Context context) {  
        mContext = context;  
    }  

    public abstract Dao<T, Integer> getDao() throws SQLException;  
  
    public int save(T t){
        try {
			return getDao().create(t);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}  
    }  
  
    public List<T> query(PreparedQuery<T> preparedQuery){  
        Dao<T, Integer> dao=null;
        List<T> list=null;
		try {
			dao = getDao();
			if(dao!=null){
				list=dao.query(preparedQuery);  
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
    }
  
    public List<T> query(String attributeName, String attributeValue){  
        QueryBuilder<T, Integer> queryBuilder=null;
		try {
			queryBuilder = getDao().queryBuilder();
	        queryBuilder.where().eq(attributeName, attributeValue);  
	        PreparedQuery<T> preparedQuery = queryBuilder.prepare();  
	        return query(preparedQuery);  
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}  
    }  
  
    public List<T> query(String[] attributeNames, String[] attributeValues) throws SQLException,  
            InvalidParamsException {  
        if (attributeNames.length != attributeValues.length) {  
            throw new InvalidParamsException("params size is not equal");  
        }  
        QueryBuilder<T, Integer> queryBuilder = getDao().queryBuilder();  
        Where<T, Integer> wheres = queryBuilder.where();  
        for (int i = 0; i < attributeNames.length; i++) {  
            wheres.eq(attributeNames[i], attributeValues[i]);  
        }  
        PreparedQuery<T> preparedQuery = queryBuilder.prepare();  
        return query(preparedQuery);  
    }  
  
    public List<T> queryAll(){  
        Dao<T, Integer> dao=null;
		try {
			dao = getDao();
	        return dao.queryForAll();  
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}  
    }  
  
    public T queryById(String idName, String idValue){  
        List<T> lst = query(idName, idValue);  
        if (null != lst && !lst.isEmpty()) {  
            return lst.get(0);  
        } else {  
            return null;  
        }  
    }  
    
    public T queryForTheFirst(){  
        QueryBuilder<T, Integer> queryBuilder=null;
		try {
			queryBuilder = getDao().queryBuilder();
	        return queryBuilder.queryForFirst();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}  
    }  
  
    public int delete(PreparedDelete<T> preparedDelete){  
        Dao<T, Integer> dao=null;
		try {
			dao = getDao();
	        return dao.delete(preparedDelete);  
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}  
    }  
  
    public int delete(T t){  
        Dao<T, Integer> dao=null;
		try {
			dao = getDao();
	        return dao.delete(t);  
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}  
    }  
  
    public int delete(List<T> lst){  
        Dao<T, Integer> dao=null;
		try {
			dao = getDao();
	        return dao.delete(lst); 
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}   
    }  
  
    public int delete(String[] attributeNames, String[] attributeValues) throws SQLException,  
            InvalidParamsException {  
        List<T> lst = query(attributeNames, attributeValues);  
        if (null != lst && !lst.isEmpty()) {  
            return delete(lst);  
        }  
        return 0;  
    }  
  
    public int deleteById(String idName, String idValue) throws SQLException,  
            InvalidParamsException {  
        T t = queryById(idName, idValue);  
        if (null != t) {  
            return delete(t);  
        }  
        return 0;  
    }  
  
    public int update(T t){  
        Dao<T, Integer> dao=null;
		try {
			dao = getDao();
	        return dao.update(t); 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}   
    }  
  
    public boolean isTableExsits(){  
        try {
			return getDao().isTableExists();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}  
    }  
  
    public long countOf(){  
        try {
			return getDao().countOf();
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}  
    }  
  
    public List<T> query(Map<String, Object> map){  
        QueryBuilder<T, Integer> queryBuilder=null;
		try {
			queryBuilder = getDao().queryBuilder();
			if (!map.isEmpty()) {  
	            Where<T, Integer> wheres = queryBuilder.where();  
	            Set<String> keys = map.keySet();  
	            ArrayList<String> keyss = new ArrayList<String>();  
	            keyss.addAll(keys);  
	            for (int i = 0; i < keyss.size(); i++) {  
	                if (i == 0) {  
	                    wheres.eq(keyss.get(i), map.get(keyss.get(i)));  
	                } else {  
	                    wheres.and().eq(keyss.get(i), map.get(keyss.get(i)));  
	                }  
	            }  
	        }  
	        PreparedQuery<T> preparedQuery = queryBuilder.prepare();  
	        return query(preparedQuery);  
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}  
        
    }  
  
    public List<T> query(Map<String, Object> map, Map<String, Object> lowMap,  
            Map<String, Object> highMap){  
        QueryBuilder<T, Integer> queryBuilder=null;
		try {
			queryBuilder = getDao().queryBuilder();
			 Where<T, Integer> wheres = queryBuilder.where();  
		        if (!map.isEmpty()) {  
		            Set<String> keys = map.keySet();  
		            ArrayList<String> keyss = new ArrayList<String>();  
		            keyss.addAll(keys);  
		            for (int i = 0; i < keyss.size(); i++) {  
		                if (i == 0) {  
		                    wheres.eq(keyss.get(i), map.get(keyss.get(i)));  
		                } else {  
		                    wheres.and().eq(keyss.get(i), map.get(keyss.get(i)));  
		                }  
		            }  
		        }  
		        if (!lowMap.isEmpty()) {  
		            Set<String> keys = lowMap.keySet();  
		            ArrayList<String> keyss = new ArrayList<String>();  
		            keyss.addAll(keys);  
		            for (int i = 0; i < keyss.size(); i++) {  
		                if(map.isEmpty()){  
		                    wheres.gt(keyss.get(i), lowMap.get(keyss.get(i)));  
		                }else{  
		                    wheres.and().gt(keyss.get(i), lowMap.get(keyss.get(i)));  
		                }  
		            }  
		        }  
		  
		        if (!highMap.isEmpty()) {  
		            Set<String> keys = highMap.keySet();  
		            ArrayList<String> keyss = new ArrayList<String>();  
		            keyss.addAll(keys);  
		            for (int i = 0; i < keyss.size(); i++) {  
		                wheres.and().lt(keyss.get(i), highMap.get(keyss.get(i)));  
		            }  
		        }  
		        PreparedQuery<T> preparedQuery = queryBuilder.prepare();  
		        return query(preparedQuery); 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}  
        
    }  
}
