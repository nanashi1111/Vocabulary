package com.it.adapters;

import java.util.ArrayList;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.it.fragments.IdiomFragment;
import com.it.models.Idiom;
import com.it.utils.LogUtils;

public class IdiomPagerAdapter extends FragmentStatePagerAdapter {

	private ArrayList<Idiom> listIdiom;

	public IdiomPagerAdapter(FragmentManager fm, ArrayList<Idiom> listIdiom) {
		super(fm);
		this.listIdiom = listIdiom;
	}

	@Override
	public Fragment getItem(int arg0) {
		Idiom idiom = listIdiom.get(arg0);
		LogUtils.logInfo("Idiom = "+idiom.getName());
		return new IdiomFragment(idiom);
	}

	@Override
	public int getCount() {
		return listIdiom.size();
	}

//	@Override
//	public int getItemPosition(Object object) {
//		IdiomFragment fragment = (IdiomFragment) object;
//		Idiom idiom = fragment.getIdiom();
//		int position = listIdiom.indexOf(idiom);
//		if (position >= 0) {
//			return position;
//		} else {
//			return PagerAdapter.POSITION_NONE;
//		}
//	}
	
	public void setData(ArrayList<Idiom> listIdiom){
		this.listIdiom = listIdiom;
	}

}
