package com.weicai.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.weicai.bean.SearchHistory;
import com.weicai.daoCore.SqliteDAO;
 
/**
 * @author jiuwuerliu@sina.com
 *
 * 调用示例
 */
public class SearchHistoryDao extends SqliteDAO {
	public static final String TAG = SearchHistoryDao.class.getSimpleName();
    private static SearchHistoryDao singleton;
    
	public static SearchHistoryDao getInstance() {
		if(singleton == null){
			singleton = new SearchHistoryDao(createDatabase());
		}
		return singleton;
	}
	
    public static SQLiteDatabase createDatabase(){    
        SQLiteDatabase db=SQLiteDatabase.create(null);  
        String createdb=
                "CREATE TABLE IF NOT EXISTS t_search_history("
                +"id         LONG PRIMARY KEY,"
                +"keywords       VARCHAR(255)"
                +");";
        db.execSQL(createdb);       
        return db;
    }
     
    
    public SearchHistoryDao(SQLiteDatabase db) {
		super(db);
	}
    
    public void deleteAll(){
    	List<SearchHistory> searchHistories = loadAll(SearchHistory.class, null);
    	for (int i = 0; i < searchHistories.size(); i++) {
    		delete(searchHistories.get(i));
		}
    }
    
    public List<String> getKeywordsList(){
    	List<String> keywords = new ArrayList<String>();
		
    	List<SearchHistory> searchHistories = loadAll(SearchHistory.class, "id DESC");
		for (int i = 0; i < searchHistories.size(); i++) {
			SearchHistory searchHistory = searchHistories.get(i);
			keywords.add(searchHistory.getKeywords());
			Log.i(TAG, "=====getKeywords===="+searchHistory.getId()+" "+searchHistory.getKeywords());

		}
		
		return keywords;
    }
    
}