package com.weicai.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.weicai.R;
import com.weicai.activity.BaseActivity.NetTask;
import com.weicai.activity.LoadingActivity;
import com.weicai.activity.MainActivity;
import com.weicai.api.CaiCai;
import com.weicai.api.UserAPI;
import com.weicai.dao.UserDao;

public class ChangePasswordFragment extends Fragment implements OnClickListener {

	private TextView message;
	private EditText old_password_edit_text, new_password_edit_text;
	private UserDao userDao;

	
	private Context context;
	
	public void setContext(Context context){
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View settingLayout = inflater.inflate(R.layout.change_password, container, false);

		settingLayout.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity)context).back();
			}
		});

		userDao = UserDao.getInstance();
		message = (TextView) settingLayout.findViewById(R.id.message);
		old_password_edit_text = (EditText) settingLayout.findViewById(R.id.oldPassword);
		new_password_edit_text = (EditText) settingLayout.findViewById(R.id.newPassword);
		settingLayout.findViewById(R.id.submit).setOnClickListener(this);

		return settingLayout;
	}

	@Override
	public void onClick(View arg0) {
		new ChangePassword().execute(0);
	}
	
	class ChangePassword extends NetTask {

		@Override
		protected String doInBackground(Integer... params) {
			Intent intent = new Intent();
	        intent.setClass((MainActivity)context, LoadingActivity.class);//跳转到加载界面
	        startActivity(intent);	
	        
			long user_id = userDao.first().getId();
			String old_password = old_password_edit_text.getText().toString();
			String new_password = new_password_edit_text.getText().toString();
			return UserAPI.change_password(user_id, old_password, new_password);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result == null || result.equals("")) {
				return;
			}
			
			
			JSONObject json = CaiCai.StringToJSONObject(result);
			try {
				message.setText(json.getString("message"));
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}
	

}


