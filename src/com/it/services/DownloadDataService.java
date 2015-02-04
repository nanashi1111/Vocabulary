package com.it.services;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.it.database.DBHelper;
import com.it.models.Idiom;
import com.it.models.Topic;
import com.it.utils.ConnectionUtils;
import com.it.utils.LogUtils;
import com.it.utils.PreferenceUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class DownloadDataService extends Service {

	private DBHelper dbh;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		downloadIdiomsFromServer();
		return Service.START_STICKY;
	}

	private void downloadIdiomsFromServer() {
		JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
			@Override
			public void onStart() {
				dbh = new DBHelper(DownloadDataService.this);
				LogUtils.logInfo("start download data...");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					// array json idiom
					JSONArray idiomJsonArray = response.getJSONArray("data");
					// get idioms from array
					for (int i = 0; i < idiomJsonArray.length(); i++) {
						JSONObject idiomJson = idiomJsonArray.getJSONObject(i);
						Idiom idiom = new Idiom(idiomJson);
						// save to db
						dbh.addIdiomToDatabase(idiom);
						LogUtils.logInfo("Idiom:" + idiom.getName());
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				downloadTopicsFromServer();
			}
		};
		ConnectionUtils.getListIdiom(handler);
	}

	private void downloadTopicsFromServer() {
		JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					JSONArray topicJsonArray = response.getJSONArray("data");
					for (int i = 0; i < topicJsonArray.length(); i++) {
						Topic topic = new Topic(topicJsonArray.getJSONObject(i));
						dbh.addTopicToDatabase(topic);
						LogUtils.logInfo("Topic:" + topic.getName());
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				LogUtils.logInfo("finished download data");
				// mark application as downloaded data
				PreferenceUtils.setDownloadedData(DownloadDataService.this,
						true);
				// notify
				Toast.makeText(DownloadDataService.this, "Data downloaded",
						Toast.LENGTH_LONG).show();
				// finish service
				stopSelf();

			}
		};
		ConnectionUtils.getListTopic(handler);
	}
}
