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
import android.widget.Toast;

import com.weicai.R;
import com.weicai.api.CaiCai;
import com.weicai.api.UserAPI;
import com.weicai.bean.User;
import com.weicai.dao.UserDao;
import com.weicai.util.tool.SIMCardInfo;

public class FindPasswordActivity extends BaseActivity implements OnClickListener{
	static final String tag = "FindPasswordActivity";
	
	private EditText phone_edit_text, validate_code_edit_text, new_password_edit_text;
	private TextView phone_text_view;
	private Button next_validate, resend_validate_code, next_set_password, update_password_button;
	private LinearLayout find_password_ly1, find_password_ly2, find_password_ly3;
	private UserDao userDao;
	private String validateCode;
	private ImageButton back;
	private int step = 1;


	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.find_password);
		BaseActivity.baseActivity = this;
		MyApplication.getInstance().addActivity(this);

		userDao = UserDao.getInstance();

		back = (ImageButton) findViewById(R.id.back);
		back.setOnClickListener(this);
		
		find_password_ly1 = (LinearLayout)findViewById(R.id.find_password_ly1);
		phone_edit_text = (EditText) findViewById(R.id.phone_edit_text);
		phone_edit_text.addTextChangedListener(new PhoneTextWatcher(phone_edit_text));
		phone_edit_text.setText(new SIMCardInfo(FindPasswordActivity.this).getNativePhoneNumber().replace("+86", ""));

		next_validate = (Button) findViewById(R.id.next_validate);
		next_validate.setOnClickListener(this);
		phone_edit_text.requestFocus();

		find_password_ly2 = (LinearLayout)findViewById(R.id.find_password_ly2);
		phone_text_view = (TextView) findViewById(R.id.phone_text_view);
		validate_code_edit_text = (EditText) findViewById(R.id.validate_code_edit_text);
		resend_validate_code = (Button) findViewById(R.id.resend_validate_code);
		resend_validate_code.setOnClickListener(this);
		next_set_password = (Button) findViewById(R.id.next_set_password);
		next_set_password.setOnClickListener(this);
		
		find_password_ly3 = (LinearLayout)findViewById(R.id.find_password_ly3);
		new_password_edit_text = (EditText) findViewById(R.id.new_password_edit_text);
		update_password_button = (Button) findViewById(R.id.update_password_button);
		update_password_button.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			back();
			break;
		case R.id.next_validate:
			step++;
			Log.i(tag, step+"");
			new SendValidateCodeTask().execute(0);
			break;
		case R.id.resend_validate_code:
			new SendValidateCodeTask().execute(0);
			break;
		case R.id.next_set_password:
			step++;
			String validate_code = validate_code_edit_text.getText().toString();
			if (validate_code!=null && !validate_code.equals("") && validate_code.equals(validateCode)) {
				find_password_ly2.setVisibility(View.GONE);
				find_password_ly3.setVisibility(View.VISIBLE);
			}else{
				new AlertDialog.Builder(FindPasswordActivity.this).setMessage("验证码错误").setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						validate_code_edit_text.requestFocus();
					}
				}).show();
			}
			break;
		case R.id.update_password_button:
			String new_password = new_password_edit_text.getText().toString();

			if (new_password!=null && !new_password.equals("")) {
				new UpdatePassword().execute(0);
			}else{
				new AlertDialog.Builder(FindPasswordActivity.this).setMessage("请输入密码").setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						new_password_edit_text.requestFocus();
					}
				}).show();
			}
			break;
		}
	}
	
	
		
	class SendValidateCodeTask extends NetTask {
		@Override
		protected String doInBackground(Integer... params) {
			Intent intent = new Intent();
	        intent.setClass(FindPasswordActivity.this, LoadingActivity.class);//跳转到加载界面
	        startActivity(intent);	
	        
			String phone = phone_edit_text.getText().toString().replace(" ", "");
			return UserAPI.send_validate_code(phone, validateCode);
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
				new AlertDialog.Builder(FindPasswordActivity.this).setMessage(json.getString("message")).setPositiveButton("确定", null).show();
			} catch (JSONException e) {
				new AlertDialog.Builder(FindPasswordActivity.this).setMessage("验证码发送失败").setPositiveButton("确定", null).show();
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
	        
			String phone = phone_edit_text.getText().toString().replace(" ", "");
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
				new AlertDialog.Builder(FindPasswordActivity.this).setMessage("密码修改失败").setPositiveButton("确定", null).show();
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
			find_password_ly2.setVisibility(View.GONE);
			find_password_ly1.setVisibility(View.VISIBLE);
			break;
		case 3:
			find_password_ly3.setVisibility(View.GONE);
			find_password_ly2.setVisibility(View.VISIBLE);
			break;
		}

		step--;
	}
	
	
}
