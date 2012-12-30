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
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

public class MainActivity extends ListActivity {

	SimpleAdapter adapter;

	private void login() {
		Intent loginActivityIntent = new Intent(this, LoginActivity.class);
		startActivity(loginActivityIntent);
		finish();
	}

	private void logout() {
		AsyncHttpClient clientSession = new AsyncHttpClient();
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		cookieStore.clear();

		ApplicationGlobal g = (ApplicationGlobal) getApplication();
		clientSession.get(g.getWthUrl() + "/logout/",
				new AsyncHttpResponseHandler() {
					@Override
					public void onFinish() {
						login();
					}
				});
	}

	private void checkLogin() {
		AsyncHttpClient clientSession = new AsyncHttpClient();
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		clientSession.setCookieStore(cookieStore);

		ApplicationGlobal g = (ApplicationGlobal) getApplication();
		clientSession.get(g.getWthUrl() + "/verifyloggedin/",
				new AsyncHttpResponseHandler() {
					@Override
					public void onFailure(Throwable e, String response) {
						login();
					}
				});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		checkLogin();
		motd();

		@SuppressWarnings("unused")
		SimpleAdapter adapter = new SimpleAdapter(this, newsFeed,
				R.layout.assignment_list_row_view, new String[] { "photo",
						"dateAssigned", "dateDue", "description" }, new int[] {
						R.id.photo, R.id.dateAssigned, R.id.dateDue,
						R.id.description });

		getNewsJson();
		// }
	}

	private void motd() {
		AsyncHttpClient clientSession = new AsyncHttpClient();
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		clientSession.setCookieStore(cookieStore);

		ApplicationGlobal g = (ApplicationGlobal) getApplication();
		clientSession.get(g.getWthUrl() + "/motd/",
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						Toast toast = Toast.makeText(MainActivity.this,
								"Logged in as " + response, Toast.LENGTH_SHORT);
						toast.show();
					}

					@Override
					public void onFailure(Throwable c, String response) {
						login();
					}
				});
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
		AsyncHttpClient clientSession = new AsyncHttpClient();
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		clientSession.setCookieStore(cookieStore);

		ApplicationGlobal g = (ApplicationGlobal) getApplication();
		clientSession.get(g.getWthUrl() + "/news/dummy/all/",
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