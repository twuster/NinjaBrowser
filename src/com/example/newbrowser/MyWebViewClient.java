//This is a custom WebViewClient that was created to handle changes in the WebView


package com.example.newbrowser;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

public class MyWebViewClient extends WebViewClient{
	public String theUrl;
	//Fragment webViewFragment;
	AutoCompleteTextView urlText;
	WebView ourBrow;
	//ActionBar bar;
	int tabNum;
	//Uri tabUri = null;
	MyFragment fragment;
	

	public MyWebViewClient(int tabNum, MyFragment fragment){
		this.tabNum = tabNum;
		urlText = ActionBarTabsPager.urlText;
		
		this.fragment = fragment;
	}
	
	
	@Override
	public void onPageFinished(WebView view, String url){

		super.onPageFinished(view, url);
		Log.d("MyCustomWebViewClient", "onPageFinished");
		ourBrow = (CustomWebView) view;
		//urlText = (AutoCompleteTextView)view.getRootView().findViewById(R.id.urlText);
		String theWebsite = url;
		
		if(!isValid(theWebsite)){
				theWebsite = (String) fixText(theWebsite);
			}
		ActionBarTabsPager.urlMap.put(tabNum, url);
		//urlView.setText(theWebsite,TextView.BufferType.EDITABLE);
		
		if(ActionBarTabsPager.mViewPager.getCurrentItem() == this.tabNum ){
			urlText.setText(theWebsite,TextView.BufferType.EDITABLE);
		}

		String summary = ourBrow.getTitle();
		String description = ourBrow.getUrl();
		ContentValues values = new ContentValues();
		values.put(TabTable.COLUMN_SUMMARY, summary);
		values.put(TabTable.COLUMN_DESCRIPTION, description);

		Log.d("onPageFinished", "tabNum: "+ tabNum + ", valid: "+ ActionBarTabsPager.validMap.get(tabNum));
		
		/*
		
		if(!ActionBarTabsPager.tabList.contains(tabNum) ){
			Log.d("onPageFinished", "creating tab");
			tabUri = getCR().insert(MyTabContentProvider.CONTENT_URI, values);
			Log.d("onPageFinished", "Creating, " + tabUri);
			ActionBarTabsPager.tabList.add(tabNum);

		}*/
		
		if(fragment.tabUri == null || ActionBarTabsPager.validMap.get(tabNum)==false){
			Log.d("onPageFinished", "creating a new tab");
			fragment.tabUri = fragment.getCR().insert(MyTabContentProvider.CONTENT_URI, values);
			ActionBarTabsPager.validMap.put(tabNum, true);
		}
		else{
			Log.d("onPageFinished", "updating tab");
			Log.d("onPageFinished", "Updating, " + fragment.tabUri);
			try{
				fragment.getCR().update(Uri.parse("content://com.example.newbrowser.MyTabContentProvider/"+ fragment.tabUri), values, null, null);
			}catch(Exception e){
				Log.d("MyFragment", "CANT UPDATE");
			}
		}



	}
	
	//Fixes the url if it doesn't follow the proper format
	public CharSequence fixText(CharSequence invalidText) {
		// TODO Auto-generated method stub
		return "http://" + invalidText;
	}
	
	//Checks if the url is in the proper format
	public boolean isValid(CharSequence text) {
		// TODO Auto-generated method stub
		if(text.length()>7){
			if(text.subSequence(0, 7).equals("http://")){
				Log.d("MainActivity", "text is valid");
				return true;
			}
			else{
				Log.d("MainActivity", "text is invalid");
				return false;
			}
		}
		else{
			Log.d("MainActivity", "text is invalid");
			return false;
		}

	}
	
	
	
}
