package com.weicai.api;

import java.util.HashMap;
import java.util.Map;

import com.weicai.util.net.HttpUtils;

//import android.util.Log;

public class SearchHistoryAPI extends CaiCai {
	public static final String TAG = SearchHistoryAPI.class.getSimpleName();

	/**
	 * 搜索历史列表
	 * 
	 * @return
	 */
	public static String searchHistories() {
		String url = BASE_URL + "/api/v2/search_histories/list";
		Map<String, String> map = new HashMap<String, String>();
		map.put("user[id]", userDao.first().getId() + "");
		return HttpUtils.doPost(url, map);
	}

}
