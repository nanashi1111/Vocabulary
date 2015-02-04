package com.it.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.it.models.Idiom;
import com.it.models.Topic;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ConnectionUtils {

	private static final String API_ROOT = "http://vinhdo.me/idiom/api/";
	private static final String API_ADD_IDIOM_TO_TOPIC = API_ROOT + "idiom/add";
	private static final String API_GET_ALL_IDIOM = API_ROOT + "idiom/getlist";
	private static final String API_ADD_TOPIC = API_ROOT + "topic/add";
	private static final String API_GET_ALL_TOPIC = API_ROOT + "topic/getlist";
	private static final String API_GET_IDIOM_FROM_TOPIC = API_ROOT
			+ "topic/getidiomoftopic";

	// add a idiom to a topic
	public static void addIdiomToTopic(Idiom idiom, Topic topic,
			JsonHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.put("name", idiom.getName());
		params.put("definition", idiom.getDefinition());
		params.put("sample", idiom.getSample());
		params.put("topic_id", topic.getTopicId());
		post(API_ADD_IDIOM_TO_TOPIC, params, handler);
	}

	// get list idiom
	public static void getListIdiom(JsonHttpResponseHandler handler) {
		post(API_GET_ALL_IDIOM, handler);
	}

	// add a topic to server
	public static void addTopic(Topic topic, JsonHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.put("name", topic.getName());
		params.put("description", topic.getDescription());
		post(API_ADD_TOPIC, params, handler);
	}

	// get list topic
	public static void getListTopic(JsonHttpResponseHandler handler) {
		post(API_GET_ALL_TOPIC, handler);
	}

	// get all idioms from a topic
	public static void getListIdiomFromTopic(Topic topic,
			JsonHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.put("topic_id", topic.getTopicId());
		post(API_GET_IDIOM_FROM_TOPIC, params, handler);
	}

	// get all idioms from a topic
	public static void getListIdiomFromTopic(int topicId,
			JsonHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.put("topic_id", topicId);
		post(API_GET_IDIOM_FROM_TOPIC, params, handler);
	}

	public static boolean checkConnection(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}

	public static void get(String url, RequestParams params,
			JsonHttpResponseHandler handler) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, params, handler);
	}

	public static void get(String url, JsonHttpResponseHandler handler) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, handler);
	}

	public static void post(String url, RequestParams params,
			JsonHttpResponseHandler handler) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(url, params, handler);
	}

	public static void post(String url, JsonHttpResponseHandler handler) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(url, handler);
	}
}
