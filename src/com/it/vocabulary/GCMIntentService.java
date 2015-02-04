package com.it.vocabulary;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.google.android.gcm.GCMBaseIntentService;
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
		LogUtils.logInfo("GCMMessage:"+message);
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {
		PreferenceUtils.putRegisteredGCM(this, true, arg1);
		LogUtils.logInfo("Device token = "+PreferenceUtils.getRegisteredId(this));
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		
	}

}
