package com.randallma.whatsthehomework;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

public class AssignmentActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assignment);
		setupActionBar();

		Intent parentIntent = getIntent();
		String assignmentId = Long.toString(parentIntent.getLongExtra(
				MainActivity.MESSAGE_ASSIGNMENT_ID, 0));

		final SQLiteHelper dbHelper = new SQLiteHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor cursor = db.query(SQLiteHelper.TABLE_ASSIGNMENTS, null,
				SQLiteHelper.COLUMN_ID + " = ?", new String[] { assignmentId },
				null, null, null);
		cursor.moveToFirst();

		Assignment assignment = new Assignment();
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

		cursor.close();
	}

	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
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
