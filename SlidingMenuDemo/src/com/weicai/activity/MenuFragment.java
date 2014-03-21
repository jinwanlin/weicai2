package com.weicai.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.weicai.R;
import com.weicai.R.layout;
import com.weicai.activity.BaseActivity.NetTask;
import com.weicai.adapter.ClassifyAdapter;
import com.weicai.api.CaiCai;
import com.weicai.api.ProductAPI;
import com.weicai.bean.ProductType;
import com.weicai.dao.SearchHistoryDao;
//import android.widget.AbsListView.LayoutParams;

public class MenuFragment extends Fragment {
	public static SearchHistoryDao searchHistoryDao;
	public static ProductType[] productTypes;
	public static ListView product_items;
//	private LinearLayout product_types;
	private Context context;
	
	public void setContext(Context context){
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View menu_layout = inflater.inflate(layout.menu2, container, false);

		
		Log.i("MenuFragment--", "MenuFragment========+++++++++++");

//		product_types = (LinearLayout) menu_layout.findViewById(R.id.product_types);
		product_items = (ListView) menu_layout.findViewById(R.id.product_items);
		new productTypesTask().execute(0);
		return menu_layout;
	}

	class productTypesTask extends NetTask {

		@Override
		protected String doInBackground(Integer... params) {
			return ProductAPI.types();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result.equals("")) {
				return;
			}
				
			
			Log.i("aa--", result);
			try {
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 0, 0, 10);
				
				JSONArray jsonArray = CaiCai.StringToJSONArray(result);
				productTypes = new ProductType[jsonArray.length()];
//				ArrayList<View> buttonList = new ArrayList<View>();
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject obj = jsonArray.getJSONObject(i);
					String type = obj.getString("type");
					String type_ = obj.getString("type_");
					JSONArray classifyArray = obj.getJSONArray("classifies");
					String[][] classifies = new String[classifyArray.length()+1][2];
					String[] all = {"", "全部"};
					classifies[0] = all;
					for (int j = 0; j < classifyArray.length(); j++) {
						JSONArray key_val = (JSONArray)classifyArray.get(j);
						String key = key_val.get(0).toString();
						String val = key_val.get(1).toString();
						String[] kv = {key, val};
						classifies[j+1] = kv;
					}

					final ProductType productType = new ProductType(type, type_, classifies);
					productTypes[i] = productType;
					
//					List<String> types = Arrays.asList("叶菜类", "根茎类", "瓜果类", "豆荚类", "葱姜蒜", "菌类", "水生菜类");
					if(i==0){
//						ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, classifies);
						ClassifyAdapter classifyAdapter = new ClassifyAdapter(context, classifies);
						product_items.setAdapter(classifyAdapter);
					}
					

//					final Button typeButton = new Button(context);
//					typeButton.setLayoutParams(params);
//					typeButton.setText(type_);
//					typeButton.setHeight(40);
//					typeButton.setWidth(40);
//					typeButton.setOnClickListener(new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							for (int j = 0; j < product_types.getChildCount(); j++) {
//								View button = product_types.getChildAt(j);
//								button.setBackgroundColor(Color.WHITE);
//							}
//							typeButton.setBackgroundColor(Color.CYAN);
//
//							ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, productType.getClassifies());
//							product_items.setAdapter(arrayAdapter);
//						}
//					});
//					
//					if(i==0){
//						typeButton.setBackgroundColor(Color.CYAN);
//					}
//					buttonList.add(typeButton);
//					product_types.addView(typeButton);

				}

//				product_types.addChildrenForAccessibility(buttonList);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}

	}
	
}

