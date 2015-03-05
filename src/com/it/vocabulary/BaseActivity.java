package com.it.vocabulary;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.internal.di;
import com.it.database.DBHelper;
import com.it.utils.DataUtils;
import com.it.utils.PreferenceUtils;

public abstract class BaseActivity extends ActionBarActivity implements
		OnClickListener {

	private android.support.v4.app.FragmentManager mFragmentManager;
	protected DBHelper dbh;
	protected ProgressBar loadingBar;
	protected TextView tvPercent;
	protected InterstitialAd interstitial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getDeviceSize();
		dbh = new DBHelper(this);
		// Create the interstitial.
		interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId(getString(R.string.banner_full));

		// Create ad request.
		AdRequest adRequest = new AdRequest.Builder().build();

		// Begin loading your interstitial.
		interstitial.loadAd(adRequest);
	}

	public void showProgressDialog(String title, String message) {
		// mProgressDialog = ProgressDialog.show(this, title, message);
		if (loadingBar == null) {
			loadingBar = (ProgressBar) findViewById(R.id.loading_bar);
		}
		if(tvPercent == null){
			tvPercent = (TextView)findViewById(R.id.percent);
		}
		loadingBar.setVisibility(View.VISIBLE);
		tvPercent.setVisibility(View.VISIBLE);
		
	}
	
	public void setProgressText(String percent){
		tvPercent.setText(percent);
	}

	public void hideProgressDialog() {
		// if (mProgressDialog != null) {
		// mProgressDialog.dismiss();
		// mProgressDialog = null;
		// }
		if (loadingBar != null && loadingBar.getVisibility() == View.VISIBLE) {
			loadingBar.setVisibility(View.INVISIBLE);
		}
		if(tvPercent != null){
			tvPercent.setVisibility(View.INVISIBLE);
		}
	}

	public void makeToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();

	}

	@SuppressLint("NewApi")
	public void switchContent(int contentId,
			android.support.v4.app.Fragment fragment, Bundle bundle) {
		if (mFragmentManager == null) {
			mFragmentManager = getSupportFragmentManager();
		}
		fragment.setArguments(bundle);
		mFragmentManager.beginTransaction().replace(contentId, fragment)
				.commit();
	}
	
	@SuppressLint("NewApi")
	public void switchContent(int contentId,
			android.support.v4.app.Fragment fragment) {
		if (mFragmentManager == null) {
			mFragmentManager = getSupportFragmentManager();
		}
		mFragmentManager.beginTransaction().replace(contentId, fragment)
				.commit();
	}

	protected abstract void setupView();

	
	protected void showDialogExit() {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_exit);
		Button btYes = (Button)dialog.findViewById(R.id.yes);
		btYes.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		Button btNo = (Button)dialog.findViewById(R.id.no);
		btNo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setCancelable(true);
		dialog.show();
		
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(0, 0);
	}

	@Override
	protected void onDestroy() {
		dbh.close();
		super.onDestroy();
	}

	public DBHelper getDBHelper() {
		return dbh;
	}

	@Override
	protected void onStop() {
		int runtime = PreferenceUtils.getRunTimeCount(this);
		PreferenceUtils.setRuntimeCount(this, ++runtime);
		if(runtime % 2 == 1){
			showAds();
		}
		super.onStop();
	}

	protected void showAds() {
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
	}
	
	private void getDeviceSize(){
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		DataUtils.SCREEN_HEIGHT = metrics.heightPixels;
		DataUtils.SCREEN_WIDTH = metrics.widthPixels;
	}
}
