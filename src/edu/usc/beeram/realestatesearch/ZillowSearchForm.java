package edu.usc.beeram.realestatesearch;

import java.util.Arrays;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


public class ZillowSearchForm extends ActionBarActivity implements android.view.View.OnClickListener {

	public static final String TAG = "ZillowSearchForm";
	private static boolean firstCall = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zillow_search_form);
        
        // Need this hack to display the ICON,
        // API 21 onwards the theme doesn't show the icon.
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_launcher);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        
        // FInd the search button & set the onclick event listener
        Button searchButton = (Button)findViewById(R.id.zillow_search_button);
        searchButton.setOnClickListener(this);
        
        final EditText streetInput = (EditText) findViewById(R.id.edittext);
        streetInput.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(firstCall){
					return;
				}
				String streetEntry = streetInput.getText().toString();
				TextView streetErrorLabel = (TextView) findViewById(R.id.form_label_error);
				if(streetEntry == null || streetEntry.isEmpty() || streetEntry.trim().equalsIgnoreCase("")){
					streetErrorLabel.setText(R.string.sample_error);
				}else{
					streetErrorLabel.setText(R.string.empty_string);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
        
        final EditText cityInput = (EditText) findViewById(R.id.citytext);
        cityInput.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(firstCall){
					return;
				}
				String cityEntry = cityInput.getText().toString();
				TextView cityErrorLabel = (TextView) findViewById(R.id.city_label_error);
				if(cityEntry == null || cityEntry.isEmpty() || cityEntry.trim().equalsIgnoreCase("") ){
					cityErrorLabel.setText(R.string.sample_error);
				}else{
					cityErrorLabel.setText(R.string.empty_string);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable arg0) {}
		});
        final Spinner stateChoice = (Spinner) findViewById(R.id.state_spinner);
        stateChoice.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(firstCall){
					return;
				}
				String stateEntry = stateChoice.getSelectedItem().toString();
				TextView stateErrorLabel = (TextView) findViewById(R.id.state_error_label);
				if(stateEntry.length() > 2){
					stateErrorLabel.setText(R.string.sample_error);
				}else{
					stateErrorLabel.setText(R.string.empty_string);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
        	
		});

    }    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	// I don't need no menu!
        //getMenuInflater().inflate(R.menu.zillow_search_form, menu);
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


	@Override
	public void onClick(View v) {
		int proceed = 1;
		firstCall = false;
		EditText streetInput = (EditText) findViewById(R.id.edittext);
		String streetEntry = streetInput.getText().toString();
		EditText cityInput = (EditText) findViewById(R.id.citytext);
		String cityEntry = cityInput.getText().toString();
		Spinner stateChoice = (Spinner) findViewById(R.id.state_spinner);
		String stateEntry = stateChoice.getSelectedItem().toString();
		
		TextView stateErrorLabel = (TextView) findViewById(R.id.state_error_label);
		TextView cityErrorLabel = (TextView) findViewById(R.id.city_label_error);
		TextView streetErrorLabel = (TextView) findViewById(R.id.form_label_error);
		//Log.v(TAG, "" + stateEntry);

		// Lets now hide the keyboard
		InputMethodManager keyboardHide = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		keyboardHide.hideSoftInputFromWindow(streetInput.getWindowToken(), 0);

		
		// Error handling on input & setting the appropriate error messages.
		if(stateEntry.length() > 2){
			proceed = 0;
			stateErrorLabel.setText(R.string.sample_error);
		}else{
			stateErrorLabel.setText(R.string.empty_string);
		}
		
		if(cityEntry == null || cityEntry.isEmpty() || cityEntry.trim().equalsIgnoreCase("") ){
			cityErrorLabel.setText(R.string.sample_error);
			proceed = 0;
		}else{
			cityErrorLabel.setText(R.string.empty_string);
		}
		
		if(streetEntry == null || streetEntry.isEmpty() || streetEntry.trim().equalsIgnoreCase("")){
			streetErrorLabel.setText(R.string.sample_error);
			proceed = 0;
		}else{
			streetErrorLabel.setText(R.string.empty_string);
		}
		if(proceed == 1){
			/*
			 * Call an AsyncTask to fetch JSON data from AWS.
			 * > Clear all the fields
			 * > Check if the address was valid
			 * >> Display error for invalid address
			 * >> Call ZillowResults activity for valid address.
			 */
			final String se = streetEntry;
			final String ce = cityEntry;
			final String ste = stateEntry;
			Log.v(TAG,"Everything is fine");
			NameValuePair streetNVP = new NameValuePair() {
				
				@Override
				public String getValue() {
					return se;
				}
				
				@Override
				public String getName() {
					return "streetInput";
				}
			};
			NameValuePair cityNVP = new NameValuePair() {
				
				@Override
				public String getValue() {
					return ce;
				}
				
				@Override
				public String getName() {
					return "cityInput";
				}
			};
			NameValuePair stateNVP = new NameValuePair() {
				
				@Override
				public String getValue() {
					return ste;
				}
				
				@Override
				public String getName() {
					return "stateInput";
				}
			};
			NameValuePair[] params = new NameValuePair[] {streetNVP, cityNVP, stateNVP};
			new GetZillowJSON(this).execute(params);
		}		
	}
	private class GetZillowJSON extends AsyncTask<NameValuePair, Integer, JSONObject>{
		public ZillowSearchForm zillowSearchRef;
		
		public GetZillowJSON(ZillowSearchForm z) {
			zillowSearchRef = z;
		}

		@Override
		protected JSONObject doInBackground(NameValuePair... params) {
			JSONFetcher getjson = new JSONFetcher();
			String url = "http://awsvbcsci571-env.elasticbeanstalk.com/";
			JSONObject jsonObj = getjson.getRequestJSON(url, Arrays.asList(params));
			try{
				JSONObject chart = jsonObj.getJSONObject("chart");
				getjson.getImage(chart.getJSONObject("1year").getString("url"), "1year.gif");
				getjson.getImage(chart.getJSONObject("5years").getString("url"), "5years.gif");
				getjson.getImage(chart.getJSONObject("10years").getString("url"), "10years.gif");
			}catch(Exception e){
				Log.e(ZillowSearchForm.TAG,e.toString());
			}
			Log.v(TAG, jsonObj.toString());
			return jsonObj;
		}
		protected void onPostExecute(JSONObject jsonObj){
			try{
				JSONObject result = jsonObj.getJSONObject("result");
				Log.v(TAG, result.toString());
				String homeDetails = result.getString("homedetails");
				TextView noMatchError = (TextView) findViewById(R.id.no_match_error);
				if(homeDetails == null || homeDetails.isEmpty() || homeDetails.length() < 2){
					noMatchError.setText(R.string.no_match);
				}else{
					
					noMatchError.setText(R.string.empty_string);

					TextView stateErrorLabel = (TextView) findViewById(R.id.state_error_label);
					TextView cityErrorLabel = (TextView) findViewById(R.id.city_label_error);
					TextView streetErrorLabel = (TextView) findViewById(R.id.form_label_error);
					
					/*
					EditText streetInput = (EditText) findViewById(R.id.edittext);
					streetInput.setText(R.string.empty_string);
					EditText cityInput = (EditText) findViewById(R.id.citytext);
					cityInput.setText(R.string.empty_string);
					Spinner stateChoice = (Spinner) findViewById(R.id.state_spinner);
					stateChoice.setSelection(0);
					*/

					streetErrorLabel.setText(R.string.empty_string);
					cityErrorLabel.setText(R.string.empty_string);
					stateErrorLabel.setText(R.string.empty_string);
					
					// Lets download the chart images since we are already in AsyncTask.
					// We will need them later.
					
					Intent resultIntent = new Intent(zillowSearchRef, ZillowResults.class);
					resultIntent.putExtra("edu.usc.beeram.realestatesearch.jsonsearchdata",jsonObj.toString());
					startActivity(resultIntent);
				}
			}catch(JSONException e){
				Log.e(TAG, e.toString());
			}
		}
	}
}
