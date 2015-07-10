package com.zzn.aeassistant.activity.attendance;

import java.util.ArrayList;
import java.util.List;

import android.widget.TextView;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.view.pla.MultiColumnListView;
import com.zzn.aeassistant.vo.AttendanceVO;

public class AttendanceListActivity extends BaseActivity {
	private MultiColumnListView listView;
	private TextView headerLable;
	private SumUserAdapter adapter;
	private List<AttendanceVO> attendances = new ArrayList<AttendanceVO>();

	@Override
	protected int layoutResID() {
		return R.layout.activity_base_staggered_list;
	}

	@Override
	protected int titleStringID() {
		return R.string.sum_by_user;
	}

	@Override
	protected void initView() {
		listView = (MultiColumnListView) findViewById(R.id.base_list);
		headerLable = (TextView) findViewById(R.id.lable);
		
		adapter = new SumUserAdapter(mContext);
		attendances = getIntent().getParcelableArrayListExtra(
				CodeConstants.KEY_ATTENDENCE_LIST);
		adapter.setData(attendances);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		headerLable.setText(getString(R.string.sum_user_total,
				adapter.getCount()));
	}

	@Override
	protected boolean needLocation() {
		return false;
	}
}
