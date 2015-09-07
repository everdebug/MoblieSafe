package com.txy.mobliesafe;

import com.txy.mobliesafe.ui.SettingItemView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class SetupWizard2 extends BaseSetupActivity {

	private SettingItemView siv_item_sim;
	private TelephonyManager tm;

	protected SharedPreferences sp;
	private CheckBox cb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_security_setup2);

		siv_item_sim = (SettingItemView) findViewById(R.id.siv_item_sim);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		// String serialNumber = tm.getSimSerialNumber();
		cb = siv_item_sim.getCheckBox();
		String sim = sp.getString("sim", null);
		if (sim == null) {
			cb.setChecked(false);
			siv_item_sim.setStatusText(siv_item_sim.getStatuOff());
		} else {
			cb.setChecked(true);
			siv_item_sim.setStatusText(siv_item_sim.getStatuOn());
		}

		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Editor editor = sp.edit();
				String serialNumber = tm.getSimSerialNumber();
				if (isChecked) {
					siv_item_sim.setStatusText(siv_item_sim.getStatuOn());
					editor.putString("sim", serialNumber);
				} else {
					siv_item_sim.setStatusText(siv_item_sim.getStatuOff());
					editor.putString("sim", null);
				}
				editor.commit();
			}
		});

	}

	public void next(View view) {
		showNext();
	}

	public void showNext() {
		if (cb.isChecked()) {
			// 下一步设置向导
			Intent intent = new Intent(SetupWizard2.this, SetupWizard3.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
		} else {
			Toast.makeText(SetupWizard2.this, "请绑定SIM卡！", 0).show();
		}

	}

	public void previous(View view) {
		showPre();

	}

	public void showPre() {
		// 返回上一步设置向导
		Intent intent = new Intent(SetupWizard2.this, SetupWizard1.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
}
