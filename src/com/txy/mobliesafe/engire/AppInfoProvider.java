package com.txy.mobliesafe.engire;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.txy.mobliesafe.db.domain.AppInfo;

public class AppInfoProvider {

	public static List<AppInfo> getInfos(Context context) {
		List<AppInfo> list = new ArrayList<AppInfo>();
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packageinfos = pm.getInstalledPackages(0);
		for (PackageInfo packageinfo : packageinfos) {
			AppInfo appinfo = new AppInfo();
			String packageName = packageinfo.packageName;
			String appName = packageinfo.applicationInfo.loadLabel(pm)
					.toString();
			Drawable appIcon = packageinfo.applicationInfo.loadIcon(pm)
					.getCurrent();
			int flags = packageinfo.applicationInfo.flags;// 应用程序信息的标记
															// 相当于用户提交的答卷
			appinfo.setAppName(appName);
			appinfo.setAppAddr(packageName);
			appinfo.setIcon(appIcon);

			if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				appinfo.setUserApp(true);
			} else {
				appinfo.setUserApp(false);
			}
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
				appinfo.setInRom(true);
			} else {
				appinfo.setInRom(false);
			}
			list.add(appinfo);
		}

		return list;
	}
}
