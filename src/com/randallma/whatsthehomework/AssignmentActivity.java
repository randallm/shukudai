package com.randallma.whatsthehomework;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class AssignmentActivity extends Activity {

	private Assignment assignment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assignment);
		setupActionBar();

		Intent parentIntent = getIntent();
		String assignmentId = Long.toString(parentIntent.getLongExtra(
				MainActivity.MESSAGE_ASSIGNMENT_ID, 0));

		getAssignment(assignmentId);

		populateViews();
		setTitle(assignment.getSchoolClass() + " "
				+ assignment.getDateAssigned());
	}

	private void getAssignment(String assignmentId) {
		final SQLiteHelper dbHelper = new SQLiteHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor cursor = db.query(SQLiteHelper.TABLE_ASSIGNMENTS, null,
				SQLiteHelper.COLUMN_ID + " = ?", new String[] { assignmentId },
				null, null, null);
		cursor.moveToFirst();

		assignment = new Assignment();
		assignment.setId(cursor.getLong(0));
		assignment.setDescription(cursor.getString(1));
		assignment.setDateDue(cursor.getString(2));
		assignment.setDateAssigned(cursor.getString(3));
		assignment.setImageUri(cursor.getString(4));

		Cursor schoolClassCursor = db.query(SQLiteHelper.TABLE_SCHOOL_CLASSES,
				null, SQLiteHelper.COLUMN_ID + " = ?",
				new String[] { Integer.toString(cursor.getInt(5)) }, null,
				null, null, null);
		schoolClassCursor.moveToFirst();
		assignment.setSchoolClass(schoolClassCursor.getString(1));

		schoolClassCursor.close();
		cursor.close();

		populateViews();
	}

	private void populateViews() {
		if (assignment.getImageUri() != null) {
			ImageView assignmentImage = (ImageView) findViewById(R.id.assignmentImage);
			Uri imageUri = Uri.parse(assignment.getImageUri());
			try {
				Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(
						this.getContentResolver(), imageUri);

				DisplayMetrics metrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(metrics);

				int scaleHeight = metrics.widthPixels * imageBitmap.getHeight()
						/ imageBitmap.getWidth();

				imageBitmap = Bitmap.createScaledBitmap(imageBitmap,
						metrics.widthPixels, scaleHeight, false);

				assignmentImage.setImageBitmap(imageBitmap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		TextView assignmentDueDate = (TextView) findViewById(R.id.assignmentDueDate);
		assignmentDueDate.setText("Due: " + assignment.getDateDue());

		TextView assignmentPostedDate = (TextView) findViewById(R.id.assignmentPostedDate);
		assignmentPostedDate.setText("Posted" + assignment.getDateAssigned());

		if (assignment.getDescription() != null) {
			TextView assignmentDescription = (TextView) findViewById(R.id.assignmentDescription);
			assignmentDescription.setText(assignment.getDescription());
		}
	}

	private void setupActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.assignment, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
