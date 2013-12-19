package se.conevo.coneventandroid;

import se.conevo.coneventandroid.ConEventAndroidContract.ConEventAndroidEntry;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ReadHistoryActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_read_history);

		ListView historyListView = (ListView) findViewById(R.id.historyListView);
		SimpleCursorAdapter historyAdapter = getHistoryAdapter();

		historyListView.setAdapter(historyAdapter);
	}

	private SimpleCursorAdapter getHistoryAdapter() {
		ConEventAndroidDbHelper dbHelper = new ConEventAndroidDbHelper(this);

		SQLiteDatabase db = dbHelper.getReadableDatabase();

		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = { ConEventAndroidEntry._ID,
				ConEventAndroidEntry.COLUMN_NAME_IMAGE_PATH,
				ConEventAndroidEntry.COLUMN_NAME_FILE_NAME,
				ConEventAndroidEntry.COLUMN_NAME_IMAGE_CAPTION,
				ConEventAndroidEntry.COLUMN_NAME_CONTENT_TYPE,
				ConEventAndroidEntry.COLUMN_NAME_PERSON_KEYS,
				ConEventAndroidEntry.COLUMN_NAME_EVENT_DATE,
				ConEventAndroidEntry.COLUMN_NAME_LATITUDE,
				ConEventAndroidEntry.COLUMN_NAME_LONGITUDE,
				ConEventAndroidEntry.COLUMN_NAME_IMAGE_ARRAY_SIZE};

		// How you want the results sorted in the resulting Cursor
		String sortOrder = ConEventAndroidEntry._ID + " DESC";

		Cursor c = db.query(ConEventAndroidEntry.TABLE_NAME, // The table to
																// query
				projection, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				sortOrder // The sort order
				);

		// long id;
		// String fileName;
		// String contentType;
		// String personKeys;
		// StringBuilder sb = new StringBuilder();
		// while (c.moveToNext()) {
		// id = c.getLong(c.getColumnIndex(ConEventAndroidEntry._ID));
		// fileName = c
		// .getString(c
		// .getColumnIndex(ConEventAndroidEntry.COLUMN_NAME_FILE_NAME));
		// contentType =
		// c.getString(c.getColumnIndex(ConEventAndroidEntry.COLUMN_NAME_CONTENT_TYPE));
		// personKeys = c
		// .getString(c
		// .getColumnIndex(ConEventAndroidEntry.COLUMN_NAME_PERSON_KEYS));
		// sb.append(id + ": " + fileName + " " + contentType + " " + personKeys
		// + "\n\n");
		// }

		String[] from = new String[] {
				ConEventAndroidEntry.COLUMN_NAME_IMAGE_PATH,
				ConEventAndroidEntry.COLUMN_NAME_FILE_NAME,
				ConEventAndroidEntry.COLUMN_NAME_PERSON_KEYS,
				ConEventAndroidEntry.COLUMN_NAME_IMAGE_CAPTION,
				ConEventAndroidEntry.COLUMN_NAME_EVENT_DATE,
				ConEventAndroidEntry.COLUMN_NAME_LATITUDE,
				ConEventAndroidEntry.COLUMN_NAME_LONGITUDE,
				ConEventAndroidEntry.COLUMN_NAME_CONTENT_TYPE,
				ConEventAndroidEntry.COLUMN_NAME_IMAGE_ARRAY_SIZE};
		int[] to = new int[] {
				R.id.historyImagePathTextView,
				R.id.historyFileNameTextView,
				R.id.historyPersonsTextView, R.id.historyCaptionTextView,
				R.id.historyEventDateTextView,
				R.id.historyLatitudeTextView,
				R.id.historyLongitudeTextView,
				R.id.historyContentTypeTextView,
				R.id.historyImageSizeTextView};

		@SuppressWarnings("deprecation")
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.list_row, c, from, to);

		return adapter;
	}

}
