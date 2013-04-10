package com.randallma.shukudai;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class AssignmentDueService extends IntentService {

	public AssignmentDueService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		ArrayList<Assignment> assignmentsDueSoon = new ArrayList<Assignment>();

		AssignmentsDataSource dao = new AssignmentsDataSource(this);
		ArrayList<Assignment> newAssignments = dao.getNewAssignments();

		for (Assignment assignment : newAssignments) {
			Calendar dateDue = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy");
			try {
				dateDue.setTime(sdf.parse(assignment.getDateDue()));
			} catch (ParseException e) {
				e.printStackTrace();
			}

			Calendar today = Calendar.getInstance();

			double daysDiff = (dateDue.getTimeInMillis() - today
					.getTimeInMillis()) / 86400000.0;
			if (daysDiff < 1.0) {
				assignmentsDueSoon.add(assignment);
			}
		}

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_menu_edit).setContentTitle(
				"1337 Assignments Due Soon");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
