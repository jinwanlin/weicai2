package com.weicai.adapter;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.weicai.R;
import com.weicai.activity.MainActivity;
import com.weicai.activity.ShowImgActivity;
import com.weicai.bean.Order;
import com.weicai.bean.Product;
import com.weicai.fragment.ProductFragment;
import com.weicai.img.ImageDownLoader;
import com.weicai.img.ImageDownLoader.onImageLoaderListener;
import com.weicai.util.tool.DoubleFormat;

public class ProductListAdapter extends BaseAdapter {
	static final String tag = "ProductListAdapter";
//	public static boolean has_dialog = false;
	private Context context;
	public static AlertDialog dialog;

	/**
	 * Image 下载器
	 */
	private ImageDownLoader mImageDownLoader;
	// 得到一个LayoutInfalter对象用来导入布局
	private LayoutInflater mInflater;

	public static List<Product> products;

	/* 构造函数 */
	public ProductListAdapter(Context context, List<Product> products) {
		this.context = context;
		this.products = products;
		this.mInflater = LayoutInflater.from(context);
		this.mImageDownLoader = new ImageDownLoader(context);
	}

	@Override
	public int getCount() {
		return products.size();// 返回数组的长度
	}

	@Override
	public Object getItem(int position) {
		return products.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public String getImgUrl(int position) {
		return products.get(position).getAvatar();
	}
	
	public String getTitle(int position) {
		return products.get(position).getName();
	}
	

	public String getLargeImgUrl(int position) {
		return products.get(position).getAvatar().replace("thumb", "large");
	}
	

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		 ViewHolder holder;
		// 观察convertView随ListView滚动情况
		 Log.v(tag, "getView " + position + " " + convertView + " " + parent);

		// if (convertView == null) {
		
	if (convertView == null) {
		holder = new ViewHolder();
		convertView = mInflater.inflate(R.layout.product_adapter, null);

		/* 得到各个控件的对象 */
		holder.product_view = convertView;
		holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
		holder.title = (TextView) convertView.findViewById(R.id.ItemTitle);
		holder.price = (TextView) convertView.findViewById(R.id.ItemPrice);
		holder.price_unit = (TextView) convertView.findViewById(R.id.price_unit);
		holder.bt = (Button) convertView.findViewById(R.id.ItemButton);
		convertView.setTag(holder);// 绑定ViewHolder对象
	} else {
		holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
	}
		// } else {
		// holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
		// Log.i(tag, "------old");
		// }

		Product product = (Product) getItem(position);

//		Log.i(tag, "url------" + product.getAvatar());
		// Log.v("product id", product.getId()+""); // 打印Button的点击信息
		/* 设置TextView显示的内容，即我们存放在动态数组中的数据 */
		showImage(holder.avatar, product.getAvatar());
		holder.title.setText(product.getName());
		holder.price.setText(DoubleFormat.format(product.getPrice()));
		holder.price_unit.setText("元/" + product.getUnit());
		
		
		Log.v("getOrderAmount ", product.getOrderAmount()+"");
//		已购买
		if (product.getOrderAmount() != 0){
			holder.product_view.setBackgroundColor(Color.parseColor("#CCFFCC"));
			
			holder.bt.setText(product.getOrderAmount() + product.getUnit());
			holder.bt.setBackgroundResource(R.drawable.bought_selector);
			if(ProductFragment.last_order_state != Order.State.PENDING){
				holder.bt.setBackgroundColor(Color.parseColor("#CCFFCC"));
			}
			
			holder.bt.setTextColor(Color.parseColor("#000000"));
		}else{ // 未购买
			holder.product_view.setBackgroundColor(Color.WHITE);
			
			if(ProductFragment.last_order_state==Order.State.CONFIRMED){
				holder.bt.setText("");
				holder.bt.setBackgroundColor(Color.parseColor("#ffffff"));
			}else{
				holder.bt.setBackgroundResource(R.drawable.buy_selector);
				holder.bt.setTextColor(Color.parseColor("#ffffff"));
				holder.bt.setText("购买");
			}
		}
		
		
		holder.product = product;

		holder.avatar.setOnClickListener(new OnClickListener() { // 点击放大
			public void onClick(View paramView) {
				
				Intent intent = new Intent();
				intent.putExtra("smallImg", getImgUrl(position));
				intent.putExtra("largeImg", getLargeImgUrl(position));
		        intent.setClass((MainActivity)context, ShowImgActivity.class);//跳转到加载界面
		        context.startActivity(intent);	
		        
			}
		});
		
		
		
		/* 为Button添加点击事件 */
		holder.bt.setOnClickListener(btOnclickLisener(position, holder));
		
		if(ProductFragment.last_order_state==null || ProductFragment.last_order_state == Order.State.PENDING){
			
		}else{
			holder.bt.setClickable(false);
		}

		return convertView;
	}
	
	public OnClickListener btOnclickLisener(final int position, final ViewHolder holder){
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
//				if (has_dialog) {
//					return;
//				}

				
				Product product = (Product) getItem(position);
//				has_dialog = true;

//				可能选择的滚动到中央
				int may_select_amount = product.getOrderAmount();
				if(may_select_amount==0){
					may_select_amount = product.getAverage_amount();
				}
				int selection = java.util.Arrays.asList(product.getAmountArray()).indexOf(may_select_amount+"");
				if(selection>3){
					selection = selection - 3;
				}else{
					selection = 0;
				}
				
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(product.getName());
				AmountsAdapter amountsAdapter = new AmountsAdapter(context, product, holder);
				builder.setSingleChoiceItems(amountsAdapter, selection, null);
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
//						has_dialog = false;
					}
				});

				dialog = builder.create();
				dialog.show();
			}
		};
	}

	/* 存放控件 */
	public final class ViewHolder {
		public Product product;
		public View product_view;
		public ImageView avatar;
		public TextView title;
		public TextView price;
		public TextView price_unit;
		public Button bt;

	}


	private void showImage(final ImageView mImageView, String mImageUrl) {
		Bitmap bitmap = null;
		bitmap = mImageDownLoader.downloadImage(mImageUrl, new onImageLoaderListener() {
			@Override
			public void onImageLoader(Bitmap bitmap, String url) {
				if (mImageView != null && bitmap != null) {
					mImageView.setImageBitmap(bitmap);
				}

			}
		});

		if (bitmap != null) {
			mImageView.setImageBitmap(bitmap);
		} else {
			mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_empty));
		}
	}
	
	public static void resetOrderAmount(){
		for (int i = 0; i < products.size(); i++) {
			Product product = products.get(i);
			product.setOrderAmount(0);
		}
	}

}