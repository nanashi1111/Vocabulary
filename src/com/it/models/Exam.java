package com.it.models;

import java.util.ArrayList;

import com.it.database.DBHelper;

public class Exam {

	public static final int NUMBER_QUESTION = 10;
	public static final int POINT_PER_QUESTION = 10;

	private ArrayList<Question> listQuestion;
	private DBHelper dbh;
	@SuppressWarnings("unused")
	private Question currentQuestion;
	private int currentPosition;
	private int point;
	@SuppressWarnings("unused")
	private boolean isCompleted;

	public Exam(DBHelper dbh) {
		this.dbh = dbh;
		generateQuestion();
		point = 0;
		isCompleted = false;
		currentPosition = 0;
		currentQuestion = listQuestion.get(currentPosition);
	}

	private void generateQuestion() {
		listQuestion = new ArrayList<Question>();
		ArrayList<Idiom> listIdiom = dbh.getRandomIdioms();
		for (int i = 0; i < listIdiom.size(); i++) {
			Idiom idiom = listIdiom.get(i);
			Question question = new Question(dbh, idiom);
			listQuestion.add(question);
		}
	}

	public ArrayList<Question> getListQuestion() {
		return listQuestion;
	}

	public void setListQuestion(ArrayList<Question> listQuestion) {
		this.listQuestion = listQuestion;
	}

	public DBHelper getDbh() {
		return dbh;
	}

	public void setDbh(DBHelper dbh) {
		this.dbh = dbh;
	}

	public Question getCurrentQuestion() {
		return listQuestion.get(currentPosition);
	}

	public void setCurrentQuestion(Question currentQuestion) {
		this.currentQuestion = currentQuestion;
	}

	public int getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(int currentPosition) {
		this.currentPosition = currentPosition;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public boolean isCompleted() {
		int unansweredCount = 0;
		for (int i = 0; i < listQuestion.size(); i++) {
			if (!listQuestion.get(i).isAnswered()) {
				unansweredCount++;
			}
		}
		return unansweredCount == 0;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

}
