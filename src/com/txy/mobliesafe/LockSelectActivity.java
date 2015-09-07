package com.txy.mobliesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LockSelectActivity extends Activity {

	private EditText et_pwd;
	private String appName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_select);
		et_pwd = (EditText) findViewById(R.id.et_pwd);
		appName = getIntent().getStringExtra("appName");
	}

	public void unlock(View view) {
		String password = et_pwd.getText().toString();
		if (password.equals("")) {
			Toast.makeText(this, "���벻Ϊ�գ�", Toast.LENGTH_SHORT).show();
			return;
		}
		if (password.equals("1")) {
			// ���͹㲥
			Intent intent = new Intent();
			intent.setAction("com.txy.mobliesafe.tempunlock");
			intent.putExtra("appUnLockName", appName);
			sendBroadcast(intent);
			finish();
			// �ر��ٴ���֤
		} else {
			Toast.makeText(this, "����������������룡", Toast.LENGTH_SHORT).show();
			et_pwd.setText("");
		}
	}

	@Override
	protected void onStop() {
		finish();
		super.onStop();
	}
}
