//package com.weicai.activity;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.app.Fragment;
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
//import android.app.PendingIntent;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.graphics.Color;
//import android.net.Uri;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.telephony.SmsManager;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.baidu.android.pushservice.PushConstants;
//import com.baidu.android.pushservice.PushManager;
//import com.weicai.R;
//import com.weicai.util.tool.BaiduPushUtils;
//
///**
// * 项目的主Activity，所有的Fragment都嵌入在这里。
// * 
// * @author guolin
// */
//public class MainActivity_3 extends BaseActivity implements OnClickListener {
//	static final String tag = "MainActivity";
//
//	/**
//	 * 用于展示消息的Fragment
//	 */
//	public ProductFragment productsFragment;
//
//	/**
//	 * 用于展示联系人的Fragment
//	 */
//	private OrdersFragment ordersFragment;
//
//	/**
//	 * 用于展示动态的Fragment
//	 */
//	public PaymentsFragment paymentsFragment;
//
//	/**
//	 * 用于展示设置的Fragment
//	 */
//	private SettingFragment settingFragment;
//
//	/**
//	 * 消息界面布局
//	 */
//	private View productsLayout;
//
//	/**
//	 * 联系人界面布局
//	 */
//	private View ordersLayout;
//
//	/**
//	 * 动态界面布局
//	 */
//	private View paymentsLayout;
//
//	/**
//	 * 设置界面布局
//	 */
//	private View settingLayout;
//
//	/**
//	 * 在Tab布局上显示消息图标的控件
//	 */
//	private ImageView productsImage;
//
//	/**
//	 * 在Tab布局上显示联系人图标的控件
//	 */
//	private ImageView ordersImage;
//
//	/**
//	 * 在Tab布局上显示动态图标的控件
//	 */
//	private ImageView paymentsImage;
//
//	/**
//	 * 在Tab布局上显示设置图标的控件
//	 */
//	private ImageView settingImage;
//
//	/**
//	 * 在Tab布局上显示消息标题的控件
//	 */
//	private TextView productsText;
//
//	/**
//	 * 在Tab布局上显示联系人标题的控件
//	 */
//	private TextView ordersText;
//
//	/**
//	 * 在Tab布局上显示动态标题的控件
//	 */
//	private TextView paymentsText;
//
//	/**
//	 * 在Tab布局上显示设置标题的控件
//	 */
//	private TextView settingText;
//
//	/**
//	 * 用于对Fragment进行管理
//	 */
//	public FragmentManager fragmentManager;
//	
//	public OrderFragment orderFragment;
//	public RechargeFragment rechargeFragment;
//	
//	
//	public ChangePasswordFragment changePasswordFragment;
//	
//	private Fragment lastFragment;
//    
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.activity_main);
//		
//		BaseActivity.baseActivity = this;
////		super.mainActivity = this;
//		// 初始化布局元素
//		initViews();
//		fragmentManager = getFragmentManager();
//		// 第一次启动时选中第0个tab
//		setTabSelection(0);
//	}
//
//	/**
//	 * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
//	 */
//	private void initViews() {
//		productsLayout = findViewById(R.id.products_layout);
//		ordersLayout = findViewById(R.id.orders_layout);
//		paymentsLayout = findViewById(R.id.payments_layout);
//		settingLayout = findViewById(R.id.setting_layout);
//		productsImage = (ImageView) findViewById(R.id.products_image);
//		ordersImage = (ImageView) findViewById(R.id.orders_image);
//		paymentsImage = (ImageView) findViewById(R.id.payments_image);
//		settingImage = (ImageView) findViewById(R.id.setting_image);
//		productsText = (TextView) findViewById(R.id.products_text);
//		ordersText = (TextView) findViewById(R.id.orders_text);
//		paymentsText = (TextView) findViewById(R.id.payments_text);
//		settingText = (TextView) findViewById(R.id.setting_text);
//		productsLayout.setOnClickListener(this);
//		ordersLayout.setOnClickListener(this);
//		paymentsLayout.setOnClickListener(this);
//		settingLayout.setOnClickListener(this);
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.products_layout:
//			// 当点击了消息tab时，选中第1个tab
//			setTabSelection(0);
//			break;
//		case R.id.orders_layout:
//			// 当点击了联系人tab时，选中第2个tab
//			setTabSelection(1);
//			break;
//		case R.id.payments_layout:
//			// 当点击了动态tab时，选中第3个tab
//			setTabSelection(2);
//			break;
//		case R.id.setting_layout:
//			// 当点击了设置tab时，选中第4个tab
//			setTabSelection(3);
//			break;
//		default:
//			break;
//		}
//	}
//
//	/**
//	 * 根据传入的index参数来设置选中的tab页。
//	 * 
//	 * @param index 每个tab页对应的下标。0表示消息，1表示联系人，2表示动态，3表示设置。
//	 */
//	private void setTabSelection(int index) {
//		Log.i(tag, "--------"+index);
//		
//		// 每次选中之前先清楚掉上次的选中状态
//		clearSelection();
//		// 开启一个Fragment事务
//		FragmentTransaction transaction = fragmentManager.beginTransaction();
//		// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
//		hideFragments(transaction);
//		switch (index) {
//		case 0:
//			// 当点击了消息tab时，改变控件的图片和文字颜色
//			productsImage.setImageResource(R.drawable.homes_selected);
//			productsText.setTextColor(Color.WHITE);
//			if (productsFragment == null) {
//				// 如果ProductsFragment为空，则创建一个并添加到界面上
//				productsFragment = new ProductFragment();
//				productsFragment.setContext(this);
//				transaction.add(R.id.content, productsFragment);
//			} else {
//				// 如果ProductsFragment不为空，则直接将它显示出来
//				transaction.show(productsFragment);
//			}
//			break;
//		case 1:
//			// 当点击了联系人tab时，改变控件的图片和文字颜色
//			ordersImage.setImageResource(R.drawable.order_selected);
//			ordersText.setTextColor(Color.WHITE);
////			if (ordersFragment == null) {
//				// 如果OrdersFragment为空，则创建一个并添加到界面上
//				ordersFragment = new OrdersFragment();
//				ordersFragment.setContext(this);
////				ordersFragment.setMainActivity(this);
//				transaction.add(R.id.content, ordersFragment);
////			} else {
////				// 如果OrdersFragment不为空，则直接将它显示出来
////				transaction.show(ordersFragment);
////			}
//			break;
//		case 2:
//			// 当点击了动态tab时，改变控件的图片和文字颜色
//			paymentsImage.setImageResource(R.drawable.payment_selected);
//			paymentsText.setTextColor(Color.WHITE);
//			
////			if (paymentsFragment == null) {
//				// 如果NewsFragment为空，则创建一个并添加到界面上
//				paymentsFragment = new PaymentsFragment();
////				paymentsFragment.setMainActivity(this);
//				paymentsFragment.setContext(this);
//				transaction.add(R.id.content, paymentsFragment);
////			} else {
////				// 如果NewsFragment不为空，则直接将它显示出来
////				transaction.show(paymentsFragment);
////			}
//			break;
//		case 3:
//		default:
//			// 当点击了设置tab时，改变控件的图片和文字颜色
//			settingImage.setImageResource(R.drawable.settings_selected);
//			settingText.setTextColor(Color.WHITE);
//			if (settingFragment == null) {
//				// 如果SettingFragment为空，则创建一个并添加到界面上
//				settingFragment = new SettingFragment();
////				settingFragment.setMainActivity(this);
//				transaction.add(R.id.content, settingFragment);
//			} else {
//				// 如果SettingFragment不为空，则直接将它显示出来
//				transaction.show(settingFragment);
//			}
//			break;
//		}
//		transaction.commit();
//	}
//
//	/**
//	 * 清除掉所有的选中状态。
//	 */
//	private void clearSelection() {
//		productsImage.setImageResource(R.drawable.homes_unselected);
//		productsText.setTextColor(Color.parseColor("#82858b"));
//		ordersImage.setImageResource(R.drawable.order_unselected);
//		ordersText.setTextColor(Color.parseColor("#82858b"));
//		paymentsImage.setImageResource(R.drawable.payment_unselected);
//		paymentsText.setTextColor(Color.parseColor("#82858b"));
//		settingImage.setImageResource(R.drawable.settings_unselected);
//		settingText.setTextColor(Color.parseColor("#82858b"));
//	}
//
//	/**
//	 * 将所有的Fragment都置为隐藏状态。
//	 * 
//	 * @param transaction
//	 *            用于对Fragment执行操作的事务
//	 */
//	private void hideFragments(FragmentTransaction transaction) {
//		if (productsFragment != null) {
//			transaction.hide(productsFragment);
//		}
//		if (ordersFragment != null) {
//			transaction.hide(ordersFragment);
//		}
//		if (orderFragment != null){
//			transaction.hide(orderFragment);
//		}
//		if (paymentsFragment != null) {
//			transaction.hide(paymentsFragment);
//		}
//		if (settingFragment != null) {
//			transaction.hide(settingFragment);
//		}
//		if (changePasswordFragment != null) {
//			transaction.hide(changePasswordFragment);
//		}
//		if (rechargeFragment != null) {
//			transaction.hide(rechargeFragment);
//		}
//		
//		
//	}
//	
//	public void call(String phone){
//		Intent intent = new Intent();
//		intent.setAction("android.intent.action.CALL");
//		intent.setData(Uri.parse("tel:" + phone));
//		startActivity(intent);
//	}
//	
//	public void sendSMS(String phone){
//        SmsManager smsManager = SmsManager.getDefault();
//        PendingIntent sentIntent = PendingIntent.getBroadcast(MainActivity_3.this, 0, new Intent(), 0);
//        smsManager.sendTextMessage(phone, null, "android. 测试短信..", sentIntent, null);
//        Log.i(tag, "sendSMS");
//	}
//	
//
//	public void showOrder(long order_id, Fragment lastFragment) {
//		FragmentTransaction transaction = fragmentManager.beginTransaction();
//		hideFragments(transaction);
//
//		OrderFragment orderFragment = new OrderFragment();
//		orderFragment.setLastFragment(lastFragment);
//		orderFragment.setOrder_id(order_id);
//		orderFragment.setContext(this);
//		this.orderFragment = orderFragment;
//
//		transaction.add(R.id.content, orderFragment);
//		transaction.commit();
//	}
//	
//
//	public void showRecharge(long payment_id) {
//		FragmentTransaction transaction = fragmentManager.beginTransaction();
//		hideFragments(transaction);
//
//		RechargeFragment rechargeFragment = new RechargeFragment();
//		rechargeFragment.setPayment_id(payment_id);
//		rechargeFragment.setContext(this);
//		this.rechargeFragment = rechargeFragment;
//
//		transaction.add(R.id.content, orderFragment);
//		transaction.commit();
//	}
//	
//	
//
//	public void changePassword() {
//		FragmentTransaction transaction = fragmentManager.beginTransaction();
//		hideFragments(transaction);
//
//		ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
//		changePasswordFragment.setMainActivity(this);
//		this.changePasswordFragment = changePasswordFragment;
//		this.lastFragment = settingFragment;
//
//		transaction.add(R.id.content, changePasswordFragment);
//		transaction.commit();
//	}
//
//	public void back() {
//		if(lastFragment!=null){
//			FragmentTransaction transaction = fragmentManager.beginTransaction();
//			hideFragments(transaction);
//			
//			Log.i(tag, lastFragment.getClass().getName());
//			transaction.show(lastFragment);
//			transaction.commit();
//		}
//	}
//	
//	
//	
//
//}
