package com.it.vocabulary;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.it.adapters.ListListAdapter;
import com.it.adapters.ListTopicAdapter;
import com.it.fragments.ExamFragment;
import com.it.fragments.ListFragments;
import com.it.fragments.NavigationDrawerFragment;
import com.it.fragments.RandomIdiomFragment;
import com.it.models.Idiom;
import com.it.models.List;
import com.it.models.Topic;
import com.it.utils.ConnectionUtils;
import com.it.utils.LogUtils;
import com.it.utils.PreferenceUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sromku.simple.fb.SimpleFacebook;

public class HomeActivity extends BaseActivity {

	private NavigationDrawerFragment mNavigationDrawerFragment;
	private SimpleFacebook mSimpleFacebook;

	public static final int STATE_RANDOM = 1;
	public static final int STATE_LISTS = 2;
	public static final int STATE_EXAM = 3;
	public static int currentState = STATE_RANDOM;

	public RandomIdiomFragment randomFragment;
	public ListFragments listFragment;
	private ExamFragment examFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		setupView();
		setupActionBar();
		// if data empty, show dialog to download data
		if (!PreferenceUtils.isDownloadedData(this)) {
			showDialogDownloadData();
		}
		// if data is downloaded, show random screen
		else {
			randomFragment = new RandomIdiomFragment();
			Bundle bundle = new Bundle();
			bundle.putBoolean("from_list_fragment", false);
			bundle.putBoolean("random_load", true);
			// randomFragment.setArguments(bundle);
			switchContent(R.id.container, randomFragment, bundle);
			currentState = STATE_RANDOM;
		}
	}

	public void setupActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle("Vocabulary");
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#3391AB")));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.random:
			LogUtils.logInfo("random");
			if (currentState != STATE_RANDOM) {
				randomFragment = new RandomIdiomFragment();
				Bundle bundle = new Bundle();
				bundle.putBoolean("from_list_fragment", false);
				bundle.putBoolean("random_load", true);
				// randomFragment.setArguments(bundle);
				switchContent(R.id.container, randomFragment, bundle);
				currentState = STATE_RANDOM;
			}
			break;
		case R.id.lists:
			LogUtils.logInfo("list");
			if (currentState != STATE_LISTS) {
				listFragment = ListFragments.getInstance();
				// take collection info to show
				// in listFragment
				String collectionType = randomFragment
						.getCurrentCollectionType();
				Bundle bundle = new Bundle();
				bundle.putString("collection_type", collectionType);
				if (collectionType.equals("topic")) {
					bundle.putInt("collection_id",
							randomFragment.getCurrentTopicID());
				} else {
					bundle.putInt("collection_id",
							randomFragment.getCurrentListID());
				}
				switchContent(R.id.container, listFragment, bundle);

				currentState = STATE_LISTS;
			}
			break;
		case R.id.exam:
			LogUtils.logInfo("exam");
			if (currentState != STATE_EXAM) {
				examFragment = ExamFragment.getInstance();
				switchContent(R.id.container, examFragment, null);
				currentState = STATE_EXAM;
			}
			break;
		case R.id.dictionary:
			LogUtils.logInfo("dictionary");
			// switchContent(R.id.container, RandomIdiomFragment.getInstance());
			break;
		}
	}

	@Override
	protected void setupView() {
		// set up drawer
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		// set up buttons
		findViewById(R.id.random).setOnClickListener(this);
		findViewById(R.id.dictionary).setOnClickListener(this);
		findViewById(R.id.lists).setOnClickListener(this);
		findViewById(R.id.exam).setOnClickListener(this);
	}

	// private void getFacebookId() {
	// mSimpleFacebook = SimpleFacebook.getInstance(this);
	// //FacebookUtils.getFacebookID(mSimpleFacebook);
	// // check logged in
	// if (!PreferenceUtils.isLoggedInFacebook(this)) {
	// // if not, login
	// FacebookUtils.login(this, mSimpleFacebook);
	// } else {
	// // if logged, get FBID
	// FacebookUtils.getFacebookID(mSimpleFacebook);
	// }
	//
	// }

	@Override
	public void onBackPressed() {
		showDiglogExit();
	}

	public SimpleFacebook getSimpleFacebookInstance() {
		mSimpleFacebook = SimpleFacebook.getInstance(this);
		return mSimpleFacebook;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void showDialogDownloadData() {
		new AlertDialog.Builder(this)
				.setMessage(
						"Your data is empty, do you want to download data from server?")
				.setTitle("Data empty")
				.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// download random topic
						RandomIdiomFragment randomFragment = new RandomIdiomFragment();
						Bundle bundle = new Bundle();
						bundle.putBoolean("from_list_fragment", false);
						// randomFragment.setArguments(bundle);
						switchContent(R.id.container, randomFragment, bundle);
						currentState = STATE_RANDOM;
					}
				})
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// download all idiom
								downloadAllIdiom();
								// download random topic

							}
						}).setCancelable(false).show();
	}

	private void downloadAllIdiom() {
		JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
			@Override
			public void onStart() {
				showProgressDialog("", "downloading data...");
				LogUtils.logInfo("start download data...");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					final JSONObject response) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							// array json idiom
							JSONArray idiomJsonArray = response
									.getJSONArray("data");
							// get idioms from array
							for (int i = 0; i < idiomJsonArray.length(); i++) {
								JSONObject idiomJson = idiomJsonArray
										.getJSONObject(i);
								Idiom idiom = new Idiom(idiomJson);
								// save to db
								dbh.addIdiomToDatabase(idiom);
								LogUtils.logInfo("Idiom:" + idiom.getName());
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}).run();

				downloadTopicsFromServer();
			}
		};
		ConnectionUtils.getListIdiom(handler);
	}

	public void showDialogSelectCollection(final int type) {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_select_collection);
		final ListView lvListTopic = (ListView) dialog
				.findViewById(R.id.collection);
		if (type == 1) { // topic
			final ArrayList<Topic> listTopic = dbh.getListTopic();
			final ListTopicAdapter adapter = new ListTopicAdapter(this, 1,
					listTopic);
			lvListTopic.setAdapter(adapter);
			lvListTopic.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// show topic selected
					// Topic topic = listTopic.get(position);
					// randomFragment = new RandomIdiomFragment();
					// Bundle bundle = new Bundle();
					// bundle.putBoolean("from_list_fragment", false);
					// bundle.putBoolean("random_load", false);
					// bundle.putInt("topic_id", topic.getTopicId());
					// // randomFragment.setArguments(bundle);
					// switchContent(R.id.container, randomFragment, bundle);
					// currentState = STATE_RANDOM;
					// dialog.dismiss();
					adapter.setSelection(position);
					adapter.notifyDataSetChanged();
				}
			});
		} else { // list
			final ArrayList<List> lists = dbh.getAllList();
			final ListListAdapter adapter = new ListListAdapter(this, 1, lists);
			lvListTopic.setAdapter(adapter);
			lvListTopic.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// show topic selected
					// List list = lists.get(position);
					// randomFragment = new RandomIdiomFragment();
					// Bundle bundle = new Bundle();
					// bundle.putBoolean("from_list_fragment", true);
					// bundle.putInt("list_id", list.getListId());
					// // randomFragment.setArguments(bundle);
					// switchContent(R.id.container, randomFragment, bundle);
					// currentState = STATE_RANDOM;
					// dialog.dismiss();
					adapter.setSelection(position);
					adapter.notifyDataSetChanged();
				}
			});
		}
		ImageView btCancel = (ImageView) dialog.findViewById(R.id.cancel);
		btCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		ImageView btDone = (ImageView) dialog.findViewById(R.id.done);
		btDone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (type == 1) {
					int selection = ((ListTopicAdapter) lvListTopic
							.getAdapter()).getSelection();
					if (selection == -1) {
						makeToast("You must select a topic first");
					} else {
						// show topic selected
						ArrayList<Topic> listTopic = ((ListTopicAdapter) lvListTopic
								.getAdapter()).getListTopic();
						Topic topic = listTopic.get(selection);
						randomFragment = new RandomIdiomFragment();
						Bundle bundle = new Bundle();
						bundle.putBoolean("from_list_fragment", false);
						bundle.putBoolean("random_load", false);
						bundle.putInt("topic_id", topic.getTopicId());
						// randomFragment.setArguments(bundle);
						switchContent(R.id.container, randomFragment, bundle);
						currentState = STATE_RANDOM;
						dialog.dismiss();
					}
				} else if (type == 2) {
					int selection = ((ListListAdapter) lvListTopic.getAdapter())
							.getSelection();
					if (selection == -1) {
						makeToast("You must select a list first");
					} else {
						ArrayList<List> lists = ((ListListAdapter) lvListTopic
								.getAdapter()).getLists();
						// show topic selected
						List list = lists.get(selection);
						randomFragment = new RandomIdiomFragment();
						Bundle bundle = new Bundle();
						bundle.putBoolean("from_list_fragment", true);
						bundle.putInt("list_id", list.getListId());
						// randomFragment.setArguments(bundle);
						switchContent(R.id.container, randomFragment, bundle);
						currentState = STATE_RANDOM;
						dialog.dismiss();
					}
				}
			}
		});
		dialog.setCancelable(true);
		dialog.show();
	}

	private void downloadTopicsFromServer() {
		JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					final JSONObject response) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							JSONArray topicJsonArray = response
									.getJSONArray("data");
							for (int i = 0; i < topicJsonArray.length(); i++) {
								Topic topic = new Topic(topicJsonArray
										.getJSONObject(i));
								dbh.addTopicToDatabase(topic);
								LogUtils.logInfo("Topic:" + topic.getName());
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}).run();

				LogUtils.logInfo("finished download data");
				// hide dialog
				hideProgressDialog();
				// mark application as downloaded data
				PreferenceUtils.setDownloadedData(HomeActivity.this, true);
				// notify
				Toast.makeText(HomeActivity.this, "Data downloaded",
						Toast.LENGTH_LONG).show();
				// show random idiom screen
				RandomIdiomFragment randomFragment = new RandomIdiomFragment();
				Bundle bundle = new Bundle();
				bundle.putBoolean("from_list_fragment", false);
				// randomFragment.setArguments(bundle);
				switchContent(R.id.container, randomFragment, bundle);
				currentState = STATE_RANDOM;

			}
		};
		ConnectionUtils.getListTopic(handler);
	}

	public RandomIdiomFragment getRandomFragment() {
		return randomFragment;
	}

	public void setRandomFragment(RandomIdiomFragment randomFragment) {
		this.randomFragment = randomFragment;
	}

	public ListFragments getListFragment() {
		return listFragment;
	}

	public void setListFragment(ListFragments listFragment) {
		this.listFragment = listFragment;
	}

	public ExamFragment getExamFragment() {
		return examFragment;
	}

	public void setExamFragment(ExamFragment examFragment) {
		this.examFragment = examFragment;
	}

	public static int getCurrentState() {
		return currentState;
	}

	public static void setCurrentState(int currentState) {
		HomeActivity.currentState = currentState;
	}

}
