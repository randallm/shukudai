package com.randallma.whatsthehomework;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class PostAssignmentActivity extends Activity {

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private File photo;
	private Uri photoUri;

	private Spinner classSpinner;
	private ArrayList<Integer> schoolClassIds;
	private ArrayList<String> schoolClassItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_assignment);

		// populateDateBoxes();
		initSchoolClassSpinner();
	}

	public void takePhoto(View v) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		try {
			photo = File.createTempFile("assignment_photo_", ".jpg",
					getPhotoStorageDir(this, "assignment_photos"));
			photo.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}

		photoUri = Uri.fromFile(photo);
		takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
		startActivityForResult(takePictureIntent,
				CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	private File getPhotoStorageDir(Context context, String name) {
		File file = new File(
				context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
				name);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// disable "Add" button until image saving process is done
				LinearLayout postAssignmentButton = (LinearLayout) findViewById(R.id.postAssignmentButton);
				postAssignmentButton.setClickable(false);

				this.getContentResolver().notifyChange(photoUri, null);
				ContentResolver cr = this.getContentResolver();

				try {
					Bitmap photoBmp = MediaStore.Images.Media.getBitmap(cr,
							photoUri);

					FileOutputStream fileOut = new FileOutputStream(photo);
					photoBmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOut);
					fileOut.flush();
					fileOut.close();

					postAssignmentButton.setClickable(true);

					System.out.println(photo.getAbsolutePath());
					System.out.println(photoUri);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				photo = null;
				photoUri = null;
			}

			super.onActivityResult(requestCode, resultCode, data);
		} else {
			Log.e(PostAssignmentActivity.class.getName(),
					"Camera capture failed");
		}
	}

	// private void populateDateBoxes() {
	// Calendar c = Calendar.getInstance();
	//
	// TextView todayInfo = (TextView) findViewById(R.id.todayInfo);
	// String month = Integer.toString(c.get(Calendar.MONTH));
	// String day = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
	// String year = Integer.toString(c.get(Calendar.YEAR)).substring(2, 4);
	// todayInfo.setText(Html.fromHtml("Date Today:<br>" + month + "/" + day
	// + "/" + year));
	//
	// TextView tomorrowInfo = (TextView) findViewById(R.id.tomorrowInfo);
	// c.add(Calendar.DAY_OF_YEAR, 7);
	// month = Integer.toString(c.get(Calendar.MONTH));
	// day = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
	// year = Integer.toString(c.get(Calendar.YEAR)).substring(2, 4);
	// tomorrowInfo.setText(Html.fromHtml("Date Next Week:<br>" + month + "/"
	// + day + "/" + year));
	// }

	@SuppressLint("UseSparseArrays")
	private void initSchoolClassSpinner() {
		schoolClassIds = new ArrayList<Integer>();
		schoolClassItems = new ArrayList<String>();

		final SQLiteHelper dbHelper = new SQLiteHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor cursor = db.query(SQLiteHelper.TABLE_SCHOOL_CLASSES, null, null,
				null, null, null, SQLiteHelper.COLUMN_ID + " DESC");
		cursor.moveToFirst();
		for (int i = 0; i < cursor.getCount(); i++) {
			schoolClassIds.add(cursor.getInt(0));
			schoolClassItems.add(cursor.getString(1));
			cursor.moveToNext();
		}
		cursor.close();

		ArrayAdapter<String> classSpinnerAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, schoolClassItems);
		classSpinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		classSpinner = (Spinner) findViewById(R.id.classSpinner);
		classSpinner.setAdapter(classSpinnerAdapter);

	}

	public void showDatePickerDialog(View v) {
		DialogFragment datePickerFragment = new DatePickerFragment();
		datePickerFragment.show(getFragmentManager(), "datePicker");
	}

	public boolean postAssignment(View v) {
		Assignment assignment = new Assignment();

		Long schoolClassId = (long) schoolClassIds.get(classSpinner
				.getSelectedItemPosition());
		assignment.setSchoolClassId(schoolClassId);

		assignment.setDescription(((EditText) findViewById(R.id.description))
				.getText().toString());

		assignment.setDateDue(((Button) findViewById(R.id.dateDue)).getText()
				.toString());

		String timeStamp = new SimpleDateFormat("MM/dd/yy").format(Calendar
				.getInstance().getTime());
		assignment.setDateAssigned(timeStamp);

		if (photoUri != null) {
			assignment.setImageUri(photoUri.toString());
		} else {
			assignment.setImageUri(null);
		}

		AssignmentsDataSource dao = new AssignmentsDataSource(this);
		dao.open();
		dao.createAssignment(assignment);
		dao.close();

		Toast.makeText(this, "Assignment Added", Toast.LENGTH_SHORT).show();

		Intent completedPostIntent = new Intent(this, MainActivity.class);
		startActivity(completedPostIntent);
		finish();

		return true;
	}

	public void cancelAssignment(View v) {
		Intent mainActivityIntent = new Intent(this, MainActivity.class);
		mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(mainActivityIntent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_post_assignment, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			cancelAssignment(null);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		cancelAssignment(null);
	}
}
