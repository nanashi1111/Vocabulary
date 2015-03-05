package com.it.vocabulary;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.it.adapters.ListListAdapter;
import com.it.adapters.ListTopicAdapter;
import com.it.fragments.DictionaryFragment;
import com.it.fragments.ExamFragment;
import com.it.fragments.ListFragments;
import com.it.fragments.NavigationDrawerFragment;
import com.it.fragments.RandomIdiomFragment;
import com.it.models.ICollection;
import com.it.models.Idiom;
import com.it.models.List;
import com.it.models.Topic;
import com.it.utils.ConnectionUtils;
import com.it.utils.DataUtils;
import com.it.utils.LogUtils;
import com.it.utils.PreferenceUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sromku.simple.fb.SimpleFacebook;

//import de.keyboardsurfer.android.widget.crouton.Crouton;
//import de.keyboardsurfer.android.widget.crouton.Style;

public class HomeActivity extends BaseActivity {

	private NavigationDrawerFragment mNavigationDrawerFragment;
	private SimpleFacebook mSimpleFacebook;

	public static final int STATE_RANDOM = 1;
	public static final int STATE_LISTS = 2;
	public static final int STATE_EXAM = 3;
	public static final int STATE_RESULT = 4;
	public static final int STATE_DICTIONARY = 5;
	public static int currentState = STATE_RANDOM;

	public RandomIdiomFragment randomFragment;
	public ListFragments listFragment;
	public ExamFragment examFragment;
	public DictionaryFragment dictionaryFragment;

	private ImageView ivRandom, ivList, ivExam, ivDictionary;
	private TextView tvRandom, tvList, tvExam, tvDictionary;

	public static boolean downloadingData = false;

	// public ResultFragment resultFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		DataUtils.listActivity.add(this);
		setupView();
		setupActionBar();
		// if data empty, show dialog to download data
		if (!PreferenceUtils.isDownloadedData(this)) {
			showDialogDownloadData();
		}
		// if data is downloaded, show random screen
		else {
			String command = getIntent().getStringExtra("command");
			if (command == null) {
				command = "";
			}
			if (!command.equals("CLEAR")) {
				randomFragment = new RandomIdiomFragment();
				Bundle bundle = new Bundle();
				bundle.putInt("from_list_fragment", ICollection.TYPE_TOPIC);
				bundle.putBoolean("random_load", true);
				// randomFragment.setArguments(bundle);
				switchContent(R.id.container, randomFragment, bundle);
				currentState = STATE_RANDOM;
				setBackground();
			} else {
				for (int i = 0; i < DataUtils.listActivity.size() - 1; i++) {
					DataUtils.listActivity.get(i).finish();

				}
				DataUtils.listActivity.clear();
				DataUtils.listActivity.add(this);
				showEverydayIdiom();
			}
		}
	}

	public void setupActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(Html
				.fromHtml("<font color=\"#FEDEB5\">Vocabulary</font>"));
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#3F2140")));
	}

	public void setupActionBar(String title) {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(Html.fromHtml("<font color=\"#FEDEB5\">" + title
				+ "</font>"));
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#3F2140")));
	}

	@Override
	public void onClick(View v) {
		if (!downloadingData) {
			switch (v.getId()) {
			case R.id.random:
				LogUtils.logInfo("random");
				if (currentState != STATE_RANDOM) {
					if (currentState != STATE_EXAM) {
						randomFragment = new RandomIdiomFragment();
						Bundle bundle = new Bundle();
						bundle.putInt("from_list_fragment",
								ICollection.TYPE_TOPIC);
						bundle.putBoolean("random_load", true);
						// randomFragment.setArguments(bundle);
						switchContent(R.id.container, randomFragment, bundle);
						currentState = STATE_RANDOM;
						setBackground();
					} else {
						examFragment.showDialogCancelExam(STATE_RANDOM, false);
					}
				}
				break;
			case R.id.lists:
				LogUtils.logInfo("list");
				if (currentState != STATE_LISTS) {
					if (currentState != STATE_EXAM) {
						listFragment = ListFragments.getInstance();
						// take collection info to show
						// in listFragment
						int collectionType = randomFragment
								.getCurrentCollectionType();
						Bundle bundle = new Bundle();
						bundle.putInt("collection_type", collectionType);
						if (collectionType == ICollection.TYPE_TOPIC) {
							bundle.putInt("collection_id",
									randomFragment.getCurrentTopicID());
						} else if (collectionType == ICollection.TYPE_LIST) {
							bundle.putInt("collection_id",
									randomFragment.getCurrentListID());
						} else if (collectionType == ICollection.TYPE_EVERYDAY_IDIOM) {

						}
						switchContent(R.id.container, listFragment, bundle);

						currentState = STATE_LISTS;
						setBackground();
					} else {
						examFragment.showDialogCancelExam(STATE_LISTS, true);
					}
				}
				break;
			case R.id.exam:
				LogUtils.logInfo("exam");
				if (currentState != STATE_EXAM) {
					examFragment = ExamFragment.getInstance();
					switchContent(R.id.container, examFragment, null);
					currentState = STATE_EXAM;
					setBackground();
				}
				break;
			case R.id.dictionary:
				LogUtils.logInfo("dictionary");
				if (currentState == STATE_RANDOM) {
					randomFragment.focusOnSearchBar();
				} else {
					if (currentState != STATE_EXAM) {
						// randomFragment = new RandomIdiomFragment(true);
						// Bundle bundle = new Bundle();
						// bundle.putInt("from_list_fragment",
						// ICollection.TYPE_TOPIC);
						// bundle.putBoolean("random_load", true);
						// randomFragment.setArguments(bundle);
						dictionaryFragment = new DictionaryFragment();
						// switchContent(R.id.container, randomFragment,
						// bundle);
						switchContent(R.id.container, dictionaryFragment);
						currentState = STATE_DICTIONARY;
						setBackground();
					} else {
						examFragment.showDialogCancelExam(STATE_DICTIONARY,
								true);
					}
				}
				break;
			}
		} else {
			makeToast("Downloading data, please wait for a moment");
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

		// image view
		ivRandom = (ImageView) findViewById(R.id.ic_random);
		ivList = (ImageView) findViewById(R.id.ic_list);
		ivExam = (ImageView) findViewById(R.id.ic_exam);
		ivDictionary = (ImageView) findViewById(R.id.ic_dictionary);

		// textview
		tvRandom = (TextView) findViewById(R.id.tv_random);
		tvList = (TextView) findViewById(R.id.tv_lists);
		tvExam = (TextView) findViewById(R.id.tv_exam);
		tvDictionary = (TextView) findViewById(R.id.tv_dictionary);
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
		// int runtimeCount = PreferenceUtils.getRunTimeCount(this);
		// if(runtimeCount % 2 == 1){
		// showDiglogExit();
		// }
		showDialogExit();
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
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_download_data);
		View.OnClickListener listener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.yes:
					downloadAllIdiom();
					break;
				case R.id.no:
					// download random topic
					randomFragment = new RandomIdiomFragment();
					Bundle bundle = new Bundle();
					bundle.putInt("from_list_fragment", ICollection.TYPE_TOPIC);
					// randomFragment.setArguments(bundle);
					switchContent(R.id.container, randomFragment, bundle);
					currentState = STATE_RANDOM;
					setBackground();
					break;
				}
				dialog.dismiss();
			}
		};
		Button btYes = (Button) dialog.findViewById(R.id.yes);
		btYes.setOnClickListener(listener);
		Button btNo = (Button) dialog.findViewById(R.id.no);
		btNo.setOnClickListener(listener);
		dialog.setCancelable(false);
		dialog.show();
	}

	// private void showDialogDownloadData() {
	// new AlertDialog.Builder(this)
	// .setMessage(
	// "Your data is empty, do you want to download data from server?")
	// .setTitle("Data empty")
	// .setNegativeButton("No", new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// // download random topic
	// RandomIdiomFragment randomFragment = new RandomIdiomFragment();
	// Bundle bundle = new Bundle();
	// bundle.putInt("from_list_fragment",
	// ICollection.TYPE_TOPIC);
	// // randomFragment.setArguments(bundle);
	// switchContent(R.id.container, randomFragment, bundle);
	// currentState = STATE_RANDOM;
	// setBackground();
	// }
	// })
	// .setPositiveButton("Yes",
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which) {
	// // download all idiom
	// downloadAllIdiom();
	// // download random topic
	//
	// }
	// }).setCancelable(false).show();
	// }

	private void downloadAllIdiom() {
		JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
			@Override
			public void onStart() {
				downloadingData = true;
				showProgressDialog("", "downloading data...");
				LogUtils.logInfo("start download data...");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					final JSONObject response) {
				new SaveIdiomToDatabase().execute(response);
			}
		};
		ConnectionUtils.getListIdiom(handler);
	}

	private class SaveIdiomToDatabase extends
			AsyncTask<JSONObject, Float, Void> {

		@Override
		protected void onPreExecute() {
			makeToast("Downloading data...");
		}

		@Override
		protected Void doInBackground(JSONObject... params) {
			JSONObject response = params[0];
			try {
				// array json idiom
				JSONArray idiomJsonArray = response.getJSONArray("data");
				// get idioms from array
				for (int i = 0; i < idiomJsonArray.length(); i++) {
					JSONObject idiomJson = idiomJsonArray.getJSONObject(i);
					Idiom idiom = new Idiom(idiomJson);
					// save to db
					dbh.addIdiomToDatabase(idiom);
					LogUtils.logInfo("Idiom:" + idiom.getName());
					float percent = i*100.0f/idiomJsonArray.length();
					publishProgress(percent);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Float... values) {
			DecimalFormat df = new DecimalFormat("###.#");
			String percent = df.format(values[0])+"%";
			setProgressText(percent);
		}

		@Override
		protected void onPostExecute(Void result) {
			setProgressText("Preparing...");
			downloadTopicsFromServer();
		}

	}

	public void showDialogSelectCollection(final int type) {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_select_collection);
		final TextView tvTitle = (TextView) dialog
				.findViewById(R.id.dialog_select_collection_title);
		final ListView lvListTopic = (ListView) dialog
				.findViewById(R.id.collection);
		if (type == 1) { // topic
			tvTitle.setText("Select a topic");
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
					int selection = position;
					if (selection == -1) {
						makeToast("You must select a topic first");
					} else {
						// show topic selected
						ArrayList<Topic> listTopic = ((ListTopicAdapter) lvListTopic
								.getAdapter()).getListTopic();
						Topic topic = listTopic.get(selection);
						randomFragment = new RandomIdiomFragment();
						Bundle bundle = new Bundle();
						bundle.putInt("from_list_fragment",
								ICollection.TYPE_TOPIC);
						bundle.putBoolean("random_load", false);
						bundle.putInt("topic_id", topic.getTopicId());
						// randomFragment.setArguments(bundle);
						switchContent(R.id.container, randomFragment, bundle);
						currentState = STATE_RANDOM;
						setBackground();
						dialog.dismiss();
					}
				}
			});
		} else { // list
			tvTitle.setText("Select a list");
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
					int selection = position;
					if (selection == -1) {
						makeToast("You must select a list first");
					} else {
						ArrayList<List> lists = ((ListListAdapter) lvListTopic
								.getAdapter()).getLists();
						// show topic selected
						List list = lists.get(selection);
						randomFragment = new RandomIdiomFragment();
						Bundle bundle = new Bundle();
						bundle.putInt("from_list_fragment",
								ICollection.TYPE_LIST);
						bundle.putInt("list_id", list.getListId());
						// randomFragment.setArguments(bundle);
						switchContent(R.id.container, randomFragment, bundle);
						currentState = STATE_RANDOM;
						dialog.dismiss();
						setBackground();
					}
				}
			});
		}
		ImageView btCancel = (ImageView) dialog.findViewById(R.id.cancel);
		btCancel.setVisibility(View.INVISIBLE);
		btCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		ImageView btDone = (ImageView) dialog.findViewById(R.id.done);
		btDone.setVisibility(View.INVISIBLE);
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
						bundle.putInt("from_list_fragment",
								ICollection.TYPE_TOPIC);
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
						bundle.putInt("from_list_fragment",
								ICollection.TYPE_LIST);
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

	public void showEverydayIdiom() {
		randomFragment = new RandomIdiomFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("from_list_fragment", ICollection.TYPE_EVERYDAY_IDIOM);
		// randomFragment.setArguments(bundle);
		switchContent(R.id.container, randomFragment, bundle);
		currentState = STATE_RANDOM;
		setBackground();
	}

	private void downloadTopicsFromServer() {
		JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					final JSONObject response) {
				new SaveTopicsToDatabase().execute(response);
			}
		};
		ConnectionUtils.getListTopic(handler);
	}

	private class SaveTopicsToDatabase extends
			AsyncTask<JSONObject, Void, Void> {

		@Override
		protected Void doInBackground(JSONObject... params) {
			JSONObject response = params[0];
			try {
				JSONArray topicJsonArray = response.getJSONArray("data");
				for (int i = 0; i < topicJsonArray.length(); i++) {
					Topic topic = new Topic(topicJsonArray.getJSONObject(i));
					dbh.addTopicToDatabase(topic);
					LogUtils.logInfo("Topic:" + topic.getName());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			LogUtils.logInfo("finished download data");
			// hide dialog
			hideProgressDialog();
			// mark application as downloaded data
			PreferenceUtils.setDownloadedData(HomeActivity.this, true);
			// notify
			Toast.makeText(HomeActivity.this, "Data downloaded",
					Toast.LENGTH_LONG).show();
			// show random idiom screen
			randomFragment = new RandomIdiomFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("from_list_fragment", ICollection.TYPE_TOPIC);
			bundle.putBoolean("random_load", true);
			// randomFragment.setArguments(bundle);
			switchContent(R.id.container, randomFragment, bundle);
			currentState = STATE_RANDOM;
			setBackground();
			// finish download
			downloadingData = false;
			//Crouton.cancelAllCroutons();
		}

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

	public DictionaryFragment getDictionaryFragment() {
		return dictionaryFragment;
	}

	public void setDictionaryFragment(DictionaryFragment dictionaryFragment) {
		this.dictionaryFragment = dictionaryFragment;
	}

	public static int getCurrentState() {
		return currentState;
	}

	public static void setCurrentState(int currentState) {
		HomeActivity.currentState = currentState;
	}

	@SuppressWarnings("deprecation")
	public void setBackground() {
		if ((currentState == STATE_RANDOM) || (currentState == STATE_LISTS)) {
			findViewById(R.id.container).setBackgroundDrawable(
					new ColorDrawable(Color.parseColor("#FEDEB5")));
		} else {
			findViewById(R.id.container).setBackgroundDrawable(
					new ColorDrawable(Color.parseColor("#E6E8E7")));
		}
		if (currentState == STATE_RANDOM) {
			ivRandom.setImageResource(R.drawable.ic_random_selected);
			tvRandom.setTextColor(Color.parseColor("#00FFC6"));
			ivList.setImageResource(R.drawable.ic_lists);
			tvList.setTextColor(Color.parseColor("#FFFFFF"));
			ivExam.setImageResource(R.drawable.ic_exam);
			tvExam.setTextColor(Color.parseColor("#FFFFFF"));
			ivDictionary.setImageResource(R.drawable.ic_search);
			tvDictionary.setTextColor(Color.parseColor("#FFFFFF"));
		} else if (currentState == STATE_LISTS) {
			ivRandom.setImageResource(R.drawable.ic_random);
			tvRandom.setTextColor(Color.parseColor("#FFFFFF"));
			ivList.setImageResource(R.drawable.ic_lists_selected);
			tvList.setTextColor(Color.parseColor("#00FFC6"));
			ivExam.setImageResource(R.drawable.ic_exam);
			tvExam.setTextColor(Color.parseColor("#FFFFFF"));
			ivDictionary.setImageResource(R.drawable.ic_search);
			tvDictionary.setTextColor(Color.parseColor("#FFFFFF"));
		} else if (currentState == STATE_EXAM) {
			ivRandom.setImageResource(R.drawable.ic_random);
			tvRandom.setTextColor(Color.parseColor("#FFFFFF"));
			ivList.setImageResource(R.drawable.ic_lists);
			tvList.setTextColor(Color.parseColor("#FFFFFF"));
			ivExam.setImageResource(R.drawable.ic_exam_selected);
			tvExam.setTextColor(Color.parseColor("#00FFC6"));
			ivDictionary.setImageResource(R.drawable.ic_search);
			tvDictionary.setTextColor(Color.parseColor("#FFFFFF"));
		} else if (currentState == STATE_DICTIONARY) {
			ivRandom.setImageResource(R.drawable.ic_random);
			tvRandom.setTextColor(Color.parseColor("#FFFFFF"));
			ivList.setImageResource(R.drawable.ic_lists);
			tvList.setTextColor(Color.parseColor("#FFFFFF"));
			ivExam.setImageResource(R.drawable.ic_exam);
			tvExam.setTextColor(Color.parseColor("#FFFFFF"));
			ivDictionary.setImageResource(R.drawable.ic_search_selected);
			tvDictionary.setTextColor(Color.parseColor("#00FFC6"));
		}

	}

	// public void changeRootMainColor(){
	// View rootMain = findViewById(R.id.root_main);
	// rootMain.setBackgroundColor(Color.parseColor("#FEDEB5"));
	// }

	@Override
	protected void onStop() {
		//Crouton.cancelAllCroutons();
		super.onStop();
	}

}
