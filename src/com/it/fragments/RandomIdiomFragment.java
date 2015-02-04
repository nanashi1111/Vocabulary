package com.it.fragments;

import java.util.ArrayList;
import java.util.Random;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;
import com.it.adapters.IdiomPagerAdapter;
import com.it.adapters.ListAdapter;
import com.it.models.Idiom;
import com.it.models.List;
import com.it.models.Topic;
import com.it.utils.ConnectionUtils;
import com.it.utils.LogUtils;
import com.it.utils.PreferenceUtils;
import com.it.vocabulary.BaseActivity;
import com.it.vocabulary.R;
import com.loopj.android.http.JsonHttpResponseHandler;

@SuppressLint("NewApi")
public class RandomIdiomFragment extends BaseFragment implements
		OnEditorActionListener, OnPageChangeListener, OnClickListener {

	private static RandomIdiomFragment INSTANCE = new RandomIdiomFragment();
	private static final int MAX_AVAILABLE_TOPIC_ID = 12;

	// private TextView tvIdiom;
	private AutoCompleteTextView searchBar;
	private ViewPager idiomPager;
	private TextView tvAddNote;

	private Topic topic;
	private List list;
	private ArrayList<String> listWord;
	private ArrayList<Idiom> listIdiom = new ArrayList<Idiom>();
	private IdiomPagerAdapter pagerAdapter;

	public static RandomIdiomFragment getInstance() {
		return INSTANCE;
	}

	private View rootView;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_random, null);
		setupView(rootView);
		return rootView;
	}

	@Override
	protected void setupView(View rootView) {
		// tvIdiom = (TextView) rootView.findViewById(R.id.idiom_name);
		// set up pager
		idiomPager = (ViewPager) rootView.findViewById(R.id.idiom_pager);
		idiomPager.setOnPageChangeListener(this);
		pagerAdapter = new IdiomPagerAdapter(getFragmentManager(), listIdiom);
		idiomPager.setAdapter(pagerAdapter);
		// set up search bar
		searchBar = (AutoCompleteTextView) rootView
				.findViewById(R.id.search_idiom);
		listWord = ((BaseActivity) getActivity()).getDBHelper().getListWord();
		searchBar.setAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, listWord));
		searchBar.setOnEditorActionListener(this);
		// set up add note view
		tvAddNote = (TextView) rootView.findViewById(R.id.add_note);
		tvAddNote.setOnClickListener(this);
		// add to list button listener
		rootView.findViewById(R.id.add_to_list).setOnClickListener(this);
		// add to already know listener
		rootView.findViewById(R.id.add_to_already_know)
				.setOnClickListener(this);

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// check if not from list fragment
		boolean fromListFragment = getArguments().getBoolean(
				"from_list_fragment");
		if (!fromListFragment) {
			// check downloaded data, if not get random topic from server
			boolean isDownloaded = PreferenceUtils
					.isDownloadedData(getActivity());
			if (!isDownloaded) {
				loadFirstTimeRandomTopic();
			} else {
				boolean randomLoad = getArguments().getBoolean("random_load");
				//Toast.makeText(getActivity(), Boolean.toString(randomLoad), Toast.LENGTH_LONG).show();
				// load from db
				if (randomLoad) {
					topic = loadRandomTopicFromDatabase();
					showView(topic);
				} else {
					int topicId = getArguments().getInt("topic_id");
					topic = loadTopicById(topicId);
					
					showView(topic);
				}
			}
			// if from list fragment
		} else {
			int listId = getArguments().getInt("list_id");
			list = ((BaseActivity) getActivity()).getDBHelper().getListById(
					listId);
			ArrayList<Idiom> listIdiom = ((BaseActivity) getActivity())
					.getDBHelper().getListIdiomOfList(list);
			list.setListIdiom(listIdiom);
			showView(list);
		}
	}

	private void loadFirstTimeRandomTopic() {
		// select a random topic
		Random random = new Random();
		final int randomTopicId = random.nextInt(MAX_AVAILABLE_TOPIC_ID) + 1;

		JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
			@Override
			public void onStart() {
				// show progress dialog
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
						((BaseActivity) getActivity()).getDBHelper()
								.addIdiomToDatabase(idiom);
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
					((BaseActivity) getActivity()).getDBHelper()
							.addTopicToDatabase(topic);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// hide dialog
				((BaseActivity) getActivity()).hideProgressDialog();
				topic = getTopicFromDatabase(topicId);
				showView(topic);
			}
		};
		ConnectionUtils.getListTopic(handler);
	}

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

	private Topic loadTopicById(int topicId) {
		topic = ((BaseActivity) getActivity()).getDBHelper().getTopic(topicId);
		ArrayList<Idiom> listIdiom = ((BaseActivity) getActivity())
				.getDBHelper().getListIdiomOfTopic(topic);
		topic.setListIdiom(listIdiom);
		return topic;
	}

	private Topic getTopicFromDatabase(int topicId) {
		Topic topic = ((BaseActivity) getActivity()).getDBHelper().getTopic(
				topicId);
		ArrayList<Idiom> listIdiom = ((BaseActivity) getActivity())
				.getDBHelper().getListIdiomOfTopic(topic);
		topic.setListIdiom(listIdiom);
		return topic;
	}

	public void showView(Topic topic) {

		listIdiom = topic.getListIdiom();
		pagerAdapter = new IdiomPagerAdapter(getFragmentManager(), listIdiom);
		idiomPager.setAdapter(pagerAdapter);
		idiomPager.setCurrentItem(0);
		LogUtils.logInfo("size = " + pagerAdapter.getCount());
	}

	private void showView(List list) {
		listIdiom = list.getListIdiom();
		pagerAdapter = new IdiomPagerAdapter(getFragmentManager(), listIdiom);
		idiomPager.setAdapter(pagerAdapter);
		idiomPager.setCurrentItem(0);
		LogUtils.logInfo("size = " + pagerAdapter.getCount());
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		boolean handled = false;
		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
			searchIdiom();
			handled = true;
		}
		return handled;
	}

	private void searchIdiom() {
		String keyword = searchBar.getText().toString();
		Idiom idiom = ((BaseActivity) getActivity()).getDBHelper()
				.searchIdiomByKeyword(keyword);
		((BaseActivity) getActivity()).makeToast(idiom.getDefinition());
		// if(!idiom.getName().isEmpty()){
		// tvIdiom.setText(idiom.getName());
		// }else{
		// tvIdiom.setText("can not find "+keyword);
		// }
	}

	private void showDialogAddNote() {
		final Dialog dialog = new Dialog(getActivity());
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
					((BaseActivity) getActivity()).getDBHelper().addIdiomNote(
							note, idiom);
					dialog.dismiss();
					((BaseActivity) getActivity()).makeToast("Note added");
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
		dialog.setTitle("Add note to [" + getCurrenIdiom().getName() + "]");
		dialog.setCancelable(true);
		dialog.show();
	}

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
								((BaseActivity) getActivity()).getDBHelper()
										.addList(new List(newListName));
								// get created list
								Idiom idiom = getCurrenIdiom();
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

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {

	}

	private void addIdiomToList(Idiom idiom, List list) {
		((BaseActivity) getActivity()).getDBHelper()
				.addIdiomToList(idiom, list);
		((BaseActivity) getActivity()).makeToast("Added " + idiom.getName()
				+ " to " + list.getName());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_note:
			showDialogAddNote();
			break;
		case R.id.add_to_list:
			showDialogAddToList();
			break;
		case R.id.add_to_already_know:
			Idiom idiom = getCurrenIdiom();
			List list = ((BaseActivity) getActivity()).getDBHelper()
					.getListById(2);
			addIdiomToList(idiom, list);
			break;
		}
	}

	private Idiom getCurrenIdiom() {
		Idiom idiom = listIdiom.get(idiomPager.getCurrentItem());
		return idiom;
	}

	public String getCurrentCollectionType() {
		if (topic == null) {
			return "list";
		} else {
			return "topic";
		}
	}

	public int getCurrentTopicID() {
		if (topic != null) {
			return topic.getTopicId();
		}
		return -1;
	}

	public int getCurrentListID() {
		if (list != null) {
			return list.getListId();
		}
		return -1;
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

}
