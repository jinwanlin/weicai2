package com.weicai.update;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class Config {
	private static final String TAG = "Config";

//	public static final String UPDATE_SERVER = "http://10.20.147.117/jtapp12/";
//	public static final String UPDATE_APKNAME = "jtapp-12-updateapksamples.apk";
//	public static final String UPDATE_VERJSON = "ver.json";
//	public static final String UPDATE_SAVENAME = "updateapksamples.apk";

//	public static int getVerCode(Context context) {
//		int verCode = -1;
//		try {
//			verCode = context.getPackageManager().getPackageInfo("com.weicai", 0).versionCode;
//		} catch (NameNotFoundException e) {
//			Log.e(TAG, e.getMessage());
//		}
//		return verCode;
//	}

	public static Double getCurrentVersion(Context context) {
		String current_version = "";
		try {
			PackageInfo p = context.getPackageManager().getPackageInfo("com.weicai", 0);
			current_version = p.versionName;
			return Double.parseDouble(current_version);
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}
		return 0.0;

	}

//	public static String getAppName(Context context) {
//		String verName = context.getResources().getText(R.string.app_name).toString();
//		return verName;
//	}
}
