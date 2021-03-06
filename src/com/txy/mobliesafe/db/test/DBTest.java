package com.txy.mobliesafe.db.test;

import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import com.txy.mobliesafe.db.BlackNumberDBOpenHelper;
import com.txy.mobliesafe.db.dao.BlackNumberDao;
import com.txy.mobliesafe.db.domain.BlackNumberInfo;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

public class DBTest extends AndroidTestCase {

	public void TestCreateDB() {
		BlackNumberDBOpenHelper dbHelper = new BlackNumberDBOpenHelper(
				getContext());
		SQLiteDatabase database = dbHelper.getReadableDatabase();
	}

	public void TestCreate() {
		BlackNumberDao db = new BlackNumberDao(getContext());
		System.out.println(db.create("12345670890", "1"));
	}

	public void TestUpdate() {
		BlackNumberDao db = new BlackNumberDao(getContext());
		boolean result = db.update("12345670890", "2");
		Assert.assertEquals(true, result);
	}

	public void Testfind() {
		BlackNumberDao db = new BlackNumberDao(getContext());
		boolean result = db.find("12345670890");
		Assert.assertEquals(true, result);
	}

	public void TestDelete() {
		BlackNumberDao db = new BlackNumberDao(getContext());
		boolean result = db.delete("12345670890");
		Assert.assertEquals(true, result);
	}

	public void TestfindAll() {
		BlackNumberDao db = new BlackNumberDao(getContext());
		Random random = new Random();
		for (int i = 0; i < 30; i++) {
			db.create(String.valueOf("13000000000" + i),
					String.valueOf(random.nextInt(3) + 1));
		}
		List<BlackNumberInfo> infos = db.findAll();
		for (BlackNumberInfo info : infos) {
			System.out.println(info.toString());
		}
	}
}
