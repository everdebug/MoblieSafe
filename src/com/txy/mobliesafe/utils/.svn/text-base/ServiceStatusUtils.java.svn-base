package com.txy.mobliesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceStatusUtils {

	/**
	 * 
	 * 判断服务是否开启
	 * 
	 * @param context
	 *            上下文
	 * @param ServiceName
	 *            要判断的服务名
	 * @return
	 */
	public static boolean isServiceRunning(Context context, String serviceName) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> listRS = am.getRunningServices(100);
		for (RunningServiceInfo rsi : listRS) {
			String className = rsi.service.getClassName();
			if (className.equals(serviceName)) {
				return true;
			}
		}
		return false;
	}
}
