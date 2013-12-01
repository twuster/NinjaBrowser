package com.example.newbrowser;
//This is the main Activity that gets called when the app is started
//It has a WebViewFragmentContainer which contains MyFragment.java
/*
package com.example.safebrowser;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;

public class MainActivity extends Activity 
{

	WebView ourBrow;
	Fragment webViewFragment;
	AutoCompleteTextView url;
	// REDO TabListener<MyFragment> tListener;
	ActionBar bar;
	static final private int NEW_TAB = 1;
	static final private int BOOKMARKS = 2;

	private static final int REQUEST_CODE = 10;

	String searchText;
	boolean initialFind=true;



	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{

		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_PROGRESS);

		setContentView(R.layout.activity_main); //Calling activity_main.xml



		bar = getActionBar();			//Creating ActionBar Object
		//bar.setDisplayShowHomeEnabled(false);   //Disabling logo on the Action Bar
		bar.setDisplayShowTitleEnabled(false);  //This will hide the title from the Action bar, so I can use it now to do tabs
		//bar.setDisplayShowHomeEnabled(false);
		//View homeIcon = findViewById(android.R.id.home);
		//((View) homeIcon.getParent()).setVisibility(View.GONE);

		//Enabling Tabs
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		//Set initial Tab
		Tab tab = bar.newTab();
		// REDO tListener = new TabListener<MyFragment>(this,R.id.WebViewFragmentContainer,MyFragment.class);
		//REDO tab.setText("New Tab").setTabListener(tListener);
		tab.setCustomView(R.layout.tab_layout);
		bar.addTab(tab);


	}



	//this creates options inside the ActionBar Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);


		//SearchView code
		final MenuItem searchItem =  menu.findItem(R.id.menu_item_search);
		final SearchView searchView = (SearchView)searchItem.getActionView();
		//searchView.setQueryHint("Find");

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				webViewFragment =  getFragmentManager().findFragmentById(R.id.WebViewFragmentContainer);
				ourBrow = (WebView)webViewFragment.getView().findViewById(R.id.WebView);

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
				searchText = newText;
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
		/*
		searchView.setOnSearchClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ourBrow = (WebView)webViewFragment.getView().findViewById(R.id.WebView);

			}

		});
		 */
/*
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		if(null!=searchManager ) {   
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		}

		return true;

	}

	//this handles clicks on Action Bar items
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case(R.id.new_tab):

			ActionBar bar = getActionBar();
		Tab tab = bar.newTab();
		// REDO tab.setText("New Tab").setTabListener(new TabListener<MyFragment>(this,R.id.WebViewFragmentContainer,MyFragment.class));
		tab.setCustomView(R.layout.tab_layout); //sets our custom view for the tab
		bar.addTab(tab);
		bar.selectTab(tab); //selects the newly created tab
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		return true;

		case(R.id.bookmarks):
			Intent intent = new Intent(this, BookmarkPopup.class);
		startActivityForResult(intent, REQUEST_CODE);
		return true;
		
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			if (data.hasExtra("Url")) {
				url = (AutoCompleteTextView) findViewById(R.id.WebViewFragmentContainer).findViewById(R.id.urlText);
				if(!url.equals("")){
					url.setText(data.getExtras().getString("Url"));
				}
				Log.d("MyFragment","ONACTIVITYRESULT");
			}
		}
	}

	@Override
	public void onResume(){
		super.onResume();

	}

	/*
	//Handles onSaveInstance
	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		Log.d("MainActivity", "Save Instance");
		webViewFragment =  getFragmentManager().findFragmentById(R.id.WebViewFragmentContainer);
		//webViewFragment = tListener.getFragment();
		ourBrow = (WebView)webViewFragment.getView().findViewById(R.id.WebView);
		ourBrow.saveState(outState);

	}
	 */
	/*
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		Log.d("MainActivity", "Restore Instance");
		webViewFragment =  getFragmentManager().findFragmentById(R.id.WebViewFragmentContainer);
		//webViewFragment = tListener.getFragment();
		ourBrow = (WebView)webViewFragment.getView().findViewById(R.id.WebView);
		ourBrow.restoreState(savedInstanceState);
	}
	 */
/*
	//Handles configuration changes, e.g. Orientation changes
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		//bar.getSelectedTab().setCustomView(R.layout.tab_layout);
		Log.d("MainActivity", "onConfigurationChanged");
		//bar.getSelectedTab().setCustomView(R.layout.tab_layout);
	}

	public void showPopup(View v) {

		LayoutInflater layoutInflater 
		= (LayoutInflater)getBaseContext()
		.getSystemService(LAYOUT_INFLATER_SERVICE);  
		View popupView = layoutInflater.inflate(R.layout.bookmark_popup, null);  
		final PopupWindow popupWindow = new PopupWindow(
				popupView, 
				LayoutParams.WRAP_CONTENT,  
				LayoutParams.WRAP_CONTENT); 
		popupWindow.showAtLocation(v, Gravity.CENTER,0, 0);
		popupWindow.setFocusable(true);
		popupWindow.update();

	}

}*/
