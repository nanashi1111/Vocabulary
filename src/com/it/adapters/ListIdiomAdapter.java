package com.it.adapters;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.it.models.Idiom;
import com.it.vocabulary.R;

public class ListIdiomAdapter extends ArrayAdapter<Idiom> {

	private Context context;
	private ArrayList<Idiom> listIdiom;

	public ListIdiomAdapter(Context context, int resource,
			ArrayList<Idiom> objects) {
		super(context, resource, objects);
		this.context = context;
		this.listIdiom = objects;
	}

	@SuppressLint({ "ViewHolder", "InflateParams" }) @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = null;
		LayoutInflater inf = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = inf.inflate(R.layout.row_idioms, null);
		TextView tvIdiomName = (TextView) v.findViewById(R.id.idiom_name);
		tvIdiomName.setText(listIdiom.get(position).getName());
		TextView tvIdiomDefinition = (TextView) v
				.findViewById(R.id.idiom_definition);
		tvIdiomDefinition.setText(listIdiom.get(position).getDefinition());
		return v;
	}

}
