package com.weicai.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.weicai.dao.SearchHistoryDao;
import com.weicai.dao.UserDao;
import com.weicai.util.net.HttpUtils;
//import android.util.Log;

public class CaiCai {
	public static final String TAG = CaiCai.class.getSimpleName();

	public static UserDao userDao;
	public static SearchHistoryDao searchHistoryDao;

	static{
		userDao = UserDao.getInstance();
		searchHistoryDao = SearchHistoryDao.getInstance();
	}

	public static String mac_pro = "http://192.168.0.103:3000";
	public static String server_host;
	public static String aliyun = "http://115.28.160.65";

	/**
	 * String 转 JSONObject
	 * 
	 * @param str
	 * @return
	 */
	public static JSONObject StringToJSONObject(String str) {
		if (str.equals("")) {
			return null;
		}
		JSONObject jsobj = null;
		try {
			jsobj = new JSONObject(str);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsobj;
	}

	/**
	 * String 转 JSONArray
	 * 
	 * @param str
	 * @return
	 */
	public static JSONArray StringToJSONArray(String str) {
		if (str == null) {
			return null;
		}

		JSONArray json = null;
		try {
			json = new JSONArray(str);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 最新版本
	 * 
	 * @return
	 */
	public static String lastVersion() {
		Log.i(TAG, "lastVersion");
		String url = server_host + "/api/versions/last_version";
		return HttpUtils.doGet(url);
	}

}
