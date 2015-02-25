package com.it.adapters;

import java.util.ArrayList;

import com.it.models.List;
import com.it.models.Topic;
import com.it.vocabulary.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListListAdapter extends ArrayAdapter<List>{
	
	Context context;
	ArrayList<List> lists;
	private int selection;

	public ListListAdapter(Context context, int resource,
			ArrayList<List> objects) {
		super(context, resource, objects);
		this.context = context;
		this.lists = objects;
		selection = -1;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = null;
		LayoutInflater inf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = inf.inflate(R.layout.row_lists, null);
		TextView tvTopicName = (TextView) v.findViewById(R.id.list_name);
		tvTopicName.setText(lists.get(position).getName());
		ImageView ivSelection = (ImageView)v.findViewById(R.id.selection);
		ivSelection.setVisibility(View.VISIBLE);
		if(position == selection){
			ivSelection.setImageResource(R.drawable.choose);
		}
		return v;
	}

	public int getSelection() {
		return selection;
	}

	public void setSelection(int selection) {
		this.selection = selection;
	}

	public ArrayList<List> getLists() {
		return lists;
	}

	public void setLists(ArrayList<List> lists) {
		this.lists = lists;
	}
	
	

}
