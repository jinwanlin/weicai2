package com.weicai.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.weicai.R;
import com.weicai.activity.BaseActivity.NetTask;
import com.weicai.activity.LoadingActivity;
import com.weicai.activity.MainActivity;
import com.weicai.adapter.OrderItemListAdapter;
import com.weicai.adapter.ProductListAdapter;
import com.weicai.api.CaiCai;
import com.weicai.api.OrderAPI;
import com.weicai.bean.Order;
import com.weicai.bean.Order.State;
import com.weicai.util.tool.DoubleFormat;
import com.weicai.util.tool.TodayYestorday;

public class OrderFragment extends Fragment implements OnClickListener {

	static final String tag = "OrderFragment";

	private Fragment lastFragment;
	private static long order_id;
	private ListView orderItemsLV;
	private static TextView sn, state, date, total;
	private LinearLayout order_info, items_header;
	private static Button cancel_order;
	public TextView amount_title;
	public TextView total_title;
	private Context context;
	
	public void setContext(Context context){
		this.context = context;
	}
	
	public void setLastFragment(Fragment lastFragment){
		this.lastFragment = lastFragment;
	}
	
	public void setOrder_id(long order_id) {
		this.order_id = order_id;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View orderLayout = inflater.inflate(R.layout.order_layout, container, false);
		
		orderLayout.findViewById(R.id.back).setOnClickListener(this);
		
		cancel_order = (Button)orderLayout.findViewById(R.id.cancel_order);
		sn = (TextView)orderLayout.findViewById(R.id.sn);
		state = (TextView)orderLayout.findViewById(R.id.state);
		date = (TextView)orderLayout.findViewById(R.id.date);
		total = (TextView)orderLayout.findViewById(R.id.total);

		amount_title = (TextView)orderLayout.findViewById(R.id.amount_title);
		total_title = (TextView)orderLayout.findViewById(R.id.total_title);
//		total_title2 = (TextView)orderLayout.findViewById(R.id.total_title2);
		
		orderItemsLV = (ListView) orderLayout.findViewById(R.id.orderItemsLV);
		
		order_info = (LinearLayout)orderLayout.findViewById(R.id.order_info);
		items_header = (LinearLayout)orderLayout.findViewById(R.id.items_header);

		cancel_order.setOnClickListener(cancelOrderButtonListner());

		order_info.setVisibility(View.GONE);
		items_header.setVisibility(View.GONE);	
		new refreshOrderTask().execute(0);
		
		return orderLayout;
	}
	
	private OnClickListener cancelOrderButtonListner() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				new AlertDialog.Builder(context).setIcon(android.R.drawable.btn_dialog).setTitle("提示").setMessage("确定要取消订单吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						new CancelOrder().execute(0);
					}
				}).setNegativeButton("不取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
			}
		};
	}
	
	/***
	 * 取消订单
	 * 
	 * @author jinwanlin
	 * 
	 */
	class CancelOrder extends NetTask {

		@Override
		protected String doInBackground(Integer... params) {
			return OrderAPI.cancelOrder(order_id);
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
//					ProductFragment.last_order_state = Order.State.valueOf(json.getString("state").toUpperCase());
//					ProductFragment.changeOrderState();
//					((MainActivity)context).productFragment.RefreshProduct("Vegetable", null, null);
					((MainActivity)context).searchProduct("Vegetable", null, null);
					Order order = Order.jsonToOrder(json);
					state.setText(order.getStateStr());
					cancel_order.setVisibility(View.GONE);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}
	
	@Override
	public void onClick(View v) {
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.hide(this);
		transaction.show(lastFragment);
		transaction.commit();
	}
	
    /**
     * 刷新订单列表
     */
	class refreshOrderTask extends NetTask {
		@Override
		protected String doInBackground(Integer... params) {
			Intent intent = new Intent();
	        intent.setClass((MainActivity)context, LoadingActivity.class);//跳转到加载界面
	        startActivity(intent);	
	        
			return OrderAPI.order(order_id);
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result == null || result.equals("")) {
				return;
			}
			
			order_info.setVisibility(View.VISIBLE);
			items_header.setVisibility(View.VISIBLE);
			
			JSONObject json = CaiCai.StringToJSONObject(result);
			Order order = Order.jsonToOrder(json);
			order_id = order.getId();
			sn.setText(order.getSn());
			state.setText(order.getStateStr());
			date.setText(TodayYestorday.getTime(order.getCreatedAt()));
			total.setText(DoubleFormat.format(order.get_sum()));
			
			amount_title.setText(order.is_ship() ? "出货量" : "订购量");
			total_title.setText(order.is_ship() ? "出货金额" : "订购金额");
//			total_title2.setText(order.is_ship() ? "出货金额" : "订购金额");
			
			if(order.getState_() == State.PENDING || order.getState_() == State.CONFIRMED){
				cancel_order.setVisibility(View.VISIBLE);
			}
			
			
			List<Order> list = new ArrayList<Order>();
			list.add(order);
			
			OrderItemListAdapter orderItemListAdapter = new OrderItemListAdapter(context, order.getOrderItems(), OrderFragment.this);
			orderItemsLV.setAdapter(orderItemListAdapter);
		}
	}

	public static void updateOrders(String orderId, String orderState){
		if(cancel_order!=null && orderId.equals(order_id+"")){
			cancel_order.setVisibility(View.GONE);
			Order order = new Order();
			order.setState(orderState);
			state.setText(order.getStateStr());
		}
		
		
//		for (int i = 0; i < orderListLV.getChildCount(); i++) {
//			LinearLayout orderLine = (LinearLayout) orderListLV.getChildAt(i);
//			orderLine = (LinearLayout)orderLine.getChildAt(0);
//			
//			LinearLayout secondRole = (LinearLayout)orderLine.getChildAt(1);
//			TextView orderNo = (TextView)secondRole.getChildAt(1);
//			
//			if(order_no.equals(orderNo.getText().toString())){
//				LinearLayout lastRow = (LinearLayout)orderLine.getChildAt(2);
//				TextView orderState = (TextView)lastRow.getChildAt(1);
//				Order o = new Order();
//				o.setState(state);
//				orderState.setText(o.getStateStr());
//				break;
//			}
//		}
	}

}
