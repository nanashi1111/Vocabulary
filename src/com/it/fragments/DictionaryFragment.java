package com.it.fragments;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.it.adapters.ListIdiomAdapter;
import com.it.database.DBHelper;
import com.it.models.Idiom;
import com.it.vocabulary.BaseActivity;
import com.it.vocabulary.R;

public class DictionaryFragment extends BaseFragment implements
		OnEditorActionListener, OnItemClickListener {

	View rootView;
	AutoCompleteTextView searchBar;
	TextView tvIdiomName, tvIdiomDefinition, tvTextSample, tvSample;
	ListView lvSugguestion;
	ArrayList<String> listWord;
	DBHelper dbh;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_dictionary, null);
		dbh = ((BaseActivity) getActivity()).getDBHelper();
		setupView(rootView);
		hideSoftKeyboard();
		return rootView;
	}

	@Override
	protected void setupView(final View rootView) {
		searchBar = (AutoCompleteTextView) rootView
				.findViewById(R.id.search_idiom);
		searchBar.setOnEditorActionListener(this);
		searchBar.setOnItemClickListener(this);
		tvIdiomName = (TextView) rootView.findViewById(R.id.idiom_name);
		tvIdiomDefinition = (TextView) rootView
				.findViewById(R.id.idiom_definition);
		tvTextSample = (TextView) rootView.findViewById(R.id.text_sample);
		tvSample = (TextView) rootView.findViewById(R.id.idiom_sample);
		lvSugguestion = (ListView) rootView.findViewById(R.id.lists);
		lvSugguestion.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ArrayList<Idiom> listIdiom = ((ListIdiomAdapter) lvSugguestion
						.getAdapter()).getListIdiom();
				Idiom idiom = listIdiom.get(position);
				lvSugguestion.setVisibility(View.INVISIBLE);
				rootView.findViewById(R.id.layout_search_result).setVisibility(
						View.VISIBLE);
				showView(idiom);
				
			}
		});
	}

	@SuppressLint("NewApi")
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		listWord = dbh.getListWord();
		searchBar.setAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, listWord));
		Bundle bundle = getArguments();
		if (bundle != null) {
			String keyword = bundle.getString("keyword", "");
			if (!keyword.isEmpty()) {
				//searchBar.setText(keyword);
				//searchIdiom(keyword);
				searchListSugguestion(keyword);
				hideSoftKeyboard();
				
			}
		}
		focusOnSearchBar();
	}

	@SuppressLint("NewApi")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String keyword = (String) parent.getItemAtPosition(position);
		if (!keyword.isEmpty()) {
			((BaseActivity) getActivity()).makeToast(keyword);
			searchIdiom(keyword);
		} else {
			searchBar.setError("Please give a word to search");
		}
	}

	private void searchListSugguestion(String keyword) {
		ArrayList<Idiom> listIdiom = dbh.searchListIdiom(keyword);
		ListIdiomAdapter adapter = new ListIdiomAdapter(getActivity(), 1,
				listIdiom);
		lvSugguestion.setAdapter(adapter);
		lvSugguestion.setVisibility(View.VISIBLE);
		rootView.findViewById(R.id.layout_search_result).setVisibility(
				View.INVISIBLE);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		boolean handled = false;
		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
			String keyword = searchBar.getText().toString();
			if (!keyword.isEmpty()) {
				// searchIdiom(keyword);
				searchListSugguestion(keyword);
				hideSoftKeyboard();
				searchBar.dismissDropDown();
			} else {
				searchBar.setError("Please give a word to search");
			}
			handled = true;
		}
		return handled;
	}

	@SuppressLint("NewApi")
	private void searchIdiom(String keyword) {
		// String keyword = searchBar.getText().toString();
		Idiom idiom = dbh.searchIdiomByKeyword(keyword);

		if (idiom.getName().isEmpty()) {
			((BaseActivity) getActivity()).makeToast(keyword + " not found");
			tvIdiomName.setText("");
			tvIdiomDefinition.setText("");
			tvSample.setText("");
			tvTextSample.setText("");
		} else {
			showView(idiom);
		}
		hideSoftKeyboard();
	}

	private void showView(Idiom idiom) {
		tvIdiomName.setText(idiom.getName());
		tvIdiomDefinition.setText(idiom.getDefinition());
		tvSample.setText(idiom.getSample());
		tvTextSample.setText(getActivity().getResources().getString(
				R.string.sample));
	}

	/* focus on search bar */
	private void focusOnSearchBar() {
		searchBar.requestFocus();
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);
	}

	private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
	}

}
