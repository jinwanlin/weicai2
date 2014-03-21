package com.weicai.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weicai.R;
import com.weicai.activity.MainActivity;
import com.weicai.activity.MenuFragment;
import com.weicai.bean.Order;

public class ClassifyAdapter extends BaseAdapter {

	static final String tag = "AmountsAdapter";

	private LayoutInflater mInflater;
	private final String[][] classifies;
	private Context context;

	/* 构造函数 */
	public ClassifyAdapter(Context context, String[][] classifies) {
		this.mInflater = LayoutInflater.from(context);
		this.classifies = classifies;
		this.context = context;
	}

	@Override
	public int getCount() {
		return classifies.length;
	}

	@Override
	public Object getItem(int position) {
		return classifies[position];
	}

	@Override
	public long getItemId(int position) {
		Order order = (Order) getItem(position);
		return order.getId();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		convertView = mInflater.inflate(R.layout.classify_adapter, null);
		holder = new ViewHolder();
		holder.classify = (TextView) convertView.findViewById(R.id.classify);
		holder.icon = (ImageButton) convertView.findViewById(R.id.icon);
		convertView.setTag(holder);
		holder.icon.setBackgroundResource(R.drawable.settings_selected);
		if(position == 0){
			LinearLayout ly = (LinearLayout)holder.classify.getParent();
			ly.setBackgroundColor(Color.CYAN);
		}
		
		holder.classify.setText(classifies[position][1]);
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.menu.showContent();
				
				for (int j = 0; j < MenuFragment.product_items.getChildCount(); j++) {
					LinearLayout line = (LinearLayout)MenuFragment.product_items.getChildAt(j);
					if(j==position){
						line.getChildAt(0).setBackgroundColor(Color.CYAN);
					}else{
						line.getChildAt(0).setBackgroundColor(Color.WHITE);
					}
				}
					
				new Handler().postDelayed(new Runnable() {
					public void run() {
						((MainActivity)context).productsFragment.RefreshProduct("Vegetable", classifies[position][0], null);
					}
				}, 350);
			}
		});
		
		return convertView;
	}

	/* 存放控件 */
	public final class ViewHolder {
		public TextView classify;
		public ImageButton icon;
	}

//	public class OrderTask extends NetTask {
//		String tag = "OrderTask";
//		String product_id;
//		String amount;
//
//		public OrderTask(String product_id, String amount) {
//			this.product_id = product_id;
//			this.amount = amount;
//		}
//
//		@Override
//		protected String doInBackground(Integer... params) {
//			return OrderAPI.buy(product_id, amount);
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//			Log.i(tag, "buy result: " + result);
//			JSONObject json = CaiCai.StringToJSONObject(result);
//
//			boolean status = false;
//			String message = "";
//			try {
//				status = json.getBoolean("status");
//				message = json.getString("message");
//				if (status) {
//					ProductFragment.last_order_state = Order.State.valueOf(json.getString("order_state").toUpperCase());
//					if (!json.getString("order_id").equals("null")) {
//						ProductFragment.last_order_id = json.getLong("order_id");
//					}
//					ProductFragment.changeOrderState();
//
//					double order_sum = json.getDouble("order_sum");
//					if (order_sum > 0) {
//						ProductFragment.auto_make_order.setVisibility(View.GONE);
//						ProductFragment.submit.setVisibility(View.VISIBLE);
//					} else if (order_sum == 0d) {
//						ProductFragment.submit.setVisibility(View.GONE);
//						ProductFragment.auto_make_order.setVisibility(View.VISIBLE);
//					}
//
//				} else {
//					Log.i(tag, "buy result: " + message);
//				}
//
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//
//			super.onPostExecute(result);
//		}
//
//	}

}