package com.weicai.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.weicai.R;
import com.weicai.activity.OrdersFragment;
import com.weicai.bean.Order;
import com.weicai.util.tool.DoubleFormat;
import com.weicai.util.tool.TodayYestorday;


public class OrderListAdapter extends BaseAdapter {
	
	static final String tag = "OrderListAdapter";

	// 得到一个LayoutInfalter对象用来导入布局
	private LayoutInflater mInflater;
	
	private List<Order> orders;
	private OrdersFragment ordersFragment;
	
	/*构造函数*/
	public OrderListAdapter(Context context, List<Order> orders, OrdersFragment ordersFragment) {
		this.orders = orders;
		this.mInflater = LayoutInflater.from(context);
		this.ordersFragment = ordersFragment;
	}
	
	@Override
	public int getCount() {
		return orders.size();// 返回数组的长度
	}

	@Override
	public Object getItem(int position) {
		return orders.get(position);
	}

	@Override
	public long getItemId(int position) {
		Order order = (Order)getItem(position);
		return order.getId();
	}

	/* 书中详细解释该方法 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		// 观察convertView随ListView滚动情况
		Log.v("MyListViewBase", "getView " + position + " " + convertView);
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.orders_adapter, null);
			holder = new ViewHolder();
			/* 得到各个控件的对象 */
			holder.date = (TextView) convertView.findViewById(R.id.date);
			holder.order_sn = (TextView) convertView.findViewById(R.id.order_sn);
			holder.amount = (TextView) convertView.findViewById(R.id.amount);
			holder.state = (TextView) convertView.findViewById(R.id.state);
			convertView.setTag(holder);// 绑定ViewHolder对象
		} else {
			holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
		}
		
		Order order = orders.get(position);
		Log.v("order id", order.getId()+""); // 打印Button的点击信息
		/* 设置TextView显示的内容，即我们存放在动态数组中的数据 */
		if (order.getCreatedAt()!=null && !"".equals(order.getCreatedAt())) {
			holder.date.setText(TodayYestorday.getTime(order.getCreatedAt()));
		}
		holder.order_sn.setText(order.getSn());

		
		holder.amount.setText(DoubleFormat.format(order.get_sum())+"元");
		holder.state.setText(order.getStateStr());
		holder.order = order;
		
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ordersFragment.getMainActivity().showOrder(getItemId(position), ordersFragment);
			}
		});
		return convertView;
	}

	/* 存放控件 */
	public final class ViewHolder {
		public Order order;
		public TextView date;
		public TextView order_sn;
		public TextView amount;
		public TextView state;
	}
	
}