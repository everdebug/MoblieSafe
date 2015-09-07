package com.txy.mobliesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.xml.XMLConstants;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.Manifest.permission;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

public class SmsUtils {
	/**
	 * 解耦 抽取出两个方法，通过别的类调用并重写方法。
	 * 
	 * @author lenovo
	 * 
	 */
	public interface SmsOptions {
		public void setMax(int max);

		public void setProgress(int progress);
	}

	private static final String TAG = null;

	public static void backupSms(Context context, SmsOptions options)
			throws Exception {
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri, new String[] { "body", "address",
				"type", "date" }, null, null, null);
		int max = cursor.getCount();
		options.setMax(max);
		XmlSerializer xs = Xml.newSerializer();
		File file = new File(Environment.getExternalStorageDirectory(),
				"Sms.xml");
		FileOutputStream fos = new FileOutputStream(file);
		xs.setOutput(fos, "UTF-8");
		xs.startDocument("UTF-8", true);
		xs.startTag(null, "Smss");
		xs.attribute(null, "count", String.valueOf(max));
		int progress = 0;
		while (cursor.moveToNext()) {
			Thread.sleep(500);
			String Body = cursor.getString(0);
			String Date = cursor.getString(3);
			String Type = cursor.getString(2);
			String Addr = cursor.getString(1);
			xs.startTag(null, "sms");
			xs.startTag(null, "body");
			xs.text(Body);
			xs.endTag(null, "body");

			xs.startTag(null, "address");
			xs.text(Addr);
			xs.endTag(null, "address");

			xs.startTag(null, "type");
			xs.text(Type);
			xs.endTag(null, "type");

			xs.startTag(null, "date");
			xs.text(Date);
			xs.endTag(null, "date");

			xs.endTag(null, "sms");
			progress++;
			options.setProgress(progress);
		}
		cursor.close();
		xs.endTag(null, "Smss");
		xs.endDocument();
		fos.close();
	}

	public static void restoreSms(Context context, boolean isNull,
			SmsOptions options) throws Exception {

		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms/");
		if (isNull) {
			resolver.delete(uri, null, null);
		}
		ContentValues values = null;

		File file = new File(Environment.getExternalStorageDirectory(),
				"Sms.xml");
		XmlPullParser parser = Xml.newPullParser();
		FileInputStream fis = new FileInputStream(file);
		parser.setInput(fis, "UTF-8");
		int type = parser.getEventType();
		int max = 0;
		int progress = 0;
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				// 得到基本信息
				if (parser.getName().equals("Smss")) {
					max = Integer.parseInt(parser.getAttributeValue(null,
							"count"));
					options.setMax(max);
					Log.i(TAG, max + "");
				} else if (parser.getName().equals("sms")) {
					values = new ContentValues();
					Log.i(TAG, "new");
				} else if (parser.getName().equals("body")) {
					values.put("body", parser.nextText());
				} else if (parser.getName().equals("address")) {
					values.put("address", parser.nextText());
				} else if (parser.getName().equals("type")) {
					values.put("type", parser.nextText());
				} else if (parser.getName().equals("date")) {
					values.put("date", parser.nextText());
				}
				break;
			case XmlPullParser.END_TAG:
				// 写入短信
				if (parser.getName().equals("sms")) {
					Thread.sleep(500);
					resolver.insert(uri, values);
					progress++;
					options.setProgress(progress);
				}
				break;

			default:
				break;
			}
			type = parser.next();
		}

	}
}
