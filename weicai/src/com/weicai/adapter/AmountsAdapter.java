package com.weicai.adapter;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.weicai.R;
import com.weicai.activity.BaseActivity.NetTask;
import com.weicai.api.CaiCai;
import com.weicai.api.OrderAPI;
import com.weicai.bean.Order;
import com.weicai.bean.Product;
import com.weicai.fragment.ProductFragment;

public class AmountsAdapter extends BaseAdapter {

	static final String tag = "AmountsAdapter";

	// 得到一个LayoutInfalter对象用来导入布局
	private LayoutInflater mInflater;
	private Product product;
	private com.weicai.adapter.ProductListAdapter.ViewHolder holder3;

	/* 构造函数 */
	public AmountsAdapter(Context context, Product product, com.weicai.adapter.ProductListAdapter.ViewHolder holder2) {
		this.product = product;
		this.mInflater = LayoutInflater.from(context);
		this.holder3 = holder2;

	}

	@Override
	public int getCount() {
		return product.getAmountArray().length;
	}

	@Override
	public Object getItem(int position) {
		return product.getAmountArray()[position];
	}

	@Override
	public long getItemId(int position) {
		Order order = (Order) getItem(position);
		return order.getId();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		convertView = mInflater.inflate(R.layout.amounts_adapter, null);
		holder = new ViewHolder();
		holder.amount = (TextView) convertView.findViewById(R.id.amount);
		convertView.setTag(holder);

		final String amount = (String) getItem(position);
		holder.amount.setText(amount + product.getUnit());
		if (product.getOrderAmount() != 0 && amount.equals(product.getOrderAmount() + "")) {
			holder.amount.setBackgroundColor(Color.parseColor("#73E673"));
		}

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				ProductListAdapter.has_dialog = false;
				ProductListAdapter.dialog.cancel();

				product.setOrderAmount(Integer.parseInt(amount));
				
				//已购买
				if (!amount.equals("0")){
					holder3.product_view.setBackgroundColor(Color.parseColor("#73E673"));
					
					holder3.bt.setText(product.getOrderAmount() + product.getUnit());
					holder3.bt.setBackgroundResource(R.drawable.bought_selector);
					holder3.bt.setTextColor(Color.parseColor("#000000"));
				}else{ // 未购买
					holder3.product_view.setBackgroundColor(Color.WHITE);
					
					holder3.bt.setBackgroundResource(R.drawable.buy_selector);
					holder3.bt.setTextColor(Color.parseColor("#ffffff"));
					holder3.bt.setText("购买");
				}
				
				new OrderTask(product.getId() + "", amount + "").execute(0);

			}
		});
		return convertView;
	}

	/* 存放控件 */
	public final class ViewHolder {
		public TextView amount;
	}

	public class OrderTask extends NetTask {
		String tag = "OrderTask";
		String product_id;
		String amount;

		public OrderTask(String product_id, String amount) {
			this.product_id = product_id;
			this.amount = amount;
		}

		@Override
		protected String doInBackground(Integer... params) {
			return OrderAPI.buy(product_id, amount);
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i(tag, "buy result: " + result);
			JSONObject json = CaiCai.StringToJSONObject(result);

			boolean status = false;
			String message = "";
			try {
				status = json.getBoolean("status");
				message = json.getString("message");
				if (status) {
					ProductFragment.last_order_state = Order.State.valueOf(json.getString("order_state").toUpperCase());
					if (!json.getString("order_id").equals("null")) {
						ProductFragment.last_order_id = json.getLong("order_id");
					}
					ProductFragment.changeOrderState();

					double order_sum = json.getDouble("order_sum");
					if (order_sum > 0) {
						ProductFragment.auto_make_order.setVisibility(View.GONE);
						ProductFragment.submit.setVisibility(View.VISIBLE);
					} else if (order_sum == 0d) {
						ProductFragment.submit.setVisibility(View.GONE);
						ProductFragment.auto_make_order.setVisibility(View.VISIBLE);
					}

				} else {
					Log.i(tag, "buy result: " + message);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			super.onPostExecute(result);
		}

	}

}