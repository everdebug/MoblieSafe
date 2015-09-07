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
			// ��ȡԭSIM����Ϣ
			String saveSIM = sp.getString("sim", "") + "a";
			// �õ��µ�SIM����Ϣ
			String newSIM = tm.getSimSerialNumber();
			// �Ƿ���ͬ
			if (newSIM.equals(saveSIM)) {
				// ��ͬ��
				System.out.println("SIMδ���");
				Log.i("----", "SIMδ���");
				Toast.makeText(context, "SIMδ���", 0).show();
			} else {
				// ��ͬ����
				System.out.println("SIM���");
				Log.i("----", "SIM���");
				Toast.makeText(context, "SIM�������", 0).show();
			}

		}
	}
}
