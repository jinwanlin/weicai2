package com.weicai.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.weicai.util.net.HttpUtils;


//import android.util.Log;

public class ProductAPI extends CaiCai {
	public static final String TAG = ProductAPI.class.getSimpleName();

	
	/**
	 * 商品列表
	 * 
	 * @return
	 */
	public static String list(String type, String classify, String searchKey) {
		String url = server_host + "/api/v2/products/list";
		Map<String, String> map = new HashMap<String, String>();
		map.put("user[id]", userDao.first().getId()+"");
		if(type!=null){
			map.put("type", type);
		}
		if(classify!=null){
			map.put("classify", classify.toString());
		}
		if(searchKey!=null && !searchKey.equals("")){
			map.put("searchKey", searchKey);
		}
		
		return HttpUtils.doPost(url, map);
	}
	

	
	/**
	 * 商品分类
	 * 
	 * @return
	 */
	public static String types() {
		String url = server_host + "/api/v2/products/types";
		return HttpUtils.doGet(url);
	}
}
