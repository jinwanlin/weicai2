package com.weicai.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

import com.weicai.util.net.NetStateUtils;

public abstract class BaseActivity extends FragmentActivity {
	static final String tag = "BaseActivity";
	public static NetStateUtils netStateUtils;

	public static BaseActivity baseActivity;
	public static LoadingActivity loadingActivity;
	public MainActivity mainActivity;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		netStateUtils = new NetStateUtils(cm, lm);

	}

	/**
	 * 后面尖括号内分别是参数（例子里是线程休息时间），进度(publishProgress用到)，返回值 类型
	 * 
	 * @author jinwanlin
	 * 
	 */
	public static abstract class NetTask extends AsyncTask<Integer, Integer, String> {
		
		@Override
		protected void onPostExecute(String result) {
			if (BaseActivity.loadingActivity != null){
				BaseActivity.loadingActivity.finish();
				BaseActivity.loadingActivity = null;
			}
			
			Log.i(tag, "----------super clear");
			
			if (result == null || result.equals("")) {
				if (!netStateUtils.isNetConnected()) {
					new AlertDialog.Builder(baseActivity).setIcon(android.R.drawable.ic_dialog_alert).setTitle("提示").setMessage("无法访问网络，请检查WIFI和3G是否打开！").setPositiveButton("确定", null).show();// show很关键
					return;
				} else {
					new AlertDialog.Builder(baseActivity).setIcon(android.R.drawable.ic_dialog_alert).setTitle("提示").setMessage("服务器异常！").setPositiveButton("确定", null).show();// show很关键
					return;
				}
			}
		}

	}
	

	public void call(String phone) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.CALL");
		intent.setData(Uri.parse("tel:" + phone));
		startActivity(intent);
	}

	public void sendSMS(String phone) {
		SmsManager smsManager = SmsManager.getDefault();
		PendingIntent sentIntent = PendingIntent.getBroadcast(BaseActivity.this, 0, new Intent(), 0);
		smsManager.sendTextMessage(phone, null, "android. 测试短信..", sentIntent, null);
		Log.i(tag, "sendSMS");
	}
	
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 结束Activity&从栈中移除该Activity
		MyApplication.getInstance().finishActivity(this);
	}
	
	private long exitTime = 0;
	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			if ((System.currentTimeMillis() - exitTime) > 2000) {
//				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
//				exitTime = System.currentTimeMillis();
//				return false;
//			} else {
//				MainActivity.productsFragment = null;
//				MyApplication.getInstance().AppExit();
//				return super.onKeyDown(keyCode, event);
//			}
//		}
//		return super.onKeyDown(keyCode, event);
//	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				MyApplication.getInstance().AppExit();
			}
			return false;
		}else{
			return super.onKeyDown(keyCode, event);
		}
	}

}
