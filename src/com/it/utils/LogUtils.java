package com.it.utils;

import android.util.Log;

public class LogUtils {

	private static boolean CAN_LOG = true;

	public static void logInfo(String log) {
		if (CAN_LOG) {
			Log.i("Vocabulary", log);
		}
	}

	public static void logError(String log) {
		if (CAN_LOG) {
			Log.e("Vocabulary", log);
		}
	}

	public static void logDebug(String log) {
		if (CAN_LOG) {
			Log.d("Vocabulary", log);
		}
	}

	public static void logWarning(String log) {
		if (CAN_LOG) {
			Log.w("Vocabulary", log);
		}
	}

}
