package com.weicai.api;

import java.util.HashMap;
import java.util.Map;

import com.weicai.util.net.HttpUtils;


//import android.util.Log;

public class OrderAPI extends CaiCai {
	public static final String TAG = OrderAPI.class.getSimpleName();

	/**
	 * 订单列表
	 * 
	 * @return
	 */
	public static String orders() {
		String url = server_host + "/api/v2/orders/list";
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("user[id]", userDao.first().getId()+"");
		
		return HttpUtils.doPost(url, map);
	}
	

	/**
	 * 订单明细
	 * 
	 * @return
	 */
	public static String order(long id) {
		String url = server_host + "/api/v2/orders/"+id;
		return HttpUtils.doGet(url);
	}
	
	
	/**
	 * 购买
	 * 
	 * @param product_id
	 * @param amount
	 * @return
	 */
	public static String buy(String product_id, String amount) {
		String url = server_host + "/api/v2/order_items";

		Map<String, String> map = new HashMap<String, String>();
		map.put("order_item[product_id]", product_id);
		map.put("order_item[order_amount]", amount);
		map.put("user[id]", userDao.first().getId()+"");

		return HttpUtils.doPost(url, map);
	}
	

	
	public static String autoMakeOrder() {
		String url = server_host + "/api/v2/orders/auto_make_order";
		Map<String, String> map = new HashMap<String, String>();
		map.put("user[id]", userDao.first().getId()+"");

		return HttpUtils.doPost(url, map);
	}
	
	public static String submitOrder(long order_id) {
		String url = server_host + "/api/v2/orders/"+order_id+"/submit";
		return HttpUtils.doGet(url);
	}
	
	
	public static String continueBuy(long order_id) {
		String url = server_host + "/api/v2/orders/"+order_id+"/continue_buy";
		return HttpUtils.doGet(url);
	}
	
	public static String cancelOrder(long order_id) {
		String url = server_host + "/api/v2/orders/"+order_id+"/cancel";
		return HttpUtils.doGet(url);
	}
}
