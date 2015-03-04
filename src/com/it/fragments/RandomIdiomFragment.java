package com.it.fragments;

import java.util.ArrayList;
import java.util.Random;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.easyandroidanimations.library.FlipHorizontalToAnimation;
import com.google.android.gms.ads.AdView;
import com.it.adapters.IdiomPagerAdapter;
import com.it.adapters.ListAdapter;
import com.it.database.DBHelper;
import com.it.models.CollectionGenerator;
import com.it.models.ICollection;
import com.it.models.Idiom;
import com.it.models.List;
import com.it.models.Topic;
import com.it.utils.ConnectionUtils;
import com.it.utils.DataUtils;
import com.it.utils.FacebookUtils;
import com.it.utils.LogUtils;
import com.it.utils.PreferenceUtils;
import com.it.vocabulary.BaseActivity;
import com.it.vocabulary.HomeActivity;
import com.it.vocabulary.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sromku.simple.fb.SimpleFacebook;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.internal.di;

@SuppressLint("NewApi")
public class RandomIdiomFragment extends BaseFragment implements
		OnEditorActionListener, OnClickListener, OnPageChangeListener,
		OnItemClickListener {

	/* number of topics in database have idioms */
	private static final int MAX_AVAILABLE_TOPIC_ID = 12;

	/* boolean value focus onn search bar */
	private boolean focusSearchBar = false;

	/* widgets */
	private AutoCompleteTextView searchBar;
	private ViewPager idiomPager;
	private TextView tvAddNote;
	private ImageView btLike;
	private ImageView btShare;

	/* use when current collection is topic */
	private ICollection topic;

	/* use when current collection is list */
	private ICollection list;

	/* use when current collection is everyday idiom */
	private ICollection everyDayIdiom;

	/* current idiom */
	private Idiom currentIdiom;

	/*
	 * list all word in database use with search bar to give user sugguestion
	 */
	private ArrayList<String> listWord;

	/* database helper */
	private DBHelper dbh;

	/* list idiom of current collection */
	private ArrayList<Idiom> listIdiom = new ArrayList<Idiom>();

	/* viewpager adapter */
	private IdiomPagerAdapter pagerAdapter;

	/* rootview */
	private View rootView;

	public RandomIdiomFragment(boolean focusSearchBar) {
		this.focusSearchBar = focusSearchBar;
	}

	public RandomIdiomFragment() {

	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_random, null);
		dbh = ((BaseActivity) getActivity()).getDBHelper();
		setupView(rootView);
		return rootView;
	}

	@Override
	protected void setupView(View rootView) {
		// setup view pager
		idiomPager = (ViewPager) rootView.findViewById(R.id.idiom_pager);
		// idiomPager.setOnPageChangeListener(this);
		pagerAdapter = new IdiomPagerAdapter(getFragmentManager(), listIdiom);
		idiomPager.setAdapter(pagerAdapter);
		idiomPager.setOnPageChangeListener(this);
		// set up search bar
		searchBar = (AutoCompleteTextView) rootView
				.findViewById(R.id.search_idiom_random);
		listWord = dbh.getListWord();
		searchBar.setAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, listWord));
		searchBar.setOnEditorActionListener(this);
		searchBar.setOnItemClickListener(this);
		// set up add note view
		tvAddNote = (TextView) rootView.findViewById(R.id.add_note);
		tvAddNote.setOnClickListener(this);

		// add to list button listener
		rootView.findViewById(R.id.add_to_list).setOnClickListener(this);

		// add to already know listener
		rootView.findViewById(R.id.add_to_already_know)
				.setOnClickListener(this);

		// like btn listener
		btLike = (ImageView) rootView.findViewById(R.id.like);
		btLike.setOnClickListener(this);

		// share btn listener
		btShare = (ImageView) rootView.findViewById(R.id.share);
		btShare.setOnClickListener(this);

		AdView adView = (AdView) rootView.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// check if not from list fragment
		int fromListFragment = getArguments().getInt("from_list_fragment");
		if (fromListFragment == ICollection.TYPE_TOPIC) {
			// check downloaded data, if not get random topic from server
			boolean isDownloaded = PreferenceUtils
					.isDownloadedData(getActivity());
			if (!isDownloaded) {
				loadFirstTimeRandomTopic();
			} else {
				boolean randomLoad = getArguments().getBoolean("random_load");
				if (randomLoad) {
					topic = loadRandomTopicFromDatabase();
					showView(topic);
				} else {
					int topicId = getArguments().getInt("topic_id");
					int pos = getArguments().getInt("position", -1);
					// topic = loadTopicById(topicId);
					topic = CollectionGenerator.getCollection(
							ICollection.TYPE_TOPIC, topicId, dbh);
					showView(topic);
					if(pos!=-1){
						idiomPager.setCurrentItem(pos, true);
					}
				}
			}
			// if from list fragment
		} else if (fromListFragment == ICollection.TYPE_LIST) {
			int listId = getArguments().getInt("list_id");
			// list = dbh.getListById(
			// listId);
			// ArrayList<Idiom> listIdiom = ((BaseActivity) getActivity())
			// .getDBHelper().getListIdiomOfList(list);
			// list.setListIdiom(listIdiom);
			int pos = getArguments().getInt("position", -1);
			list = CollectionGenerator.getCollection(ICollection.TYPE_LIST,
					listId, dbh);
			showView(list);
			if(pos!=-1){
				idiomPager.setCurrentItem(pos, true);
			}
		} else if (fromListFragment == ICollection.TYPE_EVERYDAY_IDIOM) {
			everyDayIdiom = CollectionGenerator.getCollection(
					ICollection.TYPE_EVERYDAY_IDIOM, -1, dbh);
			int pos = getArguments().getInt("position", -1);
			showView(everyDayIdiom);
			if(pos!=-1){
				idiomPager.setCurrentItem(pos, true);
			}
		}
		if (listIdiom.size() > idiomPager.getCurrentItem()) {
			currentIdiom = listIdiom.get(idiomPager.getCurrentItem());
		}
		checkCurrentIdiomInLiked();
		checkCurrentIdiomHasNote();

		if (focusSearchBar) {
			focusOnSearchBar();
		}
		switch (getCurrentCollectionType()) {
		case ICollection.TYPE_TOPIC:
			((HomeActivity) getActivity()).setupActionBar(((Topic) topic)
					.getName());
			break;
		case ICollection.TYPE_LIST:
			((HomeActivity) getActivity()).setupActionBar(((List) list)
					.getName());
			break;
		case ICollection.TYPE_EVERYDAY_IDIOM:
			((HomeActivity) getActivity()).setupActionBar("Everyday Idioms");
			break;
		}
	}

	/* load random topic when database is empty */
	private void loadFirstTimeRandomTopic() {
		// select a random topic
		Random random = new Random();
		final int randomTopicId = random.nextInt(MAX_AVAILABLE_TOPIC_ID) + 1;

		JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
			@Override
			public void onStart() {
				((BaseActivity) getActivity()).showProgressDialog("",
						"Loading new word...");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					// array json idiom
					JSONArray idiomJsonArray = response.getJSONArray("data");
					// get idioms from array
					for (int i = 0; i < idiomJsonArray.length(); i++) {
						JSONObject idiomJson = idiomJsonArray.getJSONObject(i);
						Idiom idiom = new Idiom(idiomJson);
						// save to db
						dbh.addIdiomToDatabase(idiom);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// first time loaded
				PreferenceUtils.putFirstRun(getActivity(), false);
				getTopicInfo(randomTopicId);
			}
		};
		ConnectionUtils.getListIdiomFromTopic(randomTopicId, handler);
	}

	/* download topics from server */
	private void getTopicInfo(final int topicId) {
		JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					// get topic data
					JSONArray arrayJsonTopic = response.getJSONArray("data");
					JSONObject topicJson = arrayJsonTopic
							.getJSONObject(topicId - 1);
					Topic topic = new Topic(topicJson);
					// save to db
					dbh.addTopicToDatabase(topic);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// hide dialog
				((BaseActivity) getActivity()).hideProgressDialog();
				topic = CollectionGenerator.getCollection(
						ICollection.TYPE_TOPIC, topicId, dbh);
				Topic t = (Topic) topic;
				((BaseActivity) getActivity()).makeToast("Topic = "
						+ t.getName() + " - Word count = "
						+ t.getListIdiom().size());
				showView(topic);
			}
		};
		ConnectionUtils.getListTopic(handler);
	}

	/* load random topic from database */
	private Topic loadRandomTopicFromDatabase() {
		Topic topic = null;
		ArrayList<Topic> listTopics = ((BaseActivity) getActivity())
				.getDBHelper().getListTopic();
		int topicCount = listTopics.size();
		if (topicCount > 0) {
			int random = new Random().nextInt(10) + 1;
			topic = listTopics.get(random - 1);
			LogUtils.logInfo("Topic = " + topic.getName());
			ArrayList<Idiom> listIdiom = ((BaseActivity) getActivity())
					.getDBHelper().getListIdiomOfTopic(topic);
			topic.setListIdiom(listIdiom);
		}
		return topic;
	}

	/* show collection content to views */
	private void showView(ICollection collection) {
		// topic
		if (collection.getCollectionType() == ICollection.TYPE_TOPIC) {
			listIdiom = topic.getListIdiom();
			if (listIdiom.size() > 0) {
				pagerAdapter = new IdiomPagerAdapter(getFragmentManager(),
						listIdiom);
				idiomPager.setAdapter(pagerAdapter);
				idiomPager.setCurrentItem(0);
			}
			LogUtils.logInfo("size = " + pagerAdapter.getCount());
		}
		// list
		else if (collection.getCollectionType() == ICollection.TYPE_LIST) {
			listIdiom = list.getListIdiom();
			if (listIdiom.size() > 0) {
				pagerAdapter = new IdiomPagerAdapter(getFragmentManager(),
						listIdiom);
				idiomPager.setAdapter(pagerAdapter);
				idiomPager.setCurrentItem(0);
			}
			LogUtils.logInfo("size = " + pagerAdapter.getCount());
			// everyday idiom
		} else if (collection.getCollectionType() == ICollection.TYPE_EVERYDAY_IDIOM) {
			listIdiom = everyDayIdiom.getListIdiom();
			if (listIdiom.size() > 0) {
				pagerAdapter = new IdiomPagerAdapter(getFragmentManager(),
						listIdiom);
				idiomPager.setAdapter(pagerAdapter);
				idiomPager.setCurrentItem(0);
			}
			LogUtils.logInfo("size = " + pagerAdapter.getCount());
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		boolean handled = false;
		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
			String keyword = searchBar.getText().toString();
			if (!keyword.isEmpty()) {
				searchIdiom(keyword);
			} else {
				searchBar.setError("Please give a word to search");
			}
			handled = true;
		}
		return handled;
	}

	/* search a idiom */
	private void searchIdiom(String keyword) {
		// String keyword = searchBar.getText().toString();
//		Idiom idiom = dbh.searchIdiomByKeyword(keyword);
//
//		if (idiom.getName().isEmpty()) {
//			((BaseActivity) getActivity()).makeToast(keyword + " not found");
//		} else {
//			int currentPosition = idiomPager.getCurrentItem();
//			listIdiom.remove(currentPosition);
//			listIdiom.add(currentPosition, idiom);
//			currentIdiom = idiom;
//			IdiomFragment fragment = pagerAdapter.getFragment(idiomPager
//					.getCurrentItem());
//			fragment.showView(idiom);
//			checkCurrentIdiomInLiked();
//			checkCurrentIdiomHasNote();
//			
//		}
		((HomeActivity)getActivity()).setDictionaryFragment(new DictionaryFragment());
		Bundle bundle = new Bundle();
		bundle.putString("keyword", keyword);
		((HomeActivity)getActivity()).switchContent(R.id.container, ((HomeActivity)getActivity()).getDictionaryFragment(), bundle);
		((HomeActivity)getActivity()).currentState = HomeActivity.STATE_DICTIONARY;
		((HomeActivity)getActivity()).setBackground();
	}

	/* show dialog prompt user to add a note to a idiom */
	private void showDialogAddNote() {
		final Dialog dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//dialog.getWindow().setLayout((int)(DataUtils.SCREEN_WIDTH*0.8f), (int)(DataUtils.SCREEN_HEIGHT*0.5f));
		dialog.setContentView(R.layout.dialog_add_note);
		final EditText etNote = (EditText) dialog.findViewById(R.id.note);
		Button btAdd = (Button) dialog.findViewById(R.id.bt_add_note);
		btAdd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (etNote.getText().toString().isEmpty()) {
					etNote.setError("Please type the note");
				} else {
					Idiom idiom = getCurrenIdiom();
					String note = etNote.getText().toString();
					// update
					dbh.addIdiomNote(note, idiom);
					dialog.dismiss();
					((BaseActivity) getActivity()).makeToast("Note added");
					checkCurrentIdiomHasNote();
				}
			}
		});
		Button btCancel = (Button) dialog.findViewById(R.id.bt_cancel);
		btCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		TextView tvAddNoteTo = (TextView) dialog.findViewById(R.id.add_note_to);
		tvAddNoteTo.setText("Add note to [" + getCurrenIdiom().getName() + "]");
		dialog.setCancelable(true);
		dialog.show();
	}

	/* show dialog prompt user to add a idiom to a list */
	private void showDialogAddToList() {
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
		etListName.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				adapter.setSelection(-1);
				adapter.notifyDataSetChanged();
			}
		});
		lvList.setAdapter(adapter);
		lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.setSelection(position);
				adapter.notifyDataSetChanged();
				((ImageView) dialog.findViewById(R.id.add_selection))
						.setImageResource(R.drawable.ic_lang);
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

					@Override
					public void onClick(View v) {
						// if select from exist list
						if (adapter.getSelection() != -1) {
							Idiom idiom = getCurrenIdiom();
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
								dbh.addList(new List(newListName));
								// get created list
								Idiom idiom = getCurrenIdiom();
								List list = ((BaseActivity) getActivity())
										.getDBHelper().getNewCreatedList();
								addIdiomToList(idiom, list);

								dialog.dismiss();
							}
						}
						List listLike = dbh.getListById(1);
						if (dbh.checkIdiomInList(getCurrenIdiom(), listLike)) {
							btLike.setImageResource(R.drawable.liked);
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
						ivAddNewSelection.setImageResource(R.drawable.choose);
						adapter.setSelection(-1);
						adapter.notifyDataSetChanged();
						etListName.requestFocus();
					}
				});
	}

	/* add a idiom to list */
	private void addIdiomToList(Idiom idiom, List list) {
		dbh.addIdiomToList(idiom, list);
		((BaseActivity) getActivity()).makeToast("Added " + idiom.getName()
				+ " to " + list.getName());
		// if list is liked list, show icon liked
		if (list.getListId() == 1) {
			btLike.setImageResource(R.drawable.liked);
		} else {
			btLike.setImageResource(R.drawable.like);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_note:
			if (getCurrenIdiom() != null) {
				if (!checkIdiomHasNote(getCurrenIdiom())) {
					showDialogAddNote();
				} else {
					showDialogShowNote();
				}
			}
			break;
		case R.id.add_to_list:
			showDialogAddToList();
			break;
		case R.id.add_to_already_know:
			Idiom idiom = getCurrenIdiom();
			List list = dbh.getListById(2);
			addIdiomToList(idiom, list);
			List listLike = dbh.getListById(1);
			if (checkIdiomInList(idiom, listLike)) {
				btLike.setImageResource(R.drawable.liked);
			}
			break;
		case R.id.like:
			Idiom idiomToLike = getCurrenIdiom();
			List listLiked = dbh.getListById(1);
			if (!checkIdiomInList(idiomToLike, listLiked)) {
				addIdiomToList(idiomToLike, listLiked);
				btLike.setImageResource(R.drawable.liked);
			}
			break;
		case R.id.share:
			shareIdiom(getCurrenIdiom());
			break;
		}
	}

	/* check idiom in a list? */
	private boolean checkIdiomInList(Idiom idiom, List list) {
		return dbh.checkIdiomInList(idiom, list);
	}

	private boolean checkIdiomHasNote(Idiom idiom) {
		return dbh.checkIdiomHasNote(idiom);
	}

	/* get idiom is showing in viewpager */
	private Idiom getCurrenIdiom() {
		// if (listIdiom.size() > idiomPager.getCurrentItem()) {
		// Idiom idiom = listIdiom.get(idiomPager.getCurrentItem());
		//
		// return idiom;
		// }
		// return null;
		return currentIdiom;
	}

	/* get current collection type is topic or list */
	public int getCurrentCollectionType() {
		if (topic != null) {
			return ICollection.TYPE_TOPIC;
		}
		if (list != null) {
			return ICollection.TYPE_LIST;
		}
		if (everyDayIdiom != null) {
			return ICollection.TYPE_EVERYDAY_IDIOM;
		}
		return -1;
	}

	/* get current list id */
	public int getCurrentTopicID() {
		if (topic != null) {
			return topic.getId();
		}
		return -1;
	}

	/* get current list id */
	public int getCurrentListID() {
		if (list != null) {
			return list.getId();
		}
		return -1;
	}

	/* check current idiom in list */
	private void checkCurrentIdiomInLiked() {
		Idiom idiom = getCurrenIdiom();
		if (idiom != null) {
			List listLiked = dbh.getListById(1);
			LogUtils.logInfo("LIKED = " + checkIdiomInList(idiom, listLiked));
			if (!checkIdiomInList(idiom, listLiked)) {
				btLike.setImageResource(R.drawable.like);
			} else {
				btLike.setImageResource(R.drawable.liked);
			}
		}
	}

	/* check current idiom has note? */
	private void checkCurrentIdiomHasNote() {
		Idiom idiom = getCurrenIdiom();
		if (idiom != null) {
			boolean hasNote = checkIdiomHasNote(idiom);
			if (hasNote) {
				tvAddNote.setText("[ Tap to show note of this idiom ]");
			} else {
				tvAddNote.setText("[ Add a note on this idiom]");
			}
		}
	}

	/* focus on search bar */
	public void focusOnSearchBar() {
		searchBar.requestFocus();
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);
		LogUtils.logInfo("focus search bar = " + focusSearchBar);
	}

	/* show note of a idiom */
	private void showDialogShowNote() {
		Idiom idiom = getCurrenIdiom();
//		new AlertDialog.Builder(getActivity())
//				.setMessage("Note: " + dbh.getNoteOfIdiom(idiom))
//				.setTitle(idiom.getName())
//				.setNegativeButton("OK", new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//
//					}
//				}).setCancelable(true).show();
		final Dialog dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_note);
		TextView tvIdiom = (TextView)dialog.findViewById(R.id.idiom);
		tvIdiom.setText(idiom.getName());
		TextView tvNote = (TextView)dialog.findViewById(R.id.note);
		tvNote.setText("Note: " + dbh.getNoteOfIdiom(idiom));
		Button btOK = (Button)dialog.findViewById(R.id.ok);
		btOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setCancelable(true);
		dialog.show();
	}

	/* share a idiom */
	private void shareIdiom(Idiom idiom) {

		SimpleFacebook mSimpleFacebook = ((HomeActivity) getActivity())
				.getSimpleFacebookInstance();

		// check login
		if (!PreferenceUtils.isLoggedInFacebook(getActivity())) {
			FacebookUtils.login(getActivity(), mSimpleFacebook,
					FacebookUtils.ACTION_FEED_AFTER_LOGIN, idiom);
		}
		// share
		else {
			FacebookUtils.shareIdiom(mSimpleFacebook, idiom, getActivity());
		}

	}
	
//	public void scrollToPosition(int position){
//		idiomPager.setCurrentItem(position);
//	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		if (listIdiom.size() > idiomPager.getCurrentItem()) {
			currentIdiom = listIdiom.get(idiomPager.getCurrentItem());
		}
		checkCurrentIdiomInLiked();
		checkCurrentIdiomHasNote();
	}

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

}
