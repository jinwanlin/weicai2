package com.weicai.fragment;

import java.util.List;

import org.json.JSONArray;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weicai.R;
import com.weicai.activity.BaseActivity.NetTask;
import com.weicai.activity.LoadingActivity;
import com.weicai.activity.MainActivity;
import com.weicai.adapter.OrderListAdapter;
import com.weicai.adapter.ProductListAdapter;
import com.weicai.api.CaiCai;
import com.weicai.api.OrderAPI;
import com.weicai.bean.Order;
import com.weicai.bean.Order.State;

public class OrdersFragment extends Fragment {

	static final String tag = "OrdersFragment";

	private static ListView orderListLV;
	private Context context;
	private RelativeLayout no_values_layout;
	
	private MainActivity mainActivity;


	public MainActivity getMainActivity() {
		return mainActivity;
	}

	public void setMainActivity(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}
	
	public void setContext(Context context){
		this.context = context;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.orders_layout, container, false);
		
		orderListLV = (ListView) layout.findViewById(R.id.orderListLV);
		no_values_layout = (RelativeLayout) layout.findViewById(R.id.no_values_layout);

		new refreshOrdersTask().execute(0);
		
		return layout;
	}
	
    
    /**
     * 刷新订单列表
     */
	class refreshOrdersTask extends NetTask {
		@Override
		protected String doInBackground(Integer... params) {
			Intent intent = new Intent();
	        intent.setClass(mainActivity, LoadingActivity.class);//跳转到加载界面
	        startActivity(intent);	
	        
			return OrderAPI.orders();
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result == null || result.equals("")) {
				return;
			}
			
			JSONArray json = CaiCai.StringToJSONArray(result);
			List<Order> orders = Order.jsonToList(json);
			
			if (orders.size() > 0) {
			}else{
				no_values_layout.setVisibility(View.VISIBLE);
			}
			
			OrderListAdapter orderListAdapter = new OrderListAdapter(context, orders, OrdersFragment.this);
			Log.i(tag, (orderListLV == null)+"");
			orderListLV.setAdapter(orderListAdapter);
		}
	}
	
	public static void updateOrders(String order_no, String state){
//		ProductListAdapter.resetOrderAmount();
		if(orderListLV != null){
			for (int i = 0; i < orderListLV.getChildCount(); i++) {
				LinearLayout orderLine = (LinearLayout) orderListLV.getChildAt(i);
				orderLine = (LinearLayout)orderLine.getChildAt(0);
				
				LinearLayout secondRole = (LinearLayout)orderLine.getChildAt(1);
				TextView orderNo = (TextView)secondRole.getChildAt(1);
				
				if(order_no.equals(orderNo.getText().toString())){
					LinearLayout lastRow = (LinearLayout)orderLine.getChildAt(2);
					TextView orderState = (TextView)lastRow.getChildAt(1);
					Order o = new Order();
					o.setState(state);
					orderState.setText(o.getStateStr());
					break;
				}
			}
		}

	}
}
