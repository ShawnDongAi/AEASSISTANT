package com.zzn.aeassistant.activity.project;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.vo.HttpResult;

public class CreateProjectActivity extends BaseActivity implements
		OnGetGeoCoderResultListener {
	private EditText projectName, projectAddress;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private Button location, addressLocation;
	private String city;
	private LatLng projectLocation;
	private boolean needLocation = true;
	private GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private CreateTask createTask;

	@Override
	protected int layoutResID() {
		return R.layout.activity_create_project;
	}

	@Override
	protected int titleStringID() {
		return R.string.project_create;
	}

	@Override
	protected void initView() {
		save.setText(R.string.save);
		save.setVisibility(View.VISIBLE);
		projectName = (EditText) findViewById(R.id.project_name);
		projectAddress = (EditText) findViewById(R.id.project_address);
		location = (Button) findViewById(R.id.location);
		location.setOnClickListener(this);
		addressLocation = (Button) findViewById(R.id.address_location);
		addressLocation.setOnClickListener(this);
		mMapView = (MapView) findViewById(R.id.map_view);
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				LocationMode.NORMAL, true, BitmapDescriptorFactory
						.fromResource(R.drawable.ic_address)));

		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);

		// 设置地图点击事件
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public boolean onMapPoiClick(MapPoi poi) {
				return false;
			}

			@Override
			public void onMapClick(LatLng latLng) {
				mBaiduMap.clear();
				projectLocation = latLng;
				mBaiduMap.addOverlay(new MarkerOptions().position(latLng).icon(
						BitmapDescriptorFactory
								.fromResource(R.drawable.ic_address)));
				mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
				mSearch.reverseGeoCode(new ReverseGeoCodeOption()
						.location(latLng));
			}
		});
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.location:
			startLocation();
			needLocation = true;
			break;
		case R.id.address_location:
			mSearch.geocode(new GeoCodeOption().city(city).address(
					projectAddress.getText().toString()));
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
		if (createTask != null) {
			createTask.cancel(true);
			createTask = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onActivityReceiveLocation(BDLocation location) {
		super.onActivityReceiveLocation(location);
		// map view 销毁后不在处理新接收的位置
		if (location == null || mMapView == null) {
			projectLocation = null;
			projectAddress.setText("");
			ToastUtil.show(R.string.location_failed);
			return;
		}
		LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
		projectLocation = ll;
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 17f);
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(ll).icon(
				BitmapDescriptorFactory.fromResource(R.drawable.ic_address)));
		mBaiduMap.animateMapStatus(u);
		projectAddress.setText(location.getAddrStr());
		city = location.getCity();
		stopLocation();
		needLocation = false;
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			projectLocation = null;
			ToastUtil.show(R.string.address_search_failed);
			projectAddress.requestFocus();
			return;
		}
		projectLocation = result.getLocation();
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_address)));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			projectLocation = null;
			projectAddress.setText("");
			ToastUtil.show(R.string.location_search_failed);
			return;
		}
		projectAddress.setText(result.getAddress());
	}

	@Override
	protected void onSaveClick() {
		super.onSaveClick();
		String name = projectName.getText().toString().trim();
		String address = projectAddress.getText().toString().trim();
		if (StringUtil.isEmpty(name)) {
			ToastUtil.show(R.string.null_project_name);
			projectName.requestFocus();
			return;
		}
		if (projectLocation == null) {
			ToastUtil.show(R.string.null_project_location);
			return;
		}
		createTask = new CreateTask();
		createTask
				.execute(new String[] { name, address,
						projectLocation.longitude + "",
						projectLocation.latitude + "" });
	}

	private class CreateTask extends AsyncTask<String, Integer, HttpResult> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			AEProgressDialog.showLoadingDialog(mContext);
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			String projectName = params[0];
			String address = params[1];
			String longitude = params[2];
			String latitude = params[3];
			String param = "user_id=" + AEApp.getCurrentUser(CreateProjectActivity.this).getUSER_ID()
					+ "&project_name=" + projectName + "&address=" + address
					+ "&longitude=" + longitude + "&latitude=" + latitude;
			HttpResult result = AEHttpUtil.doPost(
					URLConstants.URL_CREATE_PROJECT, param);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				setResult(RESULT_OK);
				finish();
			} else {
				ToastUtil.show(result.getRES_MESSAGE());
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			AEProgressDialog.dismissLoadingDialog();
		}
	}

	@Override
	protected boolean needLocation() {
		return needLocation;
	}
}
