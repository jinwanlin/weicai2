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
import android.widget.Toast;

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
//import android.widget.LinearLayout;
//import android.widget.LinearLayout;

public class ProductFragment extends Fragment {
	static final String tag = "ProductFragment";
	private ImageButton product_type;
	private LinearLayout search_ll;
	public static ListView productItemLV;
	public static Button auto_make_order, submit, continue_buy, search;
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
		nothing_Find = (TextView) messageLayout.findViewById(R.id.nothing_Find);
		product_type = (ImageButton) messageLayout.findViewById(R.id.product_type);

		submit.setOnClickListener(submitOrderButtonListner());
		auto_make_order.setOnClickListener(autoMakeOrderButtonListner());
		continue_buy.setOnClickListener(continueBuyButtonListner());
		search_ll.setOnClickListener(searchButtonListner());
		product_type.setOnClickListener(productTypeButtonListner());

		new RefreshProductsTask().execute(0);
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
				new RefreshProductsTask().execute(0);
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
	private class RefreshProductsTask extends NetTask {

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

				try {
					Log.i(tag, productItemLV.getFooterViewsCount()+"==++");
					
					if (searchKey != null && !searchKey.equals("")) {
						if(productItemLV.getFooterViewsCount()==0){
							productItemLV.addFooterView(searAllbutton());
						}
						
						if (products != null && products.size() > 0) {
							searchHistoryDao.updateKeyword(json.getLong("search_history_id"), searchKey);
						}
						search_textview.setText(searchKey);
					} else {
						if(productItemLV.getFooterViewsCount()>0){
							productItemLV.removeFooterView(search_button);
						}
						search_textview.setText("点击搜索");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				ProductListAdapter productListAdapter = new ProductListAdapter(context, products);
				productItemLV.setAdapter(productListAdapter);

				if (products.isEmpty()) {
					nothing_Find.setVisibility(View.VISIBLE);					
				}else{
					nothing_Find.setVisibility(View.GONE);					
				}
			}



		}

	}
	
//	static LinearLayout footer_layout;
//	private View list_footer() {
//		
//
//		if(footer_layout!=null){
//			return footer_layout;
//		}
//
//		// LinearLayout.LayoutParams params = new
//		// LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
//		// LayoutParams.WRAP_CONTENT);
////		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
////		param.setMargins(15, 15, 15, 15);
//		// params.setLayoutDirection(layoutDirection);
//
//		footer_layout = new LinearLayout(context);
//		footer_layout.setOrientation(LinearLayout.HORIZONTAL);
//		footer_layout.setWeightSum(1f);
//		footer_layout.setPadding(10, 10, 10, 10);
//
////		EditText keywords_view = new EditText(context);
////		keywords_view.setHint("请输入搜索关键字");
////		keywords_view.setLayoutParams(param);
////		search_line_layout.addView(keywords_view);
//
//		Button search_button = new Button(context);
//		search_button.setText("查看所有");
//		search_button.setOnClickListener(returnAllButtonListner());
//		footer_layout.addView(search_button);
//
//
//		LinearLayout search_history_layout = new LinearLayout(context);
//		search_history_layout.setOrientation(LinearLayout.VERTICAL);
//
//		search_history_layout.addView(footer_layout);
//		
////		footer.addView(footer_layout);
//		return footer_layout;
//	}
	
	static Button search_button;
	private Button searAllbutton(){
		if(search_button == null){
			search_button = new Button(context);
			search_button.setText("查看所有");
			search_button.setOnClickListener(returnAllButtonListner());
		}
		return search_button;
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
			try {
				Intent intent = new Intent();
				MainActivity mainActivity = (MainActivity) context;
				intent.setClass(mainActivity, LoadingActivity.class);// 跳转到加载界面
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
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
			new RefreshProductsTask().execute(0);
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
			try {
				Intent intent = new Intent();
				MainActivity mainActivity = (MainActivity) context;
				intent.setClass(mainActivity, LoadingActivity.class);// 跳转到加载界面
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
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
						}else{
							b.setBackgroundColor(Color.parseColor("#CCFFCC"));
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
			try {
				Intent intent = new Intent();
				MainActivity mainActivity = (MainActivity) context;
				intent.setClass(mainActivity, LoadingActivity.class);// 跳转到加载界面
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
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
				
				if(last_order_state == Order.State.PENDING){
					for (int i = 0; i < productItemLV.getChildCount(); i++) {
						LinearLayout l = (LinearLayout) productItemLV.getChildAt(i);
						Button b = (Button) l.getChildAt(4);
						if (b.getText().equals("")) {
							b.setText("购买");
							b.setBackgroundResource(R.drawable.buy_selector);
							b.setTextColor(Color.parseColor("#ffffff"));
						}else{
							b.setBackgroundResource(R.drawable.bought_selector);
						}
						b.setClickable(true);
					}
				}else{
					Toast.makeText(context, "订单已出库，不能继续购买。", Toast.LENGTH_LONG).show();
					for (int i = 0; i < productItemLV.getChildCount(); i++) {
						LinearLayout l = (LinearLayout) productItemLV.getChildAt(i);
						l.setBackgroundColor(Color.parseColor("#ffffff"));
						Button b = (Button) l.getChildAt(4);
						b.setText("购买");
						b.setBackgroundResource(R.drawable.buy_selector);
						b.setTextColor(Color.parseColor("#ffffff"));
						b.setClickable(true);
					}
					
					List<Product> products = ProductListAdapter.products;
					for (int i = 0; i < products.size(); i++) {
						Product product = products.get(i);
						product.setOrderAmount(0);
					}
					
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
