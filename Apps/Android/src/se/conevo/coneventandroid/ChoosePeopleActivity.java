package se.conevo.coneventandroid;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ChoosePeopleActivity extends Activity {

	// private String[] existingPeople = new String[] { "Martin Norin",
	// "Linda Christersson", "Anders Piléus", "Jim Williams" };

	private ProgressDialog progressDialog;
	
	private Person[] existingPeople;

	private ListView peopleListView;

	private OnClickListener buttonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			SparseBooleanArray checked = peopleListView
					.getCheckedItemPositions();
			ArrayList<String> checkedPeople = new ArrayList<String>();

			int checkedSize = checked.size();
			if (checkedSize > 0) {
				int i = 0;
				while (i < checkedSize) {
					int key = checked.keyAt(i);
					boolean value = checked.get(key);
					if (value) {
						Person person = (Person)peopleListView.getItemAtPosition(key);
						checkedPeople.add(person.FirstName + " " + person.LastName);
					}
					i++;
				}
			}

			Bundle b = new Bundle();
			b.putStringArray("people",
					checkedPeople.toArray(new String[checkedPeople.size()]));

			Intent returnIntent = new Intent();
			returnIntent.putExtras(b);
			setResult(RESULT_OK, returnIntent);
			finish();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.choose_people);

		peopleListView = (ListView) findViewById(R.id.peopleListView);		

		Button testButton = (Button) findViewById(R.id.testbutton);
		testButton.setOnClickListener(buttonListener);
		
		progressDialog = new ProgressDialog(this);
		
		new GetPersonsFromWCF(progressDialog).execute();
	}

	private ArrayAdapter<Person> getDataAdapter() {
		ArrayAdapter<Person> adapter = new ArrayAdapter<Person>(this,
				android.R.layout.simple_list_item_multiple_choice,
				existingPeople);

		adapter.sort(new Comparator<Person>() {
			@Override
			public int compare(Person lhs, Person rhs) {
				if (lhs.FirstName == null && rhs.FirstName != null)
					return -1;
				if (lhs.FirstName != null && rhs.FirstName == null)
					return 1;
				if (lhs.FirstName == null && rhs.FirstName == null)
				{
					if (lhs.LastName == null && rhs.LastName != null)
						return -1;
					if (lhs.LastName != null && rhs.LastName == null)
						return 1;
					if (lhs.LastName == null && rhs.LastName == null)
						return 0;
				}											
				return lhs.FirstName.compareToIgnoreCase(rhs.FirstName);
			}
		});
		return adapter;
	}
	
	private void setData(List<Person> persons) {		
		if (persons == null) {
			Person person = new Person();
			person.Email = "@";
			person.FirstName = "";
			person.LastName = "";
			existingPeople = new Person[] { person };
		} else {					
			existingPeople = (Person[]) persons.toArray();
		}
		
		ArrayAdapter<Person> adapter = getDataAdapter();
		peopleListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		peopleListView.setAdapter(adapter);
	}

	private class GetPersonsFromWCF extends AsyncTask<Void, Void, List<Person>> {

		private ProgressDialog _progressDialog;
		
		public GetPersonsFromWCF(ProgressDialog progressDialog) {
			_progressDialog = progressDialog;
		}
		
		@Override
		protected void onPreExecute() {
			_progressDialog.setTitle("Hämtar personer...");
			_progressDialog.setMessage("Vänligen vänta.");
			_progressDialog.setCancelable(false);
			_progressDialog.setIndeterminate(true);								
			_progressDialog.show();
		}
		
		@Override
		protected List<Person> doInBackground(Void... voids) {

			try {
				ConEventService service = new ConEventService(ChoosePeopleActivity.this);
				return service.GetPersons();
			} catch (Exception e) {
				Log.d("ConEvent",
						"GetPersonsFromWCF, Error: " + e.getLocalizedMessage());
			}

			return null;
		}

		@Override
		protected void onPostExecute(List<Person> result) {
			super.onPostExecute(result);
			
			setData(result);
			
			if (_progressDialog != null) {
				_progressDialog.dismiss();
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
}
