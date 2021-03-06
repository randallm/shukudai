package com.randallma.shukudai;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AssignmentsDataSource {
	private SQLiteDatabase db;
	private final SQLiteHelper dbHelper;

	public AssignmentsDataSource(Context context) {
		dbHelper = new SQLiteHelper(context);
	}

	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void createAssignment(Assignment assignment) {
		ContentValues values = new ContentValues();

		values.put(SQLiteHelper.COLUMN_DESCRIPTION, assignment.getDescription());
		values.put(SQLiteHelper.COLUMN_DATE_DUE, assignment.getDateDue());
		values.put(SQLiteHelper.COLUMN_DATE_ASSIGNED,
				assignment.getDateAssigned());
		values.put(SQLiteHelper.COLUMN_IMAGE, assignment.getImageUri());
		values.put(SQLiteHelper.COLUMN_SCHOOL_CLASS_ID,
				assignment.getSchoolClassId());
		values.put(SQLiteHelper.COLUMN_ARCHIVED, 0);
		long insertId = db.insert(SQLiteHelper.TABLE_ASSIGNMENTS, null, values);
		Cursor cursor = db.query(SQLiteHelper.TABLE_ASSIGNMENTS, null,
				SQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null,
				null);
		cursor.close();
	}

	public void editAssignment(Assignment assignment) {
		ContentValues values = new ContentValues();

		values.put(SQLiteHelper.COLUMN_DESCRIPTION, assignment.getDescription());
		values.put(SQLiteHelper.COLUMN_DATE_DUE, assignment.getDateDue());
		values.put(SQLiteHelper.COLUMN_DATE_ASSIGNED,
				assignment.getDateAssigned());
		values.put(SQLiteHelper.COLUMN_IMAGE, assignment.getImageUri());
		values.put(SQLiteHelper.COLUMN_SCHOOL_CLASS_ID,
				assignment.getSchoolClassId());

		db.update(SQLiteHelper.TABLE_ASSIGNMENTS, values,
				SQLiteHelper.COLUMN_ID + "=?",
				new String[] { Long.toString(assignment.getId()) });
	}

	public void deleteAssignment(Assignment assignment) {
		long id = assignment.getId();
		Log.i(AssignmentsDataSource.class.getName(),
				"Assignment deleted with id: " + id);
		db.delete(SQLiteHelper.TABLE_ASSIGNMENTS, SQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public ArrayList<Assignment> getNewAssignments() {
		ArrayList<Assignment> assignments = new ArrayList<Assignment>();

		Cursor cursor = db.query(SQLiteHelper.TABLE_ASSIGNMENTS, null,
				SQLiteHelper.COLUMN_ARCHIVED + " = 0", null, null, null,
				SQLiteHelper.COLUMN_ID + " DESC");
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Assignment assignment = cursorToAssignment(cursor);
			assignments.add(assignment);
			cursor.moveToNext();
		}

		cursor.close();
		return assignments;
	}

	public ArrayList<Assignment> getFilteredAssignments(Long schoolClassId) {
		ArrayList<Assignment> assignments = new ArrayList<Assignment>();

		Cursor cursor = db.query(SQLiteHelper.TABLE_ASSIGNMENTS, null,
				SQLiteHelper.COLUMN_SCHOOL_CLASS_ID + " = ?",
				new String[] { Long.toString(schoolClassId) }, null, null,
				SQLiteHelper.COLUMN_ID + " DESC");
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Assignment assignment = cursorToAssignment(cursor);
			assignments.add(assignment);
			cursor.moveToNext();
		}

		cursor.close();
		return assignments;
	}

	private Assignment cursorToAssignment(Cursor cursor) {
		Assignment assignment = new Assignment();
		assignment.setId(cursor.getLong(0));
		assignment.setDescription(cursor.getString(1));
		assignment.setDateDue(cursor.getString(2));
		assignment.setDateAssigned(cursor.getString(3));
		assignment.setImageUri(cursor.getString(4));

		Cursor schoolClassCursor = db.query(SQLiteHelper.TABLE_SCHOOL_CLASSES,
				null, SQLiteHelper.COLUMN_ID + " = ?",
				new String[] { Integer.toString(cursor.getInt(5)) }, null,
				null, null, null);
		schoolClassCursor.moveToFirst();
		assignment.setSchoolClass(schoolClassCursor.getString(1));

		assignment.setSchoolClassId(cursor.getInt(5));

		return assignment;
	}
}
