package com.txy.mobliesafe;

import java.util.List;

import com.txy.mobliesafe.db.dao.BlackNumberDao;
import com.txy.mobliesafe.db.domain.BlackNumberInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ComunicationActivity extends Activity {

	protected static final String TAG = "ComunicationActivity";
	private ListView lv_blacklist;
	private List<BlackNumberInfo> infos;
	private MyAdpter adapter;
	private BlackNumberDao dao;
	private LinearLayout ll1;
	private int offset = 0;
	private int limit = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comunication);
		ll1 = (LinearLayout) findViewById(R.id.ll_show);
		lv_blacklist = (ListView) findViewById(R.id.lv_blacklist);
		dao = new BlackNumberDao(this);
		loading();
		lv_blacklist.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:// 空闲状态
					int last = view.getLastVisiblePosition();
					if (last == (infos.size() - 1)) {
						offset = offset + limit;
						loading();
					}
					//开线程？加载图片，标志位置true
					//使用smartImageView来加载网络图片/普通图片，自定义一个实现方法加入一个标志位  滑动时为false，空闲则为true
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 触摸

					break;
				case OnScrollListener.SCROLL_STATE_FLING:// 滑行

					//不加载图片
					
					break;

				default:
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				//开始和结束设置成oncreate的变量
				//记录item的开始:firstVisibleItem，和结束：firstVisibleItem + visibleItemCount;
				//如果大于totalItemCount，则结束设置为totalItemCount-1
			}
		});

	}

	public void loading() {
		ll1.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				final List<BlackNumberInfo> temp = dao.findPart(offset, limit);
				if (infos == null) {
					infos = temp;
				} else {
					infos.addAll(temp);
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if(temp.size()==0){
							ll1.setVisibility(View.INVISIBLE);
							Toast.makeText(ComunicationActivity.this, "已经没有数据可加载", Toast.LENGTH_SHORT).show();
							return;
						}
						ll1.setVisibility(View.INVISIBLE);
						if (adapter == null) {
							adapter = new MyAdpter();
							lv_blacklist.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}

					}
				});

			}
		}).start();
	}

	public class MyAdpter extends BaseAdapter {

		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			View view;
			if (convertView == null) {
				view = View.inflate(ComunicationActivity.this,
						R.layout.item_communicate, null);
				// 保存到记事本本中
				holder = new ViewHolder();
				holder.tv = (TextView) view.findViewById(R.id.textView1);
				holder.tv2 = (TextView) view.findViewById(R.id.textView2);
				holder.iv_delete = (ImageView) view
						.findViewById(R.id.iv_delete);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			//有图片时显示默认图片
			//holder.tv.setTag(position);并且给图片设置TAg
			BlackNumberInfo info = infos.get(position);
			holder.tv.setText(info.getNumber());
			String mode = info.getMode();
			if (mode.equals("1")) {
				holder.tv2.setText("短信拦截");
			}
			if (mode.equals("2")) {
				holder.tv2.setText("电话拦截");
			}
			if (mode.equals("3")) {
				holder.tv2.setText("全部拦截");
			}
			holder.iv_delete.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new Builder(
							ComunicationActivity.this);
					builder.setTitle("警告");
					builder.setMessage("确定要删除此条信息？");
					builder.setNegativeButton("取消", null);
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dao.delete(infos.get(position).getNumber());
									infos.remove(position);
									adapter.notifyDataSetChanged();
								}
							});
					AlertDialog dialog = builder.create();
					dialog.show();
				}
			});
			return view;
		}
	}

	/**
	 * 
	 * 用来保存View内存地址
	 * 
	 * @author lenovo
	 * 
	 */
	class ViewHolder {
		TextView tv;
		TextView tv2;
		ImageView iv_delete;
	}

	private EditText et_number;
	private Button bt_ok, bt_cancel;
	private CheckBox cb_sms, cb_phone;

	public void addBlackNumber(View view) {
		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog dialog = builder.create();
		View contentView = View.inflate(getApplicationContext(),
				R.layout.activity_add_black_number_dialog, null);
		et_number = (EditText) contentView.findViewById(R.id.et_number);
		bt_ok = (Button) contentView.findViewById(R.id.bt_ok);
		bt_cancel = (Button) contentView.findViewById(R.id.bt_cancel);
		cb_sms = (CheckBox) contentView.findViewById(R.id.cb_sms);
		cb_phone = (CheckBox) contentView.findViewById(R.id.cb_phone);
		bt_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String number = et_number.getText().toString().trim();
				String mode;
				if (number.equals("")) {
					Toast.makeText(ComunicationActivity.this, "请输入要拦截的电话号码", Toast.LENGTH_SHORT)
							.show();
					return;
				} else if (cb_sms.isChecked() && cb_phone.isChecked()) {
					// 全部3
					mode = "3";
				} else if (cb_sms.isChecked() && !cb_phone.isChecked()) {
					// duanxin1
					mode = "1";
				} else if (!cb_sms.isChecked() && cb_phone.isChecked()) {
					// phone2
					mode = "2";
				} else {
					Toast.makeText(ComunicationActivity.this, "请选择拦截号码的模式",Toast.LENGTH_SHORT)
							.show();
					return;
				}
				Log.i(TAG, mode);

				// 添加到DB
				dao.create(number, mode);
				// 添加到list
				BlackNumberInfo info = new BlackNumberInfo();
				info.setNumber(number);
				info.setMode(mode);
				infos.add(0, info);
				// 适配器更新数据
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});

		bt_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setView(contentView, 0, 0, 0, 0);
		dialog.show();
	}

}
