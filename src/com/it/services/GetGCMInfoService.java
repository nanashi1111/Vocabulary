package com.it.services;

import java.io.IOException;
import org.apache.http.Header;
import org.json.JSONObject;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.it.utils.ConnectionUtils;
import com.it.utils.Constant;
import com.it.utils.LogUtils;
import com.it.utils.PreferenceUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.widget.Toast;

public class GetGCMInfoService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		getGCMInfo();
		return Service.START_STICKY;
	}

	@SuppressLint("NewApi")
	private void getGCMInfo() {
		//check registered gcm
		String deviceToken = GCMRegistrar
				.getRegistrationId(GetGCMInfoService.this);
		//if not, register
		if (deviceToken.isEmpty()) {
			new RegisterGCM().execute();
		//if already registered, stop service
		}else{
			LogUtils.logInfo("Device Token:"+deviceToken);
			LogUtils.logInfo("Device ID:"+getDeviceID());
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
			//register GCM
			String deviceToken = GCMRegistrar
					.getRegistrationId(GetGCMInfoService.this);
			//if fail, try again
			if (deviceToken.isEmpty()) {
				new RegisterGCM().execute();
			//if success, stop service 
			} else {
				LogUtils.logInfo("Complete registering GCM");
				sendUserData();
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			gcm = GoogleCloudMessaging.getInstance(GetGCMInfoService.this);
			try {
				gcm.register(Constant.GCM_PROJECT_ID);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//Toast.makeText(GetGCMInfoService.this, gcm.toString(), Toast.LENGTH_LONG).show();
			return null;
		}

	}
	
	private String getDeviceID(){
		return Secure.getString(getContentResolver(), Secure.ANDROID_ID);
	}
	
	private void sendUserData(){
		//check sent data
		boolean sent = PreferenceUtils.isSentData(this);
		if(!sent){
			JsonHttpResponseHandler handler = new JsonHttpResponseHandler(){

				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONObject response) {
					LogUtils.logInfo("Send data:"+response.toString());
					PreferenceUtils.setSentUserData(GetGCMInfoService.this, true);
					stopSelf();
				}
				
			};
			String gcmId = GCMRegistrar
					.getRegistrationId(GetGCMInfoService.this);
			String deviceId = getDeviceID();
			ConnectionUtils.sendUserData(gcmId, deviceId, handler);
		}
		stopSelf();
	}
	
}
