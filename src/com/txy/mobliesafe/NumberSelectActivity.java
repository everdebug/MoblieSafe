package com.txy.mobliesafe;

import com.txy.mobliesafe.db.dao.NumberSelectUtils;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumberSelectActivity extends Activity {
	private EditText et_number;
	private TextView tv_result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_select);
		et_number = (EditText) findViewById(R.id.et_number);
		tv_result = (TextView) findViewById(R.id.tv_result);
		et_number.addTextChangedListener(new TextWatcher() {

			// 文本改变时使用
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() >= 3) {
					String addr = NumberSelectUtils.getAddr(s.toString());
					tv_result.setText(addr);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void selectPlace(View view) {
		String number = et_number.getText().toString().trim();
		if (!TextUtils.isEmpty(number)) {
			// 查询
			String addr = NumberSelectUtils.getAddr(number);
			tv_result.setText(addr);
		} else {
			Toast.makeText(NumberSelectActivity.this, "请输入号码", 0).show();
			return;
		}
	}
}
