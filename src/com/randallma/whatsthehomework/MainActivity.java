package com.randallma.whatsthehomework;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleAdapter;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

public class MainActivity extends ListActivity {

	SimpleAdapter adapter;

	private void login() {
		ApplicationGlobal g = (ApplicationGlobal) getApplication();
		g.setUserLoggedIn(false);
		Intent loginActivityIntent = new Intent(this, LoginActivity.class);
		startActivity(loginActivityIntent);
		finish();
	}

	private void logout() {
		AsyncHttpClient clientSession = new AsyncHttpClient();
		clientSession.get("http://192.168.1.42:5000/logout/",
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						login();
					}
				});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ApplicationGlobal g = (ApplicationGlobal) getApplication();

		boolean userLoggedIn = g.getUserLoggedIn();
		AsyncHttpClient clientSession = new AsyncHttpClient();
		PersistentCookieStore cookieStore = g.getCookieStore();
		clientSession.setCookieStore(cookieStore);
		if (!userLoggedIn) {
			login();
		} else {

			SimpleAdapter adapter = new SimpleAdapter(this, newsFeed,
					R.layout.assignment_list_row_view, new String[] { "photo",
							"dateAssigned", "dateDue", "description" },
					new int[] { R.id.photo, R.id.dateAssigned, R.id.dateDue,
							R.id.description });

			getNewsJson();

		}
	}

	private SimpleAdapter initAdapter(
			ArrayList<HashMap<String, String>> newsFeed) {
		SimpleAdapter adapter = new SimpleAdapter(this, newsFeed,
				R.layout.assignment_list_row_view, new String[] { "photo",
						"dateAssigned", "dateDue", "description" }, new int[] {
						R.id.photo, R.id.dateAssigned, R.id.dateDue,
						R.id.description });
		return adapter;
	}

	ArrayList<HashMap<String, String>> newsFeed = new ArrayList<HashMap<String, String>>();

	private void getNewsJson() {
		ApplicationGlobal g = (ApplicationGlobal) getApplication();
		AsyncHttpClient clientSession = new AsyncHttpClient();
		PersistentCookieStore cookieStore = g.getCookieStore();
		clientSession.setCookieStore(cookieStore);
		clientSession.get("http://192.168.1.42:5000/news/dummy/all/",
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject response) {
						JSONArray assignments = new JSONArray();
						try {
							assignments = response.getJSONArray("assignments");
							JSONObject c = assignments.getJSONObject(0);

							SimpleAdapter adapter = initAdapter(newsFeed);

							for (int i = 0; i < assignments.length() + 1; i++) {

								JSONObject individualAssignment = new JSONObject(
										c.getString(Integer.toString(i)));

								HashMap<String, String> map = new HashMap<String, String>();
								map.put("photo",
										individualAssignment.getString("photo"));
								map.put("dateAssigned", individualAssignment
										.getString("date_assigned"));
								map.put("dateDue", individualAssignment
										.getString("date_due"));
								map.put("description", individualAssignment
										.getString("description"));

								newsFeed.add(map);

								if (i == 0) {
									adapter.notifyDataSetChanged();
									setListAdapter(adapter);
								} else {
									adapter.notifyDataSetChanged();
								}
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(Throwable e, String response) {
					}
				});
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
		case R.id.menu_settings:
			return true;
		case R.id.logout:
			logout();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}