package com.it.fragments;

import com.it.vocabulary.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@SuppressLint("NewApi")
public class ResultFragment extends BaseFragment {

	private static ResultFragment INSTANCE = new ResultFragment();

	public static ResultFragment getInstance() {
		return INSTANCE;
	}

	private View rootView;

	@SuppressLint("InflateParams") @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_result, null);
		return rootView;
	}

	@Override
	protected void setupView(View rootView) {
		// TODO Auto-generated method stub
		
	}

}
