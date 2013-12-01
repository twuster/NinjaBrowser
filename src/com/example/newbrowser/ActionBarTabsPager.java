package com.example.newbrowser;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.example.newbrowser.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.PendingIntent;

import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PatternMatcher;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class ActionBarTabsPager extends FragmentActivity implements OnClickListener, AutoCompleteTextView.Validator{
	static ViewPager mViewPager;
	static TabsAdapter mTabsAdapter;
	AutoCompleteTextView url;
	static WebView ourBrow;
	static Fragment webViewFragment;
	boolean initialFind=true;
	static ActionBar bar;
	View urlBar;
	//MenuItem urlItem;
	View urlItem;
	MenuItem tabItem;

	static ImageButton optionsButton;
	static ImageButton tabButton;
	static ImageButton clearButton;
	static AutoCompleteTextView urlText;
	static ImageButton gestureButton;

	MyTabContentProvider cp = new MyTabContentProvider();

	String resumeAction;

	//static ProgressBar pBar;



	private static final int REQUEST_CODE = 10;

	protected static HashMap<Integer, String> urlMap = new HashMap<Integer, String>();
	protected static HashMap<Integer, Boolean> validMap = new HashMap<Integer, Boolean>();
	protected static ArrayList<Integer> tabList = new ArrayList<Integer>();

	//NFC variables
	private static final String TAG = "NFCReadTag";  
	private NfcAdapter mNfcAdapter;  
	private IntentFilter[] mNdefExchangeFilters;  
	private PendingIntent mNfcPendingIntent;  
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);



		setContentView(R.layout.activity_main);

		tabList.clear();
		urlMap.clear();
		validMap.clear();
		this.deleteDatabase("tabs.db");
		//cp.delete(uri, selection, selectionArgs)
		bar = getActionBar();
		bar.setDisplayShowCustomEnabled(true);
		bar.setCustomView(R.layout.url_bar);



		mViewPager = (ViewPager)findViewById(R.id.WebViewFragmentContainer);
		mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());



		urlItem = bar.getCustomView();

		tabButton = (ImageButton)urlItem.findViewById(R.id.all_tabs);
		tabButton.setOnClickListener(this);

		urlText = (AutoCompleteTextView)urlItem.findViewById(R.id.url_text);
		urlText.setImeActionLabel("Go", KeyEvent.KEYCODE_ENTER);
		urlText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				loadPage();
				return false;
			}
		});
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(urlText.getWindowToken(),0);

		clearButton = (ImageButton)urlItem.findViewById(R.id.clear_button);
		clearButton.setOnClickListener(this);

		gestureButton = (ImageButton) this.findViewById(R.id.gesture_button);
		gestureButton.setOnClickListener(this);


		mTabsAdapter = new TabsAdapter(this, mViewPager);
		mTabsAdapter.addTab(bar.newTab().setText("New Tab").setCustomView(R.layout.tab_layout),
				MyFragment.class, null);

		/*
		if (savedInstanceState != null) {
			bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
		}*/

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);  
		mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,  
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP  
						| Intent.FLAG_ACTIVITY_CLEAR_TOP), 0);  
		IntentFilter smartwhere = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);  
		smartwhere.addDataScheme("http");  
		smartwhere.addDataAuthority("www.companies.com", null);  
		smartwhere.addDataPath(".*", PatternMatcher.PATTERN_SIMPLE_GLOB);  
		mNdefExchangeFilters = new IntentFilter[] { smartwhere };
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
	}

	@Override
	public void onAttachedToWindow(){
		super.onAttachedToWindow();
		Log.d("ActionBarTabsPager", "onAttachedToWindow");
		//openOptionsMenu();
		//closeOptionsMenu();
	}

	//This method handles the loading of the url in the CustomWebView
	public void loadPage(){
		ourBrow = (WebView) mTabsAdapter.mCurrentFragment.getView().findViewById(R.id.WebView);
		//String theWebsite = urlView.getText().toString().replaceAll("\\s", "");
		String theWebsite = urlText.getText().toString().replaceAll("\\s","");
		if(!isValid(theWebsite)){
			theWebsite = (String) fixText(theWebsite);
		}

		ActionBarTabsPager.urlMap.put(mViewPager.getCurrentItem(), theWebsite);
		ourBrow.loadUrl(theWebsite);
		bar = getActionBar();


		//Hiding keyboard after entering URL
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(urlText.getWindowToken(),0);
		ourBrow.requestFocus();
	}

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

	/**
	 * This is a helper class that implements the management of tabs and all
	 * details of connecting a ViewPager with associated TabHost.  It relies on a
	 * trick.  Normally a tab host has a simple API for supplying a View or
	 * Intent that each tab will show.  This is not sufficient for switching
	 * between pages.  So instead we make the content part of the tab host
	 * 0dp high (it is not shown) and the TabsAdapter supplies its own dummy
	 * view to show as the tab content.  It listens to changes in tabs, and takes
	 * care of switch to the correct paged in the ViewPager whenever the selected
	 * tab changes.
	 */
	public static class TabsAdapter extends FragmentPagerAdapter
	implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
		private final Context mContext;
		private final ActionBar mActionBar;
		protected final ViewPager mViewPager;
		protected Fragment mCurrentFragment;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		static final class TabInfo {
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(Class<?> _class, Bundle _args) {
				clss = _class;
				args = _args;
			}
		}

		public TabsAdapter(FragmentActivity activity, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mActionBar = activity.getActionBar();
			mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {



			//tabMap.put(getCount(), "New Tab");
			TabInfo info = new TabInfo(clss, args);
			tab.setTag(info);
			tab.setTabListener(this);
			mTabs.add(info);
			//mActionBar.addTab(tab);


			notifyDataSetChanged();
			//validMap.put(getCount()-1, true);
			mViewPager.setCurrentItem(getCount()-1);

			//ourBrow= ((MyFragment)mTabsAdapter.getItem(getCount()-1)).ourBrow;

		}


		public void removeTab(int position){
			//destroyItem(mViewPager.getRootView(),position,MyFragment.class);
			mTabs.remove(position);
			tabList.remove((Integer)position);
			validMap.put(position, false);
			notifyDataSetChanged();

			if(getCount()==0){
				addTab(ActionBarTabsPager.bar.newTab().setText("New Tab").setCustomView(R.layout.tab_layout),
						MyFragment.class, null);
				//tabMap.put(getCount(), "New Tab");
			}
			else{
				if(mViewPager.getCurrentItem() == position){
					if(position == 0){
						mViewPager.setCurrentItem(position+1);
					}
					else{
						mViewPager.setCurrentItem(position-1);
					}
				}
			}


		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public int getItemPosition(Object object){
			if(!tabList.contains(((MyFragment)object).tabNum) || validMap.get(((MyFragment)object).tabNum)==false){
				return POSITION_NONE;
			}
			else{
				return tabList.indexOf((Integer)((MyFragment)object).tabNum);
			}


		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object object){
			mCurrentFragment = (Fragment)object;
		}

		@Override
		public android.support.v4.app.Fragment getItem(int position) {

			TabInfo info = mTabs.get(position);
			return android.support.v4.app.Fragment.instantiate(mContext, info.clss.getName(), info.args);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			//mActionBar.setSelectedNavigationItem(position);
			Log.d("ONPAGESELECTED", "Current Item: " + mViewPager.getCurrentItem());
			String currUrl;
			currUrl = urlMap.get(mViewPager.getCurrentItem());
			if(currUrl == null){
				Log.d("ActionBarTabsPager", "currUrl IS NULL");
			}
			urlText.setText(currUrl);
			//ourBrow = (WebView)mViewPager.getChildAt(mViewPager.getCurrentItem()).findViewById(R.id.WebView);
		}

		@Override
		public void onPageScrollStateChanged(int state) {

		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Object tag = tab.getTag();
			for (int i=0; i<mTabs.size(); i++) {
				if (mTabs.get(i) == tag) {
					mViewPager.setCurrentItem(i);

				}
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}


	}

	//this creates options inside the ActionBar Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);


		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);

		return true;

	}

	//this handles clicks on Action Bar items
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		ourBrow = (WebView) mTabsAdapter.mCurrentFragment.getView().findViewById(R.id.WebView);
		switch(item.getItemId()){
		/*case(R.id.gestures):
			Intent intent1 = new Intent(this, GestureDetector.class);
		startActivityForResult(intent1, REQUEST_CODE);
		return true;*/
		case(R.id.nfc):
			Intent intent1 = new Intent(this, NFCActivity.class);
		startActivity(intent1);
		return true;

		case(R.id.back):

			if(ourBrow.canGoBack()){
				ourBrow.goBack();
				Log.d("MainActivity", "Back");;
			}
		return true;
		case(R.id.forward):
			if(ourBrow.canGoForward()){
				ourBrow.goForward();
			}
		return true;
		case(R.id.bookmark):
			Intent i = new Intent(this, BookmarkDetail.class);
		i.putExtra("Name", ourBrow.getTitle());
		//i.putExtra("Url", urlView.getText().toString());
		i.putExtra("Url", urlText.getText().toString());
		startActivity(i);
		return true;

		case(R.id.refresh):
			ourBrow.loadUrl(ourBrow.getUrl());
		return true;

		case(R.id.new_tab):

			ActionBar bar = getActionBar();
		mTabsAdapter.addTab(bar.newTab().setText("New Tab"),
				MyFragment.class, null);

		return true;

		case(R.id.bookmarks):
			Intent bookmarkIntent = new Intent(this, BookmarkPopup.class);
		startActivityForResult(bookmarkIntent, REQUEST_CODE);
		return true;

		case(R.id.all_tabs):
			Intent tabIntent = new Intent(this, TabPopup.class);
		startActivityForResult(tabIntent, REQUEST_CODE);
		return true;

		case(R.id.menu_item_search):


			//SearchView code
			final MenuItem searchItem =  item;
		final SearchView searchView = (SearchView)searchItem.getActionView();

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				ourBrow = (WebView) mTabsAdapter.mCurrentFragment.getView().findViewById(R.id.WebView);
				if(ourBrow == null){
					Log.d("ONQUERYTEXTSUBMIT", "OURBROW IS NULL");
				}

				if(initialFind ==true){

					ourBrow.findAll(query);
					initialFind = false;
				}
				else{
					ourBrow.findNext(true);
				}
				return true;


			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				initialFind = true;
				return true;
			}
		});

		searchView.setOnQueryTextFocusChangeListener(new OnFocusChangeListener(){

			@SuppressLint("NewApi")
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus == false){
					searchItem.collapseActionView();
				}
			}

		});

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		if(null!=searchManager ) {   
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		}

		return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			if (data.hasExtra("Url")) {
				//url = (AutoCompleteTextView) mTabsAdapter.mCurrentFragment.getView().findViewById(R.id.urlText);;
				/*if(!url.equals("")){
					urlText.setText(data.getExtras().getString("Url"));
				}*/
				//New
				urlMap.put(mViewPager.getCurrentItem(),data.getExtras().getString("Url"));
				ourBrow = (WebView) mTabsAdapter.mCurrentFragment.getView().findViewById(R.id.WebView);
				ourBrow.loadUrl(data.getExtras().getString("Url"));
				Log.d("MyFragment","ONACTIVITYRESULT");
			}
			if(data.hasExtra("Gesture")){
				resumeAction = data.getExtras().getString("Gesture");


			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) { //Back key pressed
			ourBrow = (WebView) mTabsAdapter.mCurrentFragment.getView().findViewById(R.id.WebView);
			if(ourBrow.canGoBack()){
				ourBrow.goBack();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		Log.d("MainActivity", "onConfigurationChanged");


	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){

		case(R.id.all_tabs):
			Intent tabIntent = new Intent(this, TabPopup.class);
		startActivityForResult(tabIntent, REQUEST_CODE);
		break;
		case(R.id.clear_button):
			urlText.setText("");
		break;
		case(R.id.gesture_button):
			Intent intent1 = new Intent(this, GestureDetector.class);
		startActivityForResult(intent1, REQUEST_CODE);
		break;

		default:
			break;
		}

	}

	@Override
	public void onResume(){
		super.onResume();
		if(mNfcAdapter != null) {  
			mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,  
					mNdefExchangeFilters, null);  
			/*if (!mNfcAdapter.isEnabled()){  
        LayoutInflater inflater = getLayoutInflater();  
           View dialoglayout = inflater.inflate(R.layout.nfc_settings_layout,(ViewGroup) findViewById(R.id.nfc_settings_layout));  
        new AlertDialog.Builder(this).setView(dialoglayout)  
               .setPositiveButton("Update Settings", new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface arg0, int arg1) {  
                                  Intent setnfc = new Intent(Settings.ACTION_WIRELESS_SETTINGS);  
                                  startActivity(setnfc);  
                    }  
               })  
            .setOnCancelListener(new DialogInterface.OnCancelListener() {  
                 public void onCancel(DialogInterface dialog) {  
                      finish(); // exit application if user cancels  
              }                      
            }).create().show();  
            }*/
		} else {  
			Toast.makeText(getApplicationContext(), "Sorry, No NFC Adapter found.", Toast.LENGTH_SHORT).show();  
		}  
		if(resumeAction!=null){
			Log.d("RESUMEACTION","SHOULD BE RESUMING FROM GESTURE" + resumeAction);
			ourBrow = (WebView) mTabsAdapter.mCurrentFragment.getView().findViewById(R.id.WebView);
			if(resumeAction.equals("Back")){
				try{
					WebBackForwardList mWebBackForwardList = ourBrow.copyBackForwardList();
					String historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex()-1).getUrl();
					urlMap.put(mViewPager.getCurrentItem(),historyUrl );
					ourBrow.loadUrl(historyUrl);
				}
				catch(Exception e){
					Log.d("ACtionBarTabsPAger", "BACK did nOT WORK");
				}

			}
			else if(resumeAction.equals("Forward")){
				try{
					WebBackForwardList mWebBackForwardList = ourBrow.copyBackForwardList();
					String historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex()+1).getUrl();
					urlMap.put(mViewPager.getCurrentItem(),historyUrl );
					ourBrow.loadUrl(historyUrl);
				}
				catch(Exception e){
					Log.d("ACtionBarTabsPAger", "FoRWARD did nOT WORK");
				}
			}
			/*
			else if(resumeAction.equals("Go to Bottom of Page")){
				while(ourBrow.sc){

				}

			}*/
			else if(resumeAction.equals("Go to Top of Page")){
				ourBrow.scrollTo(0, 0);
			}
			else if(resumeAction.equals("Refresh")){
				ourBrow.loadUrl(ourBrow.getUrl());
			}
			else if(resumeAction.equals("New Tab")){
				Log.d("RESUMEACTION","SHOULD BE RESUMING NEW TAB");
				ActionBar bar = getActionBar();
				mTabsAdapter.addTab(bar.newTab().setText("New Tab"),
						MyFragment.class, null);
			}
			else if(resumeAction.equals("Close Currrent Tab")){
				mTabsAdapter.removeTab(mViewPager.getCurrentItem());
			}
			else if(resumeAction.equals("Bookmark")){
				Intent i = new Intent(this, BookmarkDetail.class);
				i.putExtra("Name", ourBrow.getTitle());
				//i.putExtra("Url", urlView.getText().toString());
				i.putExtra("Url", urlText.getText().toString());
				startActivity(i);
			}
			else if(resumeAction.startsWith("http://") || resumeAction.startsWith("www.")){
				urlMap.put(mViewPager.getCurrentItem(),resumeAction );
				ourBrow.loadUrl(resumeAction);

			}
		}
		resumeAction =null;
	}

	@Override  
	protected void onPause() {  
		super.onPause();  
		if(mNfcAdapter != null) mNfcAdapter.disableForegroundDispatch(this);  
	}  
	@Override  
	protected void onNewIntent(Intent intent) {  
		super.onNewIntent(intent);            
		Log.d("ONNEWNINTENT", "ONNEWINTENT");
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) { 
			Log.d("ONNEWNINTENT", "NDEF DISCOVERED");
			NdefMessage[] messages = null;  
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);  
			if (rawMsgs != null) {  
				messages = new NdefMessage[rawMsgs.length];  
				for (int i = 0; i < rawMsgs.length; i++) {  
					messages[i] = (NdefMessage) rawMsgs[i];  
				}  
			}  
			if(messages[0] != null) {  
				Log.d("ONNEWNINTENT", "MESSGE NOT NULL");
				String result="";  
				byte[] payload = messages[0].getRecords()[0].getPayload();  
				// this assumes that we get back am SOH followed by host/code  
				for (int b = 1; b<payload.length; b++) { // skip SOH  
					result += (char) payload[b];  
				}  
				//Toast.makeText(getApplicationContext(), "Opening tabs with yahoo.com, addepar.com, mongodb.org", Toast.LENGTH_SHORT).show();
				
				mTabsAdapter.addTab(bar.newTab(),
						MyFragment.class, null);
				ourBrow = (WebView) mTabsAdapter.mCurrentFragment.getView().findViewById(R.id.WebView);
				//ourBrow.loadUrl("http://www.yahoo.com");
				urlMap.put(mViewPager.getCurrentItem(),"http://www.realultimatepower.net/index4.htm" );
				Toast.makeText(getApplicationContext(), "You are now a ninja.", Toast.LENGTH_LONG).show();
				ourBrow.startAnimation( 
					    AnimationUtils.loadAnimation(this, R.anim.rotate) );

				
				 /*
				mTabsAdapter.addTab(bar.newTab(),
						MyFragment.class, null);
				//ourBrow = (WebView) mTabsAdapter.mCurrentFragment.getView().findViewById(R.id.WebView);
				//ourBrow.loadUrl("http://www.addepar.com" );
				urlMap.put(mViewPager.getCurrentItem(),"http://www.addepar.com" );
				
				mTabsAdapter.addTab(bar.newTab(),
						MyFragment.class, null);
				//ourBrow = (WebView) mTabsAdapter.mCurrentFragment.getView().findViewById(R.id.WebView);
				//ourBrow.loadUrl("http://www.mongodb.org" );
				urlMap.put(mViewPager.getCurrentItem(),"http://www.mongodb.org" );*/
			}  
		}  
	} 

}