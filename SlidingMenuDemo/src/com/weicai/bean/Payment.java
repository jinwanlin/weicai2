package com.weicai.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.weicai.bean.Order.State;

import android.annotation.SuppressLint;

/**
 * @author jiuwuerliu@sina.com
 * 
 *         数据库实体对象
 */
public class Payment {
	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private long id;
	private Date createdAt;
	private Date updatedAt;
	/** 交易类型 */
	private String type;
	/** 描述 */
	private String desc;
	/** 摘要 */
	private String summary;
	/** 交易金额 */
	private double amount;
	/** 余额 */
	private double overage;

	private long orderId;

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

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getOverage() {
		return overage;
	}

	public void setOverage(double overage) {
		this.overage = overage;
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public static List<Payment> jsonToList(JSONArray array) {
		List<Payment> list = new ArrayList<Payment>();
		if (array != null && array.length() > 0) {
			for (int i = 0; i < array.length(); i++) {
				try {
					JSONObject p = array.getJSONObject(i);
					Payment payment = new Payment();
					payment.setId(p.getLong("id"));
					try {
						Date createdAt = sdf.parse(p.getString("created_at"));
						payment.setCreatedAt(createdAt);
					} catch (ParseException e) {
					}
					try {
						Date updatedAt = sdf.parse(p.getString("updated_at"));
						payment.setUpdatedAt(updatedAt);
					} catch (ParseException e) {
					}
					if(!"".equals(p.getString("order_id"))){
						payment.setOrderId(p.getLong("order_id"));
					}
					payment.setType(p.getString("type"));
					payment.setDesc(p.getString("desc"));
					payment.setSummary(p.getString("summary"));
					payment.setAmount(p.getDouble("amount"));
					payment.setOverage(p.getDouble("overage"));
					list.add(payment);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	@SuppressLint("DefaultLocale")
	public String getType_() {
		switch (Type.toType(type.toUpperCase())) {
		case PAY:
			return "付款";
		case RECHARGE:
			return "充值";
		case REFUND:
			return "退款";
		default:
			return "";
		}
	}
	

	public enum Type {
		PAY, RECHARGE, REFUND, NULL;
		public static Type toType(String str) {
			try {
				return valueOf(str);
			} catch (Exception ex) {
				return NULL;
			}
		}
	}
}