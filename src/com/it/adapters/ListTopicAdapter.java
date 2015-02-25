package com.it.adapters;

import java.util.ArrayList;
import com.it.models.Topic;
import com.it.vocabulary.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListTopicAdapter extends ArrayAdapter<Topic> {

	Context context;
	ArrayList<Topic> listTopic;
	private int selection;

	public ListTopicAdapter(Context context, int resource,
			ArrayList<Topic> objects) {
		super(context, resource, objects);
		this.context = context;
		this.listTopic = objects;
		selection = -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = null;
		LayoutInflater inf = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = inf.inflate(R.layout.row_topic, null);
		TextView tvTopicName = (TextView) v.findViewById(R.id.topic_name);
		tvTopicName.setText(listTopic.get(position).getName());
		ImageView ivSelection = (ImageView)v.findViewById(R.id.selection);
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

	public ArrayList<Topic> getListTopic() {
		return listTopic;
	}

	public void setListTopic(ArrayList<Topic> listTopic) {
		this.listTopic = listTopic;
	}
	
	

}
