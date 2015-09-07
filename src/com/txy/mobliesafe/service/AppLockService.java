package com.txy.mobliesafe.service;

import java.util.List;

import com.txy.mobliesafe.LockSelectActivity;
import com.txy.mobliesafe.db.dao.AppLockDao;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;
import android.util.Log;

public class AppLockService extends Service {

	private static final String TAG = "AppLockService";
	private ActivityManager am;
	private PackageManager pm;
	private boolean flag;
	private AppLockDao db;
	private UnLockBroadcast ulb;
	private String unLockName;
	private List<String> applocklist;
	private Intent intent;
	private UpdateBroadcast update;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "开启服务");
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		pm = getPackageManager();
		flag = true;
		db = new AppLockDao(this);
		ulb = new UnLockBroadcast();
		registerReceiver(ulb, new IntentFilter("com.txy.mobliesafe.tempunlock"));
		applocklist = db.findAll();
		update = new UpdateBroadcast();
		registerReceiver(update, new IntentFilter("com.txy.mobliesafe.updateunlock"));

		intent = new Intent(AppLockService.this, LockSelectActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (flag) {
					List<RunningTaskInfo> tasks = am.getRunningTasks(1);
					String packageName = tasks.get(0).topActivity
							.getPackageName();
					try {
						ApplicationInfo info = pm.getApplicationInfo(
								packageName, 0);
						String appName = (String) info.loadLabel(pm);
						Thread.sleep(20);

						if (applocklist.contains(appName)) {
							if (!appName.equals(unLockName)) {
								intent.putExtra("appName", appName);
								startActivity(intent);
								// Log.i(TAG, appName + "需要密码验证" + unLockName);
							} else {
								// Log.i(TAG, appName + "临时打开" + unLockName);
							}
						}
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "关闭服务");
		flag = false;
		unregisterReceiver(ulb);
		ulb = null;
		unregisterReceiver(update);
		update = null;
		super.onDestroy();
	}

	class UnLockBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			unLockName = intent.getStringExtra("appUnLockName");
			Log.i(TAG, unLockName);
		}

	}

	class UpdateBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			applocklist = db.findAll();
		}

	}
}
