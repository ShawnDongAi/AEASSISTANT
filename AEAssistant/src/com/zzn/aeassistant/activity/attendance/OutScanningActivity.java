package com.zzn.aeassistant.activity.attendance;

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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.activity.UserHistoryAdapter;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.constants.FileCostants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.database.UserDBHelper;
import com.zzn.aeassistant.fragment.ProListAdapter;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.AttchUtil;
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

public class OutScanningActivity extends BaseActivity {
	private TextView mCurrentProject;
	private Button mScanning;
	private SwipeMenuListView mHistoryList;
	private UserHistoryAdapter mUserAdapter;
	private ProjectVO project = null;
	private PopupWindow projectMenu;
	private ListView projectList;
	private ProListAdapter proListAdapter;

	// 打卡拍照的照片路径
	private String scanningPath = "";
	// 电话监听相关
	private TelephonyManager telephony;
	private MyPhoneStateListener myPhoneStateListener;
	// 接收到来电后的对话框
	private AlertDialog comingCallDialog;
	// 上一个来电的号码
	private String lastComingPhone = "";
	private ScanningTask scanningTask;

	private long lastClickTime = 0;

	@Override
	protected int layoutResID() {
		return R.layout.activity_out_scanning;
	}

	@Override
	protected int titleStringID() {
		return R.string.title_scanning_out;
	}

	@Override
	protected void initView() {
		telephony = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		myPhoneStateListener = new MyPhoneStateListener();
		mCurrentProject = (TextView) findViewById(R.id.current_project);
		mCurrentProject.setOnClickListener(this);
		mHistoryList = (SwipeMenuListView) findViewById(R.id.user_history);
		View headerView = View.inflate(mContext,
				R.layout.layout_out_scanning_header, null);
		mScanning = (Button) headerView.findViewById(R.id.scanning);
		mHistoryList.addHeaderView(headerView);
		mScanning.setOnClickListener(this);
		if (AEApp.getCurrentUser().getPROJECTS() != null
				&& AEApp.getCurrentUser().getPROJECTS().size() > 0) {
			project = AEApp.getCurrentUser().getPROJECTS().get(0);
			mCurrentProject.setText(project.getROOT_PROJECT_NAME() + "-"
					+ project.getPROJECT_NAME());
			mScanning.setEnabled(true);
		}

		initMenuView();
		initUserHistory();
	}

	private void initMenuView() {
		View menuView = View.inflate(mContext, R.layout.menu_list, null);
		projectList = (ListView) menuView.findViewById(R.id.menu_list);
		proListAdapter = new ProListAdapter(mContext, AEApp.getCurrentUser()
				.getPROJECTS());
		projectList.setAdapter(proListAdapter);
		projectMenu = new PopupWindow(menuView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		projectMenu.setBackgroundDrawable(getResources().getDrawable(
				R.color.transparent_lightslategray));
		projectMenu.setOutsideTouchable(true);
		projectMenu.setFocusable(true);
		projectList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (projectMenu != null && projectMenu.isShowing()) {
					projectMenu.dismiss();
				}
				project = proListAdapter.getItem(position);
				mCurrentProject.setText(project.getROOT_PROJECT_NAME() + "-"
						+ project.getPROJECT_NAME());
			}
		});
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
	}

	@Override
	protected boolean needLocation() {
		return true;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.current_project:
			if (!projectMenu.isShowing()) {
				projectMenu.showAsDropDown(mCurrentProject);
			}
			break;
		case R.id.scanning:
			String scanningPhone = AEApp.getCurrentUser().getPHONE();
			if (!StringUtil.isEmpty(lastComingPhone)) {
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
	public void onBackPressed() {
		if (projectMenu != null && projectMenu.isShowing()) {
			projectMenu.dismiss();
			return;
		}
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		if (scanningTask != null) {
			scanningTask.cancel(true);
			scanningTask = null;
		}
		super.onDestroy();
	}

	@Override
	public void onActivityReceiveLocation(BDLocation location) {
		super.onActivityReceiveLocation(location);
		AEApp.setCurrentLoc(location);
		if (location == null) {
			project = null;
			ToastUtil.show(R.string.location_failed);
			return;
		}
	}

	@Override
	protected void getImg(String path) {
		super.getImg(path);
		if (StringUtil.isEmpty(path)) {
			ToastUtil.showImp(this, R.string.scanning_null_photo);
			return;
		}
		File file = new File(path);
		if (!file.exists()) {
			ToastUtil.showImp(this, R.string.scanning_null_photo);
			return;
		}
		if (AEApp.getCurrentLoc() == null) {
			ToastUtil.showImp(this, R.string.location_failed);
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
				ToastUtil.showImp(OutScanningActivity.this,
						result.getRES_MESSAGE());
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
			ToastUtil.showImp(this, R.string.out_of_project_location);
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
								AttchUtil.capture(OutScanningActivity.this,
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
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		lastComingPhone = savedInstanceState.getString("lastComingPhone");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("lastComingPhone", lastComingPhone);
		super.onSaveInstanceState(outState);
	}
}