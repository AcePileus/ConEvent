package se.conevo.coneventandroid;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ConEventService {

	private static final String URL = "http://8b7a7182d17045db9db02a04e2329333.cloudapp.net:8080/ConEventService.svc";

	private Context context;

	public ConEventService(Context context) {
		this.context = context;
	}

	public List<Person> GetPersons() {

		try {
			CheckForConnectivity();
		} catch (Exception e1) {
			Log.d("ConEvent", "Exception: " + e1.getLocalizedMessage());
			return null;
		}

		List<Person> persons = null;

		HttpClient httpClient = new DefaultHttpClient();

		HttpGet getRequest = new HttpGet(URL + "/GetPersons");

		try {
			HttpResponse response = httpClient.execute(getRequest);

			if (response != null) {
				HttpEntity responseEntity = response.getEntity();
				String jsonResponse = EntityUtils.toString(responseEntity);
				Gson gson = new Gson();
				Person[] personArray = gson.fromJson(jsonResponse,
						Person[].class);
				persons = Arrays.asList(personArray);
			} else {
				Log.d("ConEvent", "Response is null");
			}
		} catch (JsonSyntaxException e) {
			Log.d("ConEvent", "JsonSyntaxException: " + e.getLocalizedMessage());
		} catch (ClientProtocolException e) {
			Log.d("ConEvent",
					"ClientProtocolException: " + e.getLocalizedMessage());
		} catch (ParseException e) {
			Log.d("ConEvent", "ParseException: " + e.getLocalizedMessage());
		} catch (IOException e) {
			Log.d("ConEvent", "IOException: " + e.getLocalizedMessage());
		}

		return persons;
	}

	private void CheckForConnectivity() throws Exception {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null
				&& activeNetwork.isConnectedOrConnecting();
		if (!isConnected)
			throw new Exception("No network connection available");
	}	

	public String SaveImageEvent(SendEventContainer event) {
		try {			
			CheckForConnectivity();			
		} catch (Exception e1) {
			Log.d("ConEvent", "SaveImageEvent/CheckForConnectivity Exception: "
					+ e1.getLocalizedMessage());
			return null;
		}

		String errorReturn;
		try {
			HttpClient httpClient = new DefaultHttpClient();

			HttpPost postRequest = new HttpPost(URL + "/SaveImageEvent");

			String jsonString = CreateJsonString(event);

			Log.d("ConEvent", "jsonString.length(): " + jsonString.length());
			
			StringEntity entity = new StringEntity(jsonString);
			entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			entity.setContentType("application/json");
			postRequest.setEntity(entity);
			postRequest.setHeader("Accept", "application/json; charset=utf-8");

			HttpResponse response = httpClient.execute(postRequest);

			if (response != null) {

				HttpEntity responseEntity = response.getEntity();
				String jsonResponse = EntityUtils.toString(responseEntity);
				Log.d("ConEvent", "JSONResponse: " + jsonResponse);
				return "OK";
			}
		} catch (UnsupportedEncodingException e) {
			errorReturn = "SaveImageEvent UnsupportedEncodingException: "
					+ e.getLocalizedMessage();
			Log.d("ConEvent", errorReturn);
			
		} catch (ClientProtocolException e) {
			errorReturn = "SaveImageEvent ClientProtocolException: "
					+ e.getLocalizedMessage();
			
			Log.d("ConEvent", errorReturn);
		} catch (ParseException e) {
			errorReturn = "SaveImageEvent ParseException: " + e.getLocalizedMessage();
			Log.d("ConEvent", errorReturn);
		} catch (IOException e) {
			errorReturn = "SaveImageEvent IOException: " + e.getLocalizedMessage();
			Log.d("ConEvent", errorReturn);
		} catch (Exception e) {
			errorReturn = "SaveImageEvent Exception: " + e.getLocalizedMessage();
			Log.d("ConEvent", errorReturn);
		}

		return null;
	}

	@SuppressLint("SimpleDateFormat")
	private String CreateJsonString(SendEventContainer event) {
		try {
			JSONObject jsonObject = new JSONObject();			
			
			String imageArrayString = Base64.encodeToString(event.imageArray, Base64.NO_WRAP);
//			byte[] tempByteArray = Base64.decode(imageArrayString, Base64.NO_WRAP);
//			Log.d("ConEvent", "event.imageArray.length: " + event.imageArray.length + ", tempByteArray.length: " + tempByteArray.length);
//			Log.d("ConEvent", "imageArrayString: " + imageArrayString);
			 
			jsonObject.put("base64Image", imageArrayString);
			jsonObject.put("fileName", event.fileName);
			jsonObject.put("contentType", event.contentType);
			jsonObject.put("imageCaption", event.imageCaption);

			JSONArray personArray = new JSONArray();
			if (event.personKeys == null || event.personKeys.length == 0) {
				personArray.put("none");
			} else {
				for (String person : event.personKeys) {
					personArray.put(person);
				}
			}

			jsonObject.put("personKeys", personArray);

			JSONArray locationArray = new JSONArray();
			for (double loc : event.location) {
				locationArray.put(loc);
			}

			jsonObject.put("location", locationArray);
			
			jsonObject.put("eventDate", "/Date(" + event.eventDate.getTime() + ")/");

			String jsonString = jsonObject.toString();
			return jsonString;
		} catch (JSONException e) {
			Log.d("ConEvent",
					"CreateJsonString JSONException: "
							+ e.getLocalizedMessage());
		}
		return null;
	}
}
