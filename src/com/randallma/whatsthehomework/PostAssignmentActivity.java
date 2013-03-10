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
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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

		initSchoolClassSpinner();

		Button d1 = (Button) findViewById(R.id.d1);
		d1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				calculateDateDue("1d");
			}
		});
		Button d2 = (Button) findViewById(R.id.d2);
		d2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				calculateDateDue("2d");
			}
		});
		Button w1 = (Button) findViewById(R.id.w1);
		w1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				calculateDateDue("1w");
			}
		});
		Button w2 = (Button) findViewById(R.id.w2);
		w2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				calculateDateDue("2w");
			}
		});
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

					TextView photoStatus = (TextView) findViewById(R.id.photoStatus);
					photoStatus.setText("Photo attached");
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

	private void calculateDateDue(String preset) {
		Button dateDueButton = (Button) findViewById(R.id.dateDue);
		Calendar c = Calendar.getInstance();

		if (preset.equals("1d")) {
			c.add(Calendar.DATE, 1);
		} else if (preset.equals("2d")) {
			c.add(Calendar.DATE, 2);
		} else if (preset.equals("1w")) {
			c.add(Calendar.DATE, 7);
		} else if (preset.equals("2w")) {
			c.add(Calendar.DATE, 14);
		}

		String sYear = Integer.toString(c.get(Calendar.YEAR));
		String sMonth = Integer.toString(c.get(Calendar.MONTH));
		String sDay = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
		dateDueButton.setText(sMonth + "/" + sDay + "/" + sYear);
	}

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
		DialogFragment datePickerFragment = new DateDuePickerFragment();
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
