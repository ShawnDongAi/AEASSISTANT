package com.zzn.aeassistant.activity;

import java.util.ArrayList;
import java.util.List;

import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.baidu.location.BDLocation;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.setting.VersionUpdateTask;
import com.zzn.aeassistant.activity.user.UserActivity;
import com.zzn.aeassistant.constants.Config;
import com.zzn.aeassistant.fragment.AttendanceFragment;
import com.zzn.aeassistant.fragment.BaseFragment;
import com.zzn.aeassistant.fragment.ContactFragment;
import com.zzn.aeassistant.fragment.ProjectManagerFragment;
import com.zzn.aeassistant.fragment.SettingFragment;
import com.zzn.aeassistant.fragment.WorkSpaceFragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

public class IndexActivity extends BaseActivity implements OnCheckedChangeListener {

	private RadioButton projectManager, contact, workspace, attendance, settings;
	private List<BaseFragment> fragmentList = new ArrayList<>();
	private int currentIndex = 0;
	private View workspaceUnread;

	@Override
	protected int layoutResID() {
		return R.layout.activity_index;
	}

	@Override
	protected int titleStringID() {
		return R.string.title_project_manager;
	}

	@SuppressLint("NewApi")
	@Override
	protected void initView() {
		setSwipeBackEnable(false);
		title = (TextView) findViewById(R.id.title);
		if (title != null) {
			title.setText(titleStringID());
		}
		save = (Button) findViewById(R.id.save);
		if (save != null) {
			save.setOnClickListener(this);
		}
		back = (ImageButton) findViewById(R.id.back);
		if (back != null) {
			back.setVisibility(View.INVISIBLE);
		}
		fragmentList.add(new ProjectManagerFragment());
		fragmentList.add(new ContactFragment());
		fragmentList.add(new WorkSpaceFragment());
		fragmentList.add(new AttendanceFragment());
		fragmentList.add(new SettingFragment());
		projectManager = (RadioButton) findViewById(R.id.project_manager);
		contact = (RadioButton) findViewById(R.id.contact);
		workspace = (RadioButton) findViewById(R.id.workspace);
		attendance = (RadioButton) findViewById(R.id.attendance);
		settings = (RadioButton) findViewById(R.id.settings);
		projectManager.setOnCheckedChangeListener(this);
		contact.setOnCheckedChangeListener(this);
		workspace.setOnCheckedChangeListener(this);
		attendance.setOnCheckedChangeListener(this);
		settings.setOnCheckedChangeListener(this);
		workspaceUnread = findViewById(R.id.workspace_unread);
		turnToFragment(null, fragmentList.get(0));
		if (Config.channel == Config.CHANNEL_BAIDU) {
			try {
				BDAutoUpdateSDK.uiUpdateAction(mContext, new UICheckUpdateCallback() {
					@Override
					public void onCheckComplete() {
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			new VersionUpdateTask(mContext, false).execute();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			save.setVisibility(View.INVISIBLE);
			switch (buttonView.getId()) {
			case R.id.project_manager:
				turnToFragment(fragmentList.get(currentIndex), fragmentList.get(0));
				currentIndex = 0;
				setTitle(getString(R.string.title_project_manager));
				contact.setChecked(false);
				workspace.setChecked(false);
				attendance.setChecked(false);
				settings.setChecked(false);
				break;
			case R.id.contact:
				turnToFragment(fragmentList.get(currentIndex), fragmentList.get(1));
				currentIndex = 1;
				setTitle(getString(R.string.title_contact));
				// save.setText(R.string.add);
				// save.setVisibility(View.VISIBLE);
				projectManager.setChecked(false);
				workspace.setChecked(false);
				attendance.setChecked(false);
				settings.setChecked(false);
				break;
			case R.id.workspace:
				turnToFragment(fragmentList.get(currentIndex), fragmentList.get(2));
				currentIndex = 2;
				setTitle(getString(R.string.title_work_space));
				save.setText(R.string.post);
				save.setVisibility(View.VISIBLE);
				workspaceUnread.setVisibility(View.GONE);
				projectManager.setChecked(false);
				contact.setChecked(false);
				attendance.setChecked(false);
				settings.setChecked(false);
				break;
			case R.id.attendance:
				turnToFragment(fragmentList.get(currentIndex), fragmentList.get(3));
				currentIndex = 3;
				setTitle(getString(R.string.title_attendance));
				projectManager.setChecked(false);
				contact.setChecked(false);
				workspace.setChecked(false);
				settings.setChecked(false);
				break;
			case R.id.settings:
				turnToFragment(fragmentList.get(currentIndex), fragmentList.get(4));
				currentIndex = 4;
				setTitle(getString(R.string.title_setting));
				projectManager.setChecked(false);
				contact.setChecked(false);
				workspace.setChecked(false);
				attendance.setChecked(false);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.index_user:
			startActivity(new Intent(mContext, UserActivity.class));
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		if (fragmentList.get(currentIndex).onBackPressed()) {
			return;
		}
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected boolean needLocation() {
		return true;
	}

	@Override
	protected void onActivityReceiveLocation(BDLocation location) {
		super.onActivityReceiveLocation(location);
		if (fragmentList.get(currentIndex) != null) {
			fragmentList.get(currentIndex).onActivityReceiveLocation(location);
		}
	}

	@Override
	protected void onActivityReceivePoi(BDLocation poiLocation) {
		super.onActivityReceivePoi(poiLocation);
		if (fragmentList.get(currentIndex) != null) {
			fragmentList.get(currentIndex).onActivityReceivePoi(poiLocation);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (fragmentList.get(currentIndex) != null) {
			fragmentList.get(currentIndex).onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onSaveClick() {
		super.onSaveClick();
		if (onSaveClickListener != null) {
			onSaveClickListener.onSaveClick();
		}
	}

	private SaveClickListener onSaveClickListener;

	public void setOnSaveClickListener(SaveClickListener onSaveClickListener) {
		this.onSaveClickListener = onSaveClickListener;
	}

	public interface SaveClickListener {
		void onSaveClick();
	}

	/**
	 * Fragment跳转
	 * 
	 * @param fm
	 * @param fragmentClass
	 * @param tag
	 * @param args
	 */
	public void turnToFragment(BaseFragment fromFragment, BaseFragment toFragment) {
		FragmentManager fm = getSupportFragmentManager();
		// 切换到的Fragment标签
		String toTag = toFragment.getClass().getSimpleName();
		// Fragment事务
		FragmentTransaction ft = fm.beginTransaction();
		// 设置Fragment切换效果
		// ft.setCustomAnimations(android.R.anim.fade_in,
		// android.R.anim.fade_out, android.R.anim.fade_in,
		// android.R.anim.fade_out);
		/**
		 * 如果要切换到的Fragment没有被Fragment事务添加，则隐藏被切换的Fragment，添加要切换的Fragment
		 * 否则，则隐藏被切换的Fragment，显示要切换的Fragment
		 */
		if (fromFragment != null) {
			if (!toFragment.isAdded()) {
				ft.hide(fromFragment).add(R.id.fragment_container, toFragment, toTag);
			} else {
				ft.hide(fromFragment).show(toFragment);
				toFragment.onResume();
			}
		} else {
			if (!toFragment.isAdded()) {
				ft.add(R.id.fragment_container, toFragment, toTag);
			} else {
				ft.show(toFragment);
				toFragment.onResume();
			}
		}
		// 添加到返回堆栈
		// ft.addToBackStack(tag);
		// 不保留状态提交事务
		ft.commitAllowingStateLoss();
		if (fromFragment != null) {
			fromFragment.onPause();
		}
	}
}