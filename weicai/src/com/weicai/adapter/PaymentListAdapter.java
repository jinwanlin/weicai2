package com.weicai.adapter;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.weicai.R;
import com.weicai.activity.MainActivity;
import com.weicai.bean.Payment;
import com.weicai.bean.Payment.Type;
import com.weicai.util.tool.DoubleFormat;


public class PaymentListAdapter extends BaseAdapter {
	static final String tag = "PaymentListAdapter";

	private Context context;
	private MainActivity mainActivity;
	
	// 得到一个LayoutInfalter对象用来导入布局
	private LayoutInflater mInflater;
	
	private List<Payment> payments;
	
	/*构造函数*/
	public PaymentListAdapter(Context context, List<Payment> payments) {
		this.context = context;
		this.payments = payments;
		this.mInflater = LayoutInflater.from(context);
		this.mainActivity = (MainActivity)context;
	}
	

	@Override
	public int getCount() {
		return payments.size();// 返回数组的长度
	}

	@Override
	public Object getItem(int position) {
		return payments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return payments.get(position).getId();
	}
	


	/* 书中详细解释该方法 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		// 观察convertView随ListView滚动情况
//		Log.v("MyListViewBase", "getView " + position + " " + convertView);
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.payments_adapter, null);
			holder = new ViewHolder();
			/* 得到各个控件的对象 */
			
			holder.type = (TextView) convertView.findViewById(R.id.type);
			holder.date = (TextView) convertView.findViewById(R.id.date);
			holder.summary = (TextView) convertView.findViewById(R.id.summary);
			holder.amount = (TextView) convertView.findViewById(R.id.amount);
			
			convertView.setTag(holder);// 绑定ViewHolder对象
		} else {
			holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
		}
		
		Payment payment = payments.get(position);
		/* 设置TextView显示的内容，即我们存放在动态数组中的数据 */

		holder.type.setText(payment.getType_());
		if (payment.getCreatedAt() != null){
			holder.date.setText(new SimpleDateFormat("M月d日").format(payment.getCreatedAt()));
		}
		holder.summary.setText(payment.getSummary());
		holder.amount.setText(DoubleFormat.format(payment.getAmount()));
		
		/* 为Button添加点击事件 */
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Payment payment = (Payment)getItem(position);
				Type type = Type.toType(payment.getType().toUpperCase());
				if(Payment.Type.PAY == type ){
					mainActivity.showOrder(payment.getOrderId(), mainActivity.paymentsFragment);
				}else{
					
				}
			}
		});
		

		return convertView;
	}

	/* 存放控件 */
	public final class ViewHolder {
		public Payment payment;
		public TextView type;
		public TextView date;
		public TextView summary;
		public TextView amount;
	}
	
//	class PaymentTask extends AsyncTask<Integer, Integer, String> {
//		String product_id;
//		String amount;
//		public PaymentTask(String product_id, String amount){
//			this.product_id = product_id;
//			this.amount = amount;
//		}
//		@Override
//		protected String doInBackground(Integer... params) {
//			return CaiCai.buy(product_id, amount);
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//			Log.i(tag, "buy result: " + result);
//			JSONObject json = CaiCai.StringToJSONObject(result);
//
//			int status = -1;
//			String message = "";
//			try {
//				status = json.getInt("status");
//				message = json.getString("message");
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//
//			if (status == 0) {
//				
//			} else {
//				Log.i(tag, "buy result: " + message);
//			}
//			super.onPostExecute(result);
//		}
//
//	}
}