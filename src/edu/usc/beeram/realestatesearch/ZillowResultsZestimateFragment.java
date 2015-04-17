package edu.usc.beeram.realestatesearch;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

public class ZillowResultsZestimateFragment extends Fragment{
	String[] textSwitchHead = new String[] {"1 year","5 years","10 years"};
	String[] imageSwitch = new String[] {"1year.gif", "5years.gif", "10years.gif"};
	private String addressStr = "";
	private static int textIndex = 0;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		int ltextIndex = textIndex % 3;
		final View rootView = inflater.inflate(R.layout.fragment_zillow_zestimate,container, false);
		TextView termsLink = (TextView) rootView.findViewById(R.id.zillow_terms_link);
		TextView whatZestLink = (TextView) rootView.findViewById(R.id.zillow_zestimate_link);
		addLink(termsLink, "Terms of Use", "http://www.zillow.com/corp/Terms.htm");
		addLink(whatZestLink, ".+", "http://www.zillow.com/wikipages/What-is-a-Zestimate/");
		
		// factories for text-switcher & image switcher
		ViewFactory tFactory = new ViewFactory() {
			@Override
			public View makeView() {
				TextView t = new TextView(rootView.getContext());
				t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
				t.setTextAppearance(rootView.getContext(), R.style.TextAppearance_AppCompat_Headline);
				t.setTextSize(17);
				return t;
			}
		};

		ViewFactory iFactory = new ViewFactory() {		
			@Override
			public View makeView() {
				FrameLayout.LayoutParams l = new LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER);
				ImageView i = new ImageView(rootView.getContext());
				i.setAdjustViewBounds(false);
				i.setScaleType(ImageView.ScaleType.FIT_XY);
				i.setLayoutParams(l);
				return i;
			}
		};
		
		// Default Animations provided by framework
		Animation in = AnimationUtils.loadAnimation(rootView.getContext(), R.anim.abc_fade_in);
		Animation out = AnimationUtils.loadAnimation(rootView.getContext(), R.anim.abc_fade_out);
		// set the text-switcher & imageSwitcher data to begin with.
		Bundle zestimateArgs = getArguments();
		addressStr = zestimateArgs.getString("address");
		
		// text switcher population
		TextView addressText = (TextView) rootView.findViewById(R.id.zestimate_address_text);
		addressText.setText(addressStr);
		
		final TextSwitcher tSwitcher = (TextSwitcher) rootView.findViewById(R.id.zestimate_text_switcher);
		tSwitcher.setFactory(tFactory);
		tSwitcher.setInAnimation(in);
		tSwitcher.setOutAnimation(out);
		tSwitcher.setCurrentText("Historical Zestimate for the past " 
				+ textSwitchHead[ltextIndex]);
		
		// image switcher population
		Bitmap b = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() 
				+ "/" + imageSwitch[ltextIndex]);
		BitmapDrawable bd = new BitmapDrawable(getResources(), b);
		final ImageSwitcher iSwitcher = (ImageSwitcher) rootView.findViewById(R.id.zestimate_image_switcher);
		try{
			iSwitcher.setFactory(iFactory);
			iSwitcher.setInAnimation(in);
			iSwitcher.setOutAnimation(out);
			iSwitcher.setImageDrawable(bd);
		}catch(Exception e){
			Log.e(ZillowSearchForm.TAG,e.toString());
		}
		// Onclick listener implementations:
		View.OnClickListener nextListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				textIndex += 1;
				int reqIndex = textIndex % 3;
				tSwitcher.setText("Historical Zestimate for the past " 
						+ textSwitchHead[reqIndex]);
				iSwitcher.setImageDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeFile(
						Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() +
						"/" + imageSwitch[reqIndex])));
			}
		};
		View.OnClickListener prevListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				textIndex += 2;
				int reqIndex = textIndex % 3;
				tSwitcher.setText("Historical Zestimate for the past " 
						+ textSwitchHead[reqIndex]);
				iSwitcher.setImageDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeFile(
						Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() +
						"/" + imageSwitch[reqIndex])));
			}
		};
		
		
		//setup the onClickListeners for next & previous buttons.
		Button next = (Button) rootView.findViewById(R.id.zestimate_next_button);
		Button prev = (Button) rootView.findViewById(R.id.zestimate_previous_button);
		next.setOnClickListener(nextListener);
		prev.setOnClickListener(prevListener);
		
		
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
}