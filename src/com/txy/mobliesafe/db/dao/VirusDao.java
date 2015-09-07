package com.txy.mobliesafe.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class VirusDao {

	public static boolean isVirus(String md5) {
		boolean result = false;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				"/data/data/com.txy.mobliesafe/files/antivirus.db", null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from datable where md5=?",
				new String[] { md5 });
		if (cursor.moveToNext()) {
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}

}
