package com.txy.mobliesafe;

import java.lang.reflect.Method;
import java.util.List;

import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CacheClearActivity extends Activity {
	private PackageManager pm;
	private ProgressBar pb;
	private int progress = 0;
	private TextView tv_show_cache_scan;
	private LinearLayout ll_show_cache_scan;
	private TextView temptv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cache_clear);

		pm = getPackageManager();
		pb = (ProgressBar) findViewById(R.id.pb_cache_progress);
		tv_show_cache_scan = (TextView) findViewById(R.id.tv_show_cache_scan);
		ll_show_cache_scan = (LinearLayout) findViewById(R.id.ll_show_cache_scan);
		scanCache();
	}

	private void scanCache() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				List<PackageInfo> packages = pm.getInstalledPackages(0);
				Method[] methods = PackageManager.class.getMethods();
				Method getPackageSizeInfoMethod = null;
				for (Method m : methods) {
					if ("getPackageSizeInfo".equals(m.getName())) {
						getPackageSizeInfoMethod = m;

					}
				}
				pb.setMax(packages.size());
				for (PackageInfo info : packages) {
					String packageName = (String) info.packageName;
					try {
						getPackageSizeInfoMethod.invoke(pm, packageName,
								new MyObserver());

					} catch (Exception e) {
						e.printStackTrace();
					}

					progress++;
					pb.setProgress(progress);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// 完成
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						tv_show_cache_scan.setText("扫描完成");
					}
				});
			}
		}).start();

	}

	class MyObserver extends IPackageStatsObserver.Stub {

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			final long cache = pStats.cacheSize;
			// long code = pStats.codeSize;
			// long data = pStats.dataSize;
			try {
				final String packname = pStats.packageName;
				final ApplicationInfo appinfo = pm.getApplicationInfo(packname,
						0);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						tv_show_cache_scan.setText("正在扫描： "
								+ (String) appinfo.loadLabel(pm));
						if (cache > 0) {
							temptv = new TextView(getApplicationContext());
							temptv.setText((String) appinfo.loadLabel(pm)
									+ "---缓存大小： "
									+ Formatter.formatFileSize(
											getApplicationContext(), cache));
							ll_show_cache_scan.addView(temptv, 0);
						}
					}
				});
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

}
