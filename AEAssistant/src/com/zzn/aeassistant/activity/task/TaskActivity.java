package com.zzn.aeassistant.activity.task;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BasePageActivity;
import com.zzn.aeassistant.activity.project.ProjectDetailActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.fragment.BaseFragment;
import com.zzn.aeassistant.fragment.ExpandTaskFragment;
import com.zzn.aeassistant.fragment.TaskFragment;
import com.zzn.aeassistant.view.viewpager.SectionPage;
import com.zzn.aeassistant.view.viewpager.SectionPageAdapter;
import com.zzn.aeassistant.vo.ProjectVO;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TaskActivity extends BasePageActivity {
	private View createTask;
	private ProjectVO project;

	@Override
	protected int layoutResID() {
		return R.layout.activity_page_add;
	}

	@Override
	protected int titleStringID() {
		return R.string.lable_task;
	}

	@Override
	protected void initView() {
		super.initView();
		createTask = findViewById(R.id.btn_add);
		createTask.setOnClickListener(this);
		project = (ProjectVO) getIntent().getSerializableExtra(CodeConstants.KEY_PROJECT_VO);
		if (project.getCREATE_USER().equals(AEApp.getCurrentUser().getUSER_ID())) {
			add.setVisibility(View.VISIBLE);
			add.setImageResource(R.drawable.icon_user_group);
			save.setVisibility(View.GONE);
		}
		title.setText(project.getROOT_PROJECT_NAME());
		pager.setOffscreenPageLimit(3);
		for (int i = 0; i < 3; i++) {
			SectionPage item = new SectionPage();
			BaseFragment fragment = null;
			Bundle bundle = new Bundle();
			bundle.putSerializable(CodeConstants.KEY_PROJECT_VO, project);
			if (i == 0) {
				fragment = new ExpandTaskFragment();
				item.setTitle(getString(R.string.lable_task_all));
				bundle.putInt(CodeConstants.KEY_TASK_STATUS, CodeConstants.STATUS_TASK_ALL);
			}
			if (i == 1) {
				fragment = new ExpandTaskFragment();
				item.setTitle(getString(R.string.lable_task_create));
				bundle.putInt(CodeConstants.KEY_TASK_STATUS, CodeConstants.STATUS_TASK_CREATE);
			}
			if (i == 2) {
				fragment = new TaskFragment();
				item.setTitle(getString(R.string.lable_task_process));
				bundle.putInt(CodeConstants.KEY_TASK_STATUS, CodeConstants.STATUS_TASK_PROCESS);
			}
			if (fragment != null) {
				fragment.setArguments(bundle);
				item.setFragment(fragment);
				pageList.add(item);
			}
		}
		adapterPages = new SectionPageAdapter(getSupportFragmentManager(), pageList);
		pager.setAdapter(adapterPages);
		tab.setViewPager(pager);
		tab.setOnPageChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_add:
			Intent intent = new Intent(this, CreateTaskActivity.class);
			intent.putExtra(CodeConstants.KEY_PROJECT_VO, project);
			startActivityForResult(intent, CodeConstants.REQUEST_CODE_REFRESH);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onAddClick() {
		super.onSaveClick();
		startActivity(new Intent(mContext, ProjectDetailActivity.class)
				.putExtra(CodeConstants.KEY_PROJECT_VO, project));
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CodeConstants.REQUEST_CODE_REFRESH:
				// 刷新列表
				for (int i = 0; i < pageList.size(); i++) {
					pageList.get(i).getFragment().onActivityResult(requestCode, resultCode, data);
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected boolean needLocation() {
		return false;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {

	}
}