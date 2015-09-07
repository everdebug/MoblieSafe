package com.txy.mobliesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.HttpHandler;

import org.json.JSONException;
import org.json.JSONObject;

import com.txy.mobliesafe.utils.StreamTools;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private TextView tv_splash_version;

	private final static String TAG = "MainActivity";

	// 处理不同的信息参数
	protected static final int SHOW_UPDATE_DIALOG = 0;
	protected static final int ENTER_HOME = 1;
	protected static final int URL_ERROR = 2;
	protected static final int NETWORK_ERROR = 3;
	protected static final int JSON_ERROR = 4;

	// 服务器解析出来的更新信息
	private String version;
	private String description;
	private String apkurl;// 新apk下载地址

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("V" + getVersionInfo());
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 拷贝数据库
		CopyDB("address.db");
		CopyDB("antivirus.db");

		if (sp.getBoolean("update", true)) {
			// 检查版本信息
			checkVersion();
		} else {
			// 延时进入主界面
			enterHome();
		}

	}

	/**
	 * 加载数据库
	 * 
	 * @param path
	 */
	private void CopyDB(String path) {
		try { // 创建文件
			File file = new File(getFilesDir(), path);
			if (file.exists() && file.length() > 0) {
				Log.i(TAG, "数据库已经加载");
			} else {
				// 拿到文件流
				AssetManager am = getAssets();
				InputStream is = am.open(path);// 打开assets资源文件夹
				FileOutputStream fos = new FileOutputStream(file);
				// 模板代码
				int len = 0;
				byte[] buffer = new byte[1024];
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();
				Log.i(TAG, "DB加载完成！");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_UPDATE_DIALOG:
				Log.i(TAG, "显示更新界面");
				showUpdateDialog();
				break;
			case ENTER_HOME:
				Log.i(TAG, "进入主界面");
				enterHome();
				break;
			case URL_ERROR:
				Log.i(TAG, "url错误");
				enterHome();
				break;
			case NETWORK_ERROR:
				Log.i(TAG, "网络连接超时");
				Toast.makeText(MainActivity.this, "网络连接超时", Toast.LENGTH_SHORT)
						.show();
				enterHome();
				break;
			case JSON_ERROR:
				Log.i(TAG, "获取数据错误");
				enterHome();
				break;

			}
		}

	};

	/**
	 * 显示更新版本信息对话框
	 */
	private void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		builder.setTitle("更新");
		// builder.setCancelable(false);//焦点只在对话框，点其他地方和取消没有响应，强制升级才可以用

		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// 返回主界面
				enterHome();
				dialog.dismiss();
			}
		});
		builder.setMessage("检测到新版本V" + version + "\r\n"
				+ "-----------------\r\n" + description
				+ "\r\n----------------\r\n" + "是否更新？");
		builder.setPositiveButton("是", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG, "进行apk下载");
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					dialog.dismiss();
					showDownloadDialog();
				}

			}

		});
		builder.setNegativeButton("否", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG, "不进行更新，进入主界面");
				dialog.dismiss();
				enterHome();
			}
		});
		builder.show();

	}

	/**
	 * 打开下载对话框
	 */
	private void showDownloadDialog() {
		final ProgressDialog m_pDialog = new ProgressDialog(MainActivity.this);
		// 设置进度条风格，风格为长形
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		m_pDialog.setTitle("正在下载。。。");
		m_pDialog.setProgress(100);
		// 设置ProgressDialog 的进度条是否不明确
		m_pDialog.setIndeterminate(false);
		m_pDialog.show();

		FinalHttp fh = new FinalHttp();
		HttpHandler httpHandler = fh.download(apkurl, Environment
				.getExternalStorageDirectory().getAbsolutePath()
				+ "/MoblieSate1_1_1.apk", new AjaxCallBack<File>() {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				t.printStackTrace();
				m_pDialog.dismiss();
				Toast.makeText(MainActivity.this, "下载失败" + strMsg,
						Toast.LENGTH_SHORT).show();
				super.onFailure(t, errorNo, strMsg);
			}

			@Override
			public void onLoading(long count, long current) {
				super.onLoading(count, current);
				int progress = (int) (current * 100 / count);
				m_pDialog.setProgress(progress);

			}

			@Override
			public void onSuccess(File t) {
				super.onSuccess(t);
				m_pDialog.dismiss();
				Toast.makeText(MainActivity.this, "下载成功", Toast.LENGTH_SHORT)
						.show();
				installAPK(t);
			}

		});
	}

	/**
	 * 安装apk
	 */
	private void installAPK(File t) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setDataAndType(Uri.fromFile(t),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	/**
	 * 进入主界面
	 */
	private void enterHome() {
		Intent intent = new Intent(MainActivity.this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 检查版本信息
	 */
	private void checkVersion() {
		Log.i(TAG, "访问网络中。。。");
		// 访问网络在子线程中进行
		new Thread() {
			@Override
			public void run() {
				long startTime = java.lang.System.currentTimeMillis();
				Message msg = Message.obtain();
				// url经常调换，抽取成String
				try {
					// 网络连接
					URL url = new URL(getString(R.string.serverurl));
					HttpURLConnection con = (HttpURLConnection) url
							.openConnection();
					con.setRequestMethod("GET");
					con.setConnectTimeout(3000);
					int code = con.getResponseCode();
					Log.i(TAG, code + "");
					if (code == 200) {
						InputStream is = con.getInputStream();
						String stream = StreamTools.readFromStream(is);
						Log.i(TAG, "成功" + stream);

						// json简单解析
						JSONObject object = new JSONObject(stream);
						version = (String) object.get("version");
						description = (String) object.get("description");
						apkurl = (String) object.get("apkurl");

						if (!version.equals(getVersionInfo())) {
							// 弹出更新对话框
							msg.what = SHOW_UPDATE_DIALOG;
						} else {
							// 进入主页面
							msg.what = ENTER_HOME;
						}
					} else {
						Toast.makeText(MainActivity.this, "连接失败！",
								Toast.LENGTH_SHORT).show();
					}
				} catch (MalformedURLException e) {
					msg.what = URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					msg.what = NETWORK_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					msg.what = JSON_ERROR;
					e.printStackTrace();
				} finally {
					// 控制进入主界面的时间
					long endTime = java.lang.System.currentTimeMillis();
					long costTime = endTime - startTime;
					if (costTime < 1000) {
						try {
							Thread.sleep(1000 - costTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					handler.sendMessage(msg);
				}
			}
		}.start();
	}

	/**
	 * 得到应用程序的版本信息
	 */
	private String getVersionInfo() {
		// apk包的管理者
		PackageManager pm = getPackageManager();
		try {
			// 得到功能文件清单info？
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return "未找到包";
		}
	}

}
