//This Fragment contains the CustomWebView along with all the buttons.
//It is attached to the tab when the tab is created and detached when the tab is hidden.
//When the tab is deleted, the fragment is destroyed.

package com.example.newbrowser;



import java.util.HashMap;

import com.example.newbrowser.R;

import android.annotation.SuppressLint;
import android.app.ActionBar.Tab;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnLongClickListener;

public class MyFragment extends Fragment implements OnClickListener, AutoCompleteTextView.Validator, OnGestureListener{


	public ActionBar bar; //the action bar
	CustomWebView ourBrow; //our CustomWebView instance
	Fragment CustomWebViewFragment; 
	//AutoCompleteTextView urlView;  // the textview where the user enters the url
	Tab currTab;  //currentTab that the user is seeing
	MyWebViewClient wBClient;
	int originalWidth;
	float originalX;


	private static final int REQUEST_CODE = 10;

	private GestureDetector gDetector;

	protected Uri tabUri =null;


	AutoCompleteTextView urlText;
	protected int tabNum = -1;
	ProgressBar pBar;




	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		//super.onCreateView(inflater, container, savedInstanceState);

		if(tabNum ==-1){
			tabNum = ActionBarTabsPager.mTabsAdapter.getCount()-1; 

			ActionBarTabsPager.urlMap.put(tabNum, "http://www.google.com");
		}

		if(!ActionBarTabsPager.tabList.contains(tabNum) ){
			ActionBarTabsPager.tabList.add(tabNum);
		//	ActionBarTabsPager.validMap.put(tabNum, true);

		}


		View view = inflater.inflate(R.layout.main, container, false);

		gDetector = new GestureDetector(getActivity().getBaseContext(),MyFragment.this);

		//Getting the corresponding ActionBar
		final Activity MyActivity = getActivity();
		final ProgressBar pBar = (ProgressBar)view.findViewById(R.id.progressBar);



		final ActionBar bar = getActivity().getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
		currTab = bar.getSelectedTab();


		//Get the view associated with the current tab
		//View tabView = currTab.getCustomView();

		//Getting the delete button associated with the current tab
		//ImageButton delete = (ImageButton) tabView.findViewById(R.id.delete_tab);

		//Finding all the buttons
		/*final ImageButton back = (ImageButton) view.findViewById(R.id.bBack);
		
		final ImageButton forward = (ImageButton) view.findViewById(R.id.bForward);
		final ImageButton refresh = (ImageButton) view.findViewById(R.id.bRefresh);
		final ImageButton clear = (ImageButton) view.findViewById(R.id.delete_text);
		final ImageButton bookmark = (ImageButton) view.findViewById(R.id.bookmark);
		 */

		//getActivity().openOptionsMenu();

		//Configuring the url TextView
		//urlView = (AutoCompleteTextView) view.findViewById(R.id.urlText);
		urlText = (AutoCompleteTextView) ActionBarTabsPager.urlText;


		//urlView.setImeActionLabel("Go", KeyEvent.KEYCODE_ENTER);
		//urlText.setImeActionLabel("Go", KeyEvent.KEYCODE_ENTER);
		//Allows users to press "Go" to go to website
		/*urlView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				loadPage();
				//setTabTitle();
				return false;
			}
		});*/
		/*urlText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				loadPage();
				return false;
			}
		});*/
		/*
		//Allows for the TextView to expand when clicked, and return to normal when it loses focus
		urlView.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus == true){
					back.setVisibility(View.INVISIBLE);
					forward.setVisibility(View.INVISIBLE);
					refresh.setVisibility(View.INVISIBLE);
					clear.setVisibility(View.VISIBLE);
					bookmark.setVisibility(View.VISIBLE);


					originalWidth = v.getWidth();
					originalX = v.getX();
					v.setX(0);
					((AutoCompleteTextView) v).setWidth(LayoutParams.MATCH_PARENT);
					v.bringToFront();


				}
				else{
					((AutoCompleteTextView)v).setWidth(originalWidth);
					v.setX(originalX);
					back.setVisibility(View.VISIBLE);
					forward.setVisibility(View.VISIBLE);
					refresh.setVisibility(View.VISIBLE);
					clear.setVisibility(View.INVISIBLE);
					bookmark.setVisibility(View.INVISIBLE);
				}
			}
		});*/

		urlText.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(v.hasFocus()==true){
					
					ActionBarTabsPager.tabButton.setVisibility(View.GONE);
					//ActionBarTabsPager.optionsButton.setVisibility(View.GONE);
					
					urlText.setVisibility(View.VISIBLE);
					ActionBarTabsPager.clearButton.setVisibility(View.VISIBLE);
					ActionBarTabsPager.clearButton.bringToFront();
					
					ActionBar bar = getActivity().getActionBar();
					bar.setDisplayShowHomeEnabled(false);

					//v.bringToFront();
					//v.setPadding(0, 7 ,10, 0);
					int padding_in_dp = 28; 
				    final float scale = getResources().getDisplayMetrics().density;
				    int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
				    Log.d("onFocusChange", "" + padding_in_px);
					urlText.setPadding(0, 0, padding_in_px, 0);


					//originalWidth = v.getWidth();
					//originalX = v.getX();
					//v.setX(0);
					//((AutoCompleteTextView) v).setWidth(LayoutParams.MATCH_PARENT);

				}
				else{
					ActionBarTabsPager.tabButton.setVisibility(View.VISIBLE);
					//ActionBarTabsPager.optionsButton.setVisibility(View.VISIBLE);
					ActionBarTabsPager.clearButton.setVisibility(View.GONE);
					urlText.setPadding(0, 0, 0, 0);
					urlText.bringToFront();
					ActionBar bar = getActivity().getActionBar();
					bar.setDisplayShowHomeEnabled(true);
				}

			}

		});

		//Setting the onClickListeners
		//REDO delete.setOnClickListener(this);
		/*back.setOnClickListener(this);
		forward.setOnClickListener(this);
		refresh.setOnClickListener(this);
		clear.setOnClickListener(this);
		bookmark.setOnClickListener(this);*/


		//Configuring the CustomWebView
		ourBrow = (CustomWebView) view.findViewById(R.id.WebView);
		/*if( savedInstanceState!= null){
			ourBrow.restoreState(savedInstanceState);
			Log.d("MyFragment", "Tab Restored");
		}
		else{*/
		Log.d("MyFragment", ourBrow.toString() + "No saved state");
		ourBrow.getSettings().setJavaScriptEnabled(true);
		ourBrow.getSettings().setPluginState(PluginState.ON);
		//this.getWindow().requestFeature(Window.FEATURE_PROGRESS);
		//Make Progress Bar Visible

		getActivity().getWindow().setFeatureInt( Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);		    
		//CustomWebView Properties
		ourBrow.setInitialScale(1); // < -- Restore Later
		//ourBrow.setScrollBarStyle(CustomWebView.SCROLLBARS_OUTSIDE_OVERLAY);
		//ourBrow.setScrollbarFadingEnabled(true);
		ourBrow.getSettings().setLoadWithOverviewMode(true);
		ourBrow.getSettings().setUseWideViewPort(true);
		ourBrow.getSettings().setJavaScriptEnabled(true);
		ourBrow.getSettings().setBuiltInZoomControls(true);



		wBClient = new MyWebViewClient(tabNum, this);
		ourBrow.setWebViewClient(wBClient);
		/*ourBrow.setWebViewClient(new WebViewClient(){
			//AutoCompleteTextView urlText;
			//ActionBar bar;
			//View tabView;




			//private int ftabNum = this.tabNum;

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
				urlView.setText(theWebsite,TextView.BufferType.EDITABLE);
				if(ActionBarTabsPager.mViewPager.getCurrentItem() == this.tabNum ){
					urlText.setText(theWebsite,TextView.BufferType.EDITABLE);
				}

				String summary = ourBrow.getTitle();
				String description = ourBrow.getUrl();
				ContentValues values = new ContentValues();
				values.put(TabTable.COLUMN_SUMMARY, summary);
				values.put(TabTable.COLUMN_DESCRIPTION, description);

				Log.d("onPageFinished", "tabNum: "+ tabNum);

				if(tabUri == null){
					Log.d("onPageFinished", "creating a new tab");
					tabUri = getCR().insert(MyTabContentProvider.CONTENT_URI, values);
				}
				else{
					Log.d("onPageFinished", "updating tab");
					Log.d("onPageFinished", "Updating, " + tabUri);
					try{
						getCR().update(Uri.parse("content://com.example.safebrowser.MyTabContentProvider/"+ tabUri), values, null, null);
					}catch(Exception e){
						Log.d("MyFragment", "CANT UPDATE");
					}
				}



			}

		});*/                  //Open Links in CustomWebView instead of opening them in native Device Browser


		ourBrow.setWebChromeClient(new WebChromeClient(){
			ActionBar bar;
			View tabView;


			@Override
			public void onProgressChanged(WebView view, int progress) 
			{
				bar = MyActivity.getActionBar();
				currTab = bar.getSelectedTab();

				/*if(tabView == null){
					Log.d("TAB", "NULL");
				}
				if(tabView.findViewById(R.id.tab_name) == null){
					Log.d("TAB", "NULL2");
				}*/
				//CustomWebViewFragment =  getFragmentManager().findFragmentById(R.id.WebViewFragmentContainer);
				//ourBrow = (CustomWebView)CustomWebViewFragment.getView().findViewById(R.id.WebView);
				/*if(ourBrow.getTitle()!= null){
					((TextView) tabView.findViewById(R.id.tab_name)).setText(ourBrow.getTitle());
				}*/
				if(pBar == null){
					Log.d("PBAR", "PBAR IS NuLL");
				}
				if(progress < 100 ){
					pBar.setVisibility(ProgressBar.VISIBLE);
					pBar.bringToFront();
				}
				pBar.setProgress(progress);
				if(progress == 100) {
					pBar.setProgress(progress);
					pBar.setVisibility(ProgressBar.GONE);
				}
			}
		});

		ourBrow.getSettings().setLoadWithOverviewMode(true);
		ourBrow.getSettings().setUseWideViewPort(true); 

		//urlText.setText("http://google.com");
		//setTabTitle();
		//}
		this.setRetainInstance(true);


		return view;
	}
	/*

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		Log.d("MyFragment", "onSaveInstanceState");
		CustomWebViewFragment =  getFragmentManager().findFragmentById(R.id.CustomWebViewFragmentContainer);
		ourBrow = (CustomWebView)CustomWebViewFragment.getView().findViewById(R.id.CustomWebView);
		ourBrow.saveState(outState);

	}
	 */	


	protected ContentResolver getCR() {
		return this.getActivity().getContentResolver();
	}


	//Handles the clicking of all the buttons
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.delete_tab:

			if(bar.getTabCount()==1){ //If the number of tabs is 1, deleting a tab redirects the tab to Google
				//CustomWebViewFragment =  getFragmentManager().findFragmentById(R.id.WebViewFragmentContainer);
				//ourBrow = (CustomWebView)CustomWebViewFragment.getView().findViewById(R.id.WebView);
				ourBrow.loadUrl("http://www.google.com");
				ActionBarTabsPager.urlMap.put(tabNum, "http://www.google.com");
				//setTabTitle();
			}
			else{ 
				bar.removeTab(bar.getSelectedTab()); //Removes the Tab
			}
			Log.d("MyFragment", "deleted tab");
			break;
			/*
		case R.id.bBack: //Handles the back button
			//CustomWebViewFragment =  getFragmentManager().findFragmentById(R.id.WebViewFragmentContainer);
			//ourBrow = (CustomWebView)CustomWebViewFragment.getView().findViewById(R.id.WebView);
			if(ourBrow.canGoBack()){
				ourBrow.goBack();
				Log.d("MainActivity", "Back");
				//setTabTitle();
			}
			break;
			
		case R.id.bForward: //Handles the forward button
			//CustomWebViewFragment =  getFragmentManager().findFragmentById(R.id.WebViewFragmentContainer);
			//ourBrow = (CustomWebView)CustomWebViewFragment.getView().findViewById(R.id.WebView);
			if(ourBrow.canGoForward()){
				ourBrow.goForward();
				//setTabTitle();
			}
			break;

		case R.id.bRefresh: //Handles the refresh button
			String currentPage = ourBrow.getUrl();
			ourBrow.loadUrl(currentPage);
			//setTabTitle();
			break;

		case R.id.delete_text: //Handles the deleting of the url text
			urlView.setText("");
			urlText.setText("");
			break;*/


		case R.id.bookmark: //Handles adding the current page as a bookmark
			Intent i = new Intent(getActivity(), BookmarkDetail.class);
			i.putExtra("Name", ourBrow.getTitle());
			//i.putExtra("Url", urlView.getText().toString());
			i.putExtra("Url", urlText.getText().toString());
			startActivity(i);			
			break;
		}	
	}

	//This method handles the loading of the url in the CustomWebView
	public void loadPage(){

		//String theWebsite = urlView.getText().toString().replaceAll("\\s", "");
		String theWebsite = urlText.getText().toString().replaceAll("\\s","");
		if(!isValid(theWebsite)){
			theWebsite = (String) fixText(theWebsite);
		}

		ActionBarTabsPager.urlMap.put(tabNum, theWebsite);
		ourBrow.loadUrl(theWebsite);
		bar = getActivity().getActionBar();
		currTab = bar.getSelectedTab();

		//Hiding keyboard after entering URL
		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		//imm.hideSoftInputFromWindow(urlView.getWindowToken(),0);
		imm.hideSoftInputFromWindow(urlText.getWindowToken(),0);
		ourBrow.requestFocus();
	}



	//This method makes sure the text on the tab corresponds to the page title that the CustomWebView is on
	/*public void setTabTitle(){
		ourBrow = (CustomWebView)this.getView().findViewById(R.id.CustomWebView);
		bar = getActivity().getActionBar();
		currTab = bar.getSelectedTab();
		tabView = currTab.getCustomView();
		((TextView) tabView.findViewById(R.id.tab_name)).setText(ourBrow.getTitle());
	}*/

	//Fixes the url if it is not in a correct format
	@Override
	public CharSequence fixText(CharSequence invalidText) {
		return "http://" + invalidText;
	}

	//Checks if the url is in the correct format
	@Override
	public boolean isValid(CharSequence text) {
		if(text.length()>7){

			if(text.subSequence(0, 7).equals("http://") || text.subSequence(0, 7).equals("https//") || text.subSequence(0, 8).equals("https://")){
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


	@Override
	public void onHiddenChanged(boolean hidden){
		if( hidden == false){
			CustomWebViewFragment =  getFragmentManager().findFragmentById(R.id.WebViewFragmentContainer);
			CustomWebViewFragment.getFragmentManager().beginTransaction().attach(this);
			Log.d("MyFragment", "Fragment Hidden");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		Log.d("MyFragment", " onActivityCreated");
	}

	@Override
	public void onStart(){
		super.onStart();


		Log.d("MyFragment", " onStart");
	}

	@Override
	public void onResume(){
		super.onResume();
		//loadPage();
		ourBrow.loadUrl(ActionBarTabsPager.urlMap.get(tabNum));
		Log.d("MyFragment", " onResume");
	}

	@Override
	public void onPause(){
		super.onPause();
		Log.d("MyFragment", " onPause");
	}

	@Override
	public void onStop(){
		super.onStop();
		Log.d("MyFragment", " onStop");
	}

	@Override
	public void onDestroyView(){
		super.onDestroyView();
		Log.d("MyFragment", " onDestroyView");
	}


	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}


	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		CustomWebViewFragment =  getFragmentManager().findFragmentById(R.id.WebViewFragmentContainer);
		ourBrow = (CustomWebView)CustomWebViewFragment.getView().findViewById(R.id.WebView);
		int yPos = ourBrow.getScrollY();

		Log.d("Scroll", "yPos = " + yPos); 

		return true;
	}


	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}


	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onTouchEvent(final MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			CustomWebViewFragment =  getFragmentManager().findFragmentById(R.id.WebViewFragmentContainer);
			ourBrow = (CustomWebView)CustomWebViewFragment.getView().findViewById(R.id.WebView);
			int yPos = ourBrow.getScrollY();
			Log.d("Scroll", "yPos = " + yPos); 
		}
		return false;
	}


}
