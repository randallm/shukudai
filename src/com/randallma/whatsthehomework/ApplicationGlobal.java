package com.randallma.whatsthehomework;

import java.util.ArrayList;

import android.app.Application;

public class ApplicationGlobal extends Application {
	public String wthUrl = "http://192.168.1.42:5000";

	public String getWthUrl() {
		return wthUrl;
	}

	public AssignmentAdapter assignmentAdapter;

	public AssignmentAdapter getAssignmentAdapter() {
		return assignmentAdapter;
	}

	public void setAssignmentAdapter(AssignmentAdapter assignmentAdapter) {
		this.assignmentAdapter = assignmentAdapter;
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
