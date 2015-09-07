package com.txy.mobliesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class SimNumberReceiver extends BroadcastReceiver {

	private SharedPreferences sp;
	private TelephonyManager tm;

	@Override
	public void onReceive(Context context, Intent arg1) {
		sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
		if (sp.getBoolean("protect", false)) {

			tm = (TelephonyManager) context
					.getSystemService(context.TELEPHONY_SERVICE);
			// 读取原SIM卡信息
			String saveSIM = sp.getString("sim", "") + "a";
			// 得到新的SIM卡信息
			String newSIM = tm.getSimSerialNumber();
			// 是否相同
			if (newSIM.equals(saveSIM)) {
				// 相同无
				System.out.println("SIM未变更");
				Log.i("----", "SIM未变更");
				Toast.makeText(context, "SIM未变更", 0).show();
			} else {
				// 不同报警
				System.out.println("SIM变更");
				Log.i("----", "SIM变更");
				Toast.makeText(context, "SIM变更报警", 0).show();
			}

		}
	}
}
