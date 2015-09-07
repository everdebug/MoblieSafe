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
	private WindowManager.LayoutParams params = new WindowManager.LayoutParams();// ���岼������
	private MyListener listener;

	private OutCallReceiver receiver;// �������ù㲥������

	private SharedPreferences sp;

	private View toastView;

	private long[] mHits = new long[2];// ���鳤�Ⱦ�����������³����¼�

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
		registerReceiver(receiver, filter);// �Զ������㲥������

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
			myToast(addr);// �Զ���Toast
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// ���ٷ�ֹ�ڴ����
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;

		unregisterReceiver(receiver);
		receiver = null;

		toastView = null;
	}

	/**
	 * �Զ���һ��Toast
	 * 
	 * @param addr
	 */
	public void myToast(String addr) {
		int[] bgitems = { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		// �Զ�����˾
		toastView = View.inflate(getApplicationContext(),
				R.layout.toast_call_addr, null);
		// ���������
		toastView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ��һ����������һ��������ÿ���ƶ��Ƚ�����¼�ʱ���¼�����Ȼ�����ƣ��������һ�������һ����ʱ�����ʱ���С��һ��ʱ�估�����¼���Ӧ
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					// ˫������
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

				Log.i(TAG, "���������");
			}
		});

		TextView tv_Toast = (TextView) toastView.findViewById(R.id.tv_addr);
		int i = sp.getInt("background", 0);
		toastView.setBackgroundResource(bgitems[i]);
		// ��Ӵ����¼�������
		MyTouchListener myTouchListener = new MyTouchListener();
		toastView.setOnTouchListener(myTouchListener);

		tv_Toast.setTextColor(Color.BLACK);
		tv_Toast.setText(addr);
		// Toast��������
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.gravity = Gravity.TOP + Gravity.LEFT;// �봰�����
		params.x = sp.getInt("lastX", 100);
		params.y = sp.getInt("lastY", 100);
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;// Ҫɾ��һ��FLAG_NOT_TOUCHABLE�ı�ʶ��ʹtoast���Խ��е��
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;// TYPE_TOAST--toast���Ͳ������ƶ����Զ����TYPE_PRIORITY_PHONE��ҪȨ��SYSTEM_ALERT_WINDOW
		wm.addView(toastView, params);

	}

	/**
	 * ���ִ������ƣ�����-�ƶ�-�ɿ������д���λ�ü������ƶ�ʱ����������ƶ�λ�ã����¶�λ���Ҹ��´���λ�á�
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
				Log.i(TAG, "��ָ����");
				posX = (int) event.getRawX();
				posY = (int) event.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:
				Log.i(TAG, "��ָ�ƶ�");
				int newX = (int) event.getRawX();
				int newY = (int) event.getRawY();
				int dX = newX - posX;// ÿ���ƶ���ƫ����
				int dY = newY - posY;
				params.x += dX;
				params.y += dY;
				// �߽�����
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
				// ����
				posX = (int) event.getRawX();
				posY = (int) event.getRawY();
				Log.i(TAG, posX + "--" + posY);
				break;
			case MotionEvent.ACTION_UP:
				Log.i(TAG, "��ָ�ſ�");
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
