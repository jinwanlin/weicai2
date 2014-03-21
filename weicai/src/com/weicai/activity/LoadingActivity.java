package com.weicai.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.weicai.R;

public class LoadingActivity extends Activity implements OnClickListener {

	private static Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getApplicationContext();


		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		setContentView(R.layout.loading);
		BaseActivity.loadingActivity = this;

		TextView close_loading = (TextView) findViewById(R.id.close_loading);
//		TextView loading_text = (TextView) findViewById(R.id.loading_text);
		
//		Bundle extras = getIntent().getExtras();
//		String loading_text_str = extras.getString("loading_text");
//		if(loading_text_str!=null && !loading_text.equals("")){
//			loading_text.setText(loading_text_str);
//		}

		close_loading.setOnClickListener(this);

		// 这里Handler的postDelayed方法，等待10000毫秒在执行run方法。
		// 在Activity中我们经常需要使用Handler方法更新UI或者执行一些耗时事件，
		// 并且Handler中post方法既可以执行耗时事件也可以做一些UI更新的事情，比较好用，推荐使用
		new Handler().postDelayed(new Runnable() {
			public void run() {
				// 等待10000毫秒后销毁此页面，并提示登陆成功
				// if (BaseActivity.loadingActivity != null){
				// BaseActivity.loadingActivity.finish();
				// BaseActivity.loadingActivity = null;
				// Toast.makeText(getApplicationContext(), "加载失败",
				// Toast.LENGTH_SHORT).show();
				// }
			}
		}, 5000);

	}
	
	
	

	public void onClick(View arg0) {
		if (BaseActivity.loadingActivity != null) {
			BaseActivity.loadingActivity.finish();
			BaseActivity.loadingActivity = null;
		}
	}

	/**
	 * 请求超时
	 */
	public static void timeout() {
		if (BaseActivity.loadingActivity != null && context != null) {
			BaseActivity.loadingActivity.finish();
			BaseActivity.loadingActivity = null;
			Toast.makeText(context, "请求超时", Toast.LENGTH_SHORT).show();
		}
	}
}