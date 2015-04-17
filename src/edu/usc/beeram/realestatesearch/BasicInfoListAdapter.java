package edu.usc.beeram.realestatesearch;

import org.apache.http.NameValuePair;

import com.facebook.internal.ImageDownloader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BasicInfoListAdapter extends ArrayAdapter<NameValuePair> {
	private final Context context;
	private final int resource;
	private final NameValuePair[] data;
	private final String[] helperData;
	public BasicInfoListAdapter(Context c, int r,
			NameValuePair[] objects, String[] h) {
		super(c, r, objects);
		this.context = c;
		this.resource = r;
		this.data = objects;
		this.helperData = h;
	}
	static class ViewHolder{
		private TextView leftTextView;
		private TextView rightTextView;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		ViewHolder lViewHolder = null;
		if(convertView == null){
			lViewHolder = new ViewHolder();
			LayoutInflater lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = lInflater.inflate(resource, parent, false);
			lViewHolder.leftTextView = (TextView) convertView.findViewById(R.id.list_text_1);
			lViewHolder.rightTextView = (TextView) convertView.findViewById(R.id.list_text_2);
			convertView.setTag(lViewHolder);
		}else{
			lViewHolder = (ViewHolder) convertView.getTag();
		}
		lViewHolder.leftTextView.setText(data[position].getName());
		lViewHolder.rightTextView.setText(data[position].getValue());
		
		//Overrides for specific positions
		if(position == 0){
			// huge max width so that we can have the string in single line.
			//lViewHolder.leftTextView.setMaxWidth(ViewGroup.LayoutParams.MATCH_PARENT);
			lViewHolder.leftTextView.setMaxWidth(1000);
			// Linkify the string.
			ZillowResultsBasicFragment.addLink(lViewHolder.leftTextView, data[position].getName(), helperData[2]);
		}
		if(position == 12 || position == 15){
			Drawable down = lViewHolder.rightTextView.getContext().getResources().getDrawable(R.drawable.down_r_png);
			Drawable up = lViewHolder.rightTextView.getContext().getResources().getDrawable(R.drawable.up_g_png);
			
			
			down.setBounds(0, 0, down.getIntrinsicWidth(), down.getIntrinsicHeight());
			up.setBounds(0,0,up.getIntrinsicWidth(),up.getIntrinsicHeight());
			
			String sign = "";
			if(position == 12){
				sign = helperData[0];
			}else{
				sign = helperData[1];
			}
			if(sign.equalsIgnoreCase("+")){
				lViewHolder.rightTextView.setCompoundDrawables(up, null, null, null);
				//lViewHolder.rightTextView.setText(helperData[0]+data[position].getValue());
			}
			if(sign.equalsIgnoreCase("-")){
				lViewHolder.rightTextView.setCompoundDrawables(down, null, null, null);
				//lViewHolder.rightTextView.setText(helperData[0]+data[position].getValue());
			}
		}else{
			lViewHolder.rightTextView.setCompoundDrawables(null, null, null, null);
		}
		if((position % 2) == 1){
			convertView.setBackgroundColor(0xFFF0F5F5);
		}else{
			convertView.setBackgroundColor(0xFFFFFFFF);
		}
		return convertView;
	}

}
