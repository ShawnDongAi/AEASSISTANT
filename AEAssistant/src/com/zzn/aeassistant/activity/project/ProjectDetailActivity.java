package com.zzn.aeassistant.activity.project;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.vo.ProjectVO;

public class ProjectDetailActivity extends BaseActivity {
	private TextView name, status, managerUser, createTime, projectStructure,
			projectUsers, projectUpdateParent, address;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private ProjectVO project;

	@Override
	protected int layoutResID() {
		return R.layout.activity_project_detail;
	}

	@Override
	protected int titleStringID() {
		return R.string.project_detail;
	}

	@Override
	protected void initView() {
		name = (TextView) findViewById(R.id.project_name);
		status = (TextView) findViewById(R.id.project_status);
		managerUser = (TextView) findViewById(R.id.project_manager_user);
		createTime = (TextView) findViewById(R.id.project_create_time);
		projectStructure = (TextView) findViewById(R.id.project_leaf);
		projectUsers = (TextView) findViewById(R.id.project_users);
		projectUpdateParent = (TextView) findViewById(R.id.project_update_parent);
		address = (TextView) findViewById(R.id.project_address);
		name.setOnClickListener(this);
		projectStructure.setOnClickListener(this);
		projectUsers.setOnClickListener(this);
		projectUpdateParent.setOnClickListener(this);

		mMapView = (MapView) findViewById(R.id.project_mapview);
		mBaiduMap = mMapView.getMap();

		project = (ProjectVO) getIntent().getSerializableExtra(
				CodeConstants.KEY_PROJECT_VO);
		name.setText(getString(R.string.project_name, project.getPROJECT_NAME()));
		status.setVisibility(project.getSTATUS() != null
				&& project.getSTATUS().equals("1") ? View.VISIBLE : View.GONE);
		managerUser.setText(getString(R.string.project_manager_user,
				project.getCREATE_USER_NAME()));
		createTime.setText(getString(R.string.project_create_time,
				project.getCREATE_TIME()));
		address.setText(getString(R.string.project_address,
				project.getADDRESS()));
		double longitude = Double.parseDouble(project.getLONGITUDE());
		double latitude = Double.parseDouble(project.getLATITUDE());
		LatLng ll = new LatLng(latitude, longitude);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 17f);
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(ll).icon(
				BitmapDescriptorFactory.fromResource(R.drawable.ic_location)));
		mBaiduMap.animateMapStatus(u);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.project_name:
			break;
		case R.id.project_leaf:
			startActivity(new Intent(mContext, ProjectStructureActivity.class)
					.putExtra(CodeConstants.KEY_PROJECT_ID,
							project.getPROJECT_ID()));
			break;
		case R.id.project_users:
			startActivity(new Intent(mContext, ProjectUsersActivity.class)
					.putExtra(CodeConstants.KEY_PROJECT_ID,
							project.getPROJECT_ID()));
			break;
		case R.id.project_update_parent:
			startActivity(new Intent(mContext, UpdateParentActivity.class)
					.putExtra(CodeConstants.KEY_PROJECT_VO, project));
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// 关闭定位图层
		if (mBaiduMap != null) {
			mBaiduMap.setMyLocationEnabled(false);
		}
		if (mMapView != null) {
			mMapView.onDestroy();
			mMapView = null;
		}
		super.onDestroy();
	}

	@Override
	protected boolean needLocation() {
		return false;
	}
}