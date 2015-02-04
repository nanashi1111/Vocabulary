package com.it.fragments;

import android.annotation.SuppressLint;
import android.view.View;

@SuppressLint("NewApi")
public abstract class BaseFragment extends android.support.v4.app.Fragment {
	protected abstract void setupView(View rootView);
}
