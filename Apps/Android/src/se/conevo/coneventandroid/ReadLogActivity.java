package se.conevo.coneventandroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class ReadLogActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_read_logs);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);

		TextView logsTextView = (TextView) findViewById(R.id.logsTextView);

		try {
			Process process = Runtime.getRuntime().exec("logcat -d");
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));

			StringBuilder log = new StringBuilder();
			String line = "";
			String logString = "";
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains("ConEvent")) {
					logString = line.substring(line.indexOf(":") + 2);
					log.append(logString + "\n\n");
				}
			}
			bufferedReader.close();
			logsTextView.setText(log.toString());
		} catch (IOException e) {
			Toast.makeText(this,
					"Error reading logs: " + e.getLocalizedMessage(),
					Toast.LENGTH_LONG).show();
		}
	}
}
