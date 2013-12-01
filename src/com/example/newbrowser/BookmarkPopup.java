package com.example.newbrowser;

import com.example.newbrowser.R;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;

public class BookmarkPopup extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>, OnClickListener {
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = Menu.FIRST + 2;
	// private Cursor cursor;
	private SimpleCursorAdapter adapter;

	private Uri bookmarkUri;
	public String bookmarkUrl;


	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmark_popup);
		this.getListView().setDividerHeight(2);
		fillData();
		registerForContextMenu(getListView());

		Button add = (Button) this.findViewById(R.id.add_bookmark);
		add.setOnClickListener(this);
		//LayoutInflater inflater = null;
		//Button delete = (Button) inflater.inflate(R.id.delete_bookmark, null);

		//delete.setOnClickListener(this);
	}
	/*
	// Create the menu based on the XML defintion
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.listmenu, menu);
		return true;
	}
	 */

	/*
	// Reaction to the menu selection
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.insert:
			createTodo();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	 */


	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
			.getMenuInfo();
			Uri uri = Uri.parse(MyBookmarkContentProvider.CONTENT_URI + "/"
					+ info.id);
			getContentResolver().delete(uri, null, null);
			fillData();
			return true;

		case EDIT_ID:
			AdapterContextMenuInfo info2 = (AdapterContextMenuInfo) item.getMenuInfo();
			Intent i = new Intent(this, BookmarkDetail.class);
			Uri bookmarkUri = Uri.parse(MyBookmarkContentProvider.CONTENT_URI + "/" + info2.id);
			i.putExtra(MyBookmarkContentProvider.CONTENT_ITEM_TYPE, bookmarkUri);

			startActivity(i);
			return true;

		}


		return super.onContextItemSelected(item);
	}



	// Opens the second activity if an entry is clicked
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		//String[] projection = { BookmarkTable.COLUMN_SUMMARY, BookmarkTable.COLUMN_DESCRIPTION/*, BookmarkTable.COLUMN_CATEGORY*/ };
		//Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

		//Intent i = new Intent(this, MainActivity.class);
		Uri bookmarkUri = Uri.parse(MyBookmarkContentProvider.CONTENT_URI + "/" + id);
		String[] projection = { BookmarkTable.COLUMN_SUMMARY,
				BookmarkTable.COLUMN_DESCRIPTION/*, BookmarkTable.COLUMN_CATEGORY*/ };
		Cursor cursor = getContentResolver().query(bookmarkUri, projection, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();
			bookmarkUrl =cursor.getString(cursor
					.getColumnIndexOrThrow(BookmarkTable.COLUMN_DESCRIPTION));
			cursor.close();
		}
		//i.putExtra(MyContentProvider.CONTENT_ITEM_TYPE, bookmarkUri);
		//i.putExtra("Url", "Google.com");

		//startActivity(i);
		finishWithData();
	}


	public void finishWithData() {
		Intent data = new Intent();
		data.putExtra("Url", bookmarkUrl);
		setResult(RESULT_OK, data);
		finish();
	}
	




	private void fillData() {

		// Fields from the database (projection)
		// Must include the _id column for the adapter to work
		String[] from = new String[] {BookmarkTable.COLUMN_SUMMARY };
		// Fields on the UI to which we map
		int[] to = new int[] { R.id.label };

		getLoaderManager().initLoader(0, null, this);
		adapter = new SimpleCursorAdapter(this, R.layout.bookmark_row, null, from,
				to, 0);

		setListAdapter(adapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, "Delete Bookmark");
		menu.add(0, EDIT_ID, 0, "Edit Bookmark");
	}

	// Creates a new loader after the initLoader () call
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { BookmarkTable.COLUMN_ID, BookmarkTable.COLUMN_SUMMARY };
		CursorLoader cursorLoader = new CursorLoader(this,
				MyBookmarkContentProvider.CONTENT_URI, projection, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// data is not available anymore, delete reference
		adapter.swapCursor(null);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case (R.id.add_bookmark):
			Intent i = new Intent(this, BookmarkDetail.class);
		startActivity(i);
		/*
			ContentValues values = new ContentValues();
			values.put(BookmarkTable.COLUMN_CATEGORY, "Google");
			values.put(BookmarkTable.COLUMN_SUMMARY, "BLAH BLAH");
			values.put(BookmarkTable.COLUMN_DESCRIPTION, "this is google");
			//if(bookmarkUri == null){
				bookmarkUri = getContentResolver().insert(MyContentProvider.CONTENT_URI, values);
		 */

		/*}
			else{
				getContentResolver().update(bookmarkUri, values, null, null);
			}*/
		break;

		}
		// TODO Auto-generated method stub

	}

} 