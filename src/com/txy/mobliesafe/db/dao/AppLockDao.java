package com.txy.mobliesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.txy.mobliesafe.db.AppLockDBhelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AppLockDao {
	private AppLockDBhelper dbOpenHelper;
	private Context context;

	public AppLockDao(Context context) {
		dbOpenHelper = new AppLockDBhelper(context);
		this.context = context;
	}

	/**
	 * 添加一条要进行程序锁的应用程序
	 * 
	 * @param appname
	 *            名称
	 */
	public void add(String appname) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("appname", appname);
		db.insert("applock", null, values);
		Intent intent = new Intent();
		intent.setAction("com.txy.mobliesafe.updateunlock");
		context.sendBroadcast(intent);
		db.close();
	}

	/**
	 * 删除一条
	 * 
	 * @param appname
	 */
	public void delete(String appname) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.delete("applock", "appname = ?", new String[] { appname });
		Intent intent = new Intent();
		intent.setAction("com.txy.mobliesafe.updateunlock");
		context.sendBroadcast(intent);
		db.close();

	}

	public boolean find(String appname) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.query("applock", null, "appname = ?",
				new String[] { appname }, null, null, null);
		if (cursor.moveToNext()) {
			db.close();
			cursor.close();

			return true;
		}
		db.close();
		cursor.close();
		return false;
	}

	/**
	 * 
	 * 得到所有的appname
	 * 
	 * @return
	 */
	public List<String> findAll() {
		List<String> applocklist = new ArrayList<String>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.query("applock", new String[] { "appname" }, null,
				null, null, null, null);
		while (cursor.moveToNext()) {
			applocklist.add(cursor.getString(0));
			Log.i("db", cursor.getString(0));
		}
		db.close();
		cursor.close();

		return applocklist;
	}
}
