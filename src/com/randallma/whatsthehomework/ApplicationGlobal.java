package com.randallma.whatsthehomework;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Application;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

public class ApplicationGlobal extends Application {
	// var declarations

	private AsyncHttpClient clientSession = null;
	private PersistentCookieStore cookieStore = null;
	public boolean userLoggedIn = false;
	public ArrayList<HashMap<String, String>> newsFeed = new ArrayList<HashMap<String, String>>();

	// get methods

	public AsyncHttpClient getClientSession() {
		return clientSession;
	}

	public PersistentCookieStore getCookieStore() {
		return cookieStore;
	}

	public boolean getUserLoggedIn() {
		return userLoggedIn;
	}

	public ArrayList<HashMap<String, String>> getNewsFeed() {
		return newsFeed;
	}

	// set methods

	public void setClientSession(AsyncHttpClient x) {
		this.clientSession = x;
	}

	public void setCookieStore(PersistentCookieStore x) {
		this.cookieStore = x;
	}

	public void setUserLoggedIn(boolean x) {
		this.userLoggedIn = x;
	}

	public void setNewsFeed(ArrayList<HashMap<String, String>> newsList) {
		this.newsFeed = newsList;
	}
}
