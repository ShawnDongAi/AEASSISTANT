package com.zzn.aeassistant.fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.zzn.aeassistant.activity.UserHistoryAdapter;
import com.zzn.aeassistant.activity.attendance.AttendanceRecordActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.constants.CodeConstants;
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
import com.zzn.aeassistant.view.swipemenu.SwipeMenu;
import com.zzn.aeassistant.view.swipemenu.SwipeMenuCreator;
import com.zzn.aeassistant.view.swipemenu.SwipeMenuItem;
import com.zzn.aeassistant.view.swipemenu.SwipeMenuListView;
import com.zzn.aeassistant.view.swipemenu.SwipeMenuListView.OnMenuItemClickListener;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.ProjectVO;
import com.zzn.aeassistant.vo.UserVO;

/**
 * 签到
 * 
 * @author Shawn
 */
public class AttendanceFragment extends BaseFragment {
	private TextView mCurrentProject;
	private SwipeMenuListView mHistoryList;
	private UserHistoryAdapter mUserAdapter;
	private Button mScanning, mAttendance;
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

	private long lastClickTime = 0;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	protected int layoutResID() {
		return R.layout.activity_attendance;
	}

	@Override
	protected void initView(View container) {
		telephony = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		myPhoneStateListener = new MyPhoneStateListener();

		mCurrentProject = (TextView) container
				.findViewById(R.id.home_current_project);
		mHistoryList = (SwipeMenuListView) container
				.findViewById(R.id.home_user_history);

		View headerView = View.inflate(mContext, R.layout.home_header, null);
		mScanning = (Button) headerView.findViewById(R.id.home_scanning);
		mAttendance = (Button) headerView
				.findViewById(R.id.home_attendance_record);
		mHistoryList.addHeaderView(headerView);

		mScanning.setOnClickListener(this);
		mAttendance.setOnClickListener(this);
		initUserHistory();
	}

	private void initUserHistory() {
		mUserAdapter = new UserHistoryAdapter(mContext);
		mUserAdapter.setUsers(UserDBHelper.getUserHistory(AEApp
				.getCurrentUser().getUSER_ID()));
		mHistoryList.setAdapter(mUserAdapter);
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem item = new SwipeMenuItem(mContext);
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
				UserDBHelper.delete(AEApp.getCurrentUser().getUSER_ID(),
						user.getPHONE());
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
	public void onPause() {
		if (telephony != null) {
			telephony.listen(myPhoneStateListener,
					PhoneStateListener.LISTEN_NONE);
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		telephony.listen(myPhoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);
		initProjectTask = new InitProjectTask();
		if (AEApp.getCurrentLoc() == null) {
			initProjectTask.execute(new Double[] { 0.0, 0.0 });
		} else {
			initProjectTask.execute(new Double[] {
					AEApp.getCurrentLoc().getLatitude(),
					AEApp.getCurrentLoc().getLongitude() });
		}
		/*
		 * ((IndexActivity) getActivity()) .setOnSaveClickListener(new
		 * SaveClickListener() {
		 * 
		 * @Override public void onSaveClick() { startActivityForResult(new
		 * Intent(mContext, QRScanningActivity.class),
		 * CodeConstants.REQUEST_CODE_QRCODE); } });
		 */
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.home_scanning:
			String scanningPhone = AEApp.getCurrentUser().getPHONE();
			if (!StringUtil.isEmpty(lastComingPhone)) {
				lastComingPhone = "";
			}
			setImgPath(
					FileCostants.DIR_SCANNING + scanningPhone + "_"
							+ System.currentTimeMillis() + ".jpg", true);
			AttchUtil.capture(this, getImgPath());
			break;
		case R.id.home_attendance_record:
			Intent proIntent = new Intent(mContext,
					AttendanceRecordActivity.class);
			// String date = dateFormat
			// .format(new Date(System.currentTimeMillis()));
			// proIntent.putExtra(CodeConstants.KEY_START_DATE, date);
			// proIntent.putExtra(CodeConstants.KEY_END_DATE, date);
			startActivity(proIntent);
		default:
			break;
		}
	}

	@Override
	public void onActivityReceiveLocation(BDLocation location) {
		super.onActivityReceiveLocation(location);
		AEApp.setCurrentLoc(location);
		if (location == null) {
			project = null;
			ToastUtil.show(R.string.location_failed);
			mCurrentProject.setText(R.string.out_of_project_location);
			mScanning.setEnabled(false);
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
			if (getActivity() == null) {
				return;
			}
			if (result == null) {
				mCurrentProject.setText(R.string.out_of_project_location);
				mScanning.setEnabled(false);
			} else {
				mCurrentProject.setText(getString(R.string.current_project,
						result.getROOT_PROJECT_NAME()));
				mScanning.setEnabled(true);
			}
		}
	}

	@Override
	public void onDestroyView() {
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
			ToastUtil.showImp(getActivity(), R.string.scanning_null_photo);
			return;
		}
		File file = new File(path);
		if (!file.exists()) {
			ToastUtil.showImp(getActivity(), R.string.scanning_null_photo);
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
			File file = new File(imgPath);
			if (file.exists()) {
				file.delete();
			}
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			if (result != null
					&& result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				if (result.getRES_OBJ() != null
						&& !StringUtil.isEmpty(result.getRES_OBJ().toString())) {
					JSONObject obj;
					try {
						obj = new JSONObject(result.getRES_OBJ().toString());
						if (obj.has("user_name") && obj.has("user_phone")) {
							String user_name = obj.getString("user_name");
							String user_phone = obj.getString("user_phone");
							UserDBHelper.insertUser(AEApp.getCurrentUser()
									.getUSER_ID(), user_phone, user_name);
							mUserAdapter.setUsers(UserDBHelper
									.getUserHistory(AEApp.getCurrentUser()
											.getUSER_ID()));
							mUserAdapter.notifyDataSetChanged();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				ToastUtil.show(result.getRES_MESSAGE());
			} else {
				ToastUtil.showImp(getActivity(), result.getRES_MESSAGE());
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			AEProgressDialog.dismissLoadingDialog();
		}
	}

	private void scanningForOther(String phone) {
		if (!mScanning.isEnabled()) {
			ToastUtil.showImp(getActivity(), R.string.out_of_project_location);
			return;
		}
		lastComingPhone = phone;
		comingCallDialog = new AlertDialog.Builder(mContext)
				.setTitle(R.string.warning)
				.setMessage(
						getString(R.string.scanning_call_listen,
								lastComingPhone))
				.setPositiveButton(R.string.photograph_he,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								setImgPath(
										FileCostants.DIR_SCANNING
												+ lastComingPhone + "_"
												+ System.currentTimeMillis()
												+ ".jpg", true);
								AttchUtil.capture(AttendanceFragment.this,
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case CodeConstants.REQUEST_CODE_QRCODE:
				if (comingCallDialog != null && comingCallDialog.isShowing()) {
					return;
				}
				String result = data
						.getStringExtra(CodeConstants.KEY_SCAN_RESULT);
				try {
					String phone = GsonUtil.getInstance()
							.fromJson(result, HashMap.class).get("user_phone")
							.toString();
					if (comingCallDialog != null
							&& comingCallDialog.isShowing()) {
						return;
					}
					scanningForOther(phone);
				} catch (Exception e) {
					e.printStackTrace();
					ToastUtil.show(R.string.error_qrcode);
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void onRestoreState(Bundle savedInstanceState) {
		super.onRestoreState(savedInstanceState);
		lastComingPhone = savedInstanceState.getString("lastComingPhone");
	}

	@Override
	protected void onSaveState(Bundle outState) {
		super.onSaveState(outState);
		outState.putString("lastComingPhone", lastComingPhone);
	}
}
