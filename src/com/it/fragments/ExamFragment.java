package com.it.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.it.models.Exam;
import com.it.models.ICollection;
import com.it.models.Question;
import com.it.utils.LogUtils;
import com.it.vocabulary.BaseActivity;
import com.it.vocabulary.HomeActivity;
import com.it.vocabulary.R;

@SuppressLint("NewApi")
public class ExamFragment extends BaseFragment implements OnClickListener {

	private static ExamFragment INSTANCE = new ExamFragment();
	private Exam exam;
	private TextView tvWord;
	private TextView tvPoint;
	private TextView tvQuestionCount;
	private Button[] btChoices;
	private Button btNext;
	private Button btPrev;

	public static ExamFragment getInstance() {
		return INSTANCE;
	}

	private View rootView;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_exam, null);
		setupView(rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		generateExam();

	}

	@Override
	protected void setupView(View rootView) {
		tvWord = (TextView) rootView.findViewById(R.id.word);
		tvPoint = (TextView) rootView.findViewById(R.id.point);
		tvQuestionCount = (TextView) rootView.findViewById(R.id.question_count);
		btChoices = new Button[4];
		btChoices[0] = (Button) rootView.findViewById(R.id.choice_1);
		btChoices[0].setOnClickListener(this);
		btChoices[1] = (Button) rootView.findViewById(R.id.choice_2);
		btChoices[1].setOnClickListener(this);
		btChoices[2] = (Button) rootView.findViewById(R.id.choice_3);
		btChoices[2].setOnClickListener(this);
		btChoices[3] = (Button) rootView.findViewById(R.id.choice_4);
		btChoices[3].setOnClickListener(this);
		btPrev = (Button) rootView.findViewById(R.id.prev);
		btPrev.setOnClickListener(this);
		btNext = (Button) rootView.findViewById(R.id.next);
		btNext.setOnClickListener(this);
	}

	private void generateExam() {

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				exam = new Exam(((BaseActivity) getActivity()).getDBHelper());
				return null;
			}

			@Override
			protected void onPreExecute() {
				((BaseActivity) getActivity()).showProgressDialog("",
						"Genereting exam...");
			}

			@Override
			protected void onPostExecute(Void result) {
				showView(exam.getCurrentQuestion());
				((BaseActivity) getActivity()).hideProgressDialog();
			}

		}.execute();
	}

	private void showView(Question question) {
		tvWord.setText(question.getWord());
		for (int i = 0; i < btChoices.length; i++) {
			btChoices[i].setText(question.getChoices()[i]);
			if (question.getWord().lastIndexOf(question.getChoices()[i]) > 0) {
				String word = tvWord.getText().toString();
				word = word.replace(question.getChoices()[i], "***");
				tvWord.setText(word);

			}
		}
		tvQuestionCount.setText((exam.getCurrentPosition() + 1) + "/"
				+ Exam.NUMBER_QUESTION);
		tvPoint.setText("Points: " + exam.getPoint());
		// log info
		LogUtils.logInfo("Text get = " + tvWord.getText().toString());
		LogUtils.logInfo("Question = " + question.getWord());
		LogUtils.logInfo("True answer = " + question.getCorrectChoice());
		LogUtils.logInfo("Position = "
				+ question.getWord().lastIndexOf(question.getCorrectChoice()));
	}

	private void answerQuestion(int questionPosition, int choice) {
		// if not answerd yet
		if (!exam.getListQuestion().get(questionPosition).isAnswered()) {
			exam.getListQuestion().get(questionPosition).setAnswered(true);
			exam.getListQuestion().get(questionPosition)
					.setSelectedChoice(btChoices[choice].getText().toString());
			exam.getListQuestion().get(questionPosition)
					.setSelectedChoicePosition(choice);
			// true then add 10 points, hilight answer and update point
			if (exam.getListQuestion().get(questionPosition).isCorrect()) {
				exam.setPoint(exam.getPoint() + 10);
				btChoices[choice].setBackground(getActivity().getResources()
						.getDrawable(R.drawable.lava_quiz_green));
				tvPoint.setText("Points: " + exam.getPoint());
				// hilight answer
			} else {
				btChoices[choice].setBackground(getActivity().getResources()
						.getDrawable(R.drawable.lava_quiz_red));
				btChoices[exam.getListQuestion().get(questionPosition)
						.getCorrectChoicePosition()]
						.setBackground(getActivity().getResources()
								.getDrawable(R.drawable.lava_quiz_green));
			}
			// check complete
			checkComplete();
		}
		// if answered
		else {
			((BaseActivity) getActivity())
					.makeToast("You answered this question");
		}
	}

	private void resetButtons(int questionPosition) {
		// if not answerd yet, white all
		if (!exam.getListQuestion().get(questionPosition).isAnswered()) {
			for (int i = 0; i < btChoices.length; i++) {
				btChoices[i].setBackground(getActivity().getResources()
						.getDrawable(R.drawable.choice_selector));
			}
		}
		// if answered, hilight selected answer
		else {
			int selectedPosition = exam.getListQuestion().get(questionPosition)
					.getSelectedChoicePosition();
			int correctPosition = exam.getListQuestion().get(questionPosition)
					.getCorrectChoicePosition();
			for (int i = 0; i < btChoices.length; i++) {
				if (i == selectedPosition) {
					btChoices[i].setBackground(getActivity().getResources()
							.getDrawable(R.drawable.lava_quiz_red));
				}
				if (i == correctPosition) {
					btChoices[i].setBackground(getActivity().getResources()
							.getDrawable(R.drawable.lava_quiz_green));
				}
				if ((i != selectedPosition) && (i != correctPosition)) {
					btChoices[i].setBackground(getActivity().getResources()
							.getDrawable(R.drawable.lava_quiz_neutral));
				}
			}
		}
	}

	private void checkComplete() {
		// if complete show result dialog
		if (exam.isCompleted()) {
			// new AlertDialog.Builder(getActivity())
			// .setMessage(
			// "Result: " + exam.getPoint()
			// + " points. Do you want to share?")
			// .setTitle("Result")
			// .setNegativeButton("No",
			// new DialogInterface.OnClickListener() {
			//
			// @Override
			// public void onClick(DialogInterface dialog,
			// int which) {
			//
			// }
			// })
			// .setPositiveButton("Share!!!",
			// new DialogInterface.OnClickListener() {
			//
			// @Override
			// public void onClick(DialogInterface dialog,
			// int which) {
			// SimpleFacebook mSimpleFacebook = ((HomeActivity) getActivity())
			// .getSimpleFacebookInstance();
			//
			// // check login
			// if (!PreferenceUtils
			// .isLoggedInFacebook(getActivity())) {
			// FacebookUtils
			// .login(getActivity(),
			// mSimpleFacebook,
			// FacebookUtils.ACTION_POST_SCORE_AFTER_LOGIN,
			// exam.getPoint() + "");
			// }
			// // share
			// else
			// FacebookUtils.shareScore(
			// mSimpleFacebook,
			// exam.getPoint());
			// }
			// }).setCancelable(true).show();
			Bundle bundle = new Bundle();
			bundle.putInt("number_question", Exam.NUMBER_QUESTION);
			bundle.putInt("number_correct", exam.getPoint()
					/ Exam.POINT_PER_QUESTION);
			ResultFragment resultFragment = new ResultFragment();
			resultFragment.setArguments(bundle);
			((BaseActivity) getActivity()).switchContent(R.id.container,
					resultFragment, bundle);
			HomeActivity.setCurrentState(HomeActivity.STATE_RESULT);
		}
	}

	public void showDialogCancelExam(final int screenToGo,
			final boolean randomScreenFocusSearchBar) {
		new AlertDialog.Builder(getActivity())
				.setMessage(
						"You are doing a test, do you want to cancel the test? <Dung ngu phap ko day :v>")
				.setTitle("Exam")
				.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				})
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (screenToGo) {

								/* show random idiom screen */
								case HomeActivity.STATE_RANDOM:
									((HomeActivity) getActivity())
											.setRandomFragment(new RandomIdiomFragment(
													randomScreenFocusSearchBar));
									Bundle bundle = new Bundle();
									bundle.putInt("from_list_fragment",
											ICollection.TYPE_TOPIC);
									bundle.putBoolean("random_load", true);
									// randomFragment.setArguments(bundle);
									((HomeActivity) getActivity())
											.switchContent(
													R.id.container,
													((HomeActivity) getActivity())
															.getRandomFragment(),
													bundle);
									HomeActivity.currentState = HomeActivity.STATE_RANDOM;
									((HomeActivity) getActivity())
											.setBackground();
									break;

								/* show list screen */
								case HomeActivity.STATE_LISTS:
									((HomeActivity) getActivity())
											.setListFragment(ListFragments
													.getInstance());
									// take collection info to show
									// in listFragment
									int collectionType = ((HomeActivity) getActivity())
											.getRandomFragment()
											.getCurrentCollectionType();
									Bundle bundleList = new Bundle();
									bundleList.putInt("collection_type",
											collectionType);
									if (collectionType == ICollection.TYPE_TOPIC) {
										bundleList.putInt("collection_id",
												((HomeActivity) getActivity())
														.getRandomFragment()
														.getCurrentTopicID());
									} else {
										bundleList.putInt("collection_id",
												((HomeActivity) getActivity())
														.getRandomFragment()
														.getCurrentListID());
									}
									((HomeActivity) getActivity())
											.switchContent(
													R.id.container,
													((HomeActivity) getActivity())
															.getListFragment(),
													bundleList);

									HomeActivity.currentState = HomeActivity.STATE_LISTS;
									((HomeActivity) getActivity())
											.setBackground();
									break;
								}
							}
						}).setCancelable(true).show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.next:
			//check answerd
			if(!exam.getCurrentQuestion().isAnswered()){
				((BaseActivity) getActivity())
				.makeToast("You must answer this question first");
				return;
			}
			// check can next
			if (exam.getCurrentPosition() == (Exam.NUMBER_QUESTION - 1)) {
				((BaseActivity) getActivity())
						.makeToast("This is last question");
			} else {
				exam.setCurrentPosition(exam.getCurrentPosition() + 1);
				resetButtons(exam.getCurrentPosition());
				showView(exam.getCurrentQuestion());
			}
			break;
		case R.id.prev:
			// check can prev
			if (exam.getCurrentPosition() == 0) {
				((BaseActivity) getActivity())
						.makeToast("This is first question");
			} else {
				exam.setCurrentPosition(exam.getCurrentPosition() - 1);
				resetButtons(exam.getCurrentPosition());
				showView(exam.getCurrentQuestion());
			}
			break;
		case R.id.choice_1:
			answerQuestion(exam.getCurrentPosition(), Question.CHOICE_1);

			break;
		case R.id.choice_2:
			answerQuestion(exam.getCurrentPosition(), Question.CHOICE_2);
			break;
		case R.id.choice_3:
			answerQuestion(exam.getCurrentPosition(), Question.CHOICE_3);
			break;
		case R.id.choice_4:
			answerQuestion(exam.getCurrentPosition(), Question.CHOICE_4);
			break;
		}
	}

}
