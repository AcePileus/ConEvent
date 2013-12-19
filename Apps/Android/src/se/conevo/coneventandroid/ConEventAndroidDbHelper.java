package se.conevo.coneventandroid;

import se.conevo.coneventandroid.ConEventAndroidContract.ConEventAndroidEntry;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConEventAndroidDbHelper extends SQLiteOpenHelper {

	private static final String TEXT_TYPE = " TEXT";
	private static final String DATETIME_TYPE = " TEXT";
	private static final String DOUBLE_TYPE = " REAL";
//	private static final String BYTE_ARRAY_TYPE = " BLOB";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
			+ ConEventAndroidEntry.TABLE_NAME + " (" + ConEventAndroidEntry._ID
			+ " INTEGER PRIMARY KEY,"
			+ ConEventAndroidEntry.COLUMN_NAME_IMAGE_PATH + TEXT_TYPE + COMMA_SEP + 
			ConEventAndroidEntry.COLUMN_NAME_IMAGE_ARRAY_SIZE + TEXT_TYPE + COMMA_SEP +
			ConEventAndroidEntry.COLUMN_NAME_FILE_NAME
			+ TEXT_TYPE + COMMA_SEP
			+ ConEventAndroidEntry.COLUMN_NAME_CONTENT_TYPE + TEXT_TYPE
			+ COMMA_SEP + ConEventAndroidEntry.COLUMN_NAME_IMAGE_CAPTION
			+ TEXT_TYPE + COMMA_SEP
			+ ConEventAndroidEntry.COLUMN_NAME_PERSON_KEYS + TEXT_TYPE
			+ COMMA_SEP + ConEventAndroidEntry.COLUMN_NAME_LATITUDE
			+ DOUBLE_TYPE + COMMA_SEP
			+ ConEventAndroidEntry.COLUMN_NAME_LONGITUDE + DOUBLE_TYPE
			+ COMMA_SEP + ConEventAndroidEntry.COLUMN_NAME_EVENT_DATE
			+ DATETIME_TYPE + " )";

	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
			+ ConEventAndroidEntry.TABLE_NAME;

	public static final int DATABASE_VERSION = 4;
	public static final String DATABASE_NAME = "ConEventAndroidDatabase.db";

	public ConEventAndroidDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ENTRIES);
		db.execSQL(SQL_CREATE_ENTRIES);
	}

}
