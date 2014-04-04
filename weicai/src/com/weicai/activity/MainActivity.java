package com.weicai.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.weicai.R;
import com.weicai.api.UserAPI;
import com.weicai.fragment.ChangePasswordFragment;
import com.weicai.fragment.OrderFragment;
import com.weicai.fragment.OrdersFragment;
import com.weicai.fragment.PaymentsFragment;
import com.weicai.fragment.ProductFragment;
import com.weicai.fragment.RechargeFragment;
import com.weicai.fragment.SearchFragment;
import com.weicai.fragment.SettingFragment;
import com.weicai.task.SyncSearchHistoryTask;

public class MainActivity extends BaseActivity implements OnClickListener {
	static final String tag = "MainActivity";

	public static SlidingMenu menu = null;

	public ProductFragment productFragment;
	public OrdersFragment ordersFragment;
	public PaymentsFragment paymentsFragment;
	public SettingFragment settingFragment;

	public LinearLayout bottom_menu;

	private View productsLayout;
	private View ordersLayout;
	private View paymentsLayout;
	private View settingLayout;

	private ImageView productsImage;
	private ImageView ordersImage;
	private ImageView paymentsImage;
	private ImageView settingImage;

	private TextView productsText;
	private TextView ordersText;
	private TextView paymentsText;
	private TextView settingText;

	public FragmentManager fragmentManager;
	public OrderFragment orderFragment;
	public RechargeFragment rechargeFragment;
	public ChangePasswordFragment changePasswordFragment;
	private Fragment lastFragment;
	private SearchFragment searchFragment;
	
	public static boolean need_reload_products = false; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		MyApplication.getInstance().addActivity(this);
		BaseActivity.baseActivity = this;
		super.mainActivity = this;

		initViews();
		setTabSelection(0);
		new UpdateBaiduUserId().execute(0);
		
		new SyncSearchHistoryTask().execute(0);

	}

	/**
	 * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
	 */
	private void initViews() {
		fragmentManager = getFragmentManager();

		productsLayout = findViewById(R.id.products_layout);
		ordersLayout = findViewById(R.id.orders_layout);
		paymentsLayout = findViewById(R.id.payments_layout);
		settingLayout = findViewById(R.id.setting_layout);

		bottom_menu = (LinearLayout) findViewById(R.id.bottom_menu);
		productsImage = (ImageView) findViewById(R.id.products_image);
		ordersImage = (ImageView) findViewById(R.id.orders_image);
		paymentsImage = (ImageView) findViewById(R.id.payments_image);
		settingImage = (ImageView) findViewById(R.id.setting_image);
		productsText = (TextView) findViewById(R.id.products_text);
		ordersText = (TextView) findViewById(R.id.orders_text);
		paymentsText = (TextView) findViewById(R.id.payments_text);
		settingText = (TextView) findViewById(R.id.setting_text);

		productsLayout.setOnClickListener(this);
		ordersLayout.setOnClickListener(this);
		paymentsLayout.setOnClickListener(this);
		settingLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.products_layout:
			// 当点击了消息tab时，选中第1个tab
			setTabSelection(0);
			break;
		case R.id.orders_layout:
			// 当点击了联系人tab时，选中第2个tab
			setTabSelection(1);
			break;
		case R.id.payments_layout:
			// 当点击了动态tab时，选中第3个tab
			setTabSelection(2);
			break;
		case R.id.setting_layout:
			// 当点击了设置tab时，选中第4个tab
			setTabSelection(3);
			break;
		default:
			break;
		}
	}

	/**
	 * 根据传入的index参数来设置选中的tab页。
	 * 
	 * @param index
	 *            每个tab页对应的下标。0表示消息，1表示联系人，2表示动态，3表示设置。
	 */
	private void setTabSelection(int index) {
		if (menu != null) {
			if (index == 0) {
				menu.setSlidingEnabled(true);
			} else {
				menu.setSlidingEnabled(false);
			}
		}

		Log.i(tag, "点击：" + index);

		// 每次选中之前先清楚掉上次的选中状态
		clearSelection();
		// 开启一个Fragment事务
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
		hideFragments(transaction);
		switch (index) {
		case 0:
			// 当点击了消息tab时，改变控件的图片和文字颜色
			productsImage.setImageResource(R.drawable.homes_selected);
			productsText.setTextColor(Color.WHITE);
			if (productFragment == null || need_reload_products) {
				// 如果ProductsFragment为空，则创建一个并添加到界面上
				productFragment = new ProductFragment();
				productFragment.setContext(this);
				transaction.add(R.id.content, productFragment);

				// 加载菜单
				productFragment.showMenu();
			} else {
				// 如果ProductsFragment不为空，则直接将它显示出来
				transaction.show(productFragment);
			}
			break;
		case 1:
			// 当点击了联系人tab时，改变控件的图片和文字颜色
			ordersImage.setImageResource(R.drawable.order_selected);
			ordersText.setTextColor(Color.WHITE);
			// 如果OrdersFragment为空，则创建一个并添加到界面上
			ordersFragment = new OrdersFragment();
			ordersFragment.setContext(this);
			ordersFragment.setMainActivity(this);
			transaction.add(R.id.content, ordersFragment);
			break;
		case 2:
			// 当点击了动态tab时，改变控件的图片和文字颜色
			paymentsImage.setImageResource(R.drawable.payment_selected);
			paymentsText.setTextColor(Color.WHITE);

			// 如果NewsFragment为空，则创建一个并添加到界面上
			paymentsFragment = new PaymentsFragment();
			paymentsFragment.setMainActivity(this);
			paymentsFragment.setContext(this);
			transaction.add(R.id.content, paymentsFragment);
			break;
		case 3:
		default:
			// 当点击了设置tab时，改变控件的图片和文字颜色
			settingImage.setImageResource(R.drawable.settings_selected);
			settingText.setTextColor(Color.WHITE);
			// if (settingFragment == null) {
			// 如果SettingFragment为空，则创建一个并添加到界面上
			settingFragment = new SettingFragment();
			settingFragment.setMainActivity(this);
			transaction.add(R.id.content, settingFragment);
			// } else {
			// // 如果SettingFragment不为空，则直接将它显示出来
			// transaction.show(settingFragment);
			// }
			break;
		}
		transaction.commit();
	}

	/**
	 * 清除掉所有的选中状态。
	 */
	private void clearSelection() {
		productsImage.setImageResource(R.drawable.homes_unselected);
		productsText.setTextColor(Color.parseColor("#82858b"));
		ordersImage.setImageResource(R.drawable.order_unselected);
		ordersText.setTextColor(Color.parseColor("#82858b"));
		paymentsImage.setImageResource(R.drawable.payment_unselected);
		paymentsText.setTextColor(Color.parseColor("#82858b"));
		settingImage.setImageResource(R.drawable.settings_unselected);
		settingText.setTextColor(Color.parseColor("#82858b"));
	}

	/**
	 * 将所有的Fragment都置为隐藏状态。
	 * 
	 * @param transaction
	 *            用于对Fragment执行操作的事务
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (productFragment != null) {
			transaction.hide(productFragment);
		}
		if (ordersFragment != null) {
			transaction.hide(ordersFragment);
		}
		if (orderFragment != null) {
			transaction.hide(orderFragment);
		}
		if (paymentsFragment != null) {
			transaction.hide(paymentsFragment);
		}
		if (settingFragment != null) {
			transaction.hide(settingFragment);
		}
		if (changePasswordFragment != null) {
			transaction.hide(changePasswordFragment);
		}
		if (rechargeFragment != null) {
			transaction.hide(rechargeFragment);
		}
		if (searchFragment != null) {
			transaction.hide(searchFragment);
		}

	}

	public void showOrder(long order_id, Fragment lastFragment) {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		hideFragments(transaction);

		OrderFragment orderFragment = new OrderFragment();
		orderFragment.setLastFragment(lastFragment);
		orderFragment.setOrder_id(order_id);
		orderFragment.setContext(this);
		this.orderFragment = orderFragment;

		transaction.add(R.id.content, orderFragment);
		transaction.commit();
	}

	public void showRecharge(long payment_id) {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		hideFragments(transaction);

		RechargeFragment rechargeFragment = new RechargeFragment();
		rechargeFragment.setPayment_id(payment_id);
		rechargeFragment.setContext(this);
		this.rechargeFragment = rechargeFragment;

		transaction.add(R.id.content, orderFragment);
		transaction.commit();
	}

	public void changePassword() {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		hideFragments(transaction);

		ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
		changePasswordFragment.setContext(this);
		this.changePasswordFragment = changePasswordFragment;
		this.lastFragment = settingFragment;

		transaction.add(R.id.content, changePasswordFragment);
		transaction.commit();
	}

	public void showSearch() {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		hideFragments(transaction);

		searchFragment = new SearchFragment();
		searchFragment.setContext(this);

		transaction.add(R.id.content, searchFragment);
		transaction.commit();

		bottom_menu.setVisibility(View.GONE);
		menu.setSlidingEnabled(false);
	}

	public void searchCancel() {
		bottom_menu.setVisibility(View.VISIBLE);

		FragmentTransaction transaction = fragmentManager.beginTransaction();
		hideFragments(transaction);

		transaction.show(productFragment);
		transaction.commit();

		menu.setSlidingEnabled(true);
	}

	public void searchProduct(String type, String classify, String searchKey) {
		bottom_menu.setVisibility(View.VISIBLE);

		FragmentTransaction transaction = fragmentManager.beginTransaction();
		hideFragments(transaction);

		productFragment = new ProductFragment();
		productFragment.type = type;
		productFragment.classify = classify;
		productFragment.searchKey = searchKey;
		productFragment.setContext(this);

		transaction.add(R.id.content, productFragment);
		transaction.commit();

		menu.setSlidingEnabled(true);
	}

	/** 后退一步 */
	public void back() {
		if (lastFragment != null) {
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			hideFragments(transaction);

			Log.i(tag, lastFragment.getClass().getName());
			transaction.show(lastFragment);
			transaction.commit();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			if (menu != null && menu.isMenuShowing()) {
				menu.showContent();
				return false;
			} else {
				return super.onKeyDown(keyCode, event);
			}
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	class UpdateBaiduUserId extends NetTask {

		@Override
		protected String doInBackground(Integer... params) {
			return UserAPI.update_baidu_user_id();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}

	}

}
