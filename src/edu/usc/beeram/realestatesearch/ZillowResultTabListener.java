package edu.usc.beeram.realestatesearch;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;

@SuppressWarnings("deprecation")
public class ZillowResultTabListener implements TabListener {
	
	Fragment fragment;
	public  ZillowResultTabListener(Fragment fragment) {
		this.fragment = fragment;
	}

	@Override
	public void onTabReselected(Tab t, FragmentTransaction ft) {
		// Do Nothing
	}

	@Override
	public void onTabSelected(Tab t, FragmentTransaction ft) {
		ft.replace(R.id.zillow_results_fragment_container, fragment);
	}

	@Override
	public void onTabUnselected(Tab t, FragmentTransaction ft) {
		ft.remove(fragment);
	}
	
}
