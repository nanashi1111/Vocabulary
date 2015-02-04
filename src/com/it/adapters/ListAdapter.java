package com.it.adapters;

import java.util.ArrayList;

import com.it.models.List;
import com.it.vocabulary.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter<List> {

	Context context;
	ArrayList<List> list;
	int selection;
	boolean showSelectionItem;

	public ListAdapter(Context context, int resource, ArrayList<List> objects,
			boolean showSelectionItem) {
		super(context, resource, objects);
		this.context = context;
		this.list = objects;
		selection = -1;
		this.showSelectionItem = showSelectionItem;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = null;
		LayoutInflater inf = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = inf.inflate(R.layout.row_lists, null);
		TextView tvListName = (TextView) v.findViewById(R.id.list_name);
		tvListName.setText(list.get(position).getName());
		ImageView ivSelection = (ImageView) v.findViewById(R.id.selection);
		if (showSelectionItem) {
			ivSelection.setVisibility(View.VISIBLE);
		}
		if (position == selection) {
			ivSelection.setImageResource(R.drawable.done_trans);
		}
		return v;
	}

	public int getSelection() {
		return selection;
	}

	public void setSelection(int selection) {
		this.selection = selection;
	}

}
