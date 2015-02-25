package com.it.models;

import java.util.ArrayList;

public class EverydayIdiom implements ICollection {

	private ArrayList<Idiom> listIdiom;

	@Override
	public ArrayList<Idiom> getListIdiom() {
		return listIdiom == null ? new ArrayList<Idiom>() : listIdiom;
	}

	@Override
	public void setListIdiom(ArrayList<Idiom> listIdiom) {
		this.listIdiom = listIdiom;
	}

	@Override
	public int getCollectionType() {
		return ICollection.TYPE_EVERYDAY_IDIOM;
	}

	@Override
	public int getId() {
		return 0;
	}

}
