package com.txy.mobliesafe.service;

import com.txy.mobliesafe.R;
import com.txy.mobliesafe.db.dao.NumberSelectUtils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

public class AddressService extends Service {

	private static final String TAG = "AddressService";
	private TelephonyManager tm;
	private WindowManager wm;
	private WindowManager.LayoutParams params = new WindowManager.LayoutParams();// 窗体布局配置
	private MyListener listener;

	private OutCallReceiver receiver;// 服务内置广播接收者

	private SharedPreferences sp;

	private View toastView;

	private long[] mHits = new long[2];// 数组长度决定点击多少下出发事件

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		MyListener listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);// 自定义加入广播接收者

		wm = (WindowManager) getSystemService(WINDOW_SERVICE);

		sp = getSharedPreferences("config", MODE_PRIVATE);
	}

	class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent arg1) {
			String number = getResultData();
			String addr = NumberSelectUtils.getAddr(number);
			// Toast.makeText(context.getApplicationContext(), addr,
			// Toast.LENGTH_SHORT).show();
			myToast(addr);// 自定义Toast
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 销毁防止内存溢出
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;

		unregisterReceiver(receiver);
		receiver = null;

		toastView = null;
	}

	/**
	 * 自定义一个Toast
	 * 
	 * @param addr
	 */
	public void myToast(String addr) {
		int[] bgitems = { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		// 自定义吐司
		toastView = View.inflate(getApplicationContext(),
				R.layout.toast_call_addr, null);
		// 点击监听器
		toastView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 将一个数组左移一定次数，每次移动先将点击事件时间记录到最后，然后左移，并计算第一个和最后一个的时间差，如果时间差小于一定时间及触发事件响应
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					// 双击居中
					params.x = wm.getDefaultDisplay().getWidth() / 2
							- toastView.getWidth() / 2;
					params.y = wm.getDefaultDisplay().getHeight() / 2
							- toastView.getHeight() / 2;
					wm.updateViewLayout(toastView, params);
					Editor editor = sp.edit();
					editor.putInt("lastx", params.x);
					editor.putInt("lasty", params.y);
					editor.commit();
				}

				Log.i(TAG, "被点击。。");
			}
		});

		TextView tv_Toast = (TextView) toastView.findViewById(R.id.tv_addr);
		int i = sp.getInt("background", 0);
		toastView.setBackgroundResource(bgitems[i]);
		// 添加触摸事件监听器
		MyTouchListener myTouchListener = new MyTouchListener();
		toastView.setOnTouchListener(myTouchListener);

		tv_Toast.setTextColor(Color.BLACK);
		tv_Toast.setText(addr);
		// Toast布局设置
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.gravity = Gravity.TOP + Gravity.LEFT;// 与窗体对齐
		params.x = sp.getInt("lastX", 100);
		params.y = sp.getInt("lastY", 100);
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;// 要删除一个FLAG_NOT_TOUCHABLE的标识，使toast可以进行点击
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;// TYPE_TOAST--toast类型不可以移动，自定义成TYPE_PRIORITY_PHONE需要权限SYSTEM_ALERT_WINDOW
		wm.addView(toastView, params);

	}

	/**
	 * 三种触摸手势，摁下-移动-松开，进行窗体位置监听，移动时，保存最后移动位置，重新定位并且更新窗体位置。
	 * 
	 * @author Tian
	 * 
	 */
	public class MyTouchListener implements View.OnTouchListener {

		int posX;
		int posY;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Log.i(TAG, "手指摁下");
				posX = (int) event.getRawX();
				posY = (int) event.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:
				Log.i(TAG, "手指移动");
				int newX = (int) event.getRawX();
				int newY = (int) event.getRawY();
				int dX = newX - posX;// 每次移动的偏移量
				int dY = newY - posY;
				params.x += dX;
				params.y += dY;
				// 边界问题
				if (params.x < 0) {
					params.x = 0;
				}
				if (params.y < 0) {
					params.y = 0;
				}
				if (params.x > (wm.getDefaultDisplay().getWidth() - toastView
						.getWidth())) {
					params.x = (wm.getDefaultDisplay().getWidth() - toastView
							.getWidth());
				}
				if (params.y > (wm.getDefaultDisplay().getHeight() - toastView
						.getHeight())) {
					params.y = (wm.getDefaultDisplay().getHeight() - toastView
							.getHeight());
				}
				wm.updateViewLayout(toastView, params);
				// 保存
				posX = (int) event.getRawX();
				posY = (int) event.getRawY();
				Log.i(TAG, posX + "--" + posY);
				break;
			case MotionEvent.ACTION_UP:
				Log.i(TAG, "手指放开");
				Editor edit = sp.edit();
				edit.putInt("lastX", params.x);
				edit.putInt("lastY", params.y);
				edit.commit();
				break;

			default:
				break;
			}
			return false;
		}
	}

	public class MyListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				String addr = NumberSelectUtils.getAddr(incomingNumber);
				if (toastView != null) {
					myToast(addr);
				}
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				if (toastView != null) {
					wm.removeView(toastView);
				}
				break;
			default:
				break;
			}
		}
	}

}
