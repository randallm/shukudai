package com.randallma.whatsthehomework;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

public class PostAssignmentActivity extends Activity {

	protected HomeworkAssignment homeworkAssignment = new HomeworkAssignment();

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
					bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
					byte[] input = baos.toByteArray();
					String encoded = Base64.encodeToString(input,
							Base64.DEFAULT);

					homeworkAssignment.setPhoto(encoded);

				} catch (Exception e) {

				}
			}

			super.onActivityResult(requestCode, resultCode, data);
		} else {
			// catch this scenario
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle("New Homework Assignment");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_assignment);

		// cancel assignment posting code

		LinearLayout cancelAssignmentButton = (LinearLayout) findViewById(R.id.cancelAssignmentButton);
		cancelAssignmentButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent mainActivityIntent = new Intent(
						PostAssignmentActivity.this, MainActivity.class);
				mainActivityIntent
						.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(mainActivityIntent);
				finish();
			}
		});

		// camera code

		ImageButton open_camera = (ImageButton) findViewById(R.id.takePhoto);
		open_camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent takePictureIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);

				try {
					photo = createTemporaryFile("picture", ".jpg");
					photo.delete();
				} catch (Exception e) {
				}
				fileUri = Uri.fromFile(photo);
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
				startActivityForResult(takePictureIntent, 100);
			}
		});

		// spinner code

		Spinner periodSpinner = (Spinner) findViewById(R.id.periodSpinner);

		int[] periodSpinnerIds = new int[] { 12, 24 };
		String[] periodSpinnerItems = new String[] { "AP World History",
				"IB Romance" };
		ArrayAdapter<String> periodSpinnerAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, periodSpinnerItems);
		periodSpinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		periodSpinner.setAdapter(periodSpinnerAdapter);
		int periodSpinnerIdPos = periodSpinner.getSelectedItemPosition();

		homeworkAssignment.setSchoolClass(periodSpinnerIds[periodSpinnerIdPos]);

		// submit assignment code

		LinearLayout postAssignmentButton = (LinearLayout) findViewById(R.id.postAssignmentButton);
		postAssignmentButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("post button clicked");
				postAssignment();
			}
		});
	}

	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
	}

	protected void postAssignment() {
		ApplicationGlobal g = (ApplicationGlobal) getApplication();

		AsyncHttpClient clientSession = new AsyncHttpClient();
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		clientSession.setCookieStore(cookieStore);

		RequestParams params = new RequestParams();
		params.put("class_id",
				Integer.toString(homeworkAssignment.getSchoolClass()));
		params.put("photo", homeworkAssignment.getPhoto());
		params.put("description", homeworkAssignment.getDescription());

		clientSession.post(g.getWthUrl() + "/hw/new_assignment/", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onStart() {
					}

					@Override
					public void onSuccess(String response) {
						System.out.println(response);

						Toast.makeText(PostAssignmentActivity.this,
								"Homework successfully posted",
								Toast.LENGTH_SHORT).show();
						finish();
					}

					@Override
					public void onFailure(Throwable e, String response) {
						System.out.println(response);
					}
				});
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
