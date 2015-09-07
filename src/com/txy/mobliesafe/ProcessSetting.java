package com.txy.mobliesafe;

import com.txy.mobliesafe.service.ProcessService;
import com.txy.mobliesafe.utils.ServiceStatusUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ProcessSetting extends Activity {
	protected static final String TAG = "ProcessSetting";
	private CheckBox cb_sys_show, cb_lion_clear;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_settings);

		sp = getSharedPreferences("config", MODE_PRIVATE);
		cb_sys_show = (CheckBox) findViewById(R.id.cb_sys_show);
		cb_lion_clear = (CheckBox) findViewById(R.id.cb_lion_clear);

		cb_sys_show.setChecked(sp.getBoolean("showSys", false));
		cb_lion_clear.setChecked(sp.getBoolean("lionClear", false));

		cb_sys_show.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				Editor edit = sp.edit();
				if (isChecked) {
					edit.putBoolean("showSys", true);
					Log.i(TAG, "��ʾϵͳ����");
				} else {
					edit.putBoolean("showSys", false);
					Log.i(TAG, "����ʾϵͳ����");
				}
				edit.commit();
			}
		});

		cb_lion_clear.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Intent intent = new Intent(ProcessSetting.this,
						ProcessService.class);
				if (isChecked) {
					startService(intent);
					Log.i(TAG, "��������");
				} else {
					stopService(intent);
					Log.i(TAG, "����������");
				}

			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(
				ProcessSetting.this,
				"com.txy.mobliesafe.service.ProcessService");
		cb_lion_clear.setChecked(serviceRunning);

	}
}
