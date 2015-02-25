package com.it.application;

import android.app.Application;
import android.content.Intent;

import com.it.database.DBHelper;
import com.it.services.GetGCMInfoService;
import com.it.utils.Constant;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

public class VocabularyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		// configuration facebook
		Permission[] permissions = new Permission[] { Permission.USER_PHOTOS,
				Permission.EMAIL, Permission.PUBLISH_ACTION };
		SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
				.setAppId(Constant.FACEBOOK_APPID).setPermissions(permissions)
				.build();
		SimpleFacebook.setConfiguration(configuration);
		// get GCM info
		startService(new Intent(this, GetGCMInfoService.class));
		
		//prepare db
		DBHelper dbh = new DBHelper(this);
		dbh.createDefaultLists();
		dbh.close();

	}

}
