package com.it.services;

import java.io.IOException;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.it.utils.Constant;
import com.it.utils.LogUtils;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
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
			Toast.makeText(GetGCMInfoService.this, deviceToken,
					Toast.LENGTH_LONG).show();
			stopSelf();
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
				Toast.makeText(GetGCMInfoService.this, deviceToken,
						Toast.LENGTH_LONG).show();
				stopSelf();
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
			return null;
		}

	}
}
