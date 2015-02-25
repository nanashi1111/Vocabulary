package com.it.adapters;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Hashtable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.it.fragments.IdiomFragment;
import com.it.models.Idiom;
import com.it.utils.LogUtils;

public class IdiomPagerAdapter extends FragmentStatePagerAdapter {

	private ArrayList<Idiom> listIdiom;
	protected Hashtable<Integer, WeakReference<IdiomFragment>> fragmentReferences = new Hashtable<Integer, WeakReference<IdiomFragment>>();

	public IdiomPagerAdapter(FragmentManager fm, ArrayList<Idiom> listIdiom) {
		super(fm);
		this.listIdiom = listIdiom;
	}

	@Override
	public Fragment getItem(int position) {
		Idiom idiom = listIdiom.get(position);
		LogUtils.logInfo("Idiom = "+idiom.getName());
		IdiomFragment fragment = new IdiomFragment(idiom);
		fragmentReferences.put(position, new WeakReference<IdiomFragment>(fragment));
		return fragment;
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
	
	public IdiomFragment getFragment(int position) {
	    WeakReference<IdiomFragment> ref = fragmentReferences.get(position);
	    return ref == null ? null : ref.get();
	}

}
