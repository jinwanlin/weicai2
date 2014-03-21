package com.weicai.activity;

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
import com.weicai.adapter.ProductListAdapter;
import com.weicai.api.CaiCai;
import com.weicai.api.OrderAPI;
import com.weicai.api.ProductAPI;
import com.weicai.bean.Order;
import com.weicai.bean.Product;
import com.weicai.bean.SearchHistory;
import com.weicai.dao.SearchHistoryDao;

public class ProductFragment extends Fragment {
	static final String tag = "ProductFragment";
	private ImageButton product_type;

	public static ListView productItemLV;
	public static Button auto_make_order, submit, continue_buy, search, return_all;
	private static TextView nothing_Find;
	private Context context;
	public static long last_order_id;
	public static Order.State last_order_state = Order.State.NULL;
	// public Spinner classify_spinner;
	// private String[] classifies = { "全部", "叶菜类", "根茎类", "瓜果类", "豆荚类", "葱姜蒜",
	// "菌类", "水生菜" };
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
//		productItemLV.addHeaderView(searchLayout());

		auto_make_order = (Button) messageLayout.findViewById(R.id.auto_make_order);
		submit = (Button) messageLayout.findViewById(R.id.submit);
		continue_buy = (Button) messageLayout.findViewById(R.id.continue_buy);
		// search = (Button) messageLayout.findViewById(R.id.search);
		return_all = (Button) messageLayout.findViewById(R.id.return_all);
		nothing_Find = (TextView) messageLayout.findViewById(R.id.nothing_Find);
		product_type = (ImageButton) messageLayout.findViewById(R.id.product_type);

		submit.setOnClickListener(submitOrderButtonListner());
		auto_make_order.setOnClickListener(autoMakeOrderButtonListner());
		continue_buy.setOnClickListener(continueBuyButtonListner());
		// search.setOnClickListener(searchButtonListner());
		return_all.setOnClickListener(returnAllButtonListner());
		product_type.setOnClickListener(productTypeButtonListner());

		// ArrayAdapter classifyAdapter = new ArrayAdapter(context,
		// android.R.layout.simple_spinner_item,
		// java.util.Arrays.asList(classifies));
		// classifyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// classify_spinner = (Spinner)
		// messageLayout.findViewById(R.id.product_type);
		// classify_spinner.setAdapter(classifyAdapter);
		// classify_spinner.setOnItemSelectedListener(productTypeSpinnerListener());

		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
		// WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

		new RefreshProductsTask("Vegetable", null, null).execute(0);
		// new InitMenu().start();

		return messageLayout;
	}

	// class InitMenu extends Thread {
	// public void run() {
	// showMenu();
	// }
	// }

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

	public void showMenu() {
		Log.i("aa--", "showMenu===========");

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
		// android.support.v4.app.Fragment
		MenuFragment menuFragment = new MenuFragment();
		menuFragment.setContext(context);
		((MainActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.menu, menuFragment).commit();
	}

	private OnClickListener returnAllButtonListner() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new RefreshProductsTask("Vegetable", null, null).execute(0);
			}
		};
	}

	// private OnClickListener searchButtonListner() {
	// return new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// List<String> keywordsList = searchHistoryDao.getKeywordsList();
	// final ArrayAdapter<String> arrayAdapter = new
	// ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,
	// keywordsList);
	//
	// final LayoutInflater factory = LayoutInflater.from(context);
	// final View textEntryView = factory.inflate(R.layout.search_dialog, null);
	// ListView productItem = (ListView)
	// textEntryView.findViewById(R.id.productItem);
	// productItem.setAdapter(arrayAdapter);
	//
	// final AlertDialog dialog = new
	// AlertDialog.Builder(context).setView(textEntryView).setPositiveButton("确定",
	// new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// EditText editText = (EditText) textEntryView.findViewById(R.id.likeText);
	// String searchKey = editText.getText().toString();
	// if (searchKey != null && !searchKey.equals("")) {
	// new RefreshProductsTask("Vegetable", null, searchKey).execute(0);
	// }
	// Toast.makeText(context, "搜索关键字：" + searchKey, Toast.LENGTH_SHORT).show();
	// }
	// }).setNegativeButton("取消", null).create();
	//
	// productItem.setOnItemClickListener(new OnItemClickListener() {
	// @Override
	// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long
	// arg3) {
	// String searchKey = classifies[arg2];
	// Log.i("11", classifies[arg2] + "");
	// if (searchKey != null && !searchKey.equals("")) {
	// new RefreshProductsTask("Vegetable", null, searchKey).execute(0);
	// }
	// Toast.makeText(context, "搜索关键字：" + searchKey, Toast.LENGTH_SHORT).show();
	// dialog.cancel();
	// }
	// });
	// dialog.show();
	// }
	// };
	// }

	// private AdapterView.OnItemSelectedListener productTypeSpinnerListener() {
	// return new AdapterView.OnItemSelectedListener() {
	// @Override
	// public void onItemSelected(AdapterView<?> adapter, View view, int
	// position, long id) {
	// // String
	// // str=(String)product_type_spinner.getAdapter().getItem((int)id);
	// Integer classify = null;
	// if (id > 0) {
	// classify = Integer.parseInt(id - 1 + "");
	// }
	// new RefreshProductsTask("Vegetable", classify, null).execute(0);
	// }
	//
	// @Override
	// public void onNothingSelected(AdapterView<?> arg0) {
	//
	// }
	// };
	// }

	/** 作为public方法给其他类调用 */
	public void RefreshProduct(String type, String classify, String searchKey) {
		new RefreshProductsTask("Vegetable", classify, searchKey).execute(0);
	}

	/**
	 * 刷新商品列表
	 */
	private class RefreshProductsTask extends NetTask {
		String type;
		String classify;
		String searchKey;

		public RefreshProductsTask(String type, String classify, String searchKey) {
			this.type = type;
			this.classify = classify;
			this.searchKey = searchKey;
		}

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

			Log.i("", "searchKey:" + searchKey);
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
				// productItemLV.addHeaderView(searchLayout());
				productItemLV.setAdapter(productListAdapter);

				if (products.isEmpty()) {
					nothing_Find.setVisibility(View.VISIBLE);
				}
			}

			try {
				if (searchKey != null && !searchKey.equals("")) {
					return_all.setVisibility(View.VISIBLE);

					if (products != null && products.size() > 0) {
						SearchHistory searchHistory = new SearchHistory();
						searchHistory.setId(json.getLong("search_history_id"));
						searchHistory.setKeywords(searchKey);
						searchHistory = searchHistoryDao.insert(searchHistory);
						Log.i(tag, searchHistory.getId() + "");
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}

	private OnClickListener autoMakeOrderButtonListner() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new AutoMakeOrder().execute(0);
			}
		};
	}

	/***
	 * 自动下单
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

			new RefreshProductsTask("Vegetable", null, null).execute(0);
		}

	}

	private OnClickListener submitOrderButtonListner() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new SubmitOrder().execute(0);
			}
		};
	}

	/***
	 * 提交订单
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

	private OnClickListener continueBuyButtonListner() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new ContinueBuy().execute(0);
			}
		};
	}

	/***
	 * 继续购买
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

	// private class Search extends NetTask {
	//
	// @Override
	// protected String doInBackground(Integer... params) {
	// return CaiCai.productsStr("Vegetable", null, searchKey);
	// }
	//
	// @Override
	// protected void onPostExecute(String result) {
	// super.onPostExecute(result);
	// if (result == null || result.equals("")) {
	// return;
	// }
	//
	// JSONObject json = CaiCai.StringToJSONObject(result);
	// JSONArray jsonArray = null;
	// try {
	// jsonArray = json.getJSONArray("products");
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	//
	// if (jsonArray != null) {
	// List<Product> products = Product.jsonToList(jsonArray);
	// ProductListAdapter productListAdapter = new ProductListAdapter(context,
	// products);
	// Log.i(tag, (productItemLV == null) + "");
	// productItemLV.setAdapter(productListAdapter);
	// }
	// }
	//
	// }

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

//	private View searchLayout() {
// 
//		// LinearLayout.LayoutParams params = new
//		// LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
//		// LayoutParams.WRAP_CONTENT);
//		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
//		// params.setLayoutDirection(layoutDirection);
//
//		LinearLayout search_line_layout = new LinearLayout(context);
//		search_line_layout.setOrientation(LinearLayout.HORIZONTAL);
//		search_line_layout.setWeightSum(1f);
//		search_line_layout.setPadding(10, 10, 10, 10);
//
//		EditText keywords_view = new EditText(context);
//		keywords_view.setHint("请输入搜索关键字");
//		keywords_view.setLayoutParams(param);
//		search_line_layout.addView(keywords_view);
//
//		Button search_button = new Button(context);
//		search_button.setText("搜索");
//		search_button.setBackgroundColor(Color.BLUE);
//		search_button.setWidth(30);
//		search_line_layout.addView(search_button);
//
//		LinearLayout search_history_layout = new LinearLayout(context);
//		search_history_layout.setOrientation(LinearLayout.VERTICAL);
//
//		search_history_layout.addView(search_line_layout);
//
//		for (int i = 0; i < 3; i++) {
//			TextView history = new TextView(context);
//			history.setText("keywords:" + i);
//			search_history_layout.addView(history);
//		}
//
//		return search_history_layout;
//	}
}
