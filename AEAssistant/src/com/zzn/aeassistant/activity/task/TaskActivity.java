package com.zzn.aeassistant.activity.task;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BasePageActivity;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.fragment.TaskFragment;
import com.zzn.aeassistant.view.viewpager.SectionPage;
import com.zzn.aeassistant.view.viewpager.SectionPageAdapter;
import com.zzn.aeassistant.vo.ProjectVO;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TaskActivity extends BasePageActivity {
	private ProjectVO project;

	@Override
	protected int titleStringID() {
		return R.string.lable_task;
	}

	@Override
	protected void initView() {
		super.initView();
		project = (ProjectVO) getIntent().getSerializableExtra(CodeConstants.KEY_PROJECT_VO);
		save.setVisibility(View.VISIBLE);
		save.setText(R.string.new_);
		for (int i = 0; i < 3; i++) {
			SectionPage item = new SectionPage();
			TaskFragment fragment = new TaskFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable(CodeConstants.KEY_PROJECT_VO, project);
			if (i == 0) {
				item.setTitle(getString(R.string.lable_task_all));
				bundle.putInt(CodeConstants.KEY_TASK_STATUS, CodeConstants.STATUS_TASK_ALL);
			}
			if (i == 1) {
				item.setTitle(getString(R.string.lable_task_create));
				bundle.putInt(CodeConstants.KEY_TASK_STATUS, CodeConstants.STATUS_TASK_CREATE);
			}
			if (i == 2) {
				item.setTitle(getString(R.string.lable_task_process));
				bundle.putInt(CodeConstants.KEY_TASK_STATUS, CodeConstants.STATUS_TASK_PROCESS);
			}
			fragment.setArguments(bundle);
			item.setFragment(fragment);
			pageList.add(item);
		}
		adapterPages = new SectionPageAdapter(getSupportFragmentManager(), pageList);
		pager.setAdapter(adapterPages);
		tab.setViewPager(pager);
		tab.setOnPageChangeListener(this);
	}

	@Override
	protected void onSaveClick() {
		super.onSaveClick();
		Intent intent = new Intent(this, CreateTaskActivity.class);
		intent.putExtra(CodeConstants.KEY_PROJECT_VO, project);
		startActivityForResult(intent, CodeConstants.REQUEST_CODE_REFRESH);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CodeConstants.REQUEST_CODE_REFRESH:
				//刷新列表
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