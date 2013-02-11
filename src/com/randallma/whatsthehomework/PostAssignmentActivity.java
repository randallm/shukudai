package com.randallma.whatsthehomework;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

public class PostAssignmentActivity extends Activity {

	public final static String COMPLETED_POST = "com.randallma.whatsthehomework.COMPLETED_POST";

	String b64Photo;
	int schoolClass;

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	Uri fileUri;
	File photo;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				this.getContentResolver().notifyChange(fileUri, null);
				ContentResolver cr = this.getContentResolver();
				Bitmap bitmap;
				try {
					bitmap = MediaStore.Images.Media.getBitmap(cr, fileUri);
					// bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400,
					// true); // resize bitmap to not crash program (max
					// 2048x2048)

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
					byte[] input = baos.toByteArray();
					String encoded = Base64.encodeToString(input,
							Base64.DEFAULT);

					b64Photo = encoded;

				} catch (Exception e) {

				}
			}

			super.onActivityResult(requestCode, resultCode, data);
		} else {
			// TODO: catch this scenario
		}
	}

	private File createTemporaryFile(String name, String extension)
			throws Exception {
		File tempDir = Environment.getExternalStorageDirectory();
		tempDir = new File(tempDir.getAbsolutePath() + "/.temp/");
		if (!tempDir.exists()) {
			tempDir.mkdir();
		}
		return File.createTempFile(name, extension, tempDir);
	}

	private void populateDateBoxes() {
		Calendar c = Calendar.getInstance();

		TextView todayInfo = (TextView) findViewById(R.id.todayInfo);
		String month = Integer.toString(c.get(Calendar.MONTH));
		String day = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
		String year = Integer.toString(c.get(Calendar.YEAR)).substring(2, 4);
		todayInfo.setText(Html.fromHtml("Date Today:<br>" + month + "/" + day
				+ "/" + year));

		TextView tomorrowInfo = (TextView) findViewById(R.id.tomorrowInfo);
		c.add(Calendar.DAY_OF_YEAR, 7);
		month = Integer.toString(c.get(Calendar.MONTH));
		day = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
		year = Integer.toString(c.get(Calendar.YEAR)).substring(2, 4);
		tomorrowInfo.setText(Html.fromHtml("Date Next Week:<br>" + month + "/"
				+ day + "/" + year));
	}

	public void cancelAssignment(View v) {
		Intent mainActivityIntent = new Intent(this, MainActivity.class);
		mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(mainActivityIntent);
		finish();
	}

	public void takePhotoOfAssignment(View v) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		try {
			photo = createTemporaryFile("picture", ".jpg");
			photo.delete();
		} catch (Exception e) {
		}
		fileUri = Uri.fromFile(photo);
		takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		startActivityForResult(takePictureIntent, 100);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_assignment);

		populateDateBoxes();
		initSchoolClassSpinner();
	}

	private void initSchoolClassSpinner() {
		ApplicationGlobal g = (ApplicationGlobal) getApplication();

		Spinner periodSpinner = (Spinner) findViewById(R.id.periodSpinner);
		ArrayList<String> periodSpinnerItems = g.getSchoolClassItems();
		ArrayAdapter<String> periodSpinnerAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, periodSpinnerItems);
		periodSpinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		periodSpinner.setAdapter(periodSpinnerAdapter);

		periodSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				ApplicationGlobal g = (ApplicationGlobal) getApplication();
				ArrayList<Integer> schoolClassIds = g.getSchoolClassIds();
				schoolClass = schoolClassIds.get(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	public void showDatePickerDialog(View v) {
		DialogFragment datePickerFragment = new DatePickerFragment();
		datePickerFragment.show(getFragmentManager(), "datePicker");
	}

	public boolean postAssignment(View v) {
		ApplicationGlobal g = (ApplicationGlobal) getApplication();

		AsyncHttpClient clientSession = new AsyncHttpClient();
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		clientSession.setCookieStore(cookieStore);

		RequestParams params = new RequestParams();

		params.put("class_id", Integer.toString(schoolClass));

		EditText descriptionEditText = (EditText) findViewById(R.id.description);
		String description = descriptionEditText.getText().toString();

		if ((description == null) && (b64Photo == null)) {
			Toast.makeText(PostAssignmentActivity.this,
					"Error: Need Description or Photo", Toast.LENGTH_SHORT);
		}

		if (description != null) {
			params.put("description", description);
		} else {
			params.put("description", "");
		}

		if (b64Photo != null) {
			params.put("b64_photo", b64Photo);
		} else {
			params.put("b64_photo", "");
		}

		final Button dateDue = (Button) findViewById(R.id.dateDue);
		if (dateDue.getText().toString().equals("Date Due")) {
			Toast.makeText(this, "Error: Due Date Unselected",
					Toast.LENGTH_LONG).show();
			return false;
		} else {
			params.put("date_due", dateDue.getText().toString());
		}

		clientSession.post(g.getWthUrl() + "/hw/new_assignment/", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						Toast.makeText(PostAssignmentActivity.this,
								"Assignment Posted", Toast.LENGTH_SHORT).show();
						Intent completedPostIntent = new Intent(
								PostAssignmentActivity.this, MainActivity.class);
						completedPostIntent.putExtra(COMPLETED_POST, "true");
						startActivity(completedPostIntent);
						finish();
					}

					@Override
					public void onFailure(Throwable e, String response) {
						if (response != null) {
							Toast.makeText(PostAssignmentActivity.this,
									response, Toast.LENGTH_SHORT).show();
						}
					}
				});

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_post_assignment, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent parentActivityIntent = new Intent(this, MainActivity.class);
			parentActivityIntent
					.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(parentActivityIntent);
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		Intent mainActivityIntent = new Intent(this, MainActivity.class);
		mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(mainActivityIntent);
		finish();
	}
}
