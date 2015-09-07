package com.txy.mobliesafe;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SetupWizard1 extends BaseSetupActivity {

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_security_setup1);
		
	}

	public void next(View view) {
		showNext();
	}

	public void showNext() {
		Intent intent = new Intent(SetupWizard1.this, SetupWizard2.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void showPre() {
		
	}

	

}
