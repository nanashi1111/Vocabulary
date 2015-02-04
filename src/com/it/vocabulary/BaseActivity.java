package com.it.vocabulary;

import com.it.database.DBHelper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.Toast;

public abstract class BaseActivity extends ActionBarActivity implements
		OnClickListener {

	private ProgressDialog mProgressDialog;
	private android.support.v4.app.FragmentManager mFragmentManager;
	protected DBHelper dbh;
	protected ProgressBar loadingBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbh = new DBHelper(this);
	}

	public void showProgressDialog(String title, String message) {
		//mProgressDialog = ProgressDialog.show(this, title, message);
		if(loadingBar==null){
			loadingBar = (ProgressBar)findViewById(R.id.loading_bar);
		}
		loadingBar.setVisibility(View.VISIBLE);
	}

	public void hideProgressDialog() {
//		if (mProgressDialog != null) {
//			mProgressDialog.dismiss();
//			mProgressDialog = null;
//		}
		if(loadingBar!=null && loadingBar.getVisibility() == View.VISIBLE){
			loadingBar.setVisibility(View.INVISIBLE);
		}
	}

	public void makeToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();

	}

	@SuppressLint("NewApi")
	public void switchContent(int contentId, android.support.v4.app.Fragment fragment, Bundle bundle) {
		if (mFragmentManager == null) {
			mFragmentManager = getSupportFragmentManager();
		}
		fragment.setArguments(bundle);
		mFragmentManager.beginTransaction().replace(contentId, fragment).commit();
	}

	protected abstract void setupView();

	protected void showDiglogExit() {
		new AlertDialog.Builder(this).setTitle("Exit")
				.setMessage("Are you sure want to exit")
				.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				})
				.setNeutralButton("Yes", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
						overridePendingTransition(0, 0);
					}
				}).setCancelable(true).show();
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
	
	public DBHelper getDBHelper(){
		return dbh;
	}
}
