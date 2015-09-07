package com.txy.mobliesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class SetupWizard4 extends BaseSetupActivity {

	private CheckBox cb_protect;
	private TextView tv_protect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_security_setup4);

		sp = getSharedPreferences("config", MODE_PRIVATE);

		cb_protect = (CheckBox) findViewById(R.id.cb_protect);
		tv_protect = (TextView) findViewById(R.id.tv_protect);

		boolean status = sp.getBoolean("protect", false);
		cb_protect.setChecked(status);
		if(status){
			tv_protect.setText("已经开启保护模式");
		}else{
			tv_protect.setText("已经关闭保护模式");
		}

		cb_protect.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Editor editor = sp.edit();
				if (isChecked) {
					// 开启保护模式
					tv_protect.setText("已经开启保护模式");
					editor.putBoolean("protect", true);
				} else {
					// 未开启
					tv_protect.setText("已经关闭保护模式");
					editor.putBoolean("protect", false);
				}
				editor.commit();
			}
		});
	}

	private SharedPreferences sp;

	public void finish(View view) {
		// 跳回防盗页面
		Toast.makeText(SetupWizard4.this, "完成设置", 0).show();
		// sp = getSharedPreferences("config", MODE_PRIVATE);

		Editor editor = sp.edit();
		editor.putBoolean("isSetup", true);
		editor.commit();

		Intent intent = new Intent(SetupWizard4.this, SecurityActivity.class);
		startActivity(intent);
		finish();

		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);

	}

	public void previous(View view) {
		showPre();

	}

	public void showPre() {
		// 返回上一步设置向导
		Intent intent = new Intent(SetupWizard4.this, SetupWizard3.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}

	@Override
	public void showNext() {

	}
}
