package com.weicai.api;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.weicai.util.net.HttpUtils;


//import android.util.Log;

public class UserAPI extends CaiCai {
	public static final String TAG = UserAPI.class.getSimpleName();


	
	/**
	 * 注册是否需要验证码
	 * @return
	 */
	public static String has_validate_code() {
		String url = BASE_URL + "/api/v2/users/has_validate_code";
		return HttpUtils.doGet(url);
	}
	
	
	/**
	 * 发送注册验证码
	 * @return
	 */
	public static String get_sign_up_validate_code(String phone, String validate_code) {
		String url = BASE_URL + "/api/v2/users/get_sign_up_validate_code";
		Map<String, String> map = new HashMap<String, String>();
		map.put("user[phone]", phone);
		if (validate_code != null && !validate_code.equals("")) {
			map.put("validate_code", validate_code);
		}
		return HttpUtils.doPost(url, map);
	}
	
	public static String send_validate_code(String phone, String validate_code) {
		String url = BASE_URL + "/api/v2/users/send_validate_code";
		Map<String, String> map = new HashMap<String, String>();
		map.put("user[phone]", phone);
		if (validate_code != null && !validate_code.equals("")) {
			map.put("validate_code", validate_code);
		}
		return HttpUtils.doPost(url, map);
	}
	
	/** 找回密码中的 设置密码，无需原密码 */
	public static String update_password(String phone, String password) {
		String url = BASE_URL + "/api/v2/users/update_password";
		Map<String, String> map = new HashMap<String, String>();
		map.put("user[phone]", phone);
		map.put("user[password]", password);
		return HttpUtils.doPost(url, map);
	}
	
	
	/**
	 * 修改用户百度推送的用户ID
	 * @return
	 */
	public static String update_baidu_user_id(String baidu_user_id) {
		String url = BASE_URL + "/api/v2/users/update_baidu_user_id";
		Map<String, String> map = new HashMap<String, String>();
		map.put("baidu_user_id", baidu_user_id);
		map.put("id", userDao.first().getId()+"");
		String result = HttpUtils.doPost(url, map);
		
		Log.i(TAG, "update_baidu_user_id:" + result);
		return result;
	}
	
	/** 设置中的 找回密码，需原密码 */
	public static String change_password(long user_id, String old_password, String password) {
		String url = BASE_URL + "/api/v2/users/change_password";
		Map<String, String> map = new HashMap<String, String>();
		map.put("user[id]", user_id+"");
		map.put("user[old_password]", old_password);
		map.put("user[new_password]", password);
		return HttpUtils.doPost(url, map);
	}

	/**
	 * 登录
	 * 
	 * @param phone
	 * @param password
	 * @return
	 */
	public static String sign_in(String phone, String password) {
		String url = BASE_URL + "/api/v2/users/sign_in";

		Map<String, String> map = new HashMap<String, String>();
		map.put("user[phone]", phone);
		map.put("user[password]", password);

		return HttpUtils.doPost(url, map);
	}

	/**
	 * 注册
	 * 
	 * @param phone
	 * @return
	 */
	public static String sign_up(String phone, String password) {
		String url = BASE_URL + "/api/v2/users/sign_up";

		Map<String, String> map = new HashMap<String, String>();
		map.put("user[phone]", phone);
		map.put("user[password]", password);

		return HttpUtils.doPost(url, map);
	}
}
