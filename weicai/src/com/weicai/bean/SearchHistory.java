package com.weicai.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.weicai.daoCore.Id;
import com.weicai.daoCore.Table;
import com.weicai.daoCore.Transient;


/**
 * @author jiuwuerliu@sina.com
 * 
 *         数据库实体对象
 */
@Table(name = "t_search_history")
public class SearchHistory {

	
	@Id
	private Long id;
	private String keywords;


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getKeywords() {
		return keywords;
	}


	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}


	public static SearchHistory jsonToSearchHistoryr(JSONObject obj) {
		SearchHistory user = null;
		if (obj != null) {
			user = new SearchHistory();
			try {
				user.setId(obj.getLong("id"));
				user.setKeywords(obj.getString("keywords"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return user;
	}
	
	public static List<SearchHistory> jsonToList(JSONArray array) {

		List<SearchHistory> list = new ArrayList<SearchHistory>();
		if (array != null && array.length() > 0) {
			for (int i = 0; i < array.length(); i++) {
				try {
					JSONObject p = array.getJSONObject(i);
					SearchHistory searchHistory = jsonToSearchHistoryr(p);
					list.add(searchHistory);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

}