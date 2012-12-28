package com.randallma.whatsthehomework;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
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
		AsyncHttpClient clientSession = new AsyncHttpClient();
		PersistentCookieStore cookieStore = g.getCookieStore();
		clientSession.setCookieStore(cookieStore);
		boolean userLoggedIn = g.getUserLoggedIn();

		if (!userLoggedIn) {
			login();
		}

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

		// int newsFeedPage = 1;
		// clientSession.post("http://192.168.1.42:5000/hw/news/dummy/all/"
		// + newsFeedPage + '/', new JsonHttpResponseHandler() {
		// @Override
		// public void onSuccess(JSONArray response) {
		// System.out.println(response);
		// }
		// });

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