package com.txy.mobliesafe.engire;

import java.util.ArrayList;
import java.util.List;

import com.txy.mobliesafe.R;
import com.txy.mobliesafe.db.domain.ProcessInfo;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

public class ProcessInfoProvider {

	/**
	 * 得到正在运行应用的数量
	 * 
	 * @param context
	 * @return
	 */
	public static int getRunningProcessCount(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
		return list.size();
	}

	public static long getAvaliMem(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);

		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}

	@SuppressLint("NewApi")
	public static long getTotalMem(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);

		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.totalMem;
	}

	public static List<ProcessInfo> getAllInfo(Context context) {
		List<ProcessInfo> infos = new ArrayList<ProcessInfo>();
		ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();

		List<RunningAppProcessInfo> list = am.getRunningAppProcesses();

		for (RunningAppProcessInfo info : list) {
			ProcessInfo pinfo = new ProcessInfo();
			// 包名
			String packageName = info.processName;
			pinfo.setPackname(packageName);
			android.os.Debug.MemoryInfo[] memoryInfo = am
					.getProcessMemoryInfo(new int[] { info.pid });
			long totalPrivateDirty = memoryInfo[0].getTotalPrivateDirty() * 1024;
			pinfo.setMemsize(totalPrivateDirty);
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(
						packageName, 0);
				String processName = (String) applicationInfo.loadLabel(pm);
				pinfo.setName(processName);
				Drawable icon = applicationInfo.loadIcon(pm);
				pinfo.setIcon(icon);
				if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					// 用户
					pinfo.setUserTask(true);
				} else {
					// 系统
					pinfo.setUserTask(false);
				}
			} catch (NameNotFoundException e) {
				// 系统及应用里面有没有name和icon，要设置默认值
				pinfo.setName(packageName);
				pinfo.setIcon(context.getResources().getDrawable(
						R.drawable.ic_default));
				e.printStackTrace();
			}
			pinfo.setChecked(false);
			infos.add(pinfo);
		}

		return infos;
	}
}
