package com.txy.mobliesafe.ui;

import com.txy.mobliesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemToastView extends RelativeLayout {
	private TextView tv_status;

	private TextView tv_title;

	private void initView(Context context) {
		View view = View.inflate(context, R.layout.item_setting_toast,
				SettingItemToastView.this);
		tv_status = (TextView) findViewById(R.id.tv_status);
		tv_title = (TextView) findViewById(R.id.textView1);
	}

	public SettingItemToastView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public SettingItemToastView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		String title = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.txy.mobliesafe",
				"title");
		tv_title.setText(title);
	}

	public SettingItemToastView(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * …Ë÷√Œƒ◊÷
	 */
	public void setStatusText(String text) {
		tv_status.setText(text);
	}

}
