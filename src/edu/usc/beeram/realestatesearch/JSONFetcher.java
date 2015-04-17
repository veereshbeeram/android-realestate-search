package edu.usc.beeram.realestatesearch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.Environment;
import android.util.Log;

public class JSONFetcher {
	/*
	 * exposes a get interface.
	 * Pass URL & params, will give you valid JSON if returned.
	 */
	public JSONObject getRequestJSON(String url, List<NameValuePair> params){
		JSONObject jsonObj = null;
		try{
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpEntity hEntity = null;
			HttpResponse response = null;
			if(params != null){
				String p = URLEncodedUtils.format(params, "utf-8");
				url += "?" + p;
			}
			HttpGet hget = new HttpGet(url);
			response = httpClient.execute(hget);
			
			hEntity = response.getEntity();
			String jsonStr = EntityUtils.toString(hEntity);
			Log.v("ZillowJSONFetch",jsonStr);
			jsonObj = new JSONObject(jsonStr);
			
		}catch(Exception e){
			Log.e("ZillowJSONFetch",e.toString());
			return null;
		}
		return jsonObj;
	}
	
	/*
	 * Here goes the abuse of the class to download images too!
	 */
	public void getImage(String imageUrl, String imageName){
		try{
			String sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
			Log.v(ZillowSearchForm.TAG,"download folder: "+ sdCard);
			File tfolder = new File(sdCard);
			
			File tfile = new File(tfolder, imageName);
			if(tfile.exists()){
				Log.v(ZillowSearchForm.TAG,"Delete existing image "+imageName);
				tfile.delete();
			}
			if(imageUrl == null || imageUrl.isEmpty()){
				Log.v(ZillowSearchForm.TAG,"Empty image url, returning");
				return;
			}
			
			URL iurl = new URL(imageUrl);			
			URLConnection uconn = iurl.openConnection();
			InputStream inImageStream = null;
			HttpURLConnection httpConn = (HttpURLConnection) uconn;
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			
			if(httpConn.getResponseCode() == HttpURLConnection.HTTP_OK){
				inImageStream = httpConn.getInputStream();
				FileOutputStream outImageStream = new FileOutputStream(tfile);
				byte[] buffer = new byte[1024];
				int bufferLength = 0;
				while((bufferLength = inImageStream.read(buffer)) > 0 ){
					outImageStream.write(buffer, 0, bufferLength);
				}
				outImageStream.close();
			}
			if(tfile.exists()){
				Log.v(ZillowSearchForm.TAG,"File " + imageName + " downloaded at: "+ tfile.getPath()+", Size:"+tfile.length());
			}
			
		}catch(Exception e){
			Log.e(ZillowSearchForm.TAG, e.toString());
		}
	}
}
