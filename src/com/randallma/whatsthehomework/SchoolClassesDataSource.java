package com.randallma.whatsthehomework;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SchoolClassesDataSource {
	private SQLiteDatabase db;
	private final SQLiteHelper dbHelper;

	public SchoolClassesDataSource(Context context) {
		dbHelper = new SQLiteHelper(context);
	}

	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void createSchoolClass(String schoolClassTitle) {
		ContentValues values = new ContentValues();

		values.put(SQLiteHelper.COLUMN_TITLE, schoolClassTitle);
		db.insert(SQLiteHelper.TABLE_SCHOOL_CLASSES, null, values);
	}

	// public void deleteSchoolClass(Long schoolClassId) {
	// Cursor cursor = db
	// .query(SQLiteHelper.TABLE_ASSIGNMENTS, null,
	// SQLiteHelper.COLUMN_SCHOOL_CLASS_ID + " = ?",
	// new String[] { Long.toString(schoolClassId) }, null,
	// null, null);
	// cursor.moveToFirst();
	//
	// Long id;
	// for (int i = 0; i < cursor.getCount(); i++) {
	// id = cursor.getLong(0);
	// db.delete(SQLiteHelper.TABLE_ASSIGNMENTS, SQLiteHelper.COLUMN_ID
	// + " = " + id, null);
	// Log.i(SchoolClassesDataSource.class.getName(),
	// "Assignment deleted with id: " + id);
	// cursor.moveToNext();
	// }
	// cursor.close();
	//
	// db.delete(SQLiteHelper.TABLE_SCHOOL_CLASSES, SQLiteHelper.COLUMN_ID
	// + " = " + schoolClassId, null);
	// Log.i(SchoolClassesDataSource.class.getName(),
	// "SchoolClass deleted with id: " + schoolClassId);
	// }

}
