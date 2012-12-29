package com.randallma.whatsthehomework;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

public class MainActivity extends Activity {

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
		if (!userLoggedIn) {
			login();
		} else {
			AsyncHttpClient clientSession = new AsyncHttpClient();
			PersistentCookieStore cookieStore = g.getCookieStore();
			clientSession.setCookieStore(cookieStore);

			clientSession.get("http://192.168.1.42:5000/motd/",
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(String response) {
							// bad session in cookie fallthrough
							if (response.equals("False")) {
								login();
							}
							TextView motd = (TextView) findViewById(R.id.motd);
							motd.setText("Welcome, " + response + ".");
						}
					});

			clientSession.get("http://192.168.1.42:5000/news/dummy/all/",
					new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(JSONObject response) {

							JSONArray assignments = null;
							try {
								assignments = response
										.getJSONArray("assignments");
								JSONObject c = assignments.getJSONObject(0);
								for (int i = 0; i < assignments.length() + 1; i++) {

									JSONObject individualAssignment = new JSONObject(
											c.getString(Integer.toString(i)));

									String photo = individualAssignment
											.getString("photo");
									String dateAssigned = individualAssignment
											.getString("date_assigned");
									String dateDue = individualAssignment
											.getString("date_due");
									String description = individualAssignment
											.getString("description");

									HashMap<String, String> map = new HashMap<String, String>();
									map.put("photo", photo);
									map.put("dateAssigned", dateAssigned);
									map.put("dateDue", dateDue);
									map.put("description", description);

									System.out.println(map);
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					});
		}
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