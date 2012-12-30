package com.randallma.whatsthehomework;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Application;

public class ApplicationGlobal extends Application {
	// var declarations

	public ArrayList<HashMap<String, String>> newsFeed = new ArrayList<HashMap<String, String>>();

	// get methods

	public ArrayList<HashMap<String, String>> getNewsFeed() {
		return newsFeed;
	}

	// set methods

	public void setNewsFeed(ArrayList<HashMap<String, String>> newsList) {
		this.newsFeed = newsList;
	}
}
