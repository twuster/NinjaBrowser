//This class handles all the attaching and detaching of fragments 
//that correspond to the adding and deleting of tabs in the ActionBar

package com.example.newbrowser;
import com.example.newbrowser.R;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;



public class TabListener<T extends Fragment> implements ActionBar.TabListener{
	
	private Fragment fragment;
	private Fragment currFragment;
	private Activity activity;
	private Class<T> fragmentClass;
	private int fragmentContainer;
	
	public TabListener(Activity activity, int fragmentContainer, Class<T> fragmentClass){
		this.activity = activity;
		this.fragmentContainer = fragmentContainer;
		this.fragmentClass = fragmentClass;
		
	}
	
	//Called when tab is reselected
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		if( fragment != null){
			Log.d("TabListener", "Tab reselected");
			ft.attach(fragment);
			//ft.show(fragment);
			currFragment = fragment;
		}
	}

	//Called when tab is selected
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		if (fragment ==  null){
			String fragmentName = fragmentClass.getName();
			fragment = Fragment.instantiate(activity, fragmentName);
			ft.add(fragmentContainer, fragment, null);
			//ft.show(fragment);
			currFragment = fragment;
		
		}
		else{
			ft.attach(fragment);
			currFragment = fragment;
		}
		View tabView = tab.getCustomView();
		tabView.findViewById(R.id.delete_tab).setVisibility(View.VISIBLE);
		Log.d("TabListener", "Tab Selected");
	}
	
	//Called when tab is unselected
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		if (fragment!=null){
			View tabView = tab.getCustomView();
			tabView.findViewById(R.id.delete_tab).setVisibility(View.GONE);
			
			//((TextView)tabView.findViewById(R.id.tab_name)).setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);
			ft.detach(fragment);
			
			//ft.hide(fragment);
			Log.d("TabListener", "Tab Unselected");
		}
		
	}
	
	
	public Fragment getFragment(){
		return currFragment;
	}

}
