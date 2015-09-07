package com.txy.mobliesafe.service;

import java.util.List;

import com.txy.mobliesafe.db.domain.ProcessInfo;
import com.txy.mobliesafe.engire.ProcessInfoProvider;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class ProcessService extends Service {

	public static final String TAG = "ProcessService";
	private MyLionBroadcast broadcast;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		broadcast = new MyLionBroadcast();
		registerReceiver(broadcast, new IntentFilter(Intent.ACTION_SCREEN_OFF));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcast);
	}

	class MyLionBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 开启锁屏服务
			ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			List<ProcessInfo> list = ProcessInfoProvider
					.getAllInfo(ProcessService.this);
			for (ProcessInfo info : list) {
				am.killBackgroundProcesses(info.getPackname());
			}
			Log.i(TAG, "清除成功");
		}
	}
}
