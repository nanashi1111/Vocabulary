package com.it.adapters;

import java.util.ArrayList;

import com.it.vocabulary.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SideMenuAdapter extends ArrayAdapter<String> {

	Context context;
	ArrayList<String> list;

	public SideMenuAdapter(Context context, int resource,
			ArrayList<String> objects) {
		super(context, resource, objects);
		this.context = context;
		this.list = objects;
	}

	@SuppressLint({ "ViewHolder", "InflateParams" }) @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = null;
		LayoutInflater inf = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = inf.inflate(R.layout.row_group, null);
		TextView tvHeader = (TextView) v.findViewById(R.id.header);
		tvHeader.setText(list.get(position));
		return v;
	}

}
