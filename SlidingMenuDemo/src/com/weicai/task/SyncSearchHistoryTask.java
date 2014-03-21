package com.weicai.task;

import java.util.List;

import org.json.JSONArray;

import com.baidu.android.common.logging.Log;
import com.weicai.activity.BaseActivity.NetTask;
import com.weicai.api.SearchHistoryAPI;
import com.weicai.bean.SearchHistory;
import com.weicai.dao.SearchHistoryDao;

public class SyncSearchHistoryTask extends NetTask {
	public SearchHistoryDao searchHistoryDao;

	public SyncSearchHistoryTask(){
		searchHistoryDao = SearchHistoryDao.getInstance();
	}
	
	@Override
	protected String doInBackground(Integer... params) {
		return SearchHistoryAPI.searchHistories();
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (result==null || result.equals("")) {
			return;
		}
		
		JSONArray array = SearchHistoryAPI.StringToJSONArray(result);
		List<SearchHistory> searchHistories = SearchHistory.jsonToList(array);
		for (int i = 0; i < searchHistories.size(); i++) {
			SearchHistory searchHistory = searchHistories.get(i);
			searchHistoryDao.insert(searchHistory);
		}
	}
}
