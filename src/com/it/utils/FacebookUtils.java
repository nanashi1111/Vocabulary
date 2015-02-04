package com.it.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.util.Base64;
import android.widget.Toast;

import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Feed;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.entities.Score;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;
import com.sromku.simple.fb.listeners.OnPublishListener;

public class FacebookUtils {

	public static final int ACTION_POST_SCORE_AFTER_LOGIN = 1;

	public static void getKeyHash(Context context) {
		PackageInfo info;
		try {
			info = context.getPackageManager().getPackageInfo(
					"com.it.vocabulary", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md;
				md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				String keyhash = new String(Base64.encode(md.digest(), 0));
				// String something = new
				// String(Base64.encodeBytes(md.digest()));
				LogUtils.logInfo("hash key:" + keyhash);
			}
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// login
	public static void login(final Context context,
			final SimpleFacebook simpleFacebookInstance,
			final int actionWhenLoginDone, final String params) {
		LogUtils.logInfo("start login");
		OnLoginListener onLoginListener = new OnLoginListener() {
			@Override
			public void onLogin() {
				LogUtils.logInfo("Logged in");
				PreferenceUtils.putLoggedFacebook(context, true);
				Toast.makeText(context, "Logged facebook", Toast.LENGTH_LONG)
						.show();
				if (actionWhenLoginDone == ACTION_POST_SCORE_AFTER_LOGIN) {
					LogUtils.logInfo("start share");
					publishFeed(simpleFacebookInstance,
							Integer.parseInt(params));
				}

			}

			@Override
			public void onNotAcceptingPermissions(Permission.Type type) {
				// user didn't accept READ or WRITE permission
				LogUtils.logInfo(String.format(
						"You didn't accept %s permissions", type.name()));
			}

			@Override
			public void onThinking() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onException(Throwable throwable) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFail(String reason) {
				LogUtils.logInfo(reason);
			}

		};
		simpleFacebookInstance.login(onLoginListener);
	}

	// get FBID
	public static void getFacebookID(SimpleFacebook simpleFacebookInstance) {
		OnProfileListener profileListener = new OnProfileListener() {
			@Override
			public void onComplete(Profile response) {
				String id = response.getId();
				LogUtils.logInfo("facebookID = " + id);
			}

			@Override
			public void onFail(String reason) {
				LogUtils.logInfo(reason);
				super.onFail(reason);
			}

			@Override
			public void onException(Throwable throwable) {
				LogUtils.logInfo(throwable.toString());
				super.onException(throwable);
			}

		};
		simpleFacebookInstance.getProfile(profileListener);
	}

	public static void shareScore(SimpleFacebook simpleFacebookInstance,
			int point) {
		LogUtils.logInfo("start share");
		OnPublishListener onPublishListener = new OnPublishListener() {
			@Override
			public void onComplete(String postId) {
				LogUtils.logInfo("share score successfully");
			}

			@Override
			public void onFail(String reason) {
				LogUtils.logInfo("Error:" + reason);
			}

			@Override
			public void onThinking() {
				LogUtils.logInfo("thinking...");
			}

			@Override
			public void onException(Throwable throwable) {
				LogUtils.logInfo("Exception:" + throwable.toString());
			}

		};
		Score score = new Score.Builder().setScore(point).build();
		simpleFacebookInstance.publish(score, onPublishListener);
	}

	public static void publishFeed(SimpleFacebook simpleFacebookInstance,
			int score) {
		LogUtils.logInfo("start share");
		OnPublishListener onPublishListener = new OnPublishListener() {
			@Override
			public void onComplete(String postId) {
				LogUtils.logInfo("share score successfully");
			}

			@Override
			public void onFail(String reason) {
				LogUtils.logInfo("Error:" + reason);
			}

			@Override
			public void onThinking() {
				LogUtils.logInfo("thinking...");
			}

			@Override
			public void onException(Throwable throwable) {
				LogUtils.logInfo("Exception:" + throwable.toString());
			}

		};
		Feed feed = new Feed.Builder()
				.setMessage(
						"Minh dc tan " + score
								+ " diem nay, thich vai. hi hi ^^")
				.setName("Simple Facebook")
				.setCaption(
						"Minh dc tan " + score
								+ " diem nay, thich vai. hi hi ^^")
				.setDescription("Vocabulary")
				.setLink("https://www.facebook.com").build();
		simpleFacebookInstance.publish(feed, true, onPublishListener);
	}

}
