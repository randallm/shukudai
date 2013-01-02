package com.randallma.whatsthehomework;

import java.util.ArrayList;

import android.app.Application;

public class ApplicationGlobal extends Application {
	public String wthUrl = "http://192.168.1.42:5000";

	public String getWthUrl() {
		return wthUrl;
	}

	public NewsEntriesAdapter adapter;

	public NewsEntriesAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(NewsEntriesAdapter adapter) {
		this.adapter = adapter;
	}

	ArrayList<NewsEntry> newsFeed;

	public ArrayList<NewsEntry> getNewsFeed() {
		return newsFeed;
	}

	public void setNewsFeed(ArrayList<NewsEntry> newsFeed) {
		this.newsFeed = newsFeed;
	}

}
