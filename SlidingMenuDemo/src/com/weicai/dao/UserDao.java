package com.weicai.dao;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.weicai.bean.User;
import com.weicai.daoCore.SqliteDAO;
 
/**
 * @author jiuwuerliu@sina.com
 *
 * 调用示例
 */
public class UserDao extends SqliteDAO {
	static final String tag="UserDao";
    private static UserDao singleton;
    
	// Returns the UserDao instance
	public static UserDao getInstance() {
		if(singleton == null){
			singleton = new UserDao(createDatabase());
		}
		return singleton;
	}
	
    public static SQLiteDatabase createDatabase(){    
        SQLiteDatabase db=SQLiteDatabase.create(null);  
        String createdb=
                "CREATE TABLE IF NOT EXISTS t_user("
                +"id         LONG PRIMARY KEY,"
                +"name       VARCHAR(255),"
                +"phone       VARCHAR(255)"
                +");";
        db.execSQL(createdb);       
        return db;
    }
     
    
    public UserDao(SQLiteDatabase db) {
		super(db);
	}
    
    
    public User first(){
        List<User> list = super.loadAll(User.class, null);
        if(list.size() > 0){
        	return list.get(0);
        }else{
        	return null;
        }
    }
    
    
    public void deleteAll(){
    	List<User> users = loadAll(User.class, null);
    	for (int i = 0; i < users.size(); i++) {
    		delete(users.get(i));
		}
    }
    
}