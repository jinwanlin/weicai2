package com.weicai.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.weicai.R;
import com.weicai.activity.MainActivity;

public class SearchKeysAdapter extends BaseAdapter {

	static final String tag = "AmountsAdapter";

	private LayoutInflater mInflater;
	private final List<String> keywordsList;
	private Context context;

	/* 构造函数 */
	public SearchKeysAdapter(Context context, List<String> keywordsList) {
		this.mInflater = LayoutInflater.from(context);
		this.keywordsList = keywordsList;
		this.context = context;
	}

	@Override
	public int getCount() {
		return keywordsList.size();
	}

	@Override
	public Object getItem(int position) {
		return keywordsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.searchkeys_adapter, null);
			holder = new ViewHolder();
			holder.keyword = (TextView) convertView.findViewById(R.id.keyword);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
		}
		
		
		holder.keyword.setText(getItem(position).toString());
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity)context).searchProduct(null, null, getItem(position).toString());
			}
		});
		
		return convertView;
	}

	/* 存放控件 */
	public final class ViewHolder {
		public TextView keyword;
	}

}