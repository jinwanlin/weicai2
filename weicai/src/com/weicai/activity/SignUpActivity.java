package com.weicai.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weicai.R;
import com.weicai.api.CaiCai;
import com.weicai.api.UserAPI;
import com.weicai.bean.User;
import com.weicai.dao.UserDao;
import com.weicai.util.tool.SIMCardInfo;

public class SignUpActivity extends BaseActivity implements OnClickListener {
	static final String tag = "SignUpActivity";
	private EditText phone_edit_text, password_edit_text;
	private TextView phone_text_view, validate_code_text;
	private Button validate_and_sign_up, next_or_sign_up, resend_validate_code;
	private LinearLayout sign_up_ly, validate_ly;
	public boolean hasValidateCode = false;
	private UserDao userDao;
	private String validateCode;
	private ImageButton back;
	private int step = 1;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sign_up);
		MyApplication.getInstance().addActivity(this);
		BaseActivity.baseActivity = this;

		userDao = UserDao.getInstance();

		// 是否需要验证码
		new HasValidateCodeTask().execute(0);

		sign_up_ly = (LinearLayout) findViewById(R.id.sign_up_ly);
		validate_ly = (LinearLayout) findViewById(R.id.validate_ly);

		phone_edit_text = (EditText) findViewById(R.id.phone_edit_text);
		phone_edit_text.addTextChangedListener(new PhoneTextWatcher(phone_edit_text));
		phone_edit_text.setText(new SIMCardInfo(SignUpActivity.this).getNativePhoneNumber().replace("+86", ""));

		password_edit_text = (EditText) findViewById(R.id.password_edit_text);
		
		String userName = phone_edit_text.getText().toString();

		if(userName!=null && !userName.equals("")){
			password_edit_text.requestFocus();
		}else{
			phone_edit_text.requestFocus();
		}
		
		
		phone_text_view = (TextView) findViewById(R.id.phone_text_view);

		validate_code_text = (TextView) findViewById(R.id.validate_code_text);

		// 下一步 或 注册
		next_or_sign_up = (Button) findViewById(R.id.next_or_sign_up);
		next_or_sign_up.setOnClickListener(this);

		// 注册
		validate_and_sign_up = (Button) findViewById(R.id.validate_and_sign_up);
		validate_and_sign_up.setOnClickListener(this);

		// 从新发送验证码
		resend_validate_code = (Button) findViewById(R.id.resend_validate_code);
		resend_validate_code.setOnClickListener(this);
		// 更换手机号
		
		back = (ImageButton)findViewById(R.id.back);
		back.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			back();
			break;
		case R.id.next_or_sign_up:
			String userName = phone_edit_text.getText().toString();
			String password = password_edit_text.getText().toString();
			if(userName==null || userName.equals("")){
				new AlertDialog.Builder(SignUpActivity.this).setMessage("请输入手机号").setPositiveButton("确定", null).show();// show很关键
			}else if(password==null || password.equals("")){
				new AlertDialog.Builder(SignUpActivity.this).setMessage("请输入密码").setPositiveButton("确定", null).show();// show很关键
			}else{
				if (hasValidateCode) {
					new GetValidateCodeTask().execute(0);
				} else {

					Log.i(tag, "------+-----");
					new SignUpTask().execute(0);
				}
			}
			break;
		case R.id.validate_and_sign_up:
			String validate_code = validate_code_text.getText().toString();
			
			if (validate_code!=null && !validate_code.equals("") && validate_code.equals(validateCode)) {
				new SignUpTask().execute(0);
			} else {
				Log.i(tag, "validate_code not eques");
				new AlertDialog.Builder(SignUpActivity.this).setMessage("验证码错误").setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						validate_code_text.requestFocus();
					}
				}).show();
			}
			break;
		case R.id.resend_validate_code:
			new GetValidateCodeTask().execute(0);
			break;
		}
	}

	class SignUpTask extends NetTask {
		@Override
		protected String doInBackground(Integer... params) {
			Intent intent = new Intent();
			intent.putExtra("loading_text", "注册中...");
			intent.setClass(SignUpActivity.this, LoadingActivity.class);// 跳转到加载界面
			startActivity(intent);

			String userName = phone_edit_text.getText().toString().replace(" ", "");
			String password = password_edit_text.getText().toString();
			return UserAPI.sign_up(userName, password);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result.equals("")) {
				return;
			}

			JSONObject json = CaiCai.StringToJSONObject(result);

			boolean state = false;
			String message = "";
			JSONObject userObj = null;
			try {
				state = json.getBoolean("state");
				message = json.getString("message");
				userObj = json.getJSONObject("user");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (state) {

				User user = User.jsonToUser(userObj);
				userDao.insert(user);

				Intent intent = new Intent();
				intent.setClass(SignUpActivity.this, MainActivity.class);
				startActivity(intent);
				finish();// 停止当前的Activity,如果不写,则按返回键会跳转回原来的Activity

			} else {
				Log.i(tag, "sign_in error: " + message);
			}
		}

	}

	class HasValidateCodeTask extends NetTask {

		@Override
		protected String doInBackground(Integer... params) {
			// Intent intent = new Intent();
			// intent.setClass(mainActivity, LoadingActivity.class);//跳转到加载界面
			// startActivity(intent);

			return UserAPI.has_validate_code();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result.equals("")) {
				return;
			}

			Log.i("aa--", result);
			JSONObject json = CaiCai.StringToJSONObject(result);

			try {
				hasValidateCode = json.getBoolean("has_validate_code");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (hasValidateCode) {
				next_or_sign_up.setText("下一步");
			} else {
				next_or_sign_up.setText("注册");
			}
		}

	}

	class GetValidateCodeTask extends NetTask {

		@Override
		protected String doInBackground(Integer... params) {
			try {
				Intent intent = new Intent();
				intent.setClass(SignUpActivity.this, LoadingActivity.class);// 跳转到加载界面
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}

			String userName = phone_edit_text.getText().toString().replace(" ", "");
			return UserAPI.get_sign_up_validate_code(userName, validateCode);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result == null || result.equals("")) {
				return;
			}

			Log.i("aa--", result);
			JSONObject json = CaiCai.StringToJSONObject(result);

			try {
				if (json.getBoolean("state")) {
					validateCode = json.getString("validate_code");
					
					String phone = phone_edit_text.getText().toString();
					phone_text_view.setText(phone);
					sign_up_ly.setVisibility(View.GONE);
					validate_ly.setVisibility(View.VISIBLE);

					validate_code_text.requestFocus();
					step++;
				}
				new AlertDialog.Builder(SignUpActivity.this).setMessage(json.getString("message")).setPositiveButton("确定", null).show();// show很关键
			} catch (JSONException e) {
//				message.setText("验证码发送失败");
				new AlertDialog.Builder(SignUpActivity.this).setMessage("验证码发送失败").setPositiveButton("确定", null).show();
				e.printStackTrace();
			}
		}
	}
	
	
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			back();
			return false;
		}else{
			return super.onKeyDown(keyCode, event);
		}
	}
	
	private void back(){
		Log.i(tag, step+"--");
		switch (step) {
		case 1:
			finish();
			break;
		case 2:
			sign_up_ly.setVisibility(View.VISIBLE);
			validate_ly.setVisibility(View.GONE);
			break;
		}

		step--;
	}
	
}
