package com.weicai.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.weicai.R;
import com.weicai.api.CaiCai;
import com.weicai.api.UserAPI;
import com.weicai.bean.User;
import com.weicai.dao.UserDao;
import com.weicai.update.Config;
import com.weicai.util.tool.BaiduPushUtils;
import com.weicai.util.tool.SIMCardInfo;

public class SignInActivity extends BaseActivity implements OnClickListener {
	static final String tag = "SignInActivity";
	private EditText passwordText;
	private EditText userNameText;
	private UserDao userDao;
	private boolean isTest = false;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		
		String this_phone = new SIMCardInfo(SignInActivity.this).getNativePhoneNumber().replace("+86", "");
		if( isTest && (this_phone.equals("18628405091") || this_phone.equals("15810845422"))){
			CaiCai.server_host = CaiCai.mac_pro;
		}else{
			CaiCai.server_host = CaiCai.aliyun;
		}
		
		
		
		// 以apikey的方式登录，一般放在主Activity的onCreate中
		PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, BaiduPushUtils.getMetaValue(SignInActivity.this, "api_key"));

		
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				// 获取传递的数据
				Bundle data = msg.getData();
				String url = data.getString("url");
				downFile(url);
			};
		};
		
		down = new Handler() {
			@Override
			public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
				if (pBar != null) {
					pBar.dismiss();
				}
			}
		};
		
		
		new UpdateAPK().execute(0);
		
		userDao = UserDao.getInstance();
		MyApplication.getInstance().addActivity(this);
		BaseActivity.baseActivity = this;

		// if (userDao.first() == null) {
		// User user1 = new User();
		// user1.setId(1);
		// user1.setName("望湘园");
		// user1.setPhone("18628405091");
		// userDao.insert(user1);
		// }

		User user = userDao.first();
		if (user != null) { // 已登录，跳转到首页

			Intent intent = new Intent();
			intent.setClass(SignInActivity.this, MainActivity.class);
			startActivity(intent);
			finish();// 停止当前的Activity,如果不写,则按返回键会跳转回原来的Activity
			return;
		}

		setContentView(R.layout.sign_in);

		userNameText = (EditText) findViewById(R.id.phone_number);
		userNameText.addTextChangedListener(new PhoneTextWatcher(userNameText));

		userNameText.setText(new SIMCardInfo(SignInActivity.this).getNativePhoneNumber().replace("+86", ""));
		passwordText = (EditText) findViewById(R.id.password_edit_text);

		if (userNameText != null && !userNameText.equals("")) {
			passwordText.requestFocus();
		} else {
			userNameText.requestFocus();
		}

		findViewById(R.id.sign_in).setOnClickListener(this);
		findViewById(R.id.sign_up).setOnClickListener(this);
		findViewById(R.id.find_password).setOnClickListener(this);

		if (!netStateUtils.isNetConnected()) {
			new AlertDialog.Builder(baseActivity).setIcon(android.R.drawable.ic_dialog_alert).setTitle("提示").setMessage("无法访问网络，请检查WIFI和3G是否打开！").setPositiveButton("确定", null).show();// show很关键
		}

		

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sign_up:
			Intent intent = new Intent();
			intent.setClass(SignInActivity.this, SignUpActivity.class);
			startActivity(intent);
			break;
		case R.id.sign_in:
			new SignInTask().execute(0);
			break;
		case R.id.validate_and_sign_up:
			break;
		case R.id.resend_validate_code:
			break;
		case R.id.find_password:
			Intent intent2 = new Intent();
			intent2.setClass(this, FindPasswordActivity.class);
			startActivity(intent2);
			break;
		default:
			break;
		}
	}

	/**
	 * 后面尖括号内分别是参数（例子里是线程休息时间），进度(publishProgress用到)，返回值 类型
	 * 
	 * @author jinwanlin
	 * 
	 */
	class SignInTask extends NetTask {

		@Override
		protected String doInBackground(Integer... params) {
			Intent intent = new Intent();
			intent.setClass(SignInActivity.this, LoadingActivity.class);// 跳转到加载界面
			startActivity(intent);

			String userName1 = userNameText.getText().toString().replace(" ", "");
			String password = passwordText.getText().toString();
			return UserAPI.sign_in(userName1, password);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result == null || result.equals("")) {
				return;
			}

			JSONObject json = CaiCai.StringToJSONObject(result);

			boolean state = false;
			String msg = "";
			JSONObject userObj = null;
			try {
				state = json.getBoolean("state");
				msg = json.getString("message");
				userObj = json.getJSONObject("user");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (state) {

				User user = User.jsonToUser(userObj);
				user = userDao.insert(user);

				Intent intent = new Intent();
				intent.setClass(SignInActivity.this, MainActivity.class);
				startActivity(intent);
				finish();// 停止当前的Activity,如果不写,则按返回键会跳转回原来的Activity

			} else {
				new AlertDialog.Builder(SignInActivity.this).setMessage(msg).setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						passwordText.requestFocus();
					}
				}).show();
			}
		}

	}

	@Override
	public void onStart() {
		super.onStart();

		PushManager.activityStarted(this);
	}

	@Override
	public void onResume() {
		super.onResume();

		// showChannelIds();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// 如果要统计Push引起的用户使用应用情况，请实现本方法，且加上这一个语句
		setIntent(intent);

		handleIntent(intent);
	}

	@Override
	public void onStop() {
		super.onStop();
		PushManager.activityStoped(this);
	}

	/**
	 * 处理Intent
	 * 
	 * @param intent
	 *            intent
	 */
	private void handleIntent(Intent intent) {
		String action = intent.getAction();

		// 绑定
		if (BaiduPushUtils.ACTION_RESPONSE.equals(action)) {
			Log.i(tag, "绑定");
			String method = intent.getStringExtra(BaiduPushUtils.RESPONSE_METHOD);

			if (PushConstants.METHOD_BIND.equals(method)) {
				// String toastStr = "";
				int errorCode = intent.getIntExtra(BaiduPushUtils.RESPONSE_ERRCODE, 0);
				if (errorCode == 0) {
					String content = intent.getStringExtra(BaiduPushUtils.RESPONSE_CONTENT);
					String appid = "";
					String channelid = "";
					String userid = "";

					try {
						JSONObject jsonContent = new JSONObject(content);
						JSONObject params = jsonContent.getJSONObject("response_params");
						appid = params.getString("appid");
						channelid = params.getString("channel_id");
						userid = params.getString("user_id");
					} catch (JSONException e) {
						Log.e(BaiduPushUtils.TAG, "Parse bind json infos error: " + e);
					}

					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
					Editor editor = sp.edit();
					editor.putString("appid", appid);
					editor.putString("channel_id", channelid);
					editor.putString("user_id", userid);
					editor.commit();

					showChannelIds();

					// toastStr = "Bind Success";
				} else {
					// toastStr = "Bind Fail, Error Code: " + errorCode;
					if (errorCode == 30607) {
						Log.d("Bind Fail", "update channel token-----!");
					}
				}

				// Toast.makeText(this, toastStr, Toast.LENGTH_LONG).show();
			}

			// 登录
		} else if (BaiduPushUtils.ACTION_LOGIN.equals(action)) {
			Log.i(tag, "登录");
			String accessToken = intent.getStringExtra(BaiduPushUtils.EXTRA_ACCESS_TOKEN);
			PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_ACCESS_TOKEN, accessToken);
			// isLogin = true;
			// initButton.setText("更换百度账号初始化Channel");

			// 消息
		} else if (BaiduPushUtils.ACTION_MESSAGE.equals(action)) {
			Log.i(tag, "消息");
			String message = intent.getStringExtra(BaiduPushUtils.EXTRA_MESSAGE);

			// String summary = "Receive message from server:\n\t";
			// Log.e(Utils.TAG, summary + message);
			// JSONObject contentJson = null;
			// String contentStr = message;
			// try {
			// contentJson = new JSONObject(message);
			// contentStr = contentJson.toString(4);
			// } catch (JSONException e) {
			// Log.d(Utils.TAG, "Parse message json exception.");
			// }
			// summary += contentStr;

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(message);
			builder.setCancelable(true);
			Dialog dialog = builder.create();
			dialog.setCanceledOnTouchOutside(true);
			dialog.show();
		} else {
			Log.i(BaiduPushUtils.TAG, "Activity normally start!");
		}
	}

	private void showChannelIds() {
		String appId = null;
		String channelId = null;
		String userId = null;

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		appId = sp.getString("appid", "");
		channelId = sp.getString("channel_id", "");
		userId = sp.getString("user_id", "");

		// Resources resource = this.getResources();
		// String pkgName = this.getPackageName();
		// infoText = (TextView) findViewById(resource.getIdentifier("text",
		// "id", pkgName));

		String content = "\tApp ID: " + appId + "\n\tChannel ID: " + channelId + "\n\tUser ID: " + userId + "\n\t";

		Log.i("-----", content);
		// if (infoText != null) {
		// infoText.setText(content);
		// infoText.invalidate();
		// }
	}

	private Handler mHandler, down;

	public ProgressDialog pBar;
	private Double new_version = 1.0;
	private String new_apkname = "";
	Double old_version = 1.0;

	class UpdateAPK extends NetTask {

		@Override
		protected String doInBackground(Integer... params) {

			return CaiCai.lastVersion();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result.equals("")) {
				return;
			}

			old_version = Config.getCurrentVersion(SignInActivity.this);
			try {
				JSONObject obj = new JSONObject(result);
				try {
					new_version = obj.getDouble("version");
					new_apkname = obj.getString("apkname");
				} catch (Exception e) {
				}
			} catch (Exception e) {
				Log.e(tag, e.getMessage());
			}

			if (new_version > old_version) {
				doNewVersionUpdate();
			} else {
				// notNewVersionShow();
			}

		}

	}

	private void doNewVersionUpdate() {
		StringBuffer sb = new StringBuffer();
		sb.append("当前版本:");
		sb.append(old_version);
		sb.append(", 发现新版本:");
		sb.append(new_version);
		sb.append(", 是否更新?");
		Dialog dialog = new AlertDialog.Builder(this).setTitle("软件更新").setMessage(sb.toString())
		// 设置内容
				.setPositiveButton("更新",// 设置确定按钮
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								pBar = new ProgressDialog(SignInActivity.this);
								pBar.setTitle("正在下载");
								pBar.setMessage("请稍候...");
								pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
								
								Message msg = new Message();
								// 这三句可以传递数据
								Bundle data = new Bundle();
								data.putString("url", CaiCai.server_host + "/" + new_apkname);// COUNT是标签,handleMessage中使用
								msg.setData(data);
								mHandler.sendMessage(msg);
							}

						}).setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// 点击"取消"按钮之后退出程序
						// finish();
					}
				}).create();// 创建
		// 显示对话框
		dialog.show();
	}

	void downFile(final String url) {
		pBar.show();

		
		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {

						File file = new File(Environment.getExternalStorageDirectory(), new_apkname);
						fileOutputStream = new FileOutputStream(file);

						byte[] buf = new byte[1024];
						int ch = -1;
//						 int count = 0;
						while ((ch = is.read(buf)) != -1) {
							fileOutputStream.write(buf, 0, ch);
//							 count += ch;
							if (length > 0) {
							}
						}

					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}

					down.sendMessage(new Message());
					down.post(new Runnable() {
						public void run() {
							pBar.cancel();
							update();
						}
					});
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}.start();
	}

	void update() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), new_apkname)), "application/vnd.android.package-archive");
		startActivity(intent);
	}

}
