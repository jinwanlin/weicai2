package com.weicai.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.util.Log;

import com.weicai.api.CaiCai;
import com.weicai.bean.Order.State;

/**
 * @author jiuwuerliu@sina.com
 * 
 *         数据库实体对象
 */
public class Product {
	private int id;
	private Date createdAt;
	private Date updatedAt;

	private String sn;
	private String name;
	private String type;
	private String amounts;
	private double price;
	private String unit;
	private String avatar;
	/** 平均每次订购量 */
	private int average_amount; 
	/** 今日订单实际订购量 */
	private int orderAmount;

	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAmounts() {
		return amounts;
	}

	public void setAmounts(String amounts) {
		this.amounts = amounts;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(int orderAmount) {
		this.orderAmount = orderAmount;
	}

	public int getAverage_amount() {
		return average_amount;
	}

	public String getAvatar() {
		return CaiCai.server_host+avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public void setAverage_amount(int average_amount) {
		this.average_amount = average_amount;
	}

	public String[] getAmountArray() {
		String[] amountArray;
		if (amounts != null && !amounts.equals("")) {
			amountArray = amounts.split(",");
		} else {
			amountArray = new String[0];
		}
		return amountArray;
	}

	public String[] getAmountArray_unit() {
		String[] amountArray = getAmountArray();
		for (int i = 0; i < amountArray.length; i++) {
			amountArray[i] = amountArray[i] + unit;
		}
		return amountArray;
	}

	public static List<Product> jsonToList(JSONArray array) {
		List<Product> list = new ArrayList<Product>();
		if (array != null && array.length() > 0) {
			for (int i = 0; i < array.length(); i++) {
				try {
					JSONObject p = array.getJSONObject(i);
					Product product = new Product();
					product.setId(p.getInt("id"));
					product.setSn(p.getString("sn"));
					product.setName(p.getString("name"));
					product.setType(p.getString("type"));
					product.setAmounts(p.getString("amounts"));
					product.setPrice(p.getDouble("price"));
					product.setUnit(p.getString("unit"));
					product.setOrderAmount(p.getInt("order_amount"));
					product.setAverage_amount(p.getInt("average_amount"));
					product.setAvatar(p.getString("avatar"));
					list.add(product);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

}