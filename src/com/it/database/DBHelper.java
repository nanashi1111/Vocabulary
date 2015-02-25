package com.it.database;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.it.models.Exam;
import com.it.models.Idiom;
import com.it.models.List;
import com.it.models.Topic;
import com.it.utils.DateUtils;

public class DBHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "VocabularyDB";
	private static final int DB_VERSION = 4;
	private static final String CREATE_TABLE_IDIOM = "create table Idiom(id integer primary key, name text, definition text, sample text, note text, date_create text, topic_id integer, list_id integer);";
	private static final String CREATE_TABLE_TOPIC = "create table Topic(topic_id integer primary key, name text, description text, idiom_count integer, date_create text);";
	private static final String CREATE_TABLE_LIST = "create table List(list_id integer primary key autoincrement, name text);";
	private static final String CREATE_TABLE_LIST_DETAIL = "create table ListDetail(id integer primary key autoincrement, list_id integer, idiom_id integer);";
	private static final String CREATE_TABLE_EVERY_IDIOM = "create table EveryDayIdiom(ids text, start_date text);";

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_IDIOM);
		db.execSQL(CREATE_TABLE_TOPIC);
		db.execSQL(CREATE_TABLE_LIST);
		db.execSQL(CREATE_TABLE_LIST_DETAIL);
		db.execSQL(CREATE_TABLE_EVERY_IDIOM);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists Idiom");
		db.execSQL("drop table if exists Topic");
		db.execSQL("drop table if exists List");
		db.execSQL("drop table if exists ListDetail");
		db.execSQL("drop table if exists EveryDayIdiom");
		onCreate(db);
	}

	public void addIdiomToDatabase(Idiom idiom) {
		if (!checkIdiomExist(idiom)) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("id", idiom.getId());
			contentValues.put("name", idiom.getName());
			contentValues.put("definition", idiom.getDefinition());
			contentValues.put("sample", idiom.getSample());
			contentValues.put("note", idiom.getNote());
			contentValues.put("date_create", idiom.getDateCreate());
			contentValues.put("topic_id", idiom.getTopicId());
			contentValues.put("list_id", idiom.getListId());
			getWritableDatabase().insert("Idiom", null, contentValues);
		}
	}

	public void addTopicToDatabase(Topic topic) {
		if (!checkTopicExist(topic)) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("topic_id", topic.getTopicId());
			contentValues.put("name", topic.getName());
			contentValues.put("description", topic.getDescription());
			contentValues.put("idiom_count", topic.getIdiomCount());
			contentValues.put("date_create", topic.getDateCreate());
			getWritableDatabase().insert("Topic", null, contentValues);
		}
	}

	public ArrayList<Topic> getListTopic() {
		ArrayList<Topic> listTopic = new ArrayList<Topic>();
		String query = "select * from Topic";
		Cursor cursor = getReadableDatabase().rawQuery(query, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Topic topic = new Topic();
				topic.setTopicId(cursor.getInt(cursor
						.getColumnIndex("topic_id")));
				topic.setName(cursor.getString(cursor.getColumnIndex("name")));
				topic.setDescription(cursor.getString(cursor
						.getColumnIndex("description")));
				topic.setIdiomCount(cursor.getInt(cursor
						.getColumnIndex("idiom_count")));
				topic.setDateCreate(cursor.getString(cursor
						.getColumnIndex("date_create")));
				listTopic.add(topic);
			} while (cursor.moveToNext());
		}
		return listTopic;
	}

	public Topic getTopicById(int topicId) {
		Topic topic = new Topic();
		String query = "select * from Topic where topic_id = " + topicId;
		Cursor cursor = getReadableDatabase().rawQuery(query, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			topic.setTopicId(cursor.getInt(cursor.getColumnIndex("topic_id")));
			topic.setName(cursor.getString(cursor.getColumnIndex("name")));
			topic.setDescription(cursor.getString(cursor
					.getColumnIndex("description")));
			topic.setIdiomCount(cursor.getInt(cursor
					.getColumnIndex("idiom_count")));
			topic.setDateCreate(cursor.getString(cursor
					.getColumnIndex("date_create")));
		}
		return topic;
	}

	public ArrayList<Idiom> getListIdiomOfTopic(Topic topic) {
		ArrayList<Idiom> listIdiom = new ArrayList<Idiom>();
		String query = "select * from Idiom where topic_id = "
				+ topic.getTopicId() + " order by random()";
		Cursor cursor = getReadableDatabase().rawQuery(query, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Idiom idiom = new Idiom();
				idiom.setId(cursor.getInt(cursor.getColumnIndex("id")));
				idiom.setName(cursor.getString(cursor.getColumnIndex("name")));
				idiom.setDefinition(cursor.getString(cursor
						.getColumnIndex("definition")));
				idiom.setSample(cursor.getString(cursor
						.getColumnIndex("sample")));
				idiom.setNote(cursor.getString(cursor.getColumnIndex("note")));
				idiom.setDateCreate(cursor.getString(cursor
						.getColumnIndex("date_create")));
				idiom.setTopicId(cursor.getInt(cursor
						.getColumnIndex("topic_id")));
				listIdiom.add(idiom);
			} while (cursor.moveToNext());
		}
		return listIdiom;
	}

	public Idiom getIdiomById(int id) {
		String query = "select * from Idiom where id = " + id;
		Cursor cursor = getReadableDatabase().rawQuery(query, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			Idiom idiom = new Idiom();
			idiom.setId(cursor.getInt(cursor.getColumnIndex("id")));
			idiom.setName(cursor.getString(cursor.getColumnIndex("name")));
			idiom.setDefinition(cursor.getString(cursor
					.getColumnIndex("definition")));
			idiom.setSample(cursor.getString(cursor.getColumnIndex("sample")));
			idiom.setNote(cursor.getString(cursor.getColumnIndex("note")));
			idiom.setDateCreate(cursor.getString(cursor
					.getColumnIndex("date_create")));
			idiom.setTopicId(cursor.getInt(cursor.getColumnIndex("topic_id")));
			return idiom;
		} else {
			return new Idiom();
		}
	}

	public ArrayList<Idiom> getListIdiomOfTopic(int topicId,
			boolean filterAlreadyKnow) {
		ArrayList<Idiom> listIdiom = new ArrayList<Idiom>();
		String query;
		if (!filterAlreadyKnow) {
			query = "select * from Idiom where topic_id = " + topicId
					+ " order by random()";
		} else {
			query = "select * from Idiom where topic_id = " + topicId
					+ " and list_id <> 2" + " order by random()";
		}
		Cursor cursor = getReadableDatabase().rawQuery(query, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Idiom idiom = new Idiom();
				idiom.setId(cursor.getInt(cursor.getColumnIndex("id")));
				idiom.setName(cursor.getString(cursor.getColumnIndex("name")));
				idiom.setDefinition(cursor.getString(cursor
						.getColumnIndex("definition")));
				idiom.setSample(cursor.getString(cursor
						.getColumnIndex("sample")));
				idiom.setNote(cursor.getString(cursor.getColumnIndex("note")));
				idiom.setDateCreate(cursor.getString(cursor
						.getColumnIndex("date_create")));
				idiom.setTopicId(cursor.getInt(cursor
						.getColumnIndex("topic_id")));
				listIdiom.add(idiom);
			} while (cursor.moveToNext());
		}
		return listIdiom;
	}

	public Topic getTopic(int topicId) {
		Topic topic = new Topic();
		String query = "select * from Topic where topic_id = " + topicId;
		Cursor cursor = getReadableDatabase().rawQuery(query, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			topic.setTopicId(cursor.getInt(cursor.getColumnIndex("topic_id")));
			topic.setName(cursor.getString(cursor.getColumnIndex("name")));
			topic.setDescription(cursor.getString(cursor
					.getColumnIndex("description")));
			topic.setIdiomCount(cursor.getInt(cursor
					.getColumnIndex("idiom_count")));
			topic.setDateCreate(cursor.getString(cursor
					.getColumnIndex("date_create")));
		}
		return topic;
	}

	public boolean checkIdiomExist(Idiom idiom) {
		String query = "select * from Idiom where id = " + idiom.getId();
		Cursor cursor = getReadableDatabase().rawQuery(query, null);
		boolean isIdiomExist = cursor.getCount() > 0;
		cursor.close();
		return isIdiomExist;
	}

	public boolean checkTopicExist(Topic topic) {
		String query = "select * from Topic where topic_id = "
				+ topic.getTopicId();
		Cursor cursor = getReadableDatabase().rawQuery(query, null);
		return cursor.getCount() > 0;
	}

	public ArrayList<String> getListWord() {
		ArrayList<String> listWord = new ArrayList<String>();
		String query = "select name from Idiom";
		Cursor cursor = getReadableDatabase().rawQuery(query, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				listWord.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
		return listWord;
	}

	public Idiom searchIdiomByKeyword(String keyword) {
		String query = "select * from Idiom where name = \'" + keyword + "\'";
		Cursor cursor = getReadableDatabase().rawQuery(query, null);
		Idiom idiom = new Idiom();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			idiom.setId(cursor.getInt(cursor.getColumnIndex("id")));
			idiom.setName(cursor.getString(cursor.getColumnIndex("name")));
			idiom.setDefinition(cursor.getString(cursor
					.getColumnIndex("definition")));
			idiom.setSample(cursor.getString(cursor.getColumnIndex("sample")));
			idiom.setNote(cursor.getString(cursor.getColumnIndex("note")));
			idiom.setDateCreate(cursor.getString(cursor
					.getColumnIndex("date_create")));
			idiom.setTopicId(cursor.getInt(cursor.getColumnIndex("topic_id")));
		}
		return idiom;
	}

	public void addIdiomNote(String note, Idiom idiom) {
		ContentValues contentvalues = new ContentValues();
		contentvalues.put("note", note);
		getWritableDatabase().update("Idiom", contentvalues,
				"id = " + idiom.getId(), null);
	}

	// select random idioms for an exam
	public ArrayList<Idiom> getRandomIdioms() {
		ArrayList<Idiom> listIdiom = new ArrayList<Idiom>();
		String query = "select name, definition from Idiom order by random() limit "
				+ Exam.NUMBER_QUESTION;
		Cursor cursor = getReadableDatabase().rawQuery(query, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Idiom idiom = new Idiom();
				idiom.setName(cursor.getString(cursor.getColumnIndex("name")));
				idiom.setDefinition(cursor.getString(cursor
						.getColumnIndex("definition")));
				listIdiom.add(idiom);
			} while (cursor.moveToNext());
		}
		return listIdiom;
	}

	// select incorect choices for idiom
	public String[] selectRandomIncorrectChoice(Idiom idiom) {
		String[] choices = new String[3];
		// Cursor cursor = getReadableDatabase().rawQuery(queryChoice, null);
		Cursor cursor = getReadableDatabase().query("Idiom",
				new String[] { "name" }, "name != ?",
				new String[] { idiom.getName() }, null, null,
				"random() limit 3");
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			int pos = 0;
			do {
				choices[pos] = cursor.getString(0);
				pos++;
			} while (cursor.moveToNext());
		}
		return choices;
	}

	public void addList(List list) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("name", list.getName());
		getWritableDatabase().insert("List", null, contentValues);
	}

	public int getNumberList() {
		String query = "select * from List";
		Cursor cursor = getReadableDatabase().rawQuery(query, null);
		return cursor.getCount();
	}

	// public void addIdiomToList(Idiom idiom, List list) {
	// ContentValues contentValues = new ContentValues();
	// contentValues.put("list_id", list.getListId());
	// getWritableDatabase().update("Idiom", contentValues, "id = ?",
	// new String[] { idiom.getId() + "" });
	// }

	public void addIdiomToList(Idiom idiom, List list) {
		if (!checkIdiomInList(idiom, list)) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("idiom_id", idiom.getId());
			contentValues.put("list_id", list.getListId());
			getWritableDatabase().insert("ListDetail", null, contentValues);
		}
	}

	public ArrayList<List> getAllList() {
		ArrayList<List> list = new ArrayList<List>();
		String query = "select * from List";
		Cursor cursor = getReadableDatabase().rawQuery(query, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				List l = new List();
				l.setListId(cursor.getInt(0));
				l.setName(cursor.getString(1));
				list.add(l);
			} while (cursor.moveToNext());
		}
		return list;
	}

	public void createDefaultLists() {
		if (getNumberList() == 0) {
			addList(new List("Liked"));
			addList(new List("Already know"));
		}
		if (!checkEveryDayIdiomTableHasData()) {
			addEveryDayIdiomList();
		} else {
			if (getDayEveryIdiom() > 7) {
				clearTableEveryDay();
			}
		}
	}

	public boolean checkEveryDayIdiomTableHasData() {
		String query = "select * from EveryDayIdiom";
		Cursor cursor = getReadableDatabase().rawQuery(query, null);
		return cursor.getCount() > 0;
	}

	public void addEveryDayIdiomList() {
		ContentValues contentValues = new ContentValues();
		contentValues.put("ids", "");
		contentValues.put("start_date", DateUtils.getCurrentDate());
		getWritableDatabase().insert("EveryDayIdiom", null, contentValues);
	}

	public int getDayEveryIdiom() {
		String query = "select * from EveryDayIdiom";
		Cursor cursor = getReadableDatabase().rawQuery(query, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String date1 = cursor
					.getString(cursor.getColumnIndex("start_date"));
			String date2 = DateUtils.getCurrentDate();
			return DateUtils.getDateDistance(date1, date2);
		} else {
			return -1;
		}
	}

	public String getIdsEverydayIdiom() {
		String query = "select * from EveryDayIdiom";
		Cursor cursor = getReadableDatabase().rawQuery(query, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String ids = cursor.getString(cursor.getColumnIndex("ids"));
			return ids;
		} else {
			return "";
		}
	}

	public void updateIdsEverydayIdiom(String ids) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("ids", ids);
		getWritableDatabase()
				.update("EveryDayIdiom", contentValues, null, null);
	}

	public void clearTableEveryDay() {
		String query = "delete from EveryDayIdiom";
		getWritableDatabase().execSQL(query);
	}

	public List getListById(int id) {
		String query = "select * from List where list_id = " + id;
		List list = new List();
		Cursor cursor = getReadableDatabase().rawQuery(query, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			list.setListId(cursor.getInt(0));
			list.setName(cursor.getString(1));
		}
		return list;
	}

	public List getNewCreatedList() {
		List list = new List();
		String query = "select * from List";
		Cursor cursor = getReadableDatabase().rawQuery(query, null);
		if (cursor.getCount() > 0) {
			cursor.moveToLast();
			list.setListId(cursor.getInt(0));
			list.setName(cursor.getString(1));
		}
		return list;
	}

	public ArrayList<Idiom> getListIdiomOfList(List list) {
		ArrayList<Idiom> listIdiom = new ArrayList<Idiom>();
		String query = "select Idiom.id, name, definition, sample, note, date_create, topic_id, Idiom.list_id from Idiom, ListDetail where Idiom.id = ListDetail.idiom_id and ListDetail.list_id = "+list.getListId();
//		String query = "select * from Idiom where list_id = "
//				+ list.getListId();
		Cursor cursor = getReadableDatabase().rawQuery(query, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Idiom idiom = new Idiom();
				idiom.setId(cursor.getInt(cursor.getColumnIndex("id")));
				idiom.setName(cursor.getString(cursor.getColumnIndex("name")));
				idiom.setDefinition(cursor.getString(cursor
						.getColumnIndex("definition")));
				idiom.setSample(cursor.getString(cursor
						.getColumnIndex("sample")));
				idiom.setNote(cursor.getString(cursor.getColumnIndex("note")));
				idiom.setDateCreate(cursor.getString(cursor
						.getColumnIndex("date_create")));
				idiom.setTopicId(cursor.getInt(cursor
						.getColumnIndex("topic_id")));
				idiom.setListId(cursor.getInt(cursor.getColumnIndex("list_id")));
				listIdiom.add(idiom);
			} while (cursor.moveToNext());
		}
		return listIdiom;
	}

	public ArrayList<Idiom> getListIdiomOfList(int listId) {
		ArrayList<Idiom> listIdiom = new ArrayList<Idiom>();
		String query = "select Idiom.id, name, definition, sample, note, date_create, topic_id, Idiom.list_id from Idiom, ListDetail where Idiom.id = ListDetail.idiom_id and ListDetail.list_id = "+listId;
//		String query = "select * from Idiom where list_id = " + listId;
		Cursor cursor = getReadableDatabase().rawQuery(query, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Idiom idiom = new Idiom();
				idiom.setId(cursor.getInt(cursor.getColumnIndex("id")));
				idiom.setName(cursor.getString(cursor.getColumnIndex("name")));
				idiom.setDefinition(cursor.getString(cursor
						.getColumnIndex("definition")));
				idiom.setSample(cursor.getString(cursor
						.getColumnIndex("sample")));
				idiom.setNote(cursor.getString(cursor.getColumnIndex("note")));
				idiom.setDateCreate(cursor.getString(cursor
						.getColumnIndex("date_create")));
				idiom.setTopicId(cursor.getInt(cursor
						.getColumnIndex("topic_id")));
				idiom.setListId(cursor.getInt(cursor.getColumnIndex("list_id")));
				listIdiom.add(idiom);
			} while (cursor.moveToNext());
		}
		return listIdiom;
	}

	@SuppressLint("NewApi")
	public ArrayList<Idiom> getListIdiomOfEveryDayIdiom() {
		ArrayList<Idiom> listIdiom = new ArrayList<Idiom>();
		String ids = getIdsEverydayIdiom();
		if (ids.isEmpty()) {
			return listIdiom;
		} else {
			String[] id = ids.split(",");
			for (int i = 0; i < id.length; i++) {
				Idiom idiom = getIdiomById(Integer.parseInt(id[i]));
				listIdiom.add(idiom);
			}
			return listIdiom;
		}
	}

	public boolean checkIdiomInList(Idiom idiom, List list) {
		String query = "select list_id from ListDetail where idiom_id = "
				+ idiom.getId() + " and list_id = " + list.getListId();
		Cursor cursor = getReadableDatabase().rawQuery(query, null);
		return cursor.getCount() > 0;
	}

	@SuppressLint("NewApi")
	public boolean checkIdiomHasNote(Idiom idiom) {
		if (idiom != null) {
			String query = "select note from Idiom where id = " + idiom.getId();
			Cursor cursor = getReadableDatabase().rawQuery(query, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				String note = cursor.getString(0);
				boolean hasNote = ((note == null) || (note.isEmpty()) || (note
						.equalsIgnoreCase("null"))) ? false : true;
				return hasNote;
			} else {
				return false;
			}
		}
		return false;
	}

	public String getNoteOfIdiom(Idiom idiom) {
		String query = "select note from Idiom where id = " + idiom.getId();
		Cursor cursor = getReadableDatabase().rawQuery(query, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String note = cursor.getString(0);
			return note;
		} else {
			return "";
		}
	}

}
