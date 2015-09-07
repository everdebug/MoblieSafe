package com.txy.mobliesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SecurityActivity extends Activity {
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean isSetup = sp.getBoolean("isSetup", false);
		if (isSetup) {
			// 加载防盗界面
			setContentView(R.layout.activity_security);
		} else {// 跳转到设置向导界面
			Intent intent = new Intent();
			intent.setClass(SecurityActivity.this, SetupWizard1.class);
			startActivity(intent);
			finish();
		}
	}
	
	public void restartSetup(View view) {
		Toast.makeText(SecurityActivity.this, "被点", 0).show();
		//重新跳转到设置向导
		Intent intent = new Intent();
		intent.setClass(SecurityActivity.this, SetupWizard1.class);
		startActivity(intent);
		finish();
	}
}
