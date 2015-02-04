package com.it.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Idiom {

	private int id;
	private String name;
	private String definition;
	private String sample;
	private String note;
	private String dateCreate;
	private int topicId;
	private int listId;

	public Idiom() {
	}

	public Idiom(JSONObject json) {
		try {
			id = Integer.parseInt(json.getString("id"));
			name = json.getString("name");
			definition = json.getString("definition");
			definition.replace("\'", "");
			sample = json.getString("sample");
			note = json.getString("note");
			dateCreate = json.getString("date_create");
			topicId = Integer.parseInt(json.getString("topic_id"));
			listId = -1;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name == null ? "" : name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefinition() {
		return definition == null ? "" : definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getSample() {
		return sample == null ? "" : sample;
	}

	public void setSample(String sample) {
		this.sample = sample;
	}

	public String getNote() {
		return note == null ? "" : note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getDateCreate() {
		return dateCreate == null ? "" : dateCreate;
	}

	public void setDateCreate(String dateCreate) {
		this.dateCreate = dateCreate;
	}

	public int getTopicId() {
		return topicId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	public int getListId() {
		return listId;
	}

	public void setListId(int listId) {
		this.listId = listId;
	}

}
