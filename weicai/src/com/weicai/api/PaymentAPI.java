package com.weicai.api;

import java.util.HashMap;
import java.util.Map;

import com.weicai.util.net.HttpUtils;


//import android.util.Log;

public class PaymentAPI extends CaiCai {
	public static final String TAG = PaymentAPI.class.getSimpleName();


	
	/**
	 * 交易明细
	 * 
	 * @return
	 */
	public static String payment(long id) {
		String url = BASE_URL + "/api/v2/payments/"+id;
		return HttpUtils.doGet(url);
	}
	
	/**
	 * 账单列表
	 * 
	 * @return
	 */
	public static String payments() {
		String url = BASE_URL + "/api/v2/payments/list";
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("user[id]", userDao.first().getId()+"");
		
		return HttpUtils.doPost(url, map);
	}
	
}
