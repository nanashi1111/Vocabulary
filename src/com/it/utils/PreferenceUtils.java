package com.it.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceUtils {

	// put register gcm info
	public static void putRegisteredGCM(Context context, boolean isRegisterd,
			String deviceToken) {
		SharedPreferences pref = context.getSharedPreferences(
				Constant.PREFERENCE_GCM, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putBoolean("is_registered", isRegisterd);
		editor.putString("device_token", deviceToken);
		editor.commit();
	}

	// get register gcm info
	public static boolean getRegisteredGCM(Context context) {
		SharedPreferences pref = context.getSharedPreferences(
				Constant.PREFERENCE_GCM, Context.MODE_PRIVATE);
		boolean isRegistered = pref.getBoolean("is_registered", false);
		return isRegistered;
	}

	// get register gcm id
	public static String getRegisteredId(Context context) {
		SharedPreferences pref = context.getSharedPreferences(
				Constant.PREFERENCE_GCM, Context.MODE_PRIVATE);
		String deviceToken = pref.getString("device_token", "");
		return deviceToken;
	}

	// get logged facebook info
	public static boolean isLoggedInFacebook(Context context) {
		SharedPreferences pref = context.getSharedPreferences(
				Constant.PREFERENCE_FACEBOOK, Context.MODE_PRIVATE);
		boolean isLogged = pref.getBoolean("is_logged", false);
		return isLogged;
	}

	// put register gcm info
	public static void putLoggedFacebook(Context context, boolean isLogged) {
		SharedPreferences pref = context.getSharedPreferences(
				Constant.PREFERENCE_FACEBOOK, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putBoolean("is_logged", isLogged);
		editor.commit();
	}

	// is first run
	public static boolean isFirstRun(Context context) {
		SharedPreferences pref = context.getSharedPreferences(
				Constant.PREFERENCE_RUNTIME, Context.MODE_PRIVATE);
		boolean isFirst = pref.getBoolean("first_run", true);
		return isFirst;
	}

	// put first run
	public static void putFirstRun(Context context, boolean isFirstRun) {
		SharedPreferences pref = context.getSharedPreferences(
				Constant.PREFERENCE_RUNTIME, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putBoolean("first_run", isFirstRun);
		editor.commit();
	}

	// check download database
	public static boolean isDownloadedData(Context context) {
		SharedPreferences pref = context.getSharedPreferences(
				Constant.PREFERENCE_RUNTIME, Context.MODE_PRIVATE);
		boolean downloaded = pref.getBoolean("downloaded_data", false);
		return downloaded;
	}

	// set downloaded data
	public static void setDownloadedData(Context context, boolean downloaded) {
		SharedPreferences pref = context.getSharedPreferences(
				Constant.PREFERENCE_RUNTIME, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putBoolean("downloaded_data", downloaded);
		editor.commit();
	}
}
