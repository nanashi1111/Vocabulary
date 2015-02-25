package com.it.models;

import java.util.ArrayList;

public interface ICollection {

	/*collection types*/
	public static final int TYPE_TOPIC = 1;
	public static final int TYPE_LIST = 2;
	public static final int TYPE_EVERYDAY_IDIOM = 3;

	/*get list idiom of collection*/
	public ArrayList<Idiom> getListIdiom();

	/*set list idiom of collection*/
	public void setListIdiom(ArrayList<Idiom> listIdiom);

	/*get collection type*/
	public int getCollectionType();
	
	/*get collection ID*/
	public int getId();

}
