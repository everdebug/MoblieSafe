package com.txy.mobliesafe;

import java.util.List;

import com.txy.mobliesafe.db.dao.VirusDao;
import com.txy.mobliesafe.utils.MD5Utils;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class KillVirusActivity extends Activity {
	private static final String TAG = "KillVirusActivity";
	protected static final int VIRUS = 0;
	protected static final int SAFETY = 1;
	protected static final int FINISH = 2;
	private ImageView iv_scan;
	private PackageManager pm;
	private ProgressBar pb;
	private int progress = 0;
	private TextView tv_show_scan;
	private LinearLayout ll_show_scan;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			TextView tempTv = new TextView(KillVirusActivity.this);
			switch (msg.what) {
			case VIRUS:
				tv_show_scan.setText("正在扫描： " + (String) msg.obj);
				tempTv.setText("发现病毒： " + (String) msg.obj);
				tempTv.setTextColor(Color.RED);
				ll_show_scan.addView(tempTv,0);
				break;
			case SAFETY:
				tv_show_scan.setText("正在扫描： " + (String) msg.obj);
				tempTv.setText("已经扫描： " + (String) msg.obj);
				tempTv.setTextColor(Color.BLUE);
				ll_show_scan.addView(tempTv,0);
				break;
			case FINISH:
				tv_show_scan.setText("扫描完成");
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kill_virus);

		// 旋转动画的实现
		iv_scan = (ImageView) findViewById(R.id.iv_scan);
		RotateAnimation ra = new RotateAnimation(0, 359,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ra.setDuration(1000);// 一次动画时间
		ra.setRepeatCount(Animation.INFINITE);// 重复
		iv_scan.startAnimation(ra);

		pb = (ProgressBar) findViewById(R.id.pb_progress);
		tv_show_scan = (TextView) findViewById(R.id.tv_show_scan);
		ll_show_scan = (LinearLayout) findViewById(R.id.ll_show_scan);
		scanVirus();
	}

	private void scanVirus() {
		pm = getPackageManager();
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<ApplicationInfo> applications = pm
						.getInstalledApplications(0);
				pb.setMax(applications.size());
				pb.setProgress(progress);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						tv_show_scan.setText("正在扫描初始化。。。");
					}
				});
				for (ApplicationInfo info : applications) {

					String sourceDir = info.sourceDir;
					String fileMd5 = MD5Utils.getFileMd5(sourceDir);
					boolean virus = VirusDao.isVirus(fileMd5);
					if (virus) {
						// 是病毒
						Message msg = Message.obtain();
						msg.what = VIRUS;
						msg.obj = info.loadLabel(pm);
						handler.sendMessage(msg);
					} else {
						// 安全
						Message msg = Message.obtain();
						msg.what = SAFETY;
						msg.obj = info.loadLabel(pm);
						handler.sendMessage(msg);
					}
					progress++;
					pb.setProgress(progress);

					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				iv_scan.clearAnimation();
				Message msg = Message.obtain();
				msg.what = FINISH;
				handler.sendMessage(msg);
			}
		}).start();

	}
}
