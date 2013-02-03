package com.randallma.whatsthehomework;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
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
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		cookieStore.clear();
		login();
	}

	private void checkLogin() {
		AsyncHttpClient clientSession = new AsyncHttpClient();
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		clientSession.setCookieStore(cookieStore);

		ApplicationGlobal g = (ApplicationGlobal) getApplication();
		clientSession.get(g.getWthUrl() + "/user/verify_logged_in/",
				new AsyncHttpResponseHandler() {
					@Override
					public void onFailure(Throwable e, String response) {
						login();
					}
				});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTitle("News Feed");
		ApplicationGlobal g = (ApplicationGlobal) getApplication();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		checkLogin();
		motd();

		final ListView lv = (ListView) findViewById(android.R.id.list);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				Object o = lv.getItemAtPosition(position);
				NewsEntry fullObject = (NewsEntry) o;
				Toast.makeText(MainActivity.this,
						"You chose: " + fullObject.getPk(), Toast.LENGTH_SHORT)
						.show();
			}
		});

		lv.setOnScrollListener(new OnScrollListener() {
			private final int visibleThreshold = 0;
			@SuppressWarnings("unused")
			private int currentPage = 0;
			private int previousTotal = 0;
			private boolean loading = true;

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (loading) {
					if (totalItemCount > previousTotal) {
						loading = false;
						previousTotal = totalItemCount;
						currentPage++;
					}
				}
				if (!loading
						&& (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
					oldSetNews();
					loading = true;
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

		});

		ArrayList<NewsEntry> newsFeed = new ArrayList<NewsEntry>();
		NewsEntriesAdapter adapter = new NewsEntriesAdapter(MainActivity.this,
				newsFeed);
		lv.setAdapter(adapter);
		g.setNewsFeed(newsFeed);
		g.setAdapter(adapter);
		g.setLv(lv);
		initialSetNews();

	}

	private void motd() {
		AsyncHttpClient clientSession = new AsyncHttpClient();
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		clientSession.setCookieStore(cookieStore);

		ApplicationGlobal g = (ApplicationGlobal) getApplication();
		clientSession.get(g.getWthUrl() + "/user/motd/",
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

	private void initialSetNews() {
		AsyncHttpClient clientSession = new AsyncHttpClient();
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		clientSession.setCookieStore(cookieStore);

		ApplicationGlobal g = (ApplicationGlobal) getApplication();
		clientSession.get(g.getWthUrl() + "/news/new/all/",
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject response) {
						JSONArray assignments = new JSONArray();
						try {
							assignments = response.getJSONArray("assignments");
							JSONObject c = assignments.getJSONObject(0);

							ApplicationGlobal g = (ApplicationGlobal) getApplication();
							ArrayList<NewsEntry> newsFeed = g.getNewsFeed();
							NewsEntriesAdapter adapter = g.getAdapter();

							for (int i = 0; i < c.length(); i++) {

								JSONObject a = new JSONObject(c
										.getString(Integer.toString(i)));

								NewsEntry newsEntry = new NewsEntry();
								newsEntry.setPk(a.getInt("pk"));

								String encodedThumbnail = a
										.getString("thumbnail");
								byte[] decodedThumbnailString = Base64.decode(
										encodedThumbnail, Base64.DEFAULT);
								Bitmap decodedThumbnail = BitmapFactory
										.decodeByteArray(
												decodedThumbnailString, 0,
												decodedThumbnailString.length);
								newsEntry.setThumbnail(decodedThumbnail);

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

	private void newSetNews() {
		ApplicationGlobal g = (ApplicationGlobal) getApplication();

		ListView lv = g.getLv();
		try {
			Object o = lv.getItemAtPosition(0);

			NewsEntry fullObject = (NewsEntry) o;
			String latestPost = Integer.toString(fullObject.getPk());

			AsyncHttpClient clientSession = new AsyncHttpClient();
			PersistentCookieStore cookieStore = new PersistentCookieStore(this);
			clientSession.setCookieStore(cookieStore);

			clientSession.get(g.getWthUrl() + "/news/new/all/" + latestPost
					+ "/", new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONObject response) {
					JSONArray assignments = new JSONArray();
					try {
						ApplicationGlobal g = (ApplicationGlobal) getApplication();
						ArrayList<NewsEntry> newsFeed = g.getNewsFeed();
						NewsEntriesAdapter adapter = g.getAdapter();

						assignments = response.getJSONArray("assignments");
						JSONObject c = assignments.getJSONObject(0);

						if (c.length() == 0) {
							Toast.makeText(MainActivity.this,
									"No new homework!", Toast.LENGTH_SHORT)
									.show();
						}

						try {
							c.getString("15");
							Intent intent = getIntent();
							overridePendingTransition(0, 0);
							intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							finish();
							overridePendingTransition(0, 0);
							startActivity(intent);
						} catch (JSONException e) {
							for (int i = 0; i < c.length(); i++) {

								JSONObject a = new JSONObject(c
										.getString(Integer.toString(i)));

								NewsEntry newsEntry = new NewsEntry();
								newsEntry.setPk(a.getInt("pk"));

								String encodedThumbnail = a
										.getString("thumbnail");
								byte[] decodedThumbnailString = Base64.decode(
										encodedThumbnail, Base64.DEFAULT);
								Bitmap decodedThumbnail = BitmapFactory
										.decodeByteArray(
												decodedThumbnailString, 0,
												decodedThumbnailString.length);
								newsEntry.setThumbnail(decodedThumbnail);

								newsEntry.setDateDue(a.getString("date_due"));
								newsEntry.setDateAssigned(a
										.getString("date_assigned"));
								newsEntry.setDescription(a
										.getString("description"));
								newsFeed.add(0, newsEntry);

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

		} catch (IndexOutOfBoundsException e) {
			initialSetNews();
		}
	}

	private void oldSetNews() {
		ApplicationGlobal g = (ApplicationGlobal) getApplication();
		ArrayList<NewsEntry> newsFeed = g.getNewsFeed();

		ListView lv = g.getLv();

		Object o = lv.getItemAtPosition(newsFeed.size() - 1);
		NewsEntry fullObject = (NewsEntry) o;
		String oldestPost = Integer.toString(fullObject.getPk());

		AsyncHttpClient clientSession = new AsyncHttpClient();
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		clientSession.setCookieStore(cookieStore);

		clientSession.get(g.getWthUrl() + "/news/old/all/" + oldestPost + "/",
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject response) {
						JSONArray assignments = new JSONArray();
						try {
							assignments = response.getJSONArray("assignments");
							JSONObject c = assignments.getJSONObject(0);

							ApplicationGlobal g = (ApplicationGlobal) getApplication();
							ArrayList<NewsEntry> newsFeed = g.getNewsFeed();
							NewsEntriesAdapter adapter = g.getAdapter();

							for (int i = 0; i < c.length(); i++) {

								JSONObject a = new JSONObject(c
										.getString(Integer.toString(i)));

								NewsEntry newsEntry = new NewsEntry();
								newsEntry.setPk(a.getInt("pk"));

								String encodedThumbnail = a
										.getString("thumbnail");
								byte[] decodedThumbnailString = Base64.decode(
										encodedThumbnail, Base64.DEFAULT);
								Bitmap decodedThumbnail = BitmapFactory
										.decodeByteArray(
												decodedThumbnailString, 0,
												decodedThumbnailString.length);
								newsEntry.setThumbnail(decodedThumbnail);

								newsEntry.setDateDue(a.getString("date_due"));
								newsEntry.setDateAssigned(a
										.getString("date_assigned"));
								newsEntry.setDescription(a
										.getString("description"));
								newsFeed.add(newsEntry);

								adapter.notifyDataSetChanged();
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
		case R.id.refresh_news_feed:
			newSetNews();
			return true;
		case R.id.post_new_assignment:
			Intent postAssignment = new Intent(this,
					PostAssignmentActivity.class);
			startActivity(postAssignment);
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
