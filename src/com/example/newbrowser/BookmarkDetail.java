package com.example.newbrowser;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.example.newbrowser.BookmarkTable;
import com.example.newbrowser.MyBookmarkContentProvider;
import com.example.newbrowser.R;

/*
 * TodoDetailActivity allows to enter a new todo item 
 * or to change an existing
 */
public class BookmarkDetail extends Activity {
	private EditText mTitleText;
	private EditText mBodyText;

	private Uri bookmarkUri;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.bookmark_edit);
		final Activity activity =this;
		View v = findViewById(R.layout.bookmark_edit);

		mTitleText = (EditText) findViewById(R.id.todo_edit_summary);
		mTitleText.setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER);
		mBodyText = (EditText) findViewById(R.id.todo_edit_description);
		mBodyText.setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER);
		Button confirmButton = (Button) findViewById(R.id.bookmark_confirm_button);
		Button cancelButton = (Button) findViewById(R.id.bookmark_cancel_button);
		





		mTitleText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mTitleText.getWindowToken(),0);
				return false;
			}
		});


		mBodyText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mBodyText.getWindowToken(),0);
				return false;
			}
		});
		Bundle extras = getIntent().getExtras();

		// Check from the saved Instance
		bookmarkUri = (bundle == null) ? null : (Uri) bundle
				.getParcelable(MyBookmarkContentProvider.CONTENT_ITEM_TYPE);

		// Or passed from the other activity
		if (extras != null) {


			String value1 = extras.getString("Name");
			String value2 = extras.getString("Url");

			
			if(value1 != null && value2 != null){
				mTitleText.setText(value1);
				mBodyText.setText(value2);
			}
			else{
				bookmarkUri = extras
						.getParcelable(MyBookmarkContentProvider.CONTENT_ITEM_TYPE);

				fillData(bookmarkUri);
			}

		}

		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				
				if (TextUtils.isEmpty(mTitleText.getText().toString())) {
					makeToast();
				} else {
					setResult(RESULT_OK);
					finish();
				}
			}

		});

		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mTitleText.setText("");
				mBodyText.setText("");
				setResult(RESULT_CANCELED);
				finish();
			}
		});


	}

	private void fillData(Uri uri) {
		String[] projection = { BookmarkTable.COLUMN_SUMMARY,
				BookmarkTable.COLUMN_DESCRIPTION/*, BookmarkTable.COLUMN_CATEGORY*/ };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();
			//String category = cursor.getString(cursor.getColumnIndexOrThrow(BookmarkTable.COLUMN_CATEGORY));
			/*
      for (int i = 0; i < mCategory.getCount(); i++) {

        String s = (String) mCategory.getItemAtPosition(i);
        if (s.equalsIgnoreCase(category)) {
          mCategory.setSelection(i);
        }
      }
			 */
			mTitleText.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(BookmarkTable.COLUMN_SUMMARY)));
			mBodyText.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(BookmarkTable.COLUMN_DESCRIPTION)));

			// Always close the cursor
			cursor.close();
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putParcelable(MyBookmarkContentProvider.CONTENT_ITEM_TYPE, bookmarkUri);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	private void saveState() {
		String summary = mTitleText.getText().toString();
		String description = mBodyText.getText().toString();

		// Only save if either summary or description
		// is available

		if (description.length() == 0 && summary.length() == 0) {
			return;
		}

		ContentValues values = new ContentValues();
		values.put(BookmarkTable.COLUMN_SUMMARY, summary);
		values.put(BookmarkTable.COLUMN_DESCRIPTION, description);

		if (bookmarkUri == null) {
			// New todo
			bookmarkUri = getContentResolver().insert(MyBookmarkContentProvider.CONTENT_URI, values);
		} else {
			// Update todo
			getContentResolver().update(bookmarkUri, values, null, null);
		}
	}

	private void makeToast() {
		Toast.makeText(BookmarkDetail.this, "Please maintain a summary",
				Toast.LENGTH_LONG).show();
	}
} 