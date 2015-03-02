package com.it.fragments;

import com.it.models.Exam;
import com.it.models.ICollection;
import com.it.utils.FacebookUtils;
import com.it.utils.PreferenceUtils;
import com.it.vocabulary.HomeActivity;
import com.it.vocabulary.R;
import com.sromku.simple.fb.SimpleFacebook;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

@SuppressLint("NewApi")
public class ResultFragment extends BaseFragment implements OnClickListener {

	private static ResultFragment INSTANCE = new ResultFragment();

	private TextView tvPoint;
	private TextView tvPercent;
	private Button btFinish;
	private Button btNextExam;
	private Button btShare;
	
	private int numberOfQuestion;
	private int numberCorrectAnswer;
	private int percent;
	
	public static ResultFragment getInstance() {
		return INSTANCE;
	}

	private View rootView;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_result, null);
		setupView(rootView);
		getData();
		return rootView;
	}
	
	private void getData(){
		Bundle bundle = getArguments();
		numberOfQuestion = bundle.getInt("number_question");
		numberCorrectAnswer = bundle.getInt("number_correct");
		percent = 100*numberCorrectAnswer/numberOfQuestion;
		String msgPoint = "<font color=\"gray\">Points: </font><font color=\"blue\">"+numberCorrectAnswer+"/"+numberOfQuestion+"</font>";
		tvPoint.setText(Html.fromHtml(msgPoint));
		String msgPercent = "<font color=\"gray\">Percent: </font><font color=\"blue\">"+percent+"%</font>";
		tvPercent.setText(Html.fromHtml(msgPercent));
	}

	@Override
	protected void setupView(View rootView) {
		tvPoint = (TextView) rootView.findViewById(R.id.point);
		tvPercent = (TextView) rootView.findViewById(R.id.percent);
		btFinish = (Button) rootView.findViewById(R.id.finish);
		btFinish.setOnClickListener(this);
		btNextExam = (Button) rootView.findViewById(R.id.next_exam);
		btNextExam.setOnClickListener(this);
		btShare = (Button) rootView.findViewById(R.id.share_score);
		btShare.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.finish:
			showRandomIdiomFragment();
			break;
		case R.id.next_exam:
			createNextExam();
			break;
		case R.id.share_score:
			shareScore();
			break;
		}
	}
	
	/*finish exam*/
	private void showRandomIdiomFragment(){
		((HomeActivity)getActivity()).setRandomFragment(new RandomIdiomFragment());
		Bundle bundle = new Bundle();
		bundle.putInt("from_list_fragment", ICollection.TYPE_TOPIC);
		bundle.putBoolean("random_load", true);
		// randomFragment.setArguments(bundle);
		((HomeActivity)getActivity()).switchContent(R.id.container, ((HomeActivity)getActivity()).getRandomFragment(), bundle);
		HomeActivity.currentState = HomeActivity.STATE_RANDOM;
		((HomeActivity)getActivity()).setBackground();
	}
	
	/*next exam*/
	private void createNextExam(){
		((HomeActivity)getActivity()).setExamFragment(ExamFragment.getInstance());
		((HomeActivity)getActivity()).switchContent(R.id.container, ((HomeActivity)getActivity()).getExamFragment(), null);
		HomeActivity.currentState = HomeActivity.STATE_EXAM;
		((HomeActivity)getActivity()).setBackground();
	}
	
	/*share score*/
	private void shareScore(){
		SimpleFacebook mSimpleFacebook = ((HomeActivity) getActivity())
				.getSimpleFacebookInstance();

		// check login
		if (!PreferenceUtils.isLoggedInFacebook(getActivity())) {
			FacebookUtils.login(getActivity(), mSimpleFacebook,
					FacebookUtils.ACTION_POST_SCORE_AFTER_LOGIN,
					new Integer(numberCorrectAnswer*Exam.POINT_PER_QUESTION));
		}
		// share
		else {
			FacebookUtils.shareScore(mSimpleFacebook, new Integer(numberCorrectAnswer*Exam.POINT_PER_QUESTION), getActivity());
		}
	}

}
