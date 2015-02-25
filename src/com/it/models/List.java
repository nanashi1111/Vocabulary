package com.it.models;

import java.util.ArrayList;

public class List implements ICollection{
	
	private int listId;
	private String name;
	private ArrayList<Idiom> listIdiom;
	public List(){
		
	}
	
	public List(String name){
		this.name = name;
	}

	public int getListId() {
		return listId;
	}

	public void setListId(int listId) {
		this.listId = listId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public ArrayList<Idiom> getListIdiom() {
		return listIdiom;
	}

	@Override
	public void setListIdiom(ArrayList<Idiom> listIdiom) {
		this.listIdiom = listIdiom;
	}

	@Override
	public int getCollectionType() {
		return ICollection.TYPE_LIST;
	}

	@Override
	public int getId() {
		return listId;
	}
	
	
	
}
