package com.txy.mobliesafe;

import java.util.ArrayList;
import java.util.List;

import com.txy.mobliesafe.db.domain.ProcessInfo;
import com.txy.mobliesafe.engire.ProcessInfoProvider;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProcessActivity extends Activity {

	protected static final String TAG = "ProcessActivity";
	private TextView tv_running_process, tv_avaliable, tv_shownumber;
	private ListView lv_showprocess;
	private ProcessAdapter adapter;
	private List<ProcessInfo> infos;
	private ProcessInfo info;
	private List<ProcessInfo> userInfos;
	private List<ProcessInfo> sysInfos;
	private LinearLayout ll_process_show;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_manage);
		tv_running_process = (TextView) findViewById(R.id.tv_running_process);
		tv_avaliable = (TextView) findViewById(R.id.tv_avaliable);
		sp = getSharedPreferences("config", MODE_PRIVATE);

		loadingNumInfo();

		ll_process_show = (LinearLayout) findViewById(R.id.ll_process_show);
		lv_showprocess = (ListView) findViewById(R.id.lv_showprocess);
		tv_shownumber = (TextView) findViewById(R.id.tv_shownumber);

		loadingProcessInfo();

		lv_showprocess.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int firstView = view.getFirstVisiblePosition();
				if (userInfos != null && sysInfos != null) {
					if (firstView > userInfos.size()) {
						tv_shownumber.setText("系统进程: " + sysInfos.size() + "个");
					} else {
						tv_shownumber.setText("用户进程: " + userInfos.size() + "个");
					}
				}

			}
		});

		lv_showprocess.setOnItemClickListener(new OnItemClickListener() {

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
				ViewHolder holder = (ViewHolder) view.getTag();
				if (info.isChecked()) {
					info.setChecked(false);
				} else {
					info.setChecked(true);
				}
				holder.cb_state.setChecked(info.isChecked());
			}
		});
	}

	public void loadingNumInfo() {
		int runningNum = ProcessInfoProvider.getRunningProcessCount(this);
		tv_running_process.setText("进程数：" + runningNum + "个");
		long avaliMem = ProcessInfoProvider.getAvaliMem(this);
		long totalMem = ProcessInfoProvider.getTotalMem(this);
		tv_avaliable.setText("剩余/总共空间："
				+ Formatter.formatFileSize(this, avaliMem) + "/"
				+ Formatter.formatFileSize(this, totalMem));
	}

	private void loadingProcessInfo() {
		ll_process_show.setVisibility(View.VISIBLE);
		tv_shownumber.setVisibility(View.INVISIBLE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				infos = ProcessInfoProvider.getAllInfo(ProcessActivity.this);
				userInfos = new ArrayList<ProcessInfo>();
				sysInfos = new ArrayList<ProcessInfo>();
				for (ProcessInfo pinfo : infos) {
					if (pinfo.isUserTask()) {
						userInfos.add(pinfo);
					} else {
						sysInfos.add(pinfo);
					}
					Log.i(TAG, pinfo.toString());
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (adapter == null) {
							adapter = new ProcessAdapter();
							lv_showprocess.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
						ll_process_show.setVisibility(View.INVISIBLE);
						tv_shownumber.setVisibility(View.VISIBLE);
					}
				});

			}
		}).start();

	}

	class ProcessAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (sp.getBoolean("showSys", false)) {
				return userInfos.size() + 1 + sysInfos.size() + 1;
			} else {
				return userInfos.size() + 1;
			}

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
			ProcessInfo pinfo;
			if (position == 0) {
				TextView tv = new TextView(getApplicationContext());
				tv.setText("用户进程: " + userInfos.size() + "个");
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.BLACK);
				return tv;
			} else if (position == (userInfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setText("系统进程: " + sysInfos.size() + "个");
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.BLACK);
				return tv;
			} else if (position <= userInfos.size()) {
				pinfo = userInfos.get(position - 1);
			} else {
				pinfo = sysInfos.get(position - userInfos.size() - 2);
			}

			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.item_processinfo, null);
				holder = new ViewHolder();
				holder.tv_process_name = (TextView) view
						.findViewById(R.id.tv_item_process_name);
				holder.tv_process_mem = (TextView) view
						.findViewById(R.id.tv_item_process_mem);
				holder.im_icon = (ImageView) view
						.findViewById(R.id.iv_item_process_icon);
				holder.cb_state = (CheckBox) view
						.findViewById(R.id.cb_process_state);
				view.setTag(holder);
			}
			if (holder == null) {
				Log.i(TAG, "holder为null");
				Log.i(TAG, pinfo.getName());
			} else {
				holder.tv_process_name.setText(pinfo.getName());
				holder.tv_process_mem.setText(Formatter.formatFileSize(
						ProcessActivity.this, pinfo.getMemsize()));

				holder.im_icon.setImageDrawable(pinfo.getIcon());
				holder.cb_state.setChecked(pinfo.isChecked());
			}
			return view;
		}

	}

	class ViewHolder {
		ImageView im_icon;
		TextView tv_process_name, tv_process_mem;
		CheckBox cb_state;
	}

	public void selectAll(View view) {
		for (ProcessInfo pinfo : infos) {
			pinfo.setChecked(true);
		}
		adapter.notifyDataSetChanged();
	}

	public void selectInvert(View view) {
		for (ProcessInfo pinfo : infos) {
			pinfo.setChecked(!pinfo.isChecked());
		}
		adapter.notifyDataSetChanged();
	}

	public void clear(View view) {
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		for (ProcessInfo pinfo : infos) {
			if (pinfo.isChecked()) {
				am.killBackgroundProcesses(pinfo.getPackname());
			}
		}
		loadingNumInfo();
		loadingProcessInfo();
	}

	public void setting(View view) {
		Intent intent = new Intent();
		intent.setClass(this, ProcessSetting.class);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			adapter.notifyDataSetChanged();
			loadingNumInfo();
		}
	}

}
