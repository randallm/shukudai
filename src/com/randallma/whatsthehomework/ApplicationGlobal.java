package com.randallma.whatsthehomework;

import java.util.ArrayList;

import android.app.Application;
import android.widget.ListView;

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

	public ArrayList<NewsEntry> newsFeed;

	public ArrayList<NewsEntry> getNewsFeed() {
		return newsFeed;
	}

	public void setNewsFeed(ArrayList<NewsEntry> newsFeed) {
		this.newsFeed = newsFeed;
	}

	public ListView lv;

	public ListView getLv() {
		return lv;
	}

	public void setLv(ListView lv) {
		this.lv = lv;
	}

	public ArrayList<Integer> schoolClassIds;

	public ArrayList<Integer> getSchoolClassIds() {
		return schoolClassIds;
	}

	public void setSchoolClassIds(ArrayList<Integer> schoolClassIds) {
		this.schoolClassIds = schoolClassIds;
	}

	public ArrayList<String> schoolClassItems;

	public ArrayList<String> getSchoolClassItems() {
		return schoolClassItems;
	}

	public void setSchoolClassItems(ArrayList<String> schoolClassItems) {
		this.schoolClassItems = schoolClassItems;
	}
}
