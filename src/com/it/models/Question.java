package com.it.models;

import java.util.Random;

import com.it.database.DBHelper;

public class Question {
	
	public static final int CHOICE_1 = 0;
	public static final int CHOICE_2 = 1;
	public static final int CHOICE_3 = 2;
	public static final int CHOICE_4 = 3;
	
	private String word;
	private String[] choices;
	private String correctChoice;
	private int correctChoicePosition;
	private String selectedChoice;
	private int selectedChoicePosition;
	private Idiom idiom;
	private DBHelper dbh;
	private boolean answered;

	public Question(DBHelper dbh, Idiom idiom) {
		this.dbh = dbh;
		this.idiom = idiom;
		word = idiom.getDefinition();
		correctChoice = idiom.getName();
		generateChoices();
		answered = false;
	}

	private void generateChoices() {
		choices = new String[4];
		// random correct choice position
		correctChoicePosition = new Random().nextInt(4);
		choices[correctChoicePosition] = correctChoice;
		// generate incorrect choices
		String[] incorrectChoices = dbh.selectRandomIncorrectChoice(idiom);
		int count = 0;
		for (int i = 0; i < choices.length; i++) {
			if (choices[i] == null) {
				choices[i] = incorrectChoices[count];
				count++;
			}
		}
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String[] getChoices() {
		return choices;
	}

	public void setChoices(String[] choices) {
		this.choices = choices;
	}

	public String getCorrectChoice() {
		return correctChoice;
	}

	public void setCorrectChoice(String correctChoice) {
		this.correctChoice = correctChoice;
	}

	public String getSelectedChoice() {
		return selectedChoice;
	}

	public void setSelectedChoice(String selectedChoice) {
		this.selectedChoice = selectedChoice;
	}

	public Idiom getIdiom() {
		return idiom;
	}

	public void setIdiom(Idiom idiom) {
		this.idiom = idiom;
	}

	public DBHelper getDbh() {
		return dbh;
	}

	public void setDbh(DBHelper dbh) {
		this.dbh = dbh;
	}

	public boolean isCorrect() {
		return correctChoice.trim().equals(selectedChoice.trim());
	}

	public boolean isAnswered() {
		return answered;
	}

	public void setAnswered(boolean answered) {
		this.answered = answered;
	}

	public int getCorrectChoicePosition() {
		return correctChoicePosition;
	}

	public void setCorrectChoicePosition(int correctChoicePosition) {
		this.correctChoicePosition = correctChoicePosition;
	}

	public int getSelectedChoicePosition() {
		return selectedChoicePosition;
	}

	public void setSelectedChoicePosition(int selectedChoicePosition) {
		this.selectedChoicePosition = selectedChoicePosition;
	}
	
	
	

}
