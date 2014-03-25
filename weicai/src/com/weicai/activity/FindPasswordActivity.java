package com.weicai.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weicai.R;
import com.weicai.api.CaiCai;
import com.weicai.api.UserAPI;
import com.weicai.bean.User;
import com.weicai.dao.UserDao;
import com.weicai.util.tool.SIMCardInfo;

public class FindPasswordActivity extends BaseActivity implements OnClickListener{
	static final String tag = "FindPasswordActivity";
	
	private EditText phone_edit_text, validate_code_edit_text, new_password_edit_text;
	private TextView phone_text_view, message;
	private Button next_validate, resend_validate_code, change_phone, next_set_password, update_password_button;
	private LinearLayout find_password_ly1, find_password_ly2, find_password_ly3;
	private UserDao userDao;
	private String validateCode;


	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.find_password);
		BaseActivity.baseActivity = this;
		MyApplication.getInstance().addActivity(this);

		userDao = UserDao.getInstance();

		message = (TextView) findViewById(R.id.message);
		
		find_password_ly1 = (LinearLayout)findViewById(R.id.find_password_ly1);
		phone_edit_text = (EditText) findViewById(R.id.phone_edit_text);
		phone_edit_text.setText(new SIMCardInfo(FindPasswordActivity.this).getNativePhoneNumber().replace("+86", ""));
		next_validate = (Button) findViewById(R.id.next_validate);
		next_validate.setOnClickListener(this);
		
		find_password_ly2 = (LinearLayout)findViewById(R.id.find_password_ly2);
		phone_text_view = (TextView) findViewById(R.id.phone_text_view);
		change_phone = (Button) findViewById(R.id.change_phone);
		change_phone.setOnClickListener(this);
		validate_code_edit_text = (EditText) findViewById(R.id.validate_code_edit_text);
		resend_validate_code = (Button) findViewById(R.id.resend_validate_code);
		resend_validate_code.setOnClickListener(this);
		next_set_password = (Button) findViewById(R.id.next_set_password);
		next_set_password.setOnClickListener(this);
		
		find_password_ly3 = (LinearLayout)findViewById(R.id.find_password_ly3);
		new_password_edit_text = (EditText) findViewById(R.id.new_password_edit_text);
		update_password_button = (Button) findViewById(R.id.update_password_button);
		update_password_button.setOnClickListener(this);
		
		findViewById(R.id.sign_in).setOnClickListener(this);
		findViewById(R.id.sign_up).setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.next_validate:
			new SendValidateCodeTask().execute(0);
			break;
			
		case R.id.change_phone:
			find_password_ly1.setVisibility(View.VISIBLE);
			find_password_ly2.setVisibility(View.GONE);
			break;
		case R.id.resend_validate_code:
			new SendValidateCodeTask().execute(0);
			break;
		case R.id.next_set_password:
			if (validate_code_edit_text.getText().toString().equals(validateCode)){
				find_password_ly2.setVisibility(View.GONE);
				find_password_ly3.setVisibility(View.VISIBLE);
			}else{
				message.setText("验证码错误");
			}
			break;
			
		case R.id.update_password_button:
			new UpdatePassword().execute(0);
			break;
		case R.id.sign_in:
			Intent intent = new Intent();
			intent.setClass(this, SignInActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.sign_up:
			Intent intent2 = new Intent();
			intent2.setClass(this, SignUpActivity.class);
			startActivity(intent2);
			finish();
			break;
		default:
			break;
		}
	}
	
	
		
	class SendValidateCodeTask extends NetTask {

		@Override
		protected String doInBackground(Integer... params) {
			Intent intent = new Intent();
	        intent.setClass(FindPasswordActivity.this, LoadingActivity.class);//跳转到加载界面
	        startActivity(intent);	
	        
			String phone = phone_edit_text.getText().toString();
			return UserAPI.send_validate_code(phone);
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
				if(json.getBoolean("state")){
					validateCode = json.getString("validate_code");
					phone_text_view.setText(phone_edit_text.getText().toString());
					
					find_password_ly1.setVisibility(View.GONE);
					find_password_ly2.setVisibility(View.VISIBLE);
				}
				message.setText(json.getString("message"));
			} catch (JSONException e) {
				message.setText("验证码发送失败");
				e.printStackTrace();
			}

		}

	}
	
	class UpdatePassword extends NetTask {

		@Override
		protected String doInBackground(Integer... params) {
			Intent intent = new Intent();
	        intent.setClass(FindPasswordActivity.this, LoadingActivity.class);//跳转到加载界面
	        startActivity(intent);	
	        
			String phone = phone_edit_text.getText().toString();
			String password = new_password_edit_text.getText().toString();
			return UserAPI.update_password(phone, password);
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
				if(json.getBoolean("state")){
					JSONObject userObj = json.getJSONObject("user");
					User user = User.jsonToUser(userObj);
					userDao.insert(user);
					
					Intent intent = new Intent();
					intent.setClass(FindPasswordActivity.this, MainActivity.class);
					startActivity(intent);
					finish();// 停止当前的Activity,如果不写,则按返回键会跳转回原来的Activity
				}
			} catch (JSONException e) {
				message.setText("验证码发送失败");
				e.printStackTrace();
			}

		}

	}
	
	
}
