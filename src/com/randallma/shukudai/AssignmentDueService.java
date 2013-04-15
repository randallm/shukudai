package com.randallma.shukudai;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.IBinder;

public class AssignmentDueService extends IntentService {

	public AssignmentDueService() {
		super("AssignmentDueService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		ArrayList<Assignment> assignmentsDueSoon = new ArrayList<Assignment>();

		AssignmentsDataSource dao = new AssignmentsDataSource(this);
		dao.open();
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

			long daysDiff = ((dateDue.getTimeInMillis() - today
					.getTimeInMillis()) / 86400000) + 1;

			if ((daysDiff <= 0)
					|| (daysDiff > 1)
					|| (dateDue.getTime().getDate() == today.getTime()
							.getDate()
							&& dateDue.getTime().getMonth() == today.getTime()
									.getMonth() && dateDue.getTime().getYear() == today
							.getTime().getYear())) {
			} else {
				assignmentsDueSoon.add(assignment);
			}
		}

		dao.close();

		Notification.Builder notificationBuilder = new Notification.Builder(
				this);
		if (assignmentsDueSoon.size() == 1) {
			Intent assignmentIntent = new Intent(this,
					PostAssignmentActivity.class);
			assignmentIntent.putExtra(MainActivity.ASSIGNMENT_ID,
					assignmentsDueSoon.get(0).getId());
			PendingIntent assignmentPIntent = PendingIntent.getActivity(this,
					0, assignmentIntent, 0);
			notificationBuilder = new Notification.Builder(this);
			notificationBuilder.setContentTitle("Assignment due soon");
			notificationBuilder.setContentText(assignmentsDueSoon.get(0)
					.getSchoolClass()
					+ " - "
					+ assignmentsDueSoon.get(0).getDescription());
			notificationBuilder.setSmallIcon(R.drawable.ic_menu_edit);
			notificationBuilder.setContentIntent(assignmentPIntent);
		} else if (assignmentsDueSoon.size() > 1) {
			Intent assignmentIntent = new Intent(this, MainActivity.class);
			PendingIntent assignmentPIntent = PendingIntent.getActivity(this,
					0, assignmentIntent, 0);
			notificationBuilder = new Notification.Builder(this);
			notificationBuilder.setContentTitle(Integer
					.toString(assignmentsDueSoon.size())
					+ " assignments due soon");
			notificationBuilder.setSmallIcon(R.drawable.ic_menu_edit);
			notificationBuilder.setContentIntent(assignmentPIntent);
		}

		Notification notification = notificationBuilder.build();
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, notification);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
