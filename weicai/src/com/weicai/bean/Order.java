package com.weicai.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;

/**
 * @author jiuwuerliu@sina.com
 * 
 *         数据库实体对象
 */
@SuppressLint("DefaultLocale")
public class Order {

	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private long id;
	private Date createdAt;
	private Date updatedAt;

	private String sn;
	private double orderSum;
	private double shipSum;
	private String state;
	private List<OrderItem> orderItems;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public double getOrderSum() {
		return orderSum;
	}

	public void setOrderSum(double orderSum) {
		this.orderSum = orderSum;
	}

	public double getShipSum() {
		return shipSum;
	}

	public void setShipSum(double shipSum) {
		this.shipSum = shipSum;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	/** 是否已出库完毕 */
	public boolean is_ship(){
		State order_state = State.toState(state.toUpperCase());
		
		switch (order_state) {
		case PENDING:
		case CONFIRMED:
		case SHIPING:
		case BALED:
			return false;
		case TRUCK:
		case SIGNED:
		case DONE:
			return true;
		case CANCELED:
		default:
			return false;
		}
	}
	
	public double get_sum(){
		return is_ship() ? shipSum : orderSum;
	}

	
	public static Order jsonToOrder(JSONObject p) {
		Order order = new Order();
		try {
			order.setId(p.getInt("id"));
			order.setSn(p.getString("sn"));
			order.setOrderSum(p.getDouble("order_sum"));
			order.setShipSum(p.getDouble("ship_sum"));
			order.setState(p.getString("state"));

			try {
				Date created_at = sdf.parse(p.getString("created_at"));
				order.setCreatedAt(created_at);
			} catch (ParseException e) {
			}
			try {
				Date updated_at = sdf.parse(p.getString("updated_at"));
				order.setCreatedAt(updated_at);
			} catch (ParseException e) {
			}

			if (p.has("order_items")) {
				JSONArray order_items = p.getJSONArray("order_items");
				List<OrderItem> items = OrderItem.jsonToList(order_items, order);
				order.setOrderItems(items);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return order;
	}

	public static List<Order> jsonToList(JSONArray array) {

		List<Order> list = new ArrayList<Order>();
		if (array != null && array.length() > 0) {
			for (int i = 0; i < array.length(); i++) {
				try {
					JSONObject p = array.getJSONObject(i);
					Order order = jsonToOrder(p);
					list.add(order);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	@SuppressLint("DefaultLocale")
	public String getStateStr() {
		switch (State.toState(state.toUpperCase())) {
		case PENDING:
			return "挑选商品";
		case CONFIRMED:
			return "等待配送";
		case SHIPING:
			return "配送中";
		case BALED:
			return "配送中";
		case TRUCK:
			return "配送中";
		case SIGNED:
			return "已送达";
		case DONE:
			return "交易成功";
		case CANCELED:
			return "已取消";
		default:
			return "";
		}
	}
	
	public State getState_() {
		if(state!=null && !state.equals("")){
			return State.toState(state.toUpperCase());
		}else{
			return State.NULL;
		}
	}
 
	public enum State {
//		pending/confirmed/shiping/baled/truck/signed/done/canceled
		PENDING, CONFIRMED, SHIPING, BALED, TRUCK, SIGNED, DONE, CANCELED, NULL;
		public static State toState(String str) {
			try {
				return valueOf(str);
			} catch (Exception ex) {
				return NULL;
			}
		}
	}
}