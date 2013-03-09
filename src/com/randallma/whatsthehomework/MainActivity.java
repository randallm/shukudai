package com.randallma.whatsthehomework;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ListActivity;
import android.content.Intent;
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

	ArrayList<Integer> schoolClassIds;
	ArrayList<String> schoolClassItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ApplicationGlobal g = (ApplicationGlobal) getApplication();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (getIntent().getStringExtra(PostAssignmentActivity.COMPLETED_POST) != null) {
			dao = new AssignmentsDataSource(this);
			dao.open();
			assignments = dao.getAllAssignments();
			adapter = g.getAssignmentAdapter();
			adapter.notifyDataSetChanged();
			dao.close();
		}

		setClassList();

		schoolClassIds = new ArrayList<Integer>(g.getSchoolClassIds());
		schoolClassItems = new ArrayList<String>(g.getSchoolClassItems());
		schoolClassIds.add(0, -1);
		schoolClassItems.add(0, "All Assignments");

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

				if (schoolClassIds.get(itemPosition) == -1) {
					dao = new AssignmentsDataSource(MainActivity.this);
					dao.open();

					assignments = dao.getAllAssignments();

					ApplicationGlobal g = (ApplicationGlobal) getApplication();
					adapter = new AssignmentAdapter(MainActivity.this,
							assignments);
					g.setAssignmentAdapter(adapter);
					setListAdapter(adapter);
					adapter.notifyDataSetChanged();

					dao.close();
				}
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

	private void setClassList() {
		ApplicationGlobal g = (ApplicationGlobal) getApplication();

		ArrayList<Integer> schoolClassIds = new ArrayList<Integer>();
		schoolClassIds.add(1);
		g.setSchoolClassIds(schoolClassIds);
		ArrayList<String> schoolClassItems = new ArrayList<String>();
		schoolClassItems.add("Crumpets 2013-2014 AP Tastiness");
		g.setSchoolClassItems(schoolClassItems);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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
