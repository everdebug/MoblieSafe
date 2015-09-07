package com.txy.mobliesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import com.txy.mobliesafe.MyWidget;
import com.txy.mobliesafe.R;
import com.txy.mobliesafe.engire.ProcessInfoProvider;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdateProcessInfoService extends Service {

	private Timer timer;
	private AppWidgetManager awm;
	private ScreenOnBroadcast sonb;
	private ScreenOffBroadcast soffb;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		sonb = new ScreenOnBroadcast();
		soffb = new ScreenOffBroadcast();
		registerReceiver(sonb, new IntentFilter(Intent.ACTION_SCREEN_ON));
		registerReceiver(soffb, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		awm = AppWidgetManager.getInstance(this);
		startTimer();
		super.onCreate();
	}

	public void startTimer() {
		if (timer == null) {
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					// 3秒后运行
					ComponentName provider = new ComponentName(
							UpdateProcessInfoService.this, MyWidget.class);

					RemoteViews views = new RemoteViews(getPackageName(),
							R.layout.widget_process_info);
					views.setTextViewText(
							R.id.process_count,
							"正在运行的进程： "
									+ ProcessInfoProvider
											.getRunningProcessCount(UpdateProcessInfoService.this)
									+ "个");
					views.setTextViewText(
							R.id.process_memory,
							"可用内存： "
									+ Formatter
											.formatFileSize(
													getApplicationContext(),
													ProcessInfoProvider
															.getAvaliMem(UpdateProcessInfoService.this)));

					Intent intent = new Intent();
					intent.setAction("com.txy.mobliesafe.killall");
					PendingIntent pendingIntent = PendingIntent.getBroadcast(
							getApplicationContext(), 0, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
					Log.i("update", "更新widget");
					awm.updateAppWidget(provider, views);

				}
			}, 0, 3000);
		}
	}

	class ScreenOffBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 锁屏就停止
			stopTimer();
		}
	}

	class ScreenOnBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 屏幕启动就开始
			startTimer();
		}
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(sonb);
		unregisterReceiver(soffb);
		sonb=null;
		soffb=null;
		stopTimer();
		super.onDestroy();
	}

	public void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
}
