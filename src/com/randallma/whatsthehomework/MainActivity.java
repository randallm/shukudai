package com.randallma.whatsthehomework;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

public class MainActivity extends ListActivity {

	private void login() {
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		cookieStore.clear();

		Intent loginActivityIntent = new Intent(this, LoginActivity.class);
		startActivity(loginActivityIntent);
		finish();
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ApplicationGlobal g = (ApplicationGlobal) getApplication();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		motd();

		if (getIntent().getStringExtra(PostAssignmentActivity.COMPLETED_POST) != null) {
			setNews("new");
		}

		setClassList();
		ArrayList<Integer> schoolClassIds = new ArrayList<Integer>(
				g.getSchoolClassIds());
		schoolClassIds.add(0, -1);
		ArrayList<String> schoolClassItems = new ArrayList<String>(
				g.getSchoolClassItems());
		schoolClassItems.add(0, "News Feed");

		ArrayAdapter<String> classSpinnerAdapter = new ArrayAdapter<String>(
				this, R.layout.dark_action_bar_spinner_dropdown_item,
				schoolClassItems);
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		ActionBar.OnNavigationListener navigationListener = new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int itemPosition,
					long itemId) {
				ApplicationGlobal g = (ApplicationGlobal) getApplication();

				ArrayList<Integer> schoolClassIds = new ArrayList<Integer>(
						g.getSchoolClassIds());
				schoolClassIds.add(0, -1);
				ArrayList<String> schoolClassItems = new ArrayList<String>(
						g.getSchoolClassItems());
				schoolClassItems.add(0, "News Feed");

				Toast.makeText(MainActivity.this,
						schoolClassItems.get(itemPosition), Toast.LENGTH_SHORT)
						.show();

				if (schoolClassIds.get(itemPosition) == -1) {
					initNewsFeed();
				}
				return false;
			}
		};

		getActionBar().setListNavigationCallbacks(classSpinnerAdapter,
				navigationListener);
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

	private void initNewsFeed() {
		final ListView lv = (ListView) findViewById(android.R.id.list);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				Object o = lv.getItemAtPosition(position);
				NewsEntry fullObject = (NewsEntry) o;
				System.out.println("You chose: " + fullObject.getPk());
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
					setNews("old");
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
		ApplicationGlobal g = (ApplicationGlobal) getApplication();
		g.setNewsFeed(newsFeed);
		g.setAdapter(adapter);
		g.setLv(lv);
		setNews("initial");
	}

	private ArrayList<NewsEntry> populateNewsFeed(JSONObject assignmentJson,
			boolean insertToFrontOfNewsFeed) throws JSONException {
		ApplicationGlobal g = (ApplicationGlobal) getApplication();
		ArrayList<NewsEntry> newsFeed = g.getNewsFeed();

		if (assignmentJson.length() == 0) {
			Toast.makeText(MainActivity.this, "No new homework!",
					Toast.LENGTH_SHORT).show();
		}

		try {
			assignmentJson.getString("15"); // item w/ index 15 is a spacer
											// element when a full refresh
											// should be triggered

			// ugly hack to refresh activity without breaking back button
			Intent intent = getIntent();
			overridePendingTransition(0, 0);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			finish();
			overridePendingTransition(0, 0);
			startActivity(intent);
		} catch (JSONException e) {
			for (int i = 0; i < assignmentJson.length(); i++) {

				JSONObject a = new JSONObject(assignmentJson.getString(Integer
						.toString(i)));

				NewsEntry newsEntry = new NewsEntry();
				newsEntry.setPk(a.getInt("pk"));

				String encodedThumbnail = a.getString("thumbnail");
				byte[] decodedThumbnailString = Base64.decode(encodedThumbnail,
						Base64.DEFAULT);
				Bitmap decodedThumbnail = BitmapFactory.decodeByteArray(
						decodedThumbnailString, 0,
						decodedThumbnailString.length);
				newsEntry.setThumbnail(decodedThumbnail);

				newsEntry.setDateDue("Due: " + a.getString("date_due"));
				newsEntry.setDateAssigned(a.getString("date_posted"));
				newsEntry.setDescription(a.getString("description"));

				if (insertToFrontOfNewsFeed) {
					newsFeed.add(0, newsEntry);
				} else {
					newsFeed.add(newsEntry);
				}
			}
		}
		return newsFeed;
	}

	private void setNews(String refreshContext) {
		AsyncHttpClient clientSession = new AsyncHttpClient();
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		clientSession.setCookieStore(cookieStore);
		ApplicationGlobal g = (ApplicationGlobal) getApplication();

		if (refreshContext.equals("initial")) {
			clientSession.get(g.getWthUrl() + "/news/new/all/",
					new JsonHttpResponseHandler() {
						@SuppressWarnings("unused")
						@Override
						public void onSuccess(JSONObject response) {
							try {
								JSONArray assignments = new JSONArray();
								assignments = response
										.getJSONArray("assignments");
								JSONObject assignmentJson = assignments
										.getJSONObject(0);
								ArrayList<NewsEntry> newsFeed = populateNewsFeed(
										assignmentJson, false);

								ApplicationGlobal g = (ApplicationGlobal) getApplication();
								NewsEntriesAdapter adapter = g.getAdapter();
								adapter.notifyDataSetChanged();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
		} else if (refreshContext.equals("new")) {
			ListView lv = g.getLv();
			try {
				Object o = lv.getItemAtPosition(0);

				NewsEntry fullObject = (NewsEntry) o;
				String latestPost = Integer.toString(fullObject.getPk());

				clientSession.get(g.getWthUrl() + "/news/new/all/" + latestPost
						+ "/", new JsonHttpResponseHandler() {
					@SuppressWarnings("unused")
					@Override
					public void onSuccess(JSONObject response) {
						JSONArray assignments = new JSONArray();
						ApplicationGlobal g = (ApplicationGlobal) getApplication();
						NewsEntriesAdapter adapter = g.getAdapter();

						try {
							assignments = response.getJSONArray("assignments");
							JSONObject assignmentJson = assignments
									.getJSONObject(0);
							ArrayList<NewsEntry> newsFeed = populateNewsFeed(
									assignmentJson, true);
						} catch (JSONException e) {
							e.printStackTrace();
						}

						adapter.notifyDataSetChanged();
					}
				});

			} catch (IndexOutOfBoundsException e) {
				setNews("initial");
			}
		} else if (refreshContext.equals("old")) {
			ArrayList<NewsEntry> newsFeed = g.getNewsFeed();

			ListView lv = g.getLv();

			Object o = lv.getItemAtPosition(newsFeed.size() - 1);
			NewsEntry fullObject = (NewsEntry) o;
			String oldestPost = Integer.toString(fullObject.getPk());

			clientSession.get(g.getWthUrl() + "/news/old/all/" + oldestPost
					+ "/", new JsonHttpResponseHandler() {
				@SuppressWarnings("unused")
				@Override
				public void onSuccess(JSONObject response) {
					JSONArray assignments = new JSONArray();
					try {
						assignments = response.getJSONArray("assignments");
						JSONObject assignmentJson = assignments
								.getJSONObject(0);

						ApplicationGlobal g = (ApplicationGlobal) getApplication();
						NewsEntriesAdapter adapter = g.getAdapter();

						ArrayList<NewsEntry> newsFeed = populateNewsFeed(
								assignmentJson, false);

						adapter.notifyDataSetChanged();

					} catch (JSONException e) {
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
		case R.id.refresh_news_feed:
			setNews("new");
			return true;
		case R.id.post_new_assignment:
			Intent postAssignment = new Intent(this,
					PostAssignmentActivity.class);
			startActivity(postAssignment);
		case R.id.menu_settings:
			return true;
		case R.id.logout:
			login();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
