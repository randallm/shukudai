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

	public int[] schoolClassIds;

	public int[] getSchoolClassIds() {
		return schoolClassIds;
	}

	public void setSchoolClassIds(int[] schoolClassIds) {
		this.schoolClassIds = schoolClassIds;
	}

	public String[] schoolClassTitles;

	public String[] getSchoolClassTitles() {
		return schoolClassTitles;
	}

	public void setSchoolClassTitles(String[] schoolClassTitles) {
		this.schoolClassTitles = schoolClassTitles;
	}
}
