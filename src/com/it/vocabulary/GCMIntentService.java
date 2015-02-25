package com.it.vocabulary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;
import com.it.database.DBHelper;
import com.it.utils.Constant;
import com.it.utils.LogUtils;
import com.it.utils.PreferenceUtils;

public class GCMIntentService extends GCMBaseIntentService {

	public GCMIntentService() {
		super(Constant.GCM_PROJECT_ID);
	}

	@Override
	protected void onError(Context arg0, String arg1) {
		Toast.makeText(arg0, arg1, Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		String message = arg1.getStringExtra("message");

		Bundle extra = arg1.getExtras();
		message = extra.getCharSequence("ids").toString();
		saveEveryDayIdiom(message);
		generateNotification(arg0, "You have a new word");
		LogUtils.logInfo("GCMMessage:" + message);
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {
		PreferenceUtils.putRegisteredGCM(this, true, arg1);
		LogUtils.logInfo("Device token = "
				+ PreferenceUtils.getRegisteredId(this));
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {

	}

	@SuppressWarnings({ "deprecation" })
	private static void generateNotification(Context context, String message) {
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);
		String title = context.getString(R.string.app_name);
		Intent notificationIntent = new Intent(context, HomeActivity.class);
		notificationIntent.putExtra("command", "CLEAR");
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notificationManager.notify(0, notification);

	}
	
	private void saveEveryDayIdiom(String notificationFromServer){
		DBHelper dbh = new DBHelper(this);
		String ids = dbh.getIdsEverydayIdiom();
		try {
			JSONArray arr = new JSONArray(notificationFromServer);
			for(int i=0;i<arr.length();i++){
				JSONObject idsObject = arr.getJSONObject(i);
				String id = idsObject.getString("id");
				if(!ids.contains(id)){
					ids = ids+","+id;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder(ids);
		sb.deleteCharAt(sb.length()-1);
		if(sb.charAt(0) == ','){
			sb.deleteCharAt(0);
		}
		ids = sb.toString();
		dbh.updateIdsEverydayIdiom(ids);
		LogUtils.logInfo("IDS:"+dbh.getIdsEverydayIdiom()); 
		dbh.close();
	}

}
