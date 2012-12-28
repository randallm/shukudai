package com.randallma.whatsthehomework;

import android.app.Application;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

public class ApplicationGlobal extends Application {
	private AsyncHttpClient clientSession = null;
	private PersistentCookieStore cookieStore = null;
	public boolean userLoggedIn = false;

	public AsyncHttpClient getClientSession() {
		return clientSession;
	}

	public PersistentCookieStore getCookieStore() {
		return cookieStore;
	}

	public boolean getUserLoggedIn() {
		return userLoggedIn;
	}

	public void setClientSession(AsyncHttpClient c) {
		this.clientSession = c;
	}

	public void setCookieStore(PersistentCookieStore c) {
		this.cookieStore = c;
	}

	public void setUserLoggedIn(boolean s) {
		this.userLoggedIn = s;
	}
}
