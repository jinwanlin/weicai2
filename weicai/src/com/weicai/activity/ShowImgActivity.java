package com.weicai.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.weicai.R;
import com.weicai.img.ImageDownLoader;
import com.weicai.img.ImageDownLoader.onImageLoaderListener;

public class ShowImgActivity extends Activity {

	private static Context context;
	private String smallImg, largeImg;
	private ImageDownLoader mImageDownLoader;
	private ImageView large_image;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		this.mImageDownLoader = new ImageDownLoader(context);

		Bundle extras = getIntent().getExtras();
		smallImg = extras.getString("smallImg");
		largeImg = extras.getString("largeImg");

		setContentView(R.layout.dialog_photo_entry);
		large_image = (ImageView) findViewById(R.id.large_image);
		
		showImage(large_image, smallImg, largeImg);

		RelativeLayout large_image_layout = (RelativeLayout) findViewById(R.id.large_image_layout);
		large_image_layout.setOnClickListener(new OnClickListener() {
			public void onClick(View paramView) {
				ShowImgActivity.this.finish();
			}
		});

	}

	private void showImage(final ImageView mImageView, String smallImg, String largeImg) {
		Bitmap large_bitmap = getBitmap(mImageView, largeImg);
		if (large_bitmap != null) {
			mImageView.setImageBitmap(large_bitmap);
		} else {
			Bitmap small_bitmap = getBitmap(mImageView, smallImg);
			if (small_bitmap != null) {
				mImageView.setImageBitmap(small_bitmap);
			} else {
				mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_empty));
			}
		}
	}
	
	public Bitmap getBitmap(final ImageView mImageView, String imgUrl){
		Bitmap bitmap = null;
		
		bitmap = mImageDownLoader.downloadImage(imgUrl, new onImageLoaderListener() {
			@Override
			public void onImageLoader(Bitmap bitmap, String url) {
				if (mImageView != null && bitmap != null) {
					mImageView.setImageBitmap(bitmap);
				}

			}
		});
		return bitmap;
	}
}