package com.weicai.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weicai.R;
import com.weicai.activity.MainActivity;
import com.weicai.api.CaiCai;
import com.weicai.fragment.MenuFragment;
import com.weicai.img.ImageDownLoader;
import com.weicai.img.ImageDownLoader.onImageLoaderListener;

public class ClassifyAdapter extends BaseAdapter {

	static final String tag = "AmountsAdapter";
	private ImageDownLoader mImageDownLoader;

	private LayoutInflater mInflater;
	private final String[][] classifies;
	private Context context;
	private String img_base_url;

	/* 构造函数 */
	public ClassifyAdapter(Context context, String[][] classifies) {
		this.mInflater = LayoutInflater.from(context);
		this.classifies = classifies;
		this.context = context;
		this.mImageDownLoader = new ImageDownLoader(context);
		this.img_base_url = CaiCai.server_host+"/classify/";
	}

	@Override
	public int getCount() {
		return classifies.length;
	}

	@Override
	public Object getItem(int position) {
		return classifies[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		convertView = mInflater.inflate(R.layout.classify_adapter, null);
		holder = new ViewHolder();
		holder.classify = (TextView) convertView.findViewById(R.id.classify);
		holder.icon = (ImageView) convertView.findViewById(R.id.icon);
		convertView.setTag(holder);
		if(classifies[position][0].equals("")){
			holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.all_classify));
		}else{
			showImage(holder.icon, img_base_url+classifies[position][0]+".png");
		}

		if(position == 0){
			LinearLayout ly = (LinearLayout)holder.classify.getParent();
			ly.setBackgroundColor(Color.parseColor("#73E673"));
		}
		
		holder.classify.setText(classifies[position][1]);
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.menu.showContent();
				
				for (int j = 0; j < MenuFragment.product_items.getChildCount(); j++) {
					LinearLayout line = (LinearLayout)MenuFragment.product_items.getChildAt(j);
					if(j==position){
						line.getChildAt(0).setBackgroundColor(Color.parseColor("#73E673"));
					}else{
						line.getChildAt(0).setBackgroundColor(Color.WHITE);
					}
				}
					
				new Handler().postDelayed(new Runnable() {
					public void run() {
						((MainActivity)context).searchProduct("Vegetable", classifies[position][0], null);
					}
				}, 350);
			}
		});
		
		return convertView;
	}

	/* 存放控件 */
	public final class ViewHolder {
		public TextView classify;
		public ImageView icon;
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
			mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.all_classify));
		}
	}
}