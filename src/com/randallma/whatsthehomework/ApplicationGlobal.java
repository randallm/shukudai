package com.randallma.whatsthehomework;

import android.app.Application;

public class ApplicationGlobal extends Application {
	public String wthUrl = "http://192.168.1.42:5000";

	public String getWthUrl() {
		return wthUrl;
	}
}
