package com.it.models;

import java.util.ArrayList;

import com.it.database.DBHelper;

public class CollectionGenerator {

	public CollectionGenerator() {
	}

	/* generate a collection depend on collection type (topic or list) */
	public static ICollection getCollection(int collectionType,
			int collectionId, DBHelper dbh) {
		ICollection collection;
		ArrayList<Idiom> listIdiom;
		if (collectionType == ICollection.TYPE_TOPIC) {
			collection = dbh.getTopic(collectionId);
			listIdiom = dbh.getListIdiomOfTopic(collectionId, false);
		} else if(collectionType == ICollection.TYPE_LIST){
			collection = dbh.getListById(collectionId);
			listIdiom = dbh.getListIdiomOfList(collectionId);
		}else{
			collection = new EverydayIdiom();
			listIdiom = dbh.getListIdiomOfEveryDayIdiom();
		}
		collection.setListIdiom(listIdiom);
		return collection;
	}

}
