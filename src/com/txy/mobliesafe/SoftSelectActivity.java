package com.txy.mobliesafe;

import java.util.ArrayList;
import java.util.List;

import com.txy.mobliesafe.db.dao.AppLockDao;
import com.txy.mobliesafe.db.domain.AppInfo;
import com.txy.mobliesafe.engire.AppInfoProvider;
import com.txy.mobliesafe.utils.DensityUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SoftSelectActivity extends Activity implements OnClickListener {

	protected static final String TAG = "SoftSelectActivity";
	private TextView tv_sdrom, tv_phonerom, tv_shownumber;
	private ListView lv_showsoft;
	private List<AppInfo> infos;
	private List<AppInfo> userInfos;
	private List<AppInfo> sysInfos;
	private MyAdapter adapter;
	private LinearLayout ll_soft_show, ll_pop_uninstall, ll_pop_openapp,
			ll_pop_share;
	// private final static String TAG = "SoftSelectActivity";
	private PopupWindow pw;
	private AppInfo info;
	private AppLockDao db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_softselect);
		db = new AppLockDao(this);
		tv_sdrom = (TextView) findViewById(R.id.tv_sdrom);
		tv_phonerom = (TextView) findViewById(R.id.tv_phonerom);
		Long sdSize = getSize(this, Environment.getExternalStorageDirectory()
				.getAbsolutePath());
		Long phoneSize = getSize(this, Environment.getDataDirectory()
				.getAbsolutePath());
		tv_sdrom.setText("SD卡剩余空间： " + Formatter.formatFileSize(this, sdSize));
		tv_phonerom.setText("内存剩余空间：  "
				+ Formatter.formatFileSize(this, phoneSize));
		ll_soft_show = (LinearLayout) findViewById(R.id.ll_soft_show);

		lv_showsoft = (ListView) findViewById(R.id.lv_showsoft);

		loadingDate();

		tv_shownumber = (TextView) findViewById(R.id.tv_shownumber);

		lv_showsoft.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					info = null;
					return true;
				} else if (position == userInfos.size() + 1) {
					info = null;
					return true;
				} else if (position < userInfos.size() + 1) {
					info = userInfos.get(position - 1);
				} else {
					info = sysInfos.get(position - userInfos.size() - 2);
				}

				String appName = info.getAppName();
				ViewHolder holder = (ViewHolder) view.getTag();
				Log.i(TAG, "被长摁" + appName);
				if (!db.find(appName)) {
					db.add(appName);
					holder.iv_lock_tag.setImageDrawable(getResources()
							.getDrawable(R.drawable.lock_off));
				} else {
					db.delete(appName);
					holder.iv_lock_tag.setImageDrawable(getResources()
							.getDrawable(R.drawable.lock_on));
				}
				return true;
			}
		});

		lv_showsoft.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (position == 0) {
					info = null;
					return;
				} else if (position == userInfos.size() + 1) {
					info = null;
					return;
				} else if (position < userInfos.size() + 1) {
					info = userInfos.get(position - 1);
				} else {
					info = sysInfos.get(position - userInfos.size() - 2);
				}
				dismissPop();

				if (info != null) {

					View contentView = View.inflate(getApplicationContext(),
							R.layout.popwindows_soft_option, null);
					ll_pop_uninstall = (LinearLayout) contentView
							.findViewById(R.id.ll_pop_uninstall);
					ll_pop_openapp = (LinearLayout) contentView
							.findViewById(R.id.ll_pop_openapp);
					ll_pop_share = (LinearLayout) contentView
							.findViewById(R.id.ll_pop_share);
					ll_pop_uninstall
							.setOnClickListener(SoftSelectActivity.this);
					ll_pop_openapp.setOnClickListener(SoftSelectActivity.this);
					ll_pop_share.setOnClickListener(SoftSelectActivity.this);
					pw = new PopupWindow(contentView, -2, -2);
					int[] location = new int[2];
					view.getLocationInWindow(location);
					pw.showAtLocation(parent, Gravity.LEFT | Gravity.TOP,
							DensityUtil.px2dip(getApplicationContext(), 60),
							location[1]);
				}

			}

		});

		lv_showsoft.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int firstView = view.getFirstVisiblePosition();
				dismissPop();
				if (userInfos != null && sysInfos != null) {
					if (firstView > userInfos.size()) {
						tv_shownumber.setText("系统应用: " + sysInfos.size() + "个");
					} else {
						tv_shownumber.setText("用户应用: " + userInfos.size() + "个");
					}
				}
			}
		});

	}

	public void loadingDate() {
		ll_soft_show.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				infos = AppInfoProvider.getInfos(SoftSelectActivity.this);
				userInfos = new ArrayList<AppInfo>();
				sysInfos = new ArrayList<AppInfo>();
				for (AppInfo info : infos) {
					if (info.isUserApp()) {
						userInfos.add(info);
					} else {
						sysInfos.add(info);
					}
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (adapter == null) {
							adapter = new MyAdapter();
							lv_showsoft.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
						ll_soft_show.setVisibility(View.INVISIBLE);
						tv_shownumber.setVisibility(View.VISIBLE);
					}
				});
			}
		}).start();
	}

	private void dismissPop() {
		if (pw != null && pw.isShowing()) {
			pw.dismiss();
			pw = null;
		}
	}

	private Long getSize(Context context, String absolutePath) {
		StatFs stat = new StatFs(absolutePath);
		// long count = stat.getBlockCount();
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		long size = blockSize * availableBlocks;
		return size;
	}

	static class ViewHolder {
		TextView item_tv_name, item_tv_addr;
		ImageView item_iv_icon, iv_lock_tag;
	}

	@Override
	protected void onDestroy() {
		dismissPop();
		super.onDestroy();
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return userInfos.size() + 1 + sysInfos.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position == 0) {
				TextView tv = new TextView(getApplicationContext());
				tv.setText("用户程序: " + userInfos.size() + "个");
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.BLACK);
				return tv;
			}
			if (position == (userInfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setText("系统程序: " + sysInfos.size() + "个");
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.BLACK);
				return tv;
			}
			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.item_appinfo, null);
				holder = new ViewHolder();
				holder.item_tv_name = (TextView) view
						.findViewById(R.id.tv_item_name);
				holder.item_tv_addr = (TextView) view
						.findViewById(R.id.tv_item_addr);
				holder.item_iv_icon = (ImageView) view
						.findViewById(R.id.iv_item_icon);
				holder.iv_lock_tag = (ImageView) view
						.findViewById(R.id.iv_lock_tag);
				view.setTag(holder);
			}

			AppInfo info;
			if (position <= userInfos.size()) {
				info = userInfos.get(position - 1);
			} else {
				info = sysInfos.get(position - userInfos.size() - 2);
			}
			holder.item_tv_name.setText(info.getAppName());
			String AppLocal = "";
			if (info.isInRom()) {
				AppLocal = "手机存储";
			} else {
				AppLocal = "外部存储";
			}
			holder.item_tv_addr.setText(AppLocal);
			holder.item_iv_icon.setImageDrawable(info.getIcon());
			if (db.find(info.getAppName())) {
				holder.iv_lock_tag.setImageDrawable(getResources().getDrawable(
						R.drawable.lock_off));
			} else {
				holder.iv_lock_tag.setImageDrawable(getResources().getDrawable(
						R.drawable.lock_on));
			}

			return view;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_pop_uninstall:
			if (info != null && info.isUserApp()) {
				uninstall();
			} else {
				Toast.makeText(SoftSelectActivity.this, "系统应用不可卸载",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.ll_pop_openapp:
			openApp();
			break;
		case R.id.ll_pop_share:
			share();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			// 刷新数据
			loadingDate();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void share() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "share: " + info.getAppName());
		startActivity(intent);
	}

	private void uninstall() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_DELETE);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setData(Uri.parse("package:" + info.getAppAddr()));
		startActivityForResult(intent, 0);
	}

	private void openApp() {
		Intent intent;
		PackageManager pm = getPackageManager();
		intent = pm.getLaunchIntentForPackage(info.getAppAddr());
		if (intent != null) {
			startActivity(intent);
		} else {
			Toast.makeText(SoftSelectActivity.this, "应用无法打开",
					Toast.LENGTH_SHORT).show();
		}
	}
}
