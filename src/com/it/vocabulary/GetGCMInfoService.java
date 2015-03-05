package com.it.vocabulary;

import java.io.IOException;

import org.apache.http.Header;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.Settings.Secure;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.it.utils.ConnectionUtils;
import com.it.utils.Constant;
import com.it.utils.LogUtils;
import com.it.utils.PreferenceUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

public class GetGCMInfoService extends Service {

	private static int failedCount = 0;
	private Context context;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		context = this;
		getGCMInfo();
		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtils.logInfo("destroyed with failed time is " + failedCount);
	}

	@SuppressLint("NewApi")
	private void getGCMInfo() {
		// check registered gcm
		// String deviceToken = GCMRegistrar
		// .getRegistrationId(context);
		String deviceToken = PreferenceUtils.getRegisteredId(context);
		// if not, register
		if (deviceToken.isEmpty()) {
			new RegisterGCM().execute();
			// if already registered, stop service
		} else {
			LogUtils.logInfo("Device Token:" + deviceToken);
			LogUtils.logInfo("Device ID:" + getDeviceID());
			sendUserData();
		}
	}

	private class RegisterGCM extends AsyncTask<Void, Void, Void> {
		GoogleCloudMessaging gcm;

		@Override
		protected void onPreExecute() {
			LogUtils.logInfo("Start registering GCM");
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Void result) {
			// register GCM
			String deviceToken = GCMRegistrar.getRegistrationId(context);

			// if fail, try again
			if (deviceToken.isEmpty()) {
				if (failedCount < 10) {
					failedCount++;
					LogUtils.logInfo("Fail:" + failedCount + " times");
					new RegisterGCM().execute();
				} else {
					stopSelf();
				}

				// if success, stop service
			} else {
				PreferenceUtils.putRegisteredGCM(context, true, deviceToken);
				LogUtils.logInfo("Complete registering GCM");
				LogUtils.logInfo("DEVICE_TOKEN = " + deviceToken);
				sendUserData();
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			gcm = GoogleCloudMessaging.getInstance(context);
			try {
				gcm.register(Constant.GCM_PROJECT_ID);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// Toast.makeText(context, gcm.toString(),
			// Toast.LENGTH_LONG).show();
			return null;
		}

	}

	private String getDeviceID() {
		return Secure.getString(getContentResolver(), Secure.ANDROID_ID);
	}

	private void sendUserData() {
		// check sent data
		boolean sent = PreferenceUtils.isSentData(this);
		if (!sent) {
			JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONObject response) {
					LogUtils.logInfo("Send data:" + response.toString());
					PreferenceUtils.setSentUserData(context, true);
					stopSelf();
				}

			};
			String gcmId = GCMRegistrar.getRegistrationId(context);
			String deviceId = getDeviceID();
			ConnectionUtils.sendUserData(gcmId, deviceId, handler);
		}
		stopSelf();
	}

}
