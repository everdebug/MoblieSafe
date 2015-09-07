package com.txy.mobliesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.txy.mobliesafe.db.BlackNumberDBOpenHelper;
import com.txy.mobliesafe.db.domain.BlackNumberInfo;

public class BlackNumberDao {
	private BlackNumberDBOpenHelper dbOpenHelper;

	public BlackNumberDao(Context context) {
		dbOpenHelper = new BlackNumberDBOpenHelper(context);
	}

	/**
	 * 
	 * 查找是否有某个黑名单
	 * 
	 * @param number
	 * @return 查找结果
	 */
	public boolean find(String number) {
		boolean result = false;
		SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
		Cursor cursor = database.rawQuery(
				"select * from blacknumber where number = ?",
				new String[] { number });
		while (cursor.moveToNext()) {
			result = true;
		}
		cursor.close();
		database.close();
		return result;
	}

	/**
	 * 
	 * 增加一个黑名单
	 * 
	 * @param number
	 * @param mode
	 *            1.拦截短信 2.拦截电话 3.拦截全部
	 * @return 添加结果
	 */
	public boolean create(String number, String mode) {
		boolean result = false;
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		long insert = db.insert("blacknumber", null, values);
		if (insert != -1) {
			result = true;
		}
		db.close();
		return result;
	}

	public boolean update(String number, String newmode) {
		boolean result = false;
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", newmode);
		long update = db.update("blacknumber", values, "number = ?",
				new String[] { number });
		if (update != -1) {
			result = true;
		}
		db.close();
		return result;
	}

	public boolean delete(String number) {
		boolean result = false;
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		int delete = db.delete("blacknumber", "number = ?",
				new String[] { number });
		if (delete != 0) {
			result = true;
		}
		db.close();
		return result;
	}

	public List<BlackNumberInfo> findAll() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor curosr = db.rawQuery(
				"select * from blacknumber order by _id desc", null);
		while (curosr.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			String number = curosr.getString(1);
			String mode = curosr.getString(2);
			info.setNumber(number);
			info.setMode(mode);

			list.add(info);
		}
		curosr.close();
		db.close();
		return list;
	}
/**
 * 
 * 查找部分数据
 * @param offset	查找开始的位置
 * @param limit		查找一次的最大数据个数
 * @return
 */
	public List<BlackNumberInfo> findPart(int offset,int limit) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor curosr = db.rawQuery(
				"select * from blacknumber order by _id desc limit ? offset ?",
				new String[] {limit+"",offset+""});
		while (curosr.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			String number = curosr.getString(1);
			String mode = curosr.getString(2);
			info.setNumber(number);
			info.setMode(mode);

			list.add(info);
		}
		curosr.close();
		db.close();
		return list;
	}
}
