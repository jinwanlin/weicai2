package com.weicai.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.weicai.R;
import com.weicai.activity.OrderFragment;
import com.weicai.bean.OrderItem;
import com.weicai.util.tool.DoubleFormat;


public class OrderItemListAdapter extends BaseAdapter {
	
	static final String tag = "OrderItemListAdapter";

	// 得到一个LayoutInfalter对象用来导入布局
	private LayoutInflater mInflater;
	
	private List<OrderItem> orderItems;
//	private OrderFragment orderFragment;
	
	/*构造函数*/
	public OrderItemListAdapter(Context context, List<OrderItem> orderItems, OrderFragment orderFragment) {
		this.orderItems = orderItems;
		this.mInflater = LayoutInflater.from(context);
//		this.orderFragment = orderFragment;
	}
	

	@Override
	public int getCount() {
		return orderItems.size();// 返回数组的长度
	}

	@Override
	public Object getItem(int position) {
		return orderItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		OrderItem item = (OrderItem)getItem(position);
		return item.getId();
	}

	/* 书中详细解释该方法 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		// 观察convertView随ListView滚动情况
		Log.v("MyListViewBase", "getView " + position + " " + convertView);
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.order_adapter, null);
			holder = new ViewHolder();
			/* 得到各个控件的对象 */
			holder.product_name = (TextView) convertView.findViewById(R.id.product_name);
			holder.price = (TextView) convertView.findViewById(R.id.price);
			holder.price_unit = (TextView) convertView.findViewById(R.id.price_unit);
			holder.amount = (TextView) convertView.findViewById(R.id.amount);
			holder.amount_unit = (TextView) convertView.findViewById(R.id.amount_unit);
//			holder.total = (TextView) convertView.findViewById(R.id.total);

			convertView.setTag(holder);// 绑定ViewHolder对象
		} else {
			holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
		}
		
		OrderItem orderItem = orderItems.get(position);
		Log.v("orderItem id", orderItem.getId()+""); // 打印Button的点击信息
		/* 设置TextView显示的内容，即我们存放在动态数组中的数据 */
		convertView.setBackgroundColor(Color.WHITE);
		
		holder.product_name.setText(orderItem.getProductName());
		holder.price.setText(DoubleFormat.format(orderItem.getPrice()));
		holder.price_unit.setText("元/" + orderItem.getProductUnit());
		if(orderItem.getOrder().is_ship()){
			holder.amount.setText(DoubleFormat.format(orderItem.getShipAmount()));
		}else{
			holder.amount.setText(orderItem.getOrderAmount()+"");
		}
		holder.amount_unit.setText(orderItem.getProductUnit());
//		holder.total.setText(DoubleFormat.format(orderItem.getSum()));
		holder.orderItem = orderItem;
		
		
		
//		convertView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				OrderItem orderItem = orderItems.get(position);
//				orderItemsFragment.showOrderItem(orderItem.getId());
//			}
//		});
		return convertView;
	}

	/* 存放控件 */
	public final class ViewHolder {
		public OrderItem orderItem;
		
		public TextView product_name;
		public TextView price;
		public TextView price_unit;
		public TextView amount;
		public TextView amount_unit;
//		public TextView total;

	}
	
}