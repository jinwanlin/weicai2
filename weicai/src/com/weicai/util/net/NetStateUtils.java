package com.weicai.util.net;

import java.util.List;

import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetStateUtils {

//  ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);  
	private ConnectivityManager cm;
	
//  LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
	private LocationManager lm;
	
	public NetStateUtils(ConnectivityManager cm, LocationManager lm){
		this.cm = cm;
		this.lm = lm;
	}

	/**
	 * 检测网络是否连接
	 * 
	 * @return
	 */
	public boolean isNetConnected() {
		if (cm != null) {
			NetworkInfo[] infos = cm.getAllNetworkInfo();
			if (infos != null) {
				for (NetworkInfo ni : infos) {
					if (ni.isConnected()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 检测wifi是否连接
	 * 
	 * @return
	 */
	public boolean isWifiConnected() {
		if (cm != null) {
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检测3G是否连接
	 * 
	 * @return
	 */
	public boolean is3gConnected() {
		if (cm != null) {
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检测GPS是否打开
	 * 
	 * @return
	 */
	public boolean isGpsEnabled() {
		List<String> accessibleProviders = lm.getProviders(true);
		for (String name : accessibleProviders) {
			if ("gps".equals(name)) {
				return true;
			}
		}
		return false;
	}

}
