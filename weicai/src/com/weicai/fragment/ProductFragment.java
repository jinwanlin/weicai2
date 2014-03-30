package com.weicai.fragment;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.weicai.R;
import com.weicai.activity.BaseActivity.NetTask;
import com.weicai.activity.LoadingActivity;
import com.weicai.activity.MainActivity;
import com.weicai.adapter.ProductListAdapter;
import com.weicai.api.CaiCai;
import com.weicai.api.OrderAPI;
import com.weicai.api.ProductAPI;
import com.weicai.bean.Order;
import com.weicai.bean.Product;
import com.weicai.dao.SearchHistoryDao;

public class ProductFragment extends Fragment {
	static final String tag = "ProductFragment";
	private ImageButton product_type;
	private LinearLayout search_ll;
	public static ListView productItemLV;
	public static Button auto_make_order, submit, continue_buy, search, return_all;
	private static TextView nothing_Find, search_textview;
	private Context context;
	public static long last_order_id;
	public static Order.State last_order_state = Order.State.NULL;

	public String type = "Vegetable";
	public String classify;
	public String searchKey;

	public static AlertDialog searchDialog;
	public static SearchHistoryDao searchHistoryDao;

	public void setContext(Context context) {
		this.context = context;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(tag, "ProductFragment");
		View messageLayout = inflater.inflate(R.layout.products_layout, container, false);

		searchHistoryDao = SearchHistoryDao.getInstance();
		productItemLV = (ListView) messageLayout.findViewById(R.id.productItem);

		auto_make_order = (Button) messageLayout.findViewById(R.id.auto_make_order);
		submit = (Button) messageLayout.findViewById(R.id.submit);
		continue_buy = (Button) messageLayout.findViewById(R.id.continue_buy);
		search_ll = (LinearLayout) messageLayout.findViewById(R.id.search_ll);
		search_textview = (TextView) messageLayout.findViewById(R.id.search_textview);
		return_all = (Button) messageLayout.findViewById(R.id.return_all);
		nothing_Find = (TextView) messageLayout.findViewById(R.id.nothing_Find);
		product_type = (ImageButton) messageLayout.findViewById(R.id.product_type);

		submit.setOnClickListener(submitOrderButtonListner());
		auto_make_order.setOnClickListener(autoMakeOrderButtonListner());
		continue_buy.setOnClickListener(continueBuyButtonListner());
		search_ll.setOnClickListener(searchButtonListner());
		return_all.setOnClickListener(returnAllButtonListner());
		product_type.setOnClickListener(productTypeButtonListner());

		new refreshProductsTask().execute(0);
		return messageLayout;
	}

	private OnClickListener productTypeButtonListner() {

		return new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (MainActivity.menu == null) {
					showMenu();
				}

				if (MainActivity.menu.isMenuShowing()) {
					MainActivity.menu.showContent();
				} else {
					MainActivity.menu.showMenu();
				}
			}
		};
	}

	/**
	 * 显示菜单
	 */
	public void showMenu() {
		// Log.i("aa--", "showMenu===========");

		MainActivity.menu = new SlidingMenu(context);
		MainActivity.menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		MainActivity.menu.setShadowDrawable(R.drawable.shadow);
		MainActivity.menu.setShadowWidthRes(R.dimen.shadow_width);
		// MainActivity.menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);//
		// 设置拉出菜单后，上层内容留下的宽度，即这个宽度＋菜单宽度＝屏幕宽度
		MainActivity.menu.setBehindWidth(250);
		MainActivity.menu.setFadeDegree(0f);// 设置隐藏或显示菜单时，菜单渐变值，0，不变，1黑色，值为0－1
		MainActivity.menu.attachToActivity((MainActivity) context, SlidingMenu.SLIDING_CONTENT);
		MainActivity.menu.setMenu(R.layout.menu);
		MenuFragment menuFragment = new MenuFragment();
		menuFragment.setContext(context);
		((MainActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.menu, menuFragment).commit();
	}

	/**
	 * 显示所有按钮监听
	 * 
	 * @return
	 */
	private OnClickListener returnAllButtonListner() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				type = "Vegetable";
				classify = null;
				searchKey = null;
				new refreshProductsTask().execute(0);
			}
		};
	}

	/**
	 * 搜索 文本框监听
	 * 
	 * @return
	 */
	private OnClickListener searchButtonListner() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity) context).showSearch();
			}
		};
	}

	/**
	 * 刷新商品列表
	 */
	private class refreshProductsTask extends NetTask {

		@Override
		protected String doInBackground(Integer... params) {
			try {
				Intent intent = new Intent();
				MainActivity mainActivity = (MainActivity) context;
				intent.setClass(mainActivity, LoadingActivity.class);// 跳转到加载界面
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return ProductAPI.list(type, classify, searchKey);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result == null || result.equals("")) {
				return;
			}

			nothing_Find.setVisibility(View.GONE);
			return_all.setVisibility(View.GONE);

			JSONObject json = CaiCai.StringToJSONObject(result);
			JSONArray jsonArray = null;
			try {
				last_order_state = Order.State.valueOf(json.getString("last_order_state").toUpperCase());
				if (!json.getString("last_order_id").equals("null")) {
					last_order_id = json.getLong("last_order_id");
				}
				changeOrderState();

				jsonArray = json.getJSONArray("products");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			List<Product> products = null;
			if (jsonArray != null) {
				products = Product.jsonToList(jsonArray);
				ProductListAdapter productListAdapter = new ProductListAdapter(context, products);
				productItemLV.setAdapter(productListAdapter);

				if (products.isEmpty()) {
					nothing_Find.setVisibility(View.VISIBLE);
				}
			}

			try {
				if (searchKey != null && !searchKey.equals("")) {
					return_all.setVisibility(View.VISIBLE);

					if (products != null && products.size() > 0) {
						searchHistoryDao.updateKeyword(json.getLong("search_history_id"), searchKey);
					}
					search_textview.setText(searchKey);
				} else {
					search_textview.setText("点击搜索");
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}

	/***
	 * 自动下单按钮监听
	 * 
	 * @author jinwanlin
	 * 
	 */
	private OnClickListener autoMakeOrderButtonListner() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new AutoMakeOrder().execute(0);
			}
		};
	}

	/***
	 * 自动下单任务
	 * 
	 * @author jinwanlin
	 * 
	 */
	private class AutoMakeOrder extends NetTask {

		@Override
		protected String doInBackground(Integer... params) {
			return OrderAPI.autoMakeOrder();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result == null || result.equals("")) {
				return;
			}

			type = "Vegetable";
			classify = null;
			searchKey = null;
			new refreshProductsTask().execute(0);
		}

	}

	/***
	 * 提交订单按钮监听
	 * 
	 * @author jinwanlin
	 * 
	 */
	private OnClickListener submitOrderButtonListner() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new SubmitOrder().execute(0);
			}
		};
	}

	/***
	 * 提交订单任务
	 * 
	 * @author jinwanlin
	 * 
	 */
	private class SubmitOrder extends NetTask {

		@Override
		protected String doInBackground(Integer... params) {
			return OrderAPI.submitOrder(last_order_id);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result == null || result.equals("")) {
				return;
			}

			JSONObject json = CaiCai.StringToJSONObject(result);
			boolean status = false;
			try {
				status = json.getBoolean("status");
				if (status) {
					last_order_state = Order.State.valueOf(json.getString("state").toUpperCase());
					changeOrderState();

					for (int i = 0; i < productItemLV.getChildCount(); i++) {
						LinearLayout l = (LinearLayout) productItemLV.getChildAt(i);
						Button b = (Button) l.getChildAt(4);
						if (b.getText().equals("购买")) {
							b.setText("");
							b.setBackgroundColor(Color.parseColor("#ffffff"));
						}
						b.setClickable(false);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	/***
	 * 继续购买按钮监听
	 * 
	 * @author jinwanlin
	 * 
	 */
	private OnClickListener continueBuyButtonListner() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new ContinueBuy().execute(0);
			}
		};
	}

	/***
	 * 继续购买任务
	 * 
	 * @author jinwanlin
	 * 
	 */
	private class ContinueBuy extends NetTask {

		@Override
		protected String doInBackground(Integer... params) {
			return OrderAPI.continueBuy(last_order_id);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result == null || result.equals("")) {
				return;
			}

			JSONObject json = CaiCai.StringToJSONObject(result);
			try {
				last_order_state = Order.State.valueOf(json.getString("state").toUpperCase());
				changeOrderState();

				for (int i = 0; i < productItemLV.getChildCount(); i++) {
					LinearLayout l = (LinearLayout) productItemLV.getChildAt(i);
					Button b = (Button) l.getChildAt(4);
					if (b.getText().equals("")) {
						b.setText("购买");
						b.setBackgroundResource(R.drawable.buy_selector);
						b.setTextColor(Color.parseColor("#ffffff"));
					}
					b.setClickable(true);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	/***
	 * 根据订单状态改变按钮显示
	 * 
	 * @author jinwanlin
	 * 
	 */
	public static void changeOrderState() {
		auto_make_order.setVisibility(View.GONE);
		submit.setVisibility(View.GONE);
		continue_buy.setVisibility(View.GONE);

		switch (last_order_state) {
		case PENDING:
			submit.setVisibility(View.VISIBLE);
			break;
		case CONFIRMED:
			continue_buy.setVisibility(View.VISIBLE);
			break;
		case SHIPING:
		case BALED:
		case TRUCK:
		case SIGNED:
		case DONE:
		case CANCELED:
			auto_make_order.setVisibility(View.VISIBLE);
			break;
		case NULL:
			break;
		}
	}
	
	public static void updateProducts(String orderId, String orderState){
		if(orderId.equals(last_order_id+"")){
			last_order_state = Order.State.valueOf(orderState.toUpperCase());
			changeOrderState();
			ProductListAdapter.resetOrderAmount();
			for (int i = 0; i < productItemLV.getChildCount(); i++) {
				LinearLayout l = (LinearLayout) productItemLV.getChildAt(i);
				l.setBackgroundColor(Color.parseColor("#ffffff"));

				Button b = (Button) l.getChildAt(4);
				b.setText("购买");
				b.setTextColor(Color.parseColor("#ffffff"));
				b.setBackgroundResource(R.drawable.buy_selector);
				b.setClickable(true);
			}
		}
	}

}
