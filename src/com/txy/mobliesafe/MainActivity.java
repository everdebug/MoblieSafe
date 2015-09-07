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

	// ����ͬ����Ϣ����
	protected static final int SHOW_UPDATE_DIALOG = 0;
	protected static final int ENTER_HOME = 1;
	protected static final int URL_ERROR = 2;
	protected static final int NETWORK_ERROR = 3;
	protected static final int JSON_ERROR = 4;

	// ���������������ĸ�����Ϣ
	private String version;
	private String description;
	private String apkurl;// ��apk���ص�ַ

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("V" + getVersionInfo());
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// �������ݿ�
		CopyDB("address.db");
		CopyDB("antivirus.db");

		if (sp.getBoolean("update", true)) {
			// ���汾��Ϣ
			checkVersion();
		} else {
			// ��ʱ����������
			enterHome();
		}

	}

	/**
	 * �������ݿ�
	 * 
	 * @param path
	 */
	private void CopyDB(String path) {
		try { // �����ļ�
			File file = new File(getFilesDir(), path);
			if (file.exists() && file.length() > 0) {
				Log.i(TAG, "���ݿ��Ѿ�����");
			} else {
				// �õ��ļ���
				AssetManager am = getAssets();
				InputStream is = am.open(path);// ��assets��Դ�ļ���
				FileOutputStream fos = new FileOutputStream(file);
				// ģ�����
				int len = 0;
				byte[] buffer = new byte[1024];
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();
				Log.i(TAG, "DB������ɣ�");
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
				Log.i(TAG, "��ʾ���½���");
				showUpdateDialog();
				break;
			case ENTER_HOME:
				Log.i(TAG, "����������");
				enterHome();
				break;
			case URL_ERROR:
				Log.i(TAG, "url����");
				enterHome();
				break;
			case NETWORK_ERROR:
				Log.i(TAG, "�������ӳ�ʱ");
				Toast.makeText(MainActivity.this, "�������ӳ�ʱ", Toast.LENGTH_SHORT)
						.show();
				enterHome();
				break;
			case JSON_ERROR:
				Log.i(TAG, "��ȡ���ݴ���");
				enterHome();
				break;

			}
		}

	};

	/**
	 * ��ʾ���°汾��Ϣ�Ի���
	 */
	private void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		builder.setTitle("����");
		// builder.setCancelable(false);//����ֻ�ڶԻ��򣬵������ط���ȡ��û����Ӧ��ǿ�������ſ�����

		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// ����������
				enterHome();
				dialog.dismiss();
			}
		});
		builder.setMessage("��⵽�°汾V" + version + "\r\n"
				+ "-----------------\r\n" + description
				+ "\r\n----------------\r\n" + "�Ƿ���£�");
		builder.setPositiveButton("��", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG, "����apk����");
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					dialog.dismiss();
					showDownloadDialog();
				}

			}

		});
		builder.setNegativeButton("��", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG, "�����и��£�����������");
				dialog.dismiss();
				enterHome();
			}
		});
		builder.show();

	}

	/**
	 * �����ضԻ���
	 */
	private void showDownloadDialog() {
		final ProgressDialog m_pDialog = new ProgressDialog(MainActivity.this);
		// ���ý�������񣬷��Ϊ����
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		m_pDialog.setTitle("�������ء�����");
		m_pDialog.setProgress(100);
		// ����ProgressDialog �Ľ������Ƿ���ȷ
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
				Toast.makeText(MainActivity.this, "����ʧ��" + strMsg,
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
				Toast.makeText(MainActivity.this, "���سɹ�", Toast.LENGTH_SHORT)
						.show();
				installAPK(t);
			}

		});
	}

	/**
	 * ��װapk
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
	 * ����������
	 */
	private void enterHome() {
		Intent intent = new Intent(MainActivity.this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * ���汾��Ϣ
	 */
	private void checkVersion() {
		Log.i(TAG, "���������С�����");
		// �������������߳��н���
		new Thread() {
			@Override
			public void run() {
				long startTime = java.lang.System.currentTimeMillis();
				Message msg = Message.obtain();
				// url������������ȡ��String
				try {
					// ��������
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
						Log.i(TAG, "�ɹ�" + stream);

						// json�򵥽���
						JSONObject object = new JSONObject(stream);
						version = (String) object.get("version");
						description = (String) object.get("description");
						apkurl = (String) object.get("apkurl");

						if (!version.equals(getVersionInfo())) {
							// �������¶Ի���
							msg.what = SHOW_UPDATE_DIALOG;
						} else {
							// ������ҳ��
							msg.what = ENTER_HOME;
						}
					} else {
						Toast.makeText(MainActivity.this, "����ʧ�ܣ�",
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
					// ���ƽ����������ʱ��
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
	 * �õ�Ӧ�ó���İ汾��Ϣ
	 */
	private String getVersionInfo() {
		// apk���Ĺ�����
		PackageManager pm = getPackageManager();
		try {
			// �õ������ļ��嵥info��
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return "δ�ҵ���";
		}
	}

}
