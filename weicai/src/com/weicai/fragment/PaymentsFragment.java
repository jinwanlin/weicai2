package com.weicai.fragment;

import java.util.List;

import org.json.JSONArray;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.weicai.adapter.PaymentListAdapter;
import com.weicai.api.CaiCai;
import com.weicai.api.PaymentAPI;
import com.weicai.bean.Payment;
import com.weicai.util.tool.DoubleFormat;

public class PaymentsFragment extends Fragment {

	private TextView overage;
	private MainActivity mainActivity;
	private LinearLayout basic_info_layout;
	private RelativeLayout no_values_layout;
	private ListView paymentListLV;
	private Context context;
	
	public void setContext(Context context){
		this.context = context;
	}

	public MainActivity getMainActivity() {
		return mainActivity;
	}

	public void setMainActivity(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.payments_layout, container, false);

		overage = (TextView) layout.findViewById(R.id.overage);
		basic_info_layout = (LinearLayout) layout.findViewById(R.id.basic_info_layout);
		no_values_layout = (RelativeLayout) layout.findViewById(R.id.no_values_layout);
		paymentListLV = (ListView) layout.findViewById(R.id.paymentListLV);



		new RefreshPaymentsTask().execute(0);
		return layout;
	}

	/**
	 * 刷新订单列表
	 */
	class RefreshPaymentsTask extends NetTask {
		
		@Override
		protected String doInBackground(Integer... params) {
			Intent intent = new Intent();
	        intent.setClass(mainActivity, LoadingActivity.class);//跳转到加载界面
	        startActivity(intent);	
	        
			return PaymentAPI.payments();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result == null || result.equals("")) {
				return;
			}
			
			JSONArray json = CaiCai.StringToJSONArray(result);
			List<Payment> payments = Payment.jsonToList(json);

			// 显示余额
			if (payments.size() > 0) {
				Payment payment = payments.get(0);
				overage.setText(DoubleFormat.format(payment.getOverage()));
				basic_info_layout.setVisibility(View.VISIBLE);
			}else{
				no_values_layout.setVisibility(View.VISIBLE);
			}
			
			PaymentListAdapter paymentListAdapter = new PaymentListAdapter(context, payments);
			paymentListLV.setAdapter(paymentListAdapter);
			
		}
	}

}