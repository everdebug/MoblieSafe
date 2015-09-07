package com.txy.mobliesafe.ui;

import com.txy.mobliesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {
	private TextView tv_status;
	private CheckBox cb_item;
	private TextView tv_title;
	private String desc_on;
	private String desc_off;

	private void initView(Context context) {
		View view = View.inflate(context, R.layout.item_setting,
				SettingItemView.this);
		tv_status = (TextView) findViewById(R.id.tv_status);
		tv_title = (TextView) findViewById(R.id.textView1);
		cb_item = (CheckBox) findViewById(R.id.cb_item);
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		String title = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.txy.mobliesafe",
				"title");
		desc_on = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.txy.mobliesafe",
				"desc_on");
		desc_off = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.txy.mobliesafe",
				"desc_off");
		tv_title.setText(title);
	}

	public SettingItemView(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * 设置文字
	 */
	public void setStatusText(String text) {
		tv_status.setText(text);
	}

	public String getStatuOn(){
		return desc_on;
	}
	public String getStatuOff(){
		return desc_off;
	}
	/**
	 * 获取到checkbox
	 */
	public CheckBox getCheckBox() {
		return cb_item;
	}
	
	

}
