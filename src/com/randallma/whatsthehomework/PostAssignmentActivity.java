package com.randallma.whatsthehomework;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
				this.getContentResolver().notifyChange(photoUri, null);
				ContentResolver cr = this.getContentResolver();

				try {
					Bitmap photoBmp = MediaStore.Images.Media.getBitmap(cr,
							photoUri);

					FileOutputStream fileOut = new FileOutputStream(photo);
					photoBmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOut);
					fileOut.flush();
					fileOut.close();

					RelativeLayout previewImageBox = (RelativeLayout) findViewById(R.id.previewImageBox);
					previewImageBox.setVisibility(View.VISIBLE);

					ImageView previewImage = (ImageView) findViewById(R.id.previewImage);
					previewImage.setImageBitmap(photoBmp);

					// postAssignmentButton.setClickable(true);

					// TextView photoStatus = (TextView)
					// findViewById(R.id.photoStatus);
					// photoStatus.setText("Photo attached");
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
		DateDuePickerFragment datePickerFragment = new DateDuePickerFragment();
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

		Calendar c = Calendar.getInstance();
		assignment.setDateAssigned(DateDuePickerFragment.getReadableDate(
				c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH)));

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
		boolean descIsEmpty = ((EditText) findViewById(R.id.description))
				.getText().toString().isEmpty();
		boolean dateDueIsEmpty = ((Button) findViewById(R.id.dateDue))
				.getText().toString().isEmpty();

		if (!descIsEmpty || !dateDueIsEmpty || photoUri != null) {
			new AlertDialog.Builder(this)
					.setTitle("Discard Assignment")
					.setMessage(
							"Are you sure you want to discard this assignment?")
					.setPositiveButton("Discard Assignment",
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent mainActivityIntent = new Intent(
											PostAssignmentActivity.this,
											MainActivity.class);
									mainActivityIntent
											.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
									startActivity(mainActivityIntent);
									finish();
								}
							})
					.setNegativeButton("Continue Working",
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
		} else {
			Intent mainActivityIntent = new Intent(PostAssignmentActivity.this,
					MainActivity.class);
			mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(mainActivityIntent);
			finish();
		}
	}

	public void cancelPhoto(View v) {
		new AlertDialog.Builder(this).setTitle("Discard Photo")
				.setMessage("Are you sure you want to discard this photo?")
				.setPositiveButton("Discard Photo", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						photo = null;
						photoUri = null;

						RelativeLayout previewImageBox = (RelativeLayout) findViewById(R.id.previewImageBox);
						previewImageBox.setVisibility(View.GONE);
					}
				}).setNegativeButton("Cancel", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
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
		case R.id.post_new_assignment_camera:
			takePhoto(null);
			return true;
		case R.id.post_new_assignment:
			postAssignment(null);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		cancelAssignment(null);
	}
}
