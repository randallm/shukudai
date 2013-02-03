package com.randallma.whatsthehomework;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class PostAssignmentActivity extends Activity {

	Button open_camera;
	Uri fileUri;
	File photo;

	private File createTemporaryFile(String name, String extension)
			throws Exception {
		File tempDir = Environment.getExternalStorageDirectory();
		tempDir = new File(tempDir.getAbsolutePath() + "/.temp/");
		if (!tempDir.exists()) {
			tempDir.mkdir();
		}
		return File.createTempFile(name, extension, tempDir);
	}

	class PostAssignmentTask extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(params[0]);
			// httppost.setHeader("Content-Type", "application/json");
			// httppost.setHeader("Content-Length", "");
			httppost.setHeader("charseAssignmentActivity.clt", "utf-8");

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("name", "Tom"));

			try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				HttpResponse response = httpclient.execute(httppost);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle("New Homework Assignment");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_assignment);

		// http://stackoverflow.com/questions/6448856/android-camera-intent-how-to-get-full-sized-photo

		// open_camera = (Button) findViewById(R.id.take_upload_photo);
		// open_camera.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// Intent takePictureIntent = new Intent(
		// MediaStore.ACTION_IMAGE_CAPTURE);
		//
		// try {
		// // place where to store camera taken picture
		// photo = createTemporaryFile("picture", ".jpg");
		// photo.delete();
		// } catch (Exception e) {
		// }
		// fileUri = Uri.fromFile(photo);
		// takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		// startActivityForResult(takePictureIntent, 100); // second int
		// // turns into
		// // resultCode
		//
		// }
		// });

		// spinner code

		Spinner periodSpinner = (Spinner) findViewById(R.id.periodSpinner);
		String[] periodSpinnerItems = new String[] { "AP World History",
				"IB Romance" };
		ArrayAdapter<String> periodSpinnerAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, periodSpinnerItems);
		periodSpinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		periodSpinner.setAdapter(periodSpinnerAdapter);
		System.out.println(periodSpinner.getSelectedItem().toString());

	}

	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
	}

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

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

					// http://stackoverflow.com/questions/9224056/android-bitmap-to-base64-string

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
					byte[] input = baos.toByteArray();
					String encoded = Base64.encodeToString(input,
							Base64.DEFAULT);
					// toast message this shit
					try {
						new PostAssignmentTask()
								.execute("http://192.168.1.42:8000/post/");
					} catch (Exception e) {
						System.out.println("you fucked up");
					}

				} catch (Exception e) {

				}
			}

			super.onActivityResult(requestCode, resultCode, data);
		} else {
			// catch this scenario
		}
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
			parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(parentActivityIntent);
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
