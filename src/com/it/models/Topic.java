package com.it.models;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class Topic{
	private int topicId;
	private String name;
	private String description;
	private int idiomCount;
	private String dateCreate;
	private ArrayList<Idiom> listIdiom;

	public Topic() {
	}

	public Topic(JSONObject json) {
		try {
			topicId = Integer.parseInt(json.getString("topic_id"));
			name = json.getString("name");
			description = json.getString("description");
			//idiomCount = Integer.parseInt(json.getString("idiom_count"));
			idiomCount = 100;
			dateCreate = json.getString("date_create");
		} catch (NumberFormatException e) {
			idiomCount = 0;
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public int getTopicId() {
		return topicId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	public String getName() {
		return name == null ? "" : name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description == null ? "null" : description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getIdiomCount() {
		return idiomCount;
	}

	public void setIdiomCount(int idiomCount) {
		this.idiomCount = idiomCount;
	}

	public String getDateCreate() {
		return dateCreate == null ? "" : dateCreate;
	}

	public void setDateCreate(String dateCreate) {
		this.dateCreate = dateCreate == null ? "" : dateCreate;
	}

	public ArrayList<Idiom> getListIdiom() {
		return listIdiom == null ? new ArrayList<Idiom>() : listIdiom;
	}

	public void setListIdiom(ArrayList<Idiom> listIdiom) {
		this.listIdiom = listIdiom;
	}

}
