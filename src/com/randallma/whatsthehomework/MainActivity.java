package com.randallma.whatsthehomework;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	public final static String MESSAGE_ASSIGNMENT_ID = "com.randallma.whatsthehomework.ASSIGNMENT_ID";

	private AssignmentsDataSource dao;
	private AssignmentAdapter adapter;
	private ArrayList<Assignment> assignments;

	private ArrayAdapter<String> classSpinnerAdapter;
	private ArrayList<Integer> schoolClassIds;
	private ArrayList<String> schoolClassItems;

	private EditText desiredTitle;
	private Dialog newClassPopupDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initSchoolClassSpinner();

		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				Object o = getListView().getItemAtPosition(position);
				Assignment fullO = (Assignment) o;

				Intent assignmentIntent = new Intent(MainActivity.this,
						AssignmentActivity.class);
				assignmentIntent.putExtra(MESSAGE_ASSIGNMENT_ID, fullO.getId());
				startActivity(assignmentIntent);
			}
		});
	}

	private void initSchoolClassSpinner() {
		schoolClassIds = new ArrayList<Integer>();
		schoolClassItems = new ArrayList<String>();

		schoolClassIds.add(-1);
		schoolClassItems.add("New Assignments");

		final SQLiteHelper dbHelper = new SQLiteHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();

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

		classSpinnerAdapter = new ArrayAdapter<String>(this,
				R.layout.dark_action_bar_spinner_dropdown_item,
				schoolClassItems);

		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		ActionBar.OnNavigationListener navigationListener = new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int itemPosition,
					long itemId) {
				dao = new AssignmentsDataSource(MainActivity.this);
				dao.open();

				if (schoolClassIds.get(itemPosition) == -1) {
					enableSwipe();

					assignments = dao.getNewAssignments();

					adapter = new AssignmentAdapter(MainActivity.this,
							assignments);
					setListAdapter(adapter);
					adapter.notifyDataSetChanged();
				} else {
					disableSwipe();

					assignments = dao
							.getFilteredAssignments((long) schoolClassIds
									.get(itemPosition));

					adapter = new AssignmentAdapter(MainActivity.this,
							assignments);
					setListAdapter(adapter);
					adapter.notifyDataSetChanged();
				}
				dao.close();
				return false;
			}
		};

		getActionBar().setListNavigationCallbacks(classSpinnerAdapter,
				navigationListener);
	}

	private void enableSwipe() {
		ListView listView = getListView();
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
				listView,
				new SwipeDismissListViewTouchListener.OnDismissCallback() {
					@Override
					public void onDismiss(ListView listView,
							int[] reverseSortedPositions) {
						final SQLiteHelper dbHelper = new SQLiteHelper(
								MainActivity.this);
						SQLiteDatabase db = dbHelper.getWritableDatabase();
						db.execSQL("update "
								+ SQLiteHelper.TABLE_ASSIGNMENTS
								+ " set "
								+ SQLiteHelper.COLUMN_ARCHIVED
								+ "=1 where "
								+ SQLiteHelper.COLUMN_ID
								+ "="
								+ assignments.get(reverseSortedPositions[0])
										.getId());

						assignments.remove(reverseSortedPositions[0]);
						adapter = new AssignmentAdapter(MainActivity.this,
								assignments);
						setListAdapter(adapter);
						adapter.notifyDataSetChanged();
					}
				});
		listView.setOnTouchListener(touchListener);
		listView.setOnScrollListener(touchListener.makeScrollListener());
	}

	private void disableSwipe() {
		ListView listView = getListView();
		listView.setOnTouchListener(null);
		listView.setOnScrollListener(null);
	}

	private void addNewSchoolClass() {
		AlertDialog.Builder newClassPopup = new AlertDialog.Builder(this);
		newClassPopup.setTitle("Add New Class");
		desiredTitle = new EditText(this);
		newClassPopup.setView(desiredTitle);
		newClassPopup.setMessage("Title (ex: P1 AP Chemistry):");
		newClassPopup.setPositiveButton("Add", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final SQLiteHelper dbHelper = new SQLiteHelper(
						MainActivity.this);
				SQLiteDatabase db = dbHelper.getWritableDatabase();

				ContentValues values = new ContentValues();
				values.put(SQLiteHelper.COLUMN_TITLE, desiredTitle.getText()
						.toString());
				db.insert(SQLiteHelper.TABLE_SCHOOL_CLASSES, null, values);

				Toast.makeText(
						MainActivity.this,
						"Class \"" + desiredTitle.getText().toString()
								+ "\" Added", Toast.LENGTH_SHORT).show();

				initSchoolClassSpinner();
			}
		});
		newClassPopup.setNegativeButton("Cancel", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				newClassPopupDialog.dismiss();
			}
		});
		newClassPopupDialog = newClassPopup.create();
		newClassPopupDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.post_new_assignment:
			Intent postAssignment = new Intent(this,
					PostAssignmentActivity.class);
			startActivity(postAssignment);
			finish();
			return true;
		case R.id.add_new_school_class:
			addNewSchoolClass();
			return true;
		case R.id.menu_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
