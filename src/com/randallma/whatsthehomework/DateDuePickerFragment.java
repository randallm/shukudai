package com.randallma.whatsthehomework;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;

public class DateDuePickerFragment extends DialogFragment implements
		OnDateSetListener {

	public static String getReadableDate(int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.set(year, month, day);

		SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEEE");
		SimpleDateFormat monthNameFormat = new SimpleDateFormat("MMMM");

		String sWeekday = weekdayNameFormat.format(c.getTime());
		String sMonth = monthNameFormat.format(c.getTime());
		String sDay = Integer.toString(day);
		String sYear = Integer.toString(year);

		return sWeekday + ", " + sMonth + " " + sDay + ", " + sYear;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		Activity a = getActivity();
		Button dateDueButton = (Button) a.findViewById(R.id.dateDue);

		dateDueButton.setText(getReadableDate(year, month, day));
	}
}
