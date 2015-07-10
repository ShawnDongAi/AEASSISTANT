package com.zzn.aeassistant.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.attendance.AttendanceRecordActivity;
import com.zzn.aeassistant.activity.project.ProjectManagerActivity;
import com.zzn.aeassistant.activity.setting.SettingActivity;
import com.zzn.aeassistant.activity.user.LoginActivity;
import com.zzn.aeassistant.activity.user.UserActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.app.PreConfig;
import com.zzn.aeassistant.constants.FileCostants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.database.UserDBHelper;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.AttchUtil;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.util.ToolsUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.view.CircleImageView;
import com.zzn.aeassistant.view.FastenGridView;
import com.zzn.aeassistant.view.swipemenu.SwipeMenu;
import com.zzn.aeassistant.view.swipemenu.SwipeMenuCreator;
import com.zzn.aeassistant.view.swipemenu.SwipeMenuItem;
import com.zzn.aeassistant.view.swipemenu.SwipeMenuListView;
import com.zzn.aeassistant.view.swipemenu.SwipeMenuListView.OnMenuItemClickListener;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.Module;
import com.zzn.aeassistant.vo.ProjectVO;
import com.zzn.aeassistant.vo.UserVO;

/**
 * 主Activity
 * 
 * @author Shawn
 */
public class MainActivity extends BaseActivity implements OnItemClickListener {
	private TextView mUserName, mCurrentProject;
	private CircleImageView mUserHead;
	private FastenGridView mGridView;
	private SwipeMenuListView mHistoryList;
	private ModuleAdapter adapter;
	private UserHistoryAdapter mUserAdapter;
	private Button mScanning;
	private ProjectVO project;

	// 打卡拍照的照片路径
	private String scanningPath = "";
	// 电话监听相关
	private TelephonyManager telephony;
	private MyPhoneStateListener myPhoneStateListener;
	// 接收到来电后的对话框
	private AlertDialog comingCallDialog;
	// 上一个来电的号码
	private String lastComingPhone = "";

	private InitProjectTask initProjectTask;
	private ScanningTask scanningTask;

	@Override
	protected int layoutResID() {
		return R.layout.activity_main;
	}

	@Override
	protected int titleStringID() {
		return R.string.app_name;
	}

	@Override
	protected void initView() {
		setSwipeBackEnable(false);
		if (AEApp.getCurrentUser() == null) {
			Intent intent = new Intent(this, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			finish();
		}
		telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		myPhoneStateListener = new MyPhoneStateListener();

		back.setVisibility(View.INVISIBLE);
		mCurrentProject = (TextView) findViewById(R.id.home_current_project);
		mHistoryList = (SwipeMenuListView) findViewById(R.id.home_user_history);

		View headerView = View.inflate(mContext, R.layout.home_header, null);
		mUserName = (TextView) headerView.findViewById(R.id.home_name);
		mUserHead = (CircleImageView) headerView.findViewById(R.id.home_head);
		mGridView = (FastenGridView) headerView
				.findViewById(R.id.home_module_gridview);
		mScanning = (Button) headerView.findViewById(R.id.home_scanning);
		mHistoryList.addHeaderView(headerView);

		mScanning.setOnClickListener(this);
		initUserView();
		initModuleView();
		initUserHistory();
	}

	private long lastClickTime = 0;
	private void initUserHistory() {
		mUserAdapter = new UserHistoryAdapter(mContext);
		mUserAdapter.setUsers(UserDBHelper.getUserHistory());
		mHistoryList.setAdapter(mUserAdapter);
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem item = new SwipeMenuItem(getApplicationContext());
				item.setBackground(R.drawable.swipe_menu_item1);
				item.setWidth(ToolsUtil.dip2px(mContext, 90));
				item.setTitle(R.string.delete);
				item.setTitleSize(18);
				item.setTitleColor(Color.WHITE);
				menu.addMenuItem(item);
			}
		};
		mHistoryList.setMenuCreator(creator);
		mHistoryList.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu,
					int index) {
				if (System.currentTimeMillis() - lastClickTime < 500) {
					return false;
				}
				lastClickTime = System.currentTimeMillis();
				UserVO user = mUserAdapter.getItem(position);
				UserDBHelper.delete(user.getPHONE());
				mUserAdapter.removeUser(position);
				mUserAdapter.notifyDataSetChanged();
				return false;
			}
		});
		mHistoryList.setOpenInterpolator(new DecelerateInterpolator(1.0f));
		mHistoryList.setCloseInterpolator(new DecelerateInterpolator(1.0f));
		mHistoryList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position < 1) {
					return;
				}
				if (System.currentTimeMillis() - lastClickTime < 500) {
					return;
				}
				lastClickTime = System.currentTimeMillis();
				scanningForOther(mUserAdapter.getItem(position - 1).getPHONE());
			}
		});
	}

	@Override
	protected void onPause() {
		if (telephony != null) {
			telephony.listen(myPhoneStateListener,
					PhoneStateListener.LISTEN_NONE);
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		telephony.listen(myPhoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);
	}

	private void initUserView() {
		if (AEApp.getCurrentUser() == null) {
			ToastUtil.show("用户丢失");
			return;
		}
		if (AEApp.getCurrentUser().getUSER_NAME() == null) {
			ToastUtil.show("用户昵称丢失");
			return;
		}
		mUserName.setText(AEApp.getCurrentUser().getUSER_NAME());
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.home_scanning:
			String scanningPhone = AEApp.getCurrentUser().getPHONE();
			if (!StringUtil.isEmpty(lastComingPhone)) {
				scanningPhone = lastComingPhone;
				lastComingPhone = "";
			}
			setImgPath(
					FileCostants.DIR_SCANNING + scanningPhone + "_"
							+ System.currentTimeMillis() + ".jpg", true);
			AttchUtil.capture(this, getImgPath());
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityReceiveLocation(BDLocation location) {
		super.onActivityReceiveLocation(location);
		AEApp.setCurrentLoc(location);
		if (location == null) {
			project = null;
			ToastUtil.show(R.string.location_failed);
			mCurrentProject.setText(R.string.out_of_project_location);
			return;
		}
		if (initProjectTask != null) {
			initProjectTask.cancel(true);
			initProjectTask = null;
		}
		initProjectTask = new InitProjectTask();
		initProjectTask.execute(new Double[] { location.getLatitude(),
				location.getLongitude() });
	}

	private class InitProjectTask extends AsyncTask<Double, Integer, ProjectVO> {

		@Override
		protected ProjectVO doInBackground(Double... params) {
			project = null;
			double currentLatitude = params[0];
			double currentLongitude = params[1];
			for (ProjectVO projectVO : AEApp.getCurrentUser().getPROJECTS()) {
				double proLatitude = Double
						.parseDouble(projectVO.getLATITUDE());
				double proLongitude = Double.parseDouble(projectVO
						.getLONGITUDE());
				if (ToolsUtil.getDistance(currentLongitude, currentLatitude,
						proLongitude, proLatitude) < 500) {
					project = projectVO;
					break;
				}
			}
			return project;
		}

		@Override
		protected void onPostExecute(ProjectVO result) {
			super.onPostExecute(result);
			if (result == null) {
				mCurrentProject.setText(R.string.out_of_project_location);
			} else {
				mCurrentProject.setText(getString(R.string.current_project,
						result.getPROJECT_NAME()));
			}
		}
	}

	private void initModuleView() {
		adapter = new ModuleAdapter(mContext);
		// 项目管理
		Intent projectIntent = new Intent(this, ProjectManagerActivity.class);
		adapter.addItem(new Module(R.drawable.ic_project_manager,
				R.string.title_project_manager, projectIntent));
		// 考勤记录
		Intent attendanceIntent = new Intent(this,
				AttendanceRecordActivity.class);
		adapter.addItem(new Module(R.drawable.ic_attendance_record,
				R.string.title_attendance_record, attendanceIntent));
		// 个人中心
		Intent userIntent = new Intent(this, UserActivity.class);
		adapter.addItem(new Module(R.drawable.ic_user_center,
				R.string.title_user_center, userIntent));
		// 设置
		Intent settingIntent = new Intent(this, SettingActivity.class);
		adapter.addItem(new Module(R.drawable.ic_setting,
				R.string.title_setting, settingIntent));
		mGridView.setAdapter(adapter);
		mGridView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (System.currentTimeMillis() - lastClickTime < 500) {
			return;
		}
		lastClickTime = System.currentTimeMillis();
		startActivity(adapter.getItem(position).getIntent());
	}

	/**
	 * 2秒内点击两次返回键退出
	 */
	private long lastPressTime = 0;

	@Override
	public void onBackPressed() {
		long time = System.currentTimeMillis();
		if (time - lastPressTime > 2000) {
			lastPressTime = time;
			ToastUtil.show(R.string.exit_toast);
		} else {
			super.onBackPressed();
			PreConfig.clearUserVO();
			AEApp.getInstance().exit();
		}
	}

	@Override
	protected boolean needLocation() {
		return true;
	}

	@Override
	protected void onDestroy() {
		if (scanningTask != null) {
			scanningTask.cancel(true);
			scanningTask = null;
		}
		if (initProjectTask != null) {
			initProjectTask.cancel(true);
			initProjectTask = null;
		}
		super.onDestroy();
	}

	@Override
	protected void getImg(String path) {
		super.getImg(path);
		if (StringUtil.isEmpty(path)) {
			ToastUtil.show(R.string.scanning_null_photo);
			return;
		}
		File file = new File(path);
		if (!file.exists()) {
			ToastUtil.show(R.string.scanning_null_photo);
			return;
		}
		if (AEApp.getCurrentLoc() == null) {
			return;
		}
		// 拍照后获取位置并打卡
		scanningPath = path;
		String forWho = "0";
		String scanningPhone = AEApp.getCurrentUser().getPHONE();
		if (!StringUtil.isEmpty(lastComingPhone)) {
			scanningPhone = lastComingPhone;
			forWho = "1";
		}
		scanningTask = new ScanningTask();
		scanningTask.execute(new String[] { scanningPhone, scanningPath,
				AEApp.getCurrentLoc().getLongitude() + "",
				AEApp.getCurrentLoc().getLatitude() + "", forWho,
				AEApp.getCurrentLoc().getAddrStr() });
	}

	/**
	 * 打卡异步任务
	 * 
	 * @author Shawn
	 *
	 */
	private class ScanningTask extends AsyncTask<String, Integer, HttpResult> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			AEProgressDialog.showLoadingDialog(mContext);
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			String phone = params[0];
			String imgPath = params[1];
			String longitude = params[2];
			String latitude = params[3];
			String forWho = params[4];
			String address = params[5];

			List<String> files = new ArrayList<String>();
			files.add(imgPath);
			Map<String, String> param = new HashMap<String, String>();
			param.put("phone", phone);
			String project_id = project != null ? project.getPROJECT_ID() : "";
			param.put("project_id", project_id);
			param.put("longitude", longitude);
			param.put("latitude", latitude);
			param.put("for_who", forWho);
			param.put("address", address);
			param.put("parent_user", AEApp.getCurrentUser().getUSER_ID());
			HttpResult result = AEHttpUtil.doPostWithFile(
					URLConstants.URL_SCANNING, files, param);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			ToastUtil.showLong(result.getRES_MESSAGE());
			if (result != null
					&& result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				if (result.getRES_OBJ() != null
						&& !StringUtil.isEmpty(result.getRES_OBJ().toString())) {
					JSONObject obj;
					try {
						obj = new JSONObject(result.getRES_OBJ().toString());
						if (obj.has("new_project")) {
							project = GsonUtil.getInstance().fromJson(
									obj.getString("new_project"),
									ProjectVO.class);
							AEApp.getCurrentUser().getPROJECTS().add(project);
							if (initProjectTask != null) {
								initProjectTask.cancel(true);
								initProjectTask = null;
							}
							initProjectTask = new InitProjectTask();
							if (AEApp.getCurrentLoc() == null) {
								initProjectTask
										.execute(new Double[] { 0.0, 0.0 });
							} else {
								initProjectTask.execute(new Double[] {
										AEApp.getCurrentLoc().getLatitude(),
										AEApp.getCurrentLoc().getLongitude() });
							}
						}
						String user_name = obj.getString("user_name");
						String user_phone = obj.getString("user_phone");
						UserDBHelper.insertUser(user_phone, user_name);
						mUserAdapter.setUsers(UserDBHelper.getUserHistory());
						mUserAdapter.notifyDataSetChanged();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			AEProgressDialog.dismissLoadingDialog();
		}
	}

	private void scanningForOther(String phone) {
		lastComingPhone = phone;
		comingCallDialog = new AlertDialog.Builder(mContext)
				.setTitle(R.string.warning)
				.setMessage(
						getString(R.string.scanning_call_listen,
								lastComingPhone))
				.setPositiveButton(R.string.confirm,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								setImgPath(
										FileCostants.DIR_SCANNING
												+ lastComingPhone + "_"
												+ System.currentTimeMillis()
												+ ".jpg", true);
								AttchUtil.capture(MainActivity.this,
										getImgPath());
							}
						}).setNegativeButton(R.string.cancel, null).create();
		comingCallDialog.setCanceledOnTouchOutside(false);
		comingCallDialog.show();
	}

	// 来电电话监听器
	private class MyPhoneStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			if (state == TelephonyManager.CALL_STATE_RINGING) {
				if (comingCallDialog != null && comingCallDialog.isShowing()) {
					return;
				}
				scanningForOther(incomingNumber);
			}
		}
	}
}
