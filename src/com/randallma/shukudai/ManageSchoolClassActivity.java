package com.randallma.shukudai;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ManageSchoolClassActivity extends Activity {

	private SQLiteDatabase db;
	private EditText desiredTitle;

	private ArrayAdapter<String> classSpinnerAdapter;
	private ArrayList<String> schoolClassItems;
	private ArrayList<Integer> schoolClassIds;
	private Spinner classSpinner;
	private Dialog manageClassPopupDialog;
	private Dialog deleteClassPopupDialog;
	private Dialog deletePhotosDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_school_class);
		setupActionBar();

		schoolClassIds = new ArrayList<Integer>();
		schoolClassItems = new ArrayList<String>();

		final SQLiteHelper dbHelper = new SQLiteHelper(this);
		db = dbHelper.getWritableDatabase();

		updateSchoolClasses(true);
	}

	private void updateSchoolClasses(boolean init) {
		schoolClassIds.clear();
		schoolClassItems.clear();

		Cursor cursor = db
				.query(SQLiteHelper.TABLE_SCHOOL_CLASSES, null, null, null,
						null, null, SQLiteHelper.COLUMN_TITLE
								+ " COLLATE NOCASE");
		cursor.moveToFirst();
		for (int i = 0; i < cursor.getCount(); i++) {
			schoolClassIds.add(cursor.getInt(0));
			schoolClassItems.add(cursor.getString(1));
			cursor.moveToNext();
		}
		cursor.close();

		if (init) {
			classSpinnerAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_dropdown_item,
					schoolClassItems);
			classSpinner = (Spinner) findViewById(R.id.manageClassSpinner);
			classSpinner.setAdapter(classSpinnerAdapter);
		} else {
			classSpinnerAdapter.notifyDataSetChanged();
		}
	}

	public void editClass(View v) {
		AlertDialog.Builder manageClassPopup = new AlertDialog.Builder(this);
		manageClassPopup.setTitle("Edit Class Name");
		desiredTitle = new EditText(this);
		manageClassPopup.setView(desiredTitle);
		manageClassPopup.setMessage(getResources().getString(
				R.string.example_school_class_title));
		manageClassPopup.setPositiveButton("Apply", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ContentValues values = new ContentValues();
				values.put(SQLiteHelper.COLUMN_TITLE, desiredTitle.getText()
						.toString());
				db.update(SQLiteHelper.TABLE_SCHOOL_CLASSES, values,
						SQLiteHelper.COLUMN_ID + "=?", new String[] { Integer
								.toString(schoolClassIds.get(classSpinner
										.getSelectedItemPosition())) });
				updateSchoolClasses(false);
			}
		});
		manageClassPopup.setNegativeButton("Discard", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				manageClassPopupDialog.dismiss();
			}
		});

		manageClassPopupDialog = manageClassPopup.create();
		manageClassPopupDialog.show();
	}

	public void deleteClass(View v) {
		AlertDialog.Builder deleteClassPopup = new AlertDialog.Builder(this);
		deleteClassPopup.setTitle("Delete "
				+ schoolClassItems.get(classSpinner.getSelectedItemPosition())
				+ "?");
		deleteClassPopup.setPositiveButton("Delete Class",
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						db.delete(SQLiteHelper.TABLE_SCHOOL_CLASSES,
								SQLiteHelper.COLUMN_ID + "=?",
								new String[] { Integer.toString(schoolClassIds
										.get(classSpinner
												.getSelectedItemPosition())) });

						if (schoolClassIds.size() == 0) {
							Intent mainActivityIntent = new Intent(
									ManageSchoolClassActivity.this,
									MainActivity.class);
							mainActivityIntent
									.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							startActivity(mainActivityIntent);
							finish();
						}

						updateSchoolClasses(false);

						Toast.makeText(ManageSchoolClassActivity.this,
								"Class deleted", Toast.LENGTH_SHORT).show();
					}
				});
		deleteClassPopup.setNegativeButton("Dismiss", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteClassPopupDialog.dismiss();
			}
		});

		deleteClassPopupDialog = deleteClassPopup.create();
		deleteClassPopupDialog.show();
	}

	public void deletePhotos(View v) {
		AlertDialog.Builder deletePhotosPopup = new AlertDialog.Builder(this);
		deletePhotosPopup.setTitle("Delete "
				+ schoolClassItems.get(classSpinner.getSelectedItemPosition())
				+ " Photos?");
		deletePhotosPopup.setPositiveButton("Delete Photos",
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ArrayList<String> assignments = new ArrayList<String>();

						Cursor cursor = db
								.query(SQLiteHelper.TABLE_ASSIGNMENTS,
										null,
										SQLiteHelper.COLUMN_SCHOOL_CLASS_ID
												+ " = "
												+ schoolClassIds.get(classSpinner
														.getSelectedItemPosition())
												+ " AND "
												+ SQLiteHelper.COLUMN_ARCHIVED
												+ " = 1", null, null, null,
										SQLiteHelper.COLUMN_ID + " DESC");
						cursor.moveToFirst();
						while (!cursor.isAfterLast()) {
							assignments.add(Long.toString(cursor.getLong(0)));
							cursor.moveToNext();
						}
						cursor.close();

						ContentValues values = new ContentValues();
						values.putNull(SQLiteHelper.COLUMN_IMAGE);
						db.update(SQLiteHelper.TABLE_ASSIGNMENTS, values,
								SQLiteHelper.COLUMN_ID + "=?",
								assignments.toArray(new String[assignments
										.size()]));

						Toast.makeText(
								ManageSchoolClassActivity.this,
								schoolClassItems.get(classSpinner
										.getSelectedItemPosition())
										+ " Photos Deleted", Toast.LENGTH_SHORT)
								.show();
					}
				});
		deletePhotosPopup.setNegativeButton("Dismiss", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deletePhotosDialog.dismiss();
			}
		});

		deletePhotosDialog = deletePhotosPopup.create();
		deletePhotosDialog.show();
	}

	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.manage_school_class, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		Intent mainActivityIntent = new Intent(this, MainActivity.class);
		mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(mainActivityIntent);
		finish();
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent mainActivityIntent = new Intent(this, MainActivity.class);
		mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(mainActivityIntent);
		finish();
		return super.onOptionsItemSelected(item);
	}

}
