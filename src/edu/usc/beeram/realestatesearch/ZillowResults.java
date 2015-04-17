package edu.usc.beeram.realestatesearch;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

@SuppressWarnings("deprecation")
public class ZillowResults extends ActionBarActivity {
	Fragment basicInfoFragment = new ZillowResultsBasicFragment();
	Fragment zestimateFragment = new ZillowResultsZestimateFragment();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Lets read the intent. We must have got one from search results
		Intent jsonIntent = getIntent();
		String jsonStr = jsonIntent.getStringExtra("edu.usc.beeram.realestatesearch.jsonsearchdata");
		JSONObject jsonObj = null;
		
		String addressStr = "";
		Bundle zestimateArgs = new Bundle();
		Bundle basicInfoArgs = new Bundle();
		try{
			if(jsonStr != null){
				Log.v(ZillowSearchForm.TAG,"Got the string");
				jsonObj = new JSONObject(jsonStr);
				JSONObject result = jsonObj.getJSONObject("result");
				addressStr = result.getString("street") + ", " + result.getString("city")
						+ ", " + result.getString("state") + "-" + result.getString("zipcode");
				zestimateArgs.putString("address", addressStr);
				basicInfoArgs.putString("resultJSON", result.toString());
				basicInfoArgs.putString("fbImage", jsonObj.getJSONObject("chart").getJSONObject("1year").getString("url"));
			}
		}catch(JSONException e){
			Log.e(ZillowSearchForm.TAG,e.toString());
		}
		
		// Setup the fragments.
		zestimateFragment.setArguments(zestimateArgs);
		basicInfoFragment.setArguments(basicInfoArgs);
		setContentView(R.layout.activity_zillow_results);

		// Need this hack to display the ICON,
        // API 21 onwards the theme doesn't show the icon.
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_launcher);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        
        // creating navigation tabs now.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        Tab basicInfoTab = actionBar.newTab().setText("Basic Info");
        Tab zestimateTab = actionBar.newTab().setText("Historical Zestimate");
        
        basicInfoTab.setTabListener(new ZillowResultTabListener(basicInfoFragment));
        zestimateTab.setTabListener(new ZillowResultTabListener(zestimateFragment));
        
        actionBar.addTab(basicInfoTab);
        actionBar.addTab(zestimateTab);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.zillow_results, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
