package com.weicai.fragment;

import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.weicai.R;
import com.weicai.activity.MainActivity;
import com.weicai.adapter.SearchKeysAdapter;
import com.weicai.dao.SearchHistoryDao;

public class SearchFragment extends Fragment {
	static final String tag = "ProductFragment";

	private ListView keywordsListView;
	private Button search_bt;
	private EditText searchText;
	public static SearchHistoryDao searchHistoryDao;
	public ImageButton search_text_clean_bt, cancel_search;

	private Context context;
	
	public void setContext(Context context){
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View settingLayout = inflater.inflate(R.layout.search_layout, container, false);
		searchHistoryDao = SearchHistoryDao.getInstance();

		searchText = (EditText) settingLayout.findViewById(R.id.search_text);
		searchText.requestFocus();
//		InputMethodManager imm = (InputMethodManager) searchText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);

		searchText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((actionId == 0 || actionId == 3) && event != null) {
					((MainActivity)context).searchProduct("Vegetable", null, searchText.getText().toString());
					return true;
				}else{
					return false;
				}
				
			}
		});
		
		search_bt = (Button)settingLayout.findViewById(R.id.search_bt);
		search_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity)context).searchProduct("Vegetable", null, searchText.getText().toString());
			}
		});
		
		search_text_clean_bt = (ImageButton) settingLayout.findViewById(R.id.search_text_clean_bt);
		search_text_clean_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchText.setText("");
			}
		});

		cancel_search = (ImageButton) settingLayout.findViewById(R.id.cancel_search);
		cancel_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity)context).searchCancel();
			}
		});

		List<String> keywordsList = searchHistoryDao.getKeywordsList();
		keywordsListView = (ListView)settingLayout.findViewById(R.id.keywordsListView);
		SearchKeysAdapter searchKeysAdapter = new SearchKeysAdapter(context, keywordsList);
		keywordsListView.setAdapter(searchKeysAdapter);
		
		return settingLayout;
	}

}
