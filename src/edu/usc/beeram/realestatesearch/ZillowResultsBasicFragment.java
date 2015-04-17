package edu.usc.beeram.realestatesearch;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.widget.WebDialog;
import com.facebook.android.FacebookError;
import com.facebook.FacebookException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ZillowResultsBasicFragment extends Fragment implements OnClickListener {
	private Bundle sInstanceState;
	private String fAddress;
	private String fHomedetails;
	private String fbImage;
	private AlertDialog ad;
	// Facebook variables
	private Session.StatusCallback statusCallback = new Session.StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			//updateView();
			if(session == null || !session.isOpened() || !Session.isPublishPermission("publish_stream")){
		    }else{
		    	publishFeedDialog();
		    }
			
		}
	};
	private String fbDesc = "Last Sold Price: ";
	// The massive listView variables
	private String[] names = new String[] {
			"Property Type",
			"Year Built",
			"Lot Size",
			"Finished Area",
			"Bathrooms",
			"Bedrooms",
			"Tax Assessment Year",
			"Tax Assessment",
			"Last Sold Price",
			"Last Sold Date",
			"Zestimate ® Property Estimate as of",
			"30 Days Overall Change",
			"All Time Property Range",
			"Rent Zestimate ® Valuation as of",
			"30 Days Rent Change",
			"All Time Rent Range"	
	};
	private String[] helperData = new String[] {"", "", ""};
	private NameValuePair generatePair(String name, String value){
		final String n = name;
		final String v = value;
		return new NameValuePair() {
			
			@Override
			public String getValue() {
				return v;
			}
			
			@Override
			public String getName() {
				return  n;
			}
		};
	}
	private NameValuePair[] generateNVP(JSONObject data){
		NameValuePair[] npv = new NameValuePair[17];
		try{
			String address = data.getString("street") + " ," + data.getString("city")
					+ " ," + data.getString("state") + "-" + data.getString("zipcode");
			helperData[2] = data.getString("homedetails");
			npv[0] = generatePair(address, "");
			npv[1] = generatePair(names[0], data.getString("useCode"));
			npv[2] = generatePair(names[1], data.getString("yearBuilt"));
			String localStrCheck = data.getString("lotSizeSqFt");
			if(localStrCheck == null || localStrCheck.isEmpty() || 
					localStrCheck.equalsIgnoreCase("0.00") || localStrCheck.equalsIgnoreCase("N/A")){
				npv[3] = generatePair(names[2], localStrCheck);
			}else{
				npv[3] = generatePair(names[2], localStrCheck + " sq. ft.");
			}
			localStrCheck = data.getString("finishedSqFt");
			if(localStrCheck == null || localStrCheck.isEmpty() || 
					localStrCheck.equalsIgnoreCase("0.00") || localStrCheck.equalsIgnoreCase("N/A")){
				npv[4] = generatePair(names[3], localStrCheck);
			}else{
				npv[4] = generatePair(names[3], localStrCheck + " sq. ft.");
			}
			npv[5] = generatePair(names[4], data.getString("bathrooms"));
			npv[6] = generatePair(names[5], data.getString("bedrooms"));
			npv[7] = generatePair(names[6], data.getString("taxAssessmentYear"));
			localStrCheck = data.getString("taxAssessment");
			if(localStrCheck == null || localStrCheck.isEmpty()
					|| localStrCheck.equalsIgnoreCase("N/A")){
				npv[8] = generatePair(names[7], localStrCheck);
			}else{
				npv[8] = generatePair(names[7], "$"+localStrCheck);
			}
			
			String localStringPrice = data.getString("lastSoldPrice");
			localStrCheck = data.getString("lastSoldDate");
			if((localStringPrice == null || localStringPrice.isEmpty() || 
								localStringPrice.equalsIgnoreCase("0.00") || localStringPrice.equalsIgnoreCase("N/A"))  
					&& (localStrCheck == null || localStrCheck.isEmpty() || localStrCheck.equalsIgnoreCase("N/A") ||
								localStrCheck.equalsIgnoreCase("01-jan-1970") || localStrCheck.equalsIgnoreCase("31-dec-1969"))){
				npv[9] = generatePair(names[8], "N/A");
				npv[10] = generatePair(names[9], "N/A");
				fbDesc = fbDesc + "N/A";
			}else{
				npv[9] = generatePair(names[8], "$"+localStringPrice);
				npv[10] = generatePair(names[9], localStrCheck);
				fbDesc = fbDesc + "$"+localStringPrice;
			}
			fbDesc = fbDesc + ", 30 Days Overall Change: ";
			localStrCheck = data.getString("estimateLastUpdate");
			localStringPrice = data.getString("estimateAmount");
			if(localStrCheck == null || localStrCheck.isEmpty() || localStrCheck.equalsIgnoreCase("n/a")
					|| localStrCheck.equalsIgnoreCase("01-jan-1970") || localStrCheck.equalsIgnoreCase("31-dec-1969")){
				if(localStringPrice == null || localStringPrice.isEmpty() || localStringPrice.equalsIgnoreCase("n/a")){
					npv[11] = generatePair(names[10], localStringPrice);
				}else{
					npv[11] = generatePair(names[10], "$"+localStringPrice);
				}
			}else{
				if(localStringPrice == null || localStringPrice.isEmpty() || localStringPrice.equalsIgnoreCase("n/a")){
					npv[11] = generatePair(names[10]+" "+localStrCheck, localStringPrice);
				}else{
					npv[11] = generatePair(names[10]+" "+localStrCheck, "$"+localStringPrice);
				}				
			}
			
			localStrCheck = data.getString("estimateValueChange");
			if(localStrCheck == null || localStrCheck.isEmpty() || localStrCheck.equalsIgnoreCase("0.00")
					|| localStrCheck.equalsIgnoreCase("n/a")){
				npv[12] = generatePair(names[11], "N/A");
				fbDesc = fbDesc + "N/A";
			}else{
				helperData[0] = data.getString("estimateValueChangeSign");
				npv[12] = generatePair(names[11], "$"+localStrCheck);
				fbDesc = fbDesc + data.getString("estimateValueChangeSign") + "$"+localStrCheck;
			}
			localStrCheck = data.getString("estimateValuationRangeLow");
			localStringPrice = data.getString("estimateValuationRangeHigh");
			if(!(localStrCheck == null || localStrCheck.isEmpty() 
					|| localStrCheck.equalsIgnoreCase("n/a"))){
				localStrCheck = "$"+localStrCheck;
			}
			if(!(localStringPrice == null || localStringPrice.isEmpty() 
					|| localStringPrice.equalsIgnoreCase("n/a"))){
				localStringPrice = "$"+localStringPrice;
			}
			npv[13] = generatePair(names[12], localStrCheck+" - "+localStringPrice);
			
			localStrCheck = data.getString("restimateLastUpdate");
			localStringPrice = data.getString("restimateAmount");
			if(localStrCheck == null || localStrCheck.isEmpty() || localStrCheck.equalsIgnoreCase("n/a")
					|| localStrCheck.equalsIgnoreCase("01-jan-1970") || localStrCheck.equalsIgnoreCase("31-dec-1969")){
				if(localStringPrice == null || localStringPrice.isEmpty() || localStringPrice.equalsIgnoreCase("n/a")){
					npv[14] = generatePair(names[13], localStringPrice);
				}else{
					npv[14] = generatePair(names[13], "$"+localStringPrice);
				}
			}else{
				if(localStringPrice == null || localStringPrice.isEmpty() || localStringPrice.equalsIgnoreCase("n/a")){
					npv[14] = generatePair(names[13]+" "+localStrCheck, localStringPrice);
				}else{
					npv[14] = generatePair(names[13]+" "+localStrCheck, "$"+localStringPrice);
				}				
			}
			
			localStrCheck = data.getString("restimateValueChange");
			if(localStrCheck == null || localStrCheck.isEmpty() || localStrCheck.equalsIgnoreCase("0.00")
					|| localStrCheck.equalsIgnoreCase("n/a")){
				npv[15] = generatePair(names[14], "N/A");
			}else{
				helperData[1] = data.getString("restimateValueChangeSign");
				npv[15] = generatePair(names[14], "$"+localStrCheck);
			}
			
			localStrCheck = data.getString("restimateValuationRangeLow");
			localStringPrice = data.getString("restimateValuationRangeHigh");
			if(!(localStrCheck == null || localStrCheck.isEmpty() 
					|| localStrCheck.equalsIgnoreCase("n/a"))){
				localStrCheck = "$"+localStrCheck;
			}
			if(!(localStringPrice == null || localStringPrice.isEmpty() 
					|| localStringPrice.equalsIgnoreCase("n/a"))){
				localStringPrice = "$"+localStringPrice;
			}
			npv[16] = generatePair(names[15], localStrCheck+" - "+localStringPrice);
			
		}catch(Exception e){
			Log.e(ZillowSearchForm.TAG,e.toString());
		}
		return npv;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		sInstanceState = savedInstanceState;
		View rootView = inflater.inflate(R.layout.fragment_zillow_results, container, false);
		TextView termsLink = (TextView) rootView.findViewById(R.id.zillow_terms_link);
		TextView whatZestLink = (TextView) rootView.findViewById(R.id.zillow_zestimate_link);
		addLink(termsLink, "Terms of Use", "http://www.zillow.com/corp/Terms.htm");
		addLink(whatZestLink, ".+", "http://www.zillow.com/wikipages/What-is-a-Zestimate/");
		
		Bundle basicInfoArgs = getArguments();
		JSONObject result = null;
		NameValuePair[] listData = new NameValuePair[] {};
		 String address = "";
		 String homedetails = "";
		try{
			result = new JSONObject(basicInfoArgs.getString("resultJSON"));
			address = result.getString("street") + " ," + result.getString("city")
					+ " ," + result.getString("state") + "-" + result.getString("zipcode");
			homedetails = result.getString("homedetails");
			
		}catch(Exception e){
			Log.e(ZillowSearchForm.TAG, e.toString());
		}
		if(result != null){
			listData = generateNVP(result);
		}
		final BasicInfoListAdapter adapter = new BasicInfoListAdapter(rootView.getContext(), 
				R.layout.basic_info_list_layout, listData, helperData);
		final ListView listView = (ListView) rootView.findViewById(R.id.basic_result_listview);
		listView.setAdapter(adapter);
		
		// Facebook session stuff.
		fAddress = address;
		fHomedetails = homedetails;
		fbImage = basicInfoArgs.getString("fbImage");
		ImageButton fbShareButton = (ImageButton) rootView.findViewById(R.id.fshare_button);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Post To Facebook");
        builder.setPositiveButton("Post Property Details", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			    if(Session.getActiveSession() == null || !Session.getActiveSession().isOpened() || !Session.isPublishPermission("publish_stream")){
			    	openFBSession();
			    }else{
			    	publishFeedDialog();
			    }
			}
		});
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(getActivity().getApplicationContext(), 
                        "Post cancelled", 
                        Toast.LENGTH_SHORT).show();
			}
		});
        ad = builder.create();
		fbShareButton.setOnClickListener(this);
		return rootView;
	}
	public static void addLink(TextView tv, String sPat, final String link){
		// Linkify the required URL's
		Linkify.TransformFilter noAppendLinker = new Linkify.TransformFilter() {		
			@Override
			public String transformUrl(Matcher match, String url) {
				return link;
			}
		};
		Pattern termsPat = Pattern.compile(sPat);
		Linkify.addLinks(tv, termsPat, null, null, noAppendLinker);
		
	}
    @Override
    public void onStart() {
        super.onStart();
        if(Session.getActiveSession() != null){
        	Session.getActiveSession().addCallback(statusCallback);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(Session.getActiveSession() != null){
        	Session.getActiveSession().removeCallback(statusCallback);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(Session.getActiveSession() != null){
        	Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
        }
     }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(Session.getActiveSession() != null){
        	Session session = Session.getActiveSession();
        	Session.saveSession(session, outState);
        }
    }
    private void openFBSession(){
        Session session = Session.getActiveSession();
        final List<String> PERMISSIONS = Arrays.asList("publish_stream");
        final OpenRequest or = new Session.OpenRequest(this);
        or.setPermissions(PERMISSIONS);
        or.setCallback(statusCallback);
        or.setDefaultAudience(SessionDefaultAudience.ONLY_ME);
        or.setLoginBehavior(SessionLoginBehavior.SSO_ONLY);
        if (session == null) {
 
        	if (sInstanceState != null) {
            	session = Session.restoreSession(getActivity(), null, statusCallback, sInstanceState);
            	Log.d(ZillowSearchForm.TAG,"1");
            }

            if (session == null) {
                session = new Session(getActivity());
                Log.d(ZillowSearchForm.TAG,"2");
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForPublish(or);
                Log.d(ZillowSearchForm.TAG,"3");
            }
        }
        
        if (!session.isOpened() && !session.isClosed()) {
            session.openForPublish(or);
            Session.openActiveSession(getActivity(), this, true, statusCallback);
            Log.d(ZillowSearchForm.TAG,"4");
        } else {
            Session.openActiveSession(getActivity(), this, true, statusCallback);
            Log.d(ZillowSearchForm.TAG,"5");
        }

    }
    @Override
	public void onClick(View v) {
        ad.show();
	}
	private void publishFeedDialog() {
	    Bundle params = new Bundle();
	    params.putString("name", fAddress);
	    params.putString("caption", "Property Information from Zillow.com");
	    params.putString("description", fbDesc);
	    params.putString("link", fHomedetails);
	    params.putString("picture", fbImage);
	    WebDialog feedDialog = (
	        new WebDialog.FeedDialogBuilder(getActivity(),
	            Session.getActiveSession(),
	            params))
	        .setOnCompleteListener(new WebDialog.OnCompleteListener() {

	            @Override
	            public void onComplete(Bundle values,
	                FacebookException error) {
	                if (error == null) {
	                    // When the story is posted, echo the success
	                    // and the post Id.
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                        Toast.makeText(getActivity(),
	                            "Posted story, id: "+postId,
	                            Toast.LENGTH_SHORT).show();
	                    } else {
	                        // User clicked the Cancel button
	                        Toast.makeText(getActivity().getApplicationContext(), 
	                            "Publish cancelled", 
	                            Toast.LENGTH_SHORT).show();
	                    }
	                } else if (error instanceof FacebookOperationCanceledException) {
	                    // User clicked the "x" button
	                    Toast.makeText(getActivity().getApplicationContext(), 
	                        "Publish cancelled", 
	                        Toast.LENGTH_SHORT).show();
	                } else {
	                    // Generic, ex: network error
	                    Toast.makeText(getActivity().getApplicationContext(), 
	                        "Error posting story", 
	                        Toast.LENGTH_SHORT).show();
	                }
	            }

	        })
	        .build();
	    //feedDialog.setTitle("Post to Wall");
	    feedDialog.show();
	}

}
