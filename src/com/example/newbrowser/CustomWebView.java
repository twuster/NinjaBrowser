package com.example.newbrowser;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

public class CustomWebView extends WebView{
	AutoCompleteTextView urlText ;
	ImageButton back ;
	ImageButton forward;
	ImageButton refresh;
	GestureDetector gd;
	boolean flinged;
	
	
	GestureDetector.SimpleOnGestureListener sogl = new GestureDetector.SimpleOnGestureListener() {
	    // your fling code here
		public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
			flinged = true;
			return true;
			
		}
	    };

	public CustomWebView(Context context, AttributeSet attrs, int defStyle)
	{
	    super(context, attrs, defStyle);
		gd = new GestureDetector(context, sogl);

	}   


	public CustomWebView(Context context, AttributeSet attrs)
	{
	    super(context, attrs);
	    gd = new GestureDetector(context, sogl);
	}

	public CustomWebView(Context context) {
		super(context);
		gd = new GestureDetector(context, sogl);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
	    super.onScrollChanged(l, t, oldl, oldt);
	}
	
	
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		gd.onTouchEvent(ev);
		if (ev.getAction() == MotionEvent.ACTION_UP || flinged == true){
			
			Log.d("CustomWebView", "SCROLL " + this.getScrollY());
			/*if(this.getScrollY()<=10){
				urlText = (AutoCompleteTextView)this.getRootView().findViewById(R.id.urlText);
				back = (ImageButton)this.getRootView().findViewById(R.id.bBack);
				forward = (ImageButton)this.getRootView().findViewById(R.id.bForward);
				refresh = (ImageButton)this.getRootView().findViewById(R.id.bRefresh);
				
				urlText.setVisibility(View.VISIBLE);
				back.setVisibility(View.VISIBLE);
				forward.setVisibility(View.VISIBLE);
				refresh.setVisibility(View.VISIBLE);

			}
			else{
				urlText = (AutoCompleteTextView)this.getRootView().findViewById(R.id.urlText);
				back = (ImageButton)this.getRootView().findViewById(R.id.bBack);
				forward = (ImageButton)this.getRootView().findViewById(R.id.bForward);
				refresh = (ImageButton)this.getRootView().findViewById(R.id.bRefresh);
				
				urlText.setVisibility(View.GONE);
				back.setVisibility(View.GONE);
				forward.setVisibility(View.GONE);
				refresh.setVisibility(View.GONE);
				
			}
			flinged = false;*/
		}
		//if(ev.getAction() == MotionEvent.a)
	    return super.onTouchEvent(ev);
	}


}
