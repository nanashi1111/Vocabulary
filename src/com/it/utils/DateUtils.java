package com.it.utils;

import android.annotation.SuppressLint;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	private static int SECONDS_PER_DAY = 86400;

	@SuppressLint("SimpleDateFormat")
	public static String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		return dateFormat.format(date);
	}

	@SuppressWarnings("deprecation")
	public static int getDateDistance(String date1, String date2) {
		Date d1 = new Date(date1);
		Date d2 = new Date(date2);
		return (int) (d2.getTime() - d1.getTime()) / SECONDS_PER_DAY;
	}

}
