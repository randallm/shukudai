package com.randallma.whatsthehomework;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ListActivity;
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
import android.widget.Toast;

public class MainActivity extends ListActivity {

	public final static String MESSAGE_ASSIGNMENT_ID = "com.randallma.whatsthehomework.ASSIGNMENT_ID";

	private AssignmentsDataSource dao;
	private AssignmentAdapter adapter;
	private ArrayList<Assignment> assignments;

	private ArrayList<Integer> schoolClassIds;
	private ArrayList<String> schoolClassItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setClassList();
		ArrayAdapter<String> classSpinnerAdapter = new ArrayAdapter<String>(
				this, R.layout.dark_action_bar_spinner_dropdown_item,
				schoolClassItems);

		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		ActionBar.OnNavigationListener navigationListener = new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int itemPosition,
					long itemId) {
				Toast.makeText(MainActivity.this,
						schoolClassItems.get(itemPosition), Toast.LENGTH_SHORT)
						.show();

				dao = new AssignmentsDataSource(MainActivity.this);
				dao.open();

				if (schoolClassIds.get(itemPosition) == -1) {
					assignments = dao.getAllAssignments();

					adapter = new AssignmentAdapter(MainActivity.this,
							assignments);
					setListAdapter(adapter);
					adapter.notifyDataSetChanged();
				} else {
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

	@SuppressLint("UseSparseArrays")
	private void setClassList() {
		schoolClassIds = new ArrayList<Integer>();
		schoolClassItems = new ArrayList<String>();

		schoolClassIds.add(-1);
		schoolClassItems.add("All Assignments");

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
		case R.id.menu_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
