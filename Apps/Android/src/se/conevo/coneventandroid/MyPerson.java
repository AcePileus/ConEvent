package se.conevo.coneventandroid;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class MyPerson {
	public int Age;
	public String Name;

	public MyPerson(int age, String name) {
		Age = age;
		Name = name;
	}

	public MyPerson(String jsonString) throws JSONException {
		Log.d("ConEvent", "jsonString: " + jsonString);
		JSONObject jsonObject = new JSONObject(jsonString);
		
		Log.d("ConEvent", "jsonObject: " + jsonObject.toString());
		if (jsonObject.has("Age")) {
			this.Age = jsonObject.getInt("Age");
		} else
			this.Age = 0;
		
		if (jsonObject.has("Name")) {
			this.Name = jsonObject.getString("Name");
		} else
			this.Name = "";
	}

	public String toJSONString() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("Age", Age);
			jsonObject.put("Name", Name);
			return jsonObject.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Name: " + this.Name + ", Age: " + this.Age;
	}
}
