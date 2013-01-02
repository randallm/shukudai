package com.randallma.whatsthehomework;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

public class MainActivity extends ListActivity {

	private void login() {
		Intent loginActivityIntent = new Intent(this, LoginActivity.class);
		startActivity(loginActivityIntent);
		finish();
	}

	private void logout() {
		// AsyncHttpClient clientSession = new AsyncHttpClient();
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		cookieStore.clear();

		// unnecessary because session is cleared
		// ApplicationGlobal g = (ApplicationGlobal) getApplication();
		// clientSession.get(g.getWthUrl() + "/logout/",
		// new AsyncHttpResponseHandler() {
		// @Override
		// public void onFinish() {
		// login();
		// }
		// });

		login();
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

		final ListView lv = (ListView) findViewById(android.R.id.list);
		updateNews();
		// lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				Object o = lv.getItemAtPosition(position);
				NewsEntry fullObject = (NewsEntry) o;
				Toast.makeText(MainActivity.this,
						"You chose: " + fullObject.getDateDue(),
						Toast.LENGTH_SHORT).show();
			}
		});
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

	private void updateNews() {
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

							ArrayList<NewsEntry> newsFeed = new ArrayList<NewsEntry>();
							NewsEntriesAdapter adapter = new NewsEntriesAdapter(
									MainActivity.this, newsFeed);

							for (int i = 0; i < assignments.length() + 1; i++) {

								JSONObject a = new JSONObject(c
										.getString(Integer.toString(i)));

								NewsEntry newsEntry = new NewsEntry();
								newsEntry.setPhoto(a.getString("photo"));
								newsEntry.setDateDue(a.getString("date_due"));
								newsEntry.setDateAssigned(a
										.getString("date_assigned"));
								newsEntry.setDescription(a
										.getString("description"));
								newsFeed.add(newsEntry);

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
			System.out.println("logging out");
			logout();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}