package se.conevo.coneventandroid;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import se.conevo.coneventandroid.ConEventAndroidContract.ConEventAndroidEntry;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StartPageActivity extends Activity {

	private ProgressDialog progressDialog;
	private EditText captionEditText;

	private String[] people;
	private byte[] image = new byte[0];
	private double[] locationArray;
	private String fileName;
	private String contentType;

	private Uri fileUri;

	private LocationManager locationManager;

	private static final int REQUEST_CODE_CHOOSE_PEOPLE = 1;
	private static final int REQUEST_CODE_TAKE_PICTURE = 2;

	private LocationListener locationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onLocationChanged(Location location) {
			if (locationManager != null) {
				locationArray[0] = location.getLatitude();
				locationArray[1] = location.getLongitude();
				locationManager.removeUpdates(locationListener);

				Toast t = Toast.makeText(StartPageActivity.this,
						"Location obtained from " + location.getProvider(),
						Toast.LENGTH_LONG);
				t.show();
			}
		}
	};	

	private OnClickListener takePictureButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			fileUri = getOutputMediaFileUri();
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

			startActivityForResult(cameraIntent, REQUEST_CODE_TAKE_PICTURE);
		}
	};

	private OnClickListener choosePeopleButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(StartPageActivity.this,
					ChoosePeopleActivity.class);
			startActivityForResult(intent, REQUEST_CODE_CHOOSE_PEOPLE);
		}
	};

	private OnClickListener sendEventButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			SendEventContainer event = new SendEventContainer();
			event.contentType = contentType;
			event.eventDate = new Date();
			event.personKeys = people;
			event.imageArray = image;
			event.fileName = fileName;
			event.imageCaption = captionEditText.getText().toString();
			event.location = locationArray;

			progressDialog = new ProgressDialog(StartPageActivity.this);

			new SendEventOverWCF(progressDialog).execute(event);
		}
	};

	private OnClickListener locationButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			// Turn on GPS if off?

			if (locationManager == null)
				locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setAltitudeRequired(false);
			criteria.setPowerRequirement(Criteria.POWER_HIGH);
			criteria.setBearingRequired(false);
			criteria.setCostAllowed(true);
			criteria.setSpeedRequired(false);
			String bestProvider = locationManager.getBestProvider(criteria,
					true);

			Toast t = Toast.makeText(StartPageActivity.this,
					"Getting location from " + bestProvider, Toast.LENGTH_LONG);
			t.show();

			int minDistanceInMeters = 5;
			int minTimeInMilliseconds = 1000;
			locationManager.requestLocationUpdates(bestProvider,
					minTimeInMilliseconds, minDistanceInMeters,
					locationListener);
		}
	};

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// Log.d("ConEvent", "onSaveInstanceState");

		outState.putString("caption", captionEditText.getText().toString());
		// Log.d("ConEvent", "caption: " +
		// captionEditText.getText().toString());

		outState.putStringArray("people", people);
		// Log.d("ConEvent", "people: " + people);
		// if (people != null) {
		// for (String p : people) {
		// Log.d("ConEvent", "existing person: " + p);
		// }
		// } else
		// Log.d("ConEvent", "no persons");

		outState.putDoubleArray("location", locationArray);
		// Log.d("ConEvent", "location: " + locationArray);

		outState.putString("fileName", fileName);
		// Log.d("ConEvent", "fileName: " + fileName);

		outState.putString("contentType", contentType);
		// Log.d("ConEvent", "contentType: " + contentType);

		String filePath = fileUri == null ? null : fileUri.getPath();
		outState.putString("filePath", filePath);
		// Log.d("ConEvent", "filePath: " + filePath);

		outState.putByteArray("image", image);
		// Log.d("ConEvent", "image: " + image);
	}

	private void restoreData(Bundle savedInstanceState) {
		captionEditText.setText(savedInstanceState.getString("caption"));
		// if (people != null) {
		// for (String p : people) {
		// Log.d("ConEvent", "existing person: " + p);
		// }
		// }
		if (people == null || people.length == 0)
			people = savedInstanceState.getStringArray("people");
		locationArray = savedInstanceState.getDoubleArray("location");
		fileName = savedInstanceState.getString("fileName");
		contentType = savedInstanceState.getString("contentType");
		String filePath = savedInstanceState.getString("filePath");
		if (filePath != null)
			fileUri = Uri.fromFile(new File(filePath));
		else
			fileUri = null;
		image = savedInstanceState.getByteArray("image");
	}

	private void initData() {
		captionEditText.setText("");
		// people = new String[] { "[]" };
		image = new byte[0];
		locationArray = new double[] { 0, 0 };
		fileName = "";
		contentType = "";
		fileUri = null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.start_page_layout);

		captionEditText = (EditText) findViewById(R.id.captionEditText);

		Button takePictureButton = (Button) findViewById(R.id.takePictureButton);
		takePictureButton.setOnClickListener(takePictureButtonListener);

		Button choosePeopleButton = (Button) findViewById(R.id.choosePeopleButton);
		choosePeopleButton.setOnClickListener(choosePeopleButtonListener);

		Button sendEventButton = (Button) findViewById(R.id.sendEventButton);
		sendEventButton.setOnClickListener(sendEventButtonListener);

		Button getLocationButton = (Button) findViewById(R.id.getLocationButton);
		getLocationButton.setOnClickListener(locationButtonListener);		

		if (savedInstanceState != null)
			restoreData(savedInstanceState);
		else
			initData();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_CHOOSE_PEOPLE) {
				people = data.getExtras().getStringArray("people");
			}
			if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
				image = getImageByteArray();
				if (image == null) {
					// TODO ERROR
				}
			}
		}
	}

	private Uri getOutputMediaFileUri() {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		// if (Environment.getExternalStorageState() !=
		// Environment.MEDIA_MOUNTED)
		// {
		//
		// }

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"ConEvent");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}

		Locale current = getResources().getConfiguration().locale;
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", current)
				.format(new Date());
		File mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "IMG_" + timeStamp + ".jpg");
		fileName = mediaFile.getName();

		contentType = getMimeType(mediaFile.getAbsolutePath());
		// Log.d("ConEvent", "contentType: " + contentType);

		return Uri.fromFile(mediaFile);
	}

	public static String getMimeType(String url) {
		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (extension != null) {
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			type = mime.getMimeTypeFromExtension(extension);
		}
		return type;
	}

	public int getCameraPhotoOrientation(String imagePath) {
		int rotate = 0;
		try {
			File imageFile = new File(imagePath);

			ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			}

			// Log.d("ConEvent", "Exif orientation: " + orientation);
			// Log.d("ConEvent", "Rotate value: " + rotate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rotate;
	}

	private Bitmap rotateImage(Bitmap image, String imagePath) {
		int rotate = getCameraPhotoOrientation(imagePath);

		Matrix matrix = new Matrix();
		matrix.postRotate(rotate);

		return Bitmap.createBitmap(image, 0, 0, image.getWidth(),
				image.getHeight(), matrix, true);
	}

	private byte[] getImageByteArray() {
		try {
			Bitmap image = BitmapFactory.decodeFile(fileUri.getPath());
			image = rotateImage(image, fileUri.getPath());
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			byte[] byteArray = stream.toByteArray();
						
			stream.close();
			
			return byteArray;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {

		}
		return null;
	}

	public void setResult(String result) {
		if (result == "OK") {
			Toast.makeText(this, "Händelse skickad", Toast.LENGTH_LONG).show();
			initData();
		}
		else {
			Toast.makeText(this, result, Toast.LENGTH_LONG).show();
		}
	}

	private class SendEventOverWCF extends
			AsyncTask<SendEventContainer, Void, String> {

		private ProgressDialog _progressDialog;

		public SendEventOverWCF(ProgressDialog progressDialog) {
			_progressDialog = progressDialog;
		}

		@Override
		protected void onPreExecute() {
			_progressDialog.setTitle("Skickar...");
			_progressDialog.setMessage("Vänligen vänta.");
			_progressDialog.setCancelable(false);
			_progressDialog.setIndeterminate(true);
			_progressDialog.show();
		}

		@Override
		protected String doInBackground(SendEventContainer... params) {

			try {
				
				SaveToDatabase(params[0]);
				
//				String result = new ConEventService(StartPageActivity.this)
//						.SaveImageEvent(params[0]);
//				Log.d("ConEvent", "Result: " + result);
//				return result;
				Log.d("ConEvent", "Saved to database");
				return "Saved to database";
			} catch (Exception e) {
				String message = "Error: " + e.getLocalizedMessage(); 
				Log.d("ConEvent", message);
				return message;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			setResult(result);

			if (_progressDialog != null) {
				_progressDialog.dismiss();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	
	public void SaveToDatabase(SendEventContainer sendEventContainer) {
		ConEventAndroidDbHelper dbHelper = new ConEventAndroidDbHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();	
		
		ContentValues values = getContentValues(sendEventContainer);	
		
		db.insert(ConEventAndroidEntry.TABLE_NAME, "null", values);
	}

	@SuppressLint("SimpleDateFormat")
	private ContentValues getContentValues(SendEventContainer sendEventContainer) {
		ContentValues values = new ContentValues();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		values.put(ConEventAndroidEntry.COLUMN_NAME_IMAGE_PATH, fileUri.getPath());
		values.put(ConEventAndroidEntry.COLUMN_NAME_IMAGE_ARRAY_SIZE, sendEventContainer.imageArray.length);
		values.put(ConEventAndroidEntry.COLUMN_NAME_FILE_NAME, sendEventContainer.fileName);
		values.put(ConEventAndroidEntry.COLUMN_NAME_CONTENT_TYPE, sendEventContainer.contentType);
		values.put(ConEventAndroidEntry.COLUMN_NAME_IMAGE_CAPTION, sendEventContainer.imageCaption);
		values.put(ConEventAndroidEntry.COLUMN_NAME_PERSON_KEYS, TextUtils.join(",", sendEventContainer.personKeys));
		values.put(ConEventAndroidEntry.COLUMN_NAME_LATITUDE, sendEventContainer.location[0]);
		values.put(ConEventAndroidEntry.COLUMN_NAME_LONGITUDE, sendEventContainer.location[1]);
		values.put(ConEventAndroidEntry.COLUMN_NAME_EVENT_DATE, sdf.format(sendEventContainer.eventDate));
		
		return values;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.start_page_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.itemSettings:
	            openSettings();
	            return true;
	        case R.id.itemViewHistory:
	            openHistory();
	            return true;
	        case R.id.itemViewLogs:
	        	openLogs();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	private void openLogs() {
		Intent intent = new Intent(this, ReadLogActivity.class);
		startActivity(intent);
	}

	private void openHistory() {
		Intent intent = new Intent(this, ReadHistoryActivity.class);
		startActivity(intent);
	}

	private void openSettings() {
		Toast.makeText(this, "Opening settings", Toast.LENGTH_SHORT).show();		
	}
}
