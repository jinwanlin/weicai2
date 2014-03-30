package com.weicai.util.net;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpUtils {


	public static String doGet(String url) {
		Log.i("get url", url);
//		Log.v("url", "get:address-->" + url);
		HttpGet request = new HttpGet(url);
		try {
			HttpResponse response = new DefaultHttpClient().execute(request);
			int code = response.getStatusLine().getStatusCode();
//			Log.v("url", "get:address-->" + code);
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity());
				Log.v("response", result);
				return result;
			} else {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	// Map<String,String> map=new HashMap<String,String>();
	// map.put("k1", "v1");
	// map.put("k2", "v2");
	// map.put("k3", "v3");
	public static String doPost(String url, Map<String, String> map) {
		Log.i("post url", url);
		// BasicNameValuePair pair = new BasicNameValuePair(String name,String
		// value);//创建一个请求头的字段，比如content-type,text/plain

		List<NameValuePair> list = new ArrayList<NameValuePair>();
		if (map != null && map.size() > 0) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				Log.i("post params", entry.getKey()+"="+entry.getValue());
				NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
				list.add(pair);
			}
		}

		String result = null;
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
			HttpPost post = new HttpPost(url);// 此处的URL为http://..../path
			post.setEntity(entity);
			HttpResponse response = new DefaultHttpClient().execute(post);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				result = EntityUtils.toString(response.getEntity());
			} else {

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

//		Log.i("post", "请求结果-->" + result);
		Log.v("response", result+"");

		return result;
	}

}
