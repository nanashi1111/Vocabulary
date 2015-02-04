package com.it.models;

import java.util.ArrayList;

public class List{
	
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

	public ArrayList<Idiom> getListIdiom() {
		return listIdiom;
	}

	public void setListIdiom(ArrayList<Idiom> listIdiom) {
		this.listIdiom = listIdiom;
	}
	
	
	
}
