package com.txy.mobliesafe;

import com.txy.mobliesafe.service.AddressService;
import com.txy.mobliesafe.service.AppLockService;
import com.txy.mobliesafe.ui.SettingItemToastView;
import com.txy.mobliesafe.ui.SettingItemView;
import com.txy.mobliesafe.utils.ServiceStatusUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingActivity extends Activity {

	protected static final String TAG = "SettingActivity";
	private SettingItemView siv_item, siv_item_callservice,
			siv_item_applockservice;
	private CheckBox siv_item_cb, siv_item_callservice_cb,
			siv_item_applockservice_cb;
	private SettingItemToastView sitv;
	private SharedPreferences sp;
	private Intent showAddress, appLock;
	private boolean applockserviceStatu;

	@Override
	protected void onResume() {
		super.onResume();
		showAddress = new Intent(this, AddressService.class);
		boolean serviceStatu = ServiceStatusUtils.isServiceRunning(this,
				"com.txy.mobliesafe.service.AddressService");
		siv_item_callservice_cb.setChecked(serviceStatu);

		appLock = new Intent(this, AppLockService.class);
		applockserviceStatu = ServiceStatusUtils.isServiceRunning(this,
				"com.txy.mobliesafe.service.AppLockService");
		siv_item_applockservice_cb.setChecked(applockserviceStatu);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		// 1
		siv_item = (SettingItemView) findViewById(R.id.siv_item);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		siv_item_cb = siv_item.getCheckBox();

		boolean update = sp.getBoolean("update", true);
		siv_item_cb.setChecked(update);
		if (update) {
			siv_item.setStatusText(siv_item.getStatuOn());
		} else {
			siv_item.setStatusText(siv_item.getStatuOff());
		}

		siv_item_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Editor et = sp.edit();
				if (isChecked) {
					et.putBoolean("update", true);
					siv_item.setStatusText(siv_item.getStatuOn());
				} else {
					et.putBoolean("update", false);
					siv_item.setStatusText(siv_item.getStatuOff());
				}
				et.commit();
			}
		});
		// 2
		siv_item_callservice = (SettingItemView) findViewById(R.id.siv_item_callservice);
		siv_item_callservice_cb = siv_item_callservice.getCheckBox();
		boolean serviceStatu = ServiceStatusUtils.isServiceRunning(this,
				"com.txy.mobliesafe.service.AddressService");
		if (serviceStatu) {
			siv_item_callservice_cb.setChecked(serviceStatu);
			siv_item_callservice.setStatusText(siv_item_callservice
					.getStatuOn());
		} else {
			siv_item_callservice_cb.setChecked(serviceStatu);
			siv_item_callservice.setStatusText(siv_item_callservice
					.getStatuOff());
		}

		showAddress = new Intent(this, AddressService.class);

		siv_item_callservice_cb
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							// 打开service
							startService(showAddress);
							siv_item_callservice
									.setStatusText(siv_item_callservice
											.getStatuOn());
						} else {
							// 关闭服务
							stopService(showAddress);
							siv_item_callservice
									.setStatusText(siv_item_callservice
											.getStatuOff());
						}
					}
				});
		// 3
		siv_item_applockservice = (SettingItemView) findViewById(R.id.siv_item_applockservice);
		siv_item_applockservice_cb = siv_item_applockservice.getCheckBox();
		applockserviceStatu = ServiceStatusUtils.isServiceRunning(this,
				"com.txy.mobliesafe.service.AppLockService");
		if (applockserviceStatu) {
			siv_item_applockservice_cb.setChecked(serviceStatu);
			siv_item_applockservice.setStatusText(siv_item_applockservice
					.getStatuOn());
		} else {
			siv_item_applockservice_cb.setChecked(serviceStatu);
			siv_item_applockservice.setStatusText(siv_item_applockservice
					.getStatuOff());
		}

		appLock = new Intent(this, AppLockService.class);

		siv_item_applockservice_cb
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							// 打开service
							startService(appLock);
							siv_item_applockservice
									.setStatusText(siv_item_applockservice
											.getStatuOn());
						} else {
							// 关闭服务
							stopService(appLock);
							siv_item_applockservice
									.setStatusText(siv_item_applockservice
											.getStatuOff());
						}
					}
				});

		sitv = (SettingItemToastView) findViewById(R.id.sitv);
		final String[] item = new String[] { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };
		final int savebg = sp.getInt("background", 0);
		sitv.setStatusText(item[savebg]);
		sitv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// 弹出对话框
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("请设置背景色")
						.setSingleChoiceItems(item, savebg,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Editor et = sp.edit();
										et.putInt("background", which);
										et.commit();
										sitv.setStatusText(item[which]);
										dialog.dismiss();
									}
								}).setNegativeButton("cancel", null);
				builder.show();
			}
		});

	}
}
