package com.weicai.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.weicai.R;
import com.weicai.activity.MainActivity;
import com.weicai.activity.MyApplication;
import com.weicai.dao.UserDao;

public class SettingFragment extends Fragment {

	private TextView call, user_name, address;
	private TextView changePassword;
	private Button exit;
	private MainActivity mainActivity;
	private UserDao userDao;

	public void setMainActivity(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View settingLayout = inflater.inflate(R.layout.setting_layout, container, false);
		
		userDao = UserDao.getInstance();
		
//		user_name = (TextView) settingLayout.findViewById(R.id.user_name);
//		user_name.setText(userDao.first().getPhone());
//		
//		address = (TextView) settingLayout.findViewById(R.id.address);
//		address.setText(userDao.first().getAddress());

		call = (TextView) settingLayout.findViewById(R.id.call);
		call.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mainActivity.call("15810845422");
			}
		});
		 

		changePassword = (TextView) settingLayout.findViewById(R.id.changePassword);
		changePassword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mainActivity.changePassword();
			}
		});
		
		exit = (Button) settingLayout.findViewById(R.id.exit);
		exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				userDao.deleteAll();
				MyApplication.getInstance().AppExit();
			}
		});
		

		return settingLayout;
	}

}
