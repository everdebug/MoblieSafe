package com.txy.mobliesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SetupWizard3 extends BaseSetupActivity {

	private EditText et_number;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_security_setup3);
		et_number = (EditText) findViewById(R.id.et_number);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		et_number.setText(sp.getString("safephone", "110"));
	}

	public void selectcontact(View view) {
		Intent intent = new Intent(SetupWizard3.this, ContactsActivity.class);
		startActivityForResult(intent, 0);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			if (data != null) {
				et_number
						.setText(data.getStringExtra("phone").replace("-", ""));
			}
		}
	}

	public void next(View view) {
		showNext();

	}

	public void showNext() {
		String number = et_number.getText().toString().trim();
		if (!TextUtils.isEmpty(number)) {
			Editor editor = sp.edit();
			editor.putString("safephone", number);
			editor.commit();
		} else {
			Toast.makeText(SetupWizard3.this, "请选择安全号码！", 0).show();
			return;
		}

		// 下一步设置向导
		Intent intent = new Intent(SetupWizard3.this, SetupWizard4.class);
		startActivity(intent);
		finish();

		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	public void previous(View view) {
		showPre();

	}

	public void showPre() {
		// 返回上一步设置向导
		Intent intent = new Intent(SetupWizard3.this, SetupWizard2.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
}
