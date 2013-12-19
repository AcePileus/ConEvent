package se.conevo.coneventandroid;

import android.provider.BaseColumns;

public final class ConEventAndroidContract {

	public ConEventAndroidContract() { }
	
	public static abstract class ConEventAndroidEntry implements BaseColumns {
        public static final String TABLE_NAME = "androidEventEntry";        
        public static final String COLUMN_NAME_IMAGE_PATH = "imagePath";
        public static final String COLUMN_NAME_IMAGE_ARRAY_SIZE = "imageArraySize";
    	public static final String COLUMN_NAME_FILE_NAME = "fileName";
    	public static final String COLUMN_NAME_CONTENT_TYPE = "contentType";
    	public static final String COLUMN_NAME_IMAGE_CAPTION = "imageCaption";
    	public static final String COLUMN_NAME_PERSON_KEYS = "personKeys";
    	public static final String COLUMN_NAME_LATITUDE = "latitude";
    	public static final String COLUMN_NAME_LONGITUDE = "longitude";
    	public static final String COLUMN_NAME_EVENT_DATE = "eventDate";
    }

}
