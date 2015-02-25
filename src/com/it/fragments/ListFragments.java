package com.it.fragments;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.it.adapters.ListAdapter;
import com.it.adapters.ListIdiomAdapter;
import com.it.database.DBHelper;
import com.it.models.ICollection;
import com.it.models.Idiom;
import com.it.models.List;
import com.it.vocabulary.BaseActivity;
import com.it.vocabulary.R;

public class ListFragments extends BaseFragment implements OnItemClickListener {

	private static ListFragments INSTANCE = new ListFragments();

	/* widgets */
	private View rootView;
	private ListView lvLists;

	/* list idiom of current collection */
	private ArrayList<Idiom> listIdiom;

	/* database helper */
	DBHelper dbh;

	public static ListFragments getInstance() {
		return INSTANCE;
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_lists, null);
		dbh = ((BaseActivity) getActivity()).getDBHelper();
		setupView(rootView);
		return rootView;
	}

	@Override
	protected void setupView(View rootView) {
		lvLists = (ListView) rootView.findViewById(R.id.lists);
		lvLists.setOnItemClickListener(this);
		getData();
		ListIdiomAdapter adapter = new ListIdiomAdapter(getActivity(), 1,
				listIdiom);
		lvLists.setAdapter(adapter);
	}

	/*
	 * get data to prepare collection type detect user request a topic or list
	 * of word
	 */
	private void getData() {
		Bundle bundle = getArguments();
		int collectionType = bundle.getInt("collection_type");
		if (collectionType == ICollection.TYPE_TOPIC) {
			int topicId = bundle.getInt("collection_id");
			if (topicId == 2) {
				listIdiom = ((BaseActivity) getActivity()).getDBHelper()
						.getListIdiomOfTopic(topicId, false);
			} else {
				listIdiom = ((BaseActivity) getActivity()).getDBHelper()
						.getListIdiomOfTopic(topicId, true);
			}
		}
		if (collectionType == ICollection.TYPE_LIST) {
			int listId = bundle.getInt("collection_id");
			listIdiom = ((BaseActivity) getActivity()).getDBHelper()
					.getListIdiomOfList(listId);
		}
		if(collectionType == ICollection.TYPE_EVERYDAY_IDIOM){
			listIdiom = ((BaseActivity) getActivity()).getDBHelper().getListIdiomOfEveryDayIdiom();
		}
	}

	/* show dialog with definition of a idiom */
	private void showDialogDefinition(final Idiom idiom) {
		new AlertDialog.Builder(getActivity())
				.setMessage(idiom.getDefinition())
				.setTitle(idiom.getName())
				.setPositiveButton("Add to list",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								showDialogAddToList(idiom);
							}
						})
				.setNeutralButton("Already know",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								List list = ((BaseActivity) getActivity())
										.getDBHelper().getListById(2);
								addIdiomToList(idiom, list);
							}
						}).setCancelable(true).show();
	}

	/* show dialog add idiom to a list */
	private void showDialogAddToList(final Idiom idiom) {
		final Dialog dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_add_to_list);
		// edittext
		final EditText etListName = (EditText) dialog
				.findViewById(R.id.list_name);
		// list view
		ListView lvList = (ListView) dialog.findViewById(R.id.lists);
		final ArrayList<List> lists = ((BaseActivity) getActivity())
				.getDBHelper().getAllList();
		final ListAdapter adapter = new ListAdapter(getActivity(), 1, lists,
				true);
		lvList.setAdapter(adapter);
		lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.setSelection(position);
				adapter.notifyDataSetChanged();
			}
		});
		dialog.setCancelable(true);
		dialog.show();
		// cancel button
		dialog.findViewById(R.id.cancel).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
		// done button
		dialog.findViewById(R.id.done).setOnClickListener(
				new View.OnClickListener() {

					@SuppressLint("NewApi")
					@Override
					public void onClick(View v) {
						// if select from exist list
						if (adapter.getSelection() != -1) {
							List list = lists.get(adapter.getSelection());
							addIdiomToList(idiom, list);
							dialog.dismiss();

						} else {
							String newListName = etListName.getText()
									.toString();
							if (newListName.isEmpty()) {
								etListName.setError("Please give list name");
							} else {
								// create list
								((BaseActivity) getActivity()).getDBHelper()
										.addList(new List(newListName));
								// get created list
								List list = ((BaseActivity) getActivity())
										.getDBHelper().getNewCreatedList();
								addIdiomToList(idiom, list);
								dialog.dismiss();
							}
						}
					}
				});
		// add new

		dialog.findViewById(R.id.add_new).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						ImageView ivAddNewSelection = (ImageView) dialog
								.findViewById(R.id.add_selection);
						ivAddNewSelection
								.setImageResource(R.drawable.done_trans);
						adapter.setSelection(-1);
						adapter.notifyDataSetChanged();
						etListName.requestFocus();
					}
				});
	}

	/* add a idiom to a list */
	private void addIdiomToList(Idiom idiom, List list) {
		((BaseActivity) getActivity()).getDBHelper()
				.addIdiomToList(idiom, list);
		((BaseActivity) getActivity()).makeToast("Added " + idiom.getName()
				+ " to " + list.getName());
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Idiom idiom = listIdiom.get(position);
		showDialogDefinition(idiom);

	}

}
