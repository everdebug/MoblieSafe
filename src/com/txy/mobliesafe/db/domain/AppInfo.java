package com.txy.mobliesafe.db.domain;

import android.graphics.drawable.Drawable;

public class AppInfo {
	private Drawable icon;
	private String appName;
	private String appAddr;
	private boolean inRom;
	private boolean UserApp;

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppAddr() {
		return appAddr;
	}

	public void setAppAddr(String appAddr) {
		this.appAddr = appAddr;
	}

	public boolean isInRom() {
		return inRom;
	}

	public void setInRom(boolean inRom) {
		this.inRom = inRom;
	}

	public boolean isUserApp() {
		return UserApp;
	}

	public void setUserApp(boolean userApp) {
		UserApp = userApp;
	}

	@Override
	public String toString() {
		return "AppInfo [icon=" + icon + ", appName=" + appName + ", appAddr="
				+ appAddr + ", inRom=" + inRom + ", UserApp=" + UserApp + "]";
	}

}
