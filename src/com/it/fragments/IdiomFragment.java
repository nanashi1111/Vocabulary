package com.it.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.easyandroidanimations.library.FlipHorizontalToAnimation;
import com.it.models.Idiom;
import com.it.utils.LogUtils;
import com.it.vocabulary.R;

@SuppressLint("NewApi")
public class IdiomFragment extends BaseFragment implements OnClickListener {

	private Idiom idiom;
	private TextView tvIdiom, tvDefinition;
	private LinearLayout llIdiom, llDefinition;
	private View rootView;

	public IdiomFragment(Idiom idiom) {
		this.idiom = idiom;
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_idiom, null);
		setupView(rootView);
		return rootView;
	}

	@Override
	protected void setupView(View rootView) {
		// set up textview to show idiom
		tvIdiom = (TextView) rootView.findViewById(R.id.idiom_name);
		tvIdiom.setText(idiom.getName());
		// set up textview to show definition
		tvDefinition = (TextView) rootView.findViewById(R.id.idiom_definition);
		tvDefinition.setText(idiom.getDefinition());
		// set up broad to show idiom
		llIdiom = (LinearLayout) rootView.findViewById(R.id.idiom_name_layout);
		llIdiom.setOnClickListener(this);
		// set up broad to show definition
		llDefinition = (LinearLayout) rootView
				.findViewById(R.id.idiom_definition_layout);
		llDefinition.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.idiom_definition_layout:
			new FlipHorizontalToAnimation(llDefinition).setFlipToView(llIdiom)
					.setInterpolator(new LinearInterpolator()).animate();
			break;
		case R.id.idiom_name_layout:
			new FlipHorizontalToAnimation(llIdiom).setFlipToView(llDefinition)
					.setInterpolator(new LinearInterpolator()).animate();
			break;
		}
	}

	public void showView(Idiom idiom) {
		if (tvIdiom != null) {
			tvIdiom.setText(idiom.getName());
			tvDefinition.setText(idiom.getDefinition());
		} else {
			LogUtils.logInfo("null widgets");
		}
	}

	public Idiom getIdiom() {
		LogUtils.logInfo(idiom.getName() + " - " + idiom.getDefinition());
		return idiom;
	}

	public void setIdiom(Idiom idiom) {
		this.idiom = idiom;
	}

}
