package com.example.newbrowser;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class GestureDetector  extends Activity implements OnGesturePerformedListener, OnClickListener {
	private GestureLibrary gestureLib;
	String bestFit;
	double bestScore;
	ImageButton back;
	ImageButton addGesture;
	private final File mStoreFile = new File(Environment.getExternalStorageDirectory(), "gestures");
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	    GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
	    View inflate = getLayoutInflater().inflate(R.layout.gesture_view, null);
	    gestureOverlayView.addView(inflate);
	    gestureOverlayView.addOnGesturePerformedListener(this);
	    gestureOverlayView.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);
	    gestureOverlayView.setGestureColor(android.graphics.Color.BLUE);
	    gestureOverlayView.setUncertainGestureColor (android.graphics.Color.BLUE);
	    gestureOverlayView.setGestureStrokeType(2);
	    //gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
	    gestureLib = GestureLibraries.fromFile(mStoreFile);
	    if (!gestureLib.load()) {
	      finish();
	    }
	    setContentView(gestureOverlayView);
	    
	    back = (ImageButton)gestureOverlayView.findViewById(R.id.back);
	    back.setOnClickListener(this);
	    addGesture = (ImageButton)gestureOverlayView.findViewById(R.id.add_gesture);
	    addGesture.setOnClickListener(this);
	}

	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
		bestScore = 0;
	    for (Prediction prediction : predictions) {
	      if (prediction.score > 1.5) {
	    	  if(prediction.score > bestScore){
	    		  bestScore =  prediction.score;
	    		  bestFit = prediction.name;
	    	  }
	        
	        
	      }
	    }
	    if(bestScore == 0){
	    	Toast.makeText(this, "Could not recognize gesture", Toast.LENGTH_SHORT).show();
	    }
	    else{
	    	Toast.makeText(this, bestFit, Toast.LENGTH_SHORT).show();
	    	finishWithData();
	    	
	    }
		
	}
	
	public void finishWithData() {
		Intent data = new Intent();
		data.putExtra("Gesture", bestFit);
		setResult(RESULT_OK, data);
		finish();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case(R.id.back):
			setResult(RESULT_CANCELED);
			finish();
			break;
		case(R.id.add_gesture):
			Intent i =  new Intent(this, GestureBuilderActivity.class);
			startActivity(i);
			break;
		default:
			break;
		}
		
	}

}
