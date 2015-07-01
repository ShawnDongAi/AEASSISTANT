package com.zzn.aeassistant.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.util.AttchUtil;
import com.zzn.aeassistant.util.BitmapUtil;
import com.zzn.aeassistant.util.FilePathUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.view.swipeback.SwipeBackActivity;

public abstract class BaseActivity extends SwipeBackActivity implements
		OnClickListener {
	public Context mContext;
	private TextView title;
	protected View back;
	protected Button save;

	private String imgPath;
	private boolean compress = false;
	private float screenW;// 屏幕像素宽度
	private float screenH;// 屏幕像素高度
	private DisplayMetrics displayMetrics;

	// 定位相关
	private LocationClient mLocClient;
	private MyLocationListenner myListener = new MyLocationListenner();

	protected abstract int layoutResID();

	protected abstract int titleStringID();

	protected abstract void initView();

	protected void onSaveClick() {
	}

	protected void getImg(String path) {
	}

	protected void onActivityReceiveLocation(BDLocation location) {
	}

	protected void onActivityReceivePoi(BDLocation poiLocation) {
	}

	protected void setImgPath(String imgPath, boolean compress) {
		this.imgPath = imgPath;
		this.compress = compress;
	}

	protected String getImgPath() {
		return imgPath;
	}

	protected abstract boolean needLocation();

	protected void startLocation() {
		mLocClient.registerLocationListener(myListener);
		mLocClient.start();
	}

	protected void stopLocation() {
		// 退出时销毁定位
		if (mLocClient != null) {
			mLocClient.stop();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		screenW = displayMetrics.widthPixels;
		screenH = displayMetrics.heightPixels;
		setContentView(layoutResID());
		title = (TextView) findViewById(R.id.title);
		if (title != null) {
			title.setText(titleStringID());
		}
		back = findViewById(R.id.back);
		if (back != null) {
			back.setOnClickListener(this);
		}
		save = (Button) findViewById(R.id.save);
		if (save != null) {
			save.setOnClickListener(this);
		}
		initView();
		if (needLocation()) {
			mLocClient = new LocationClient(this);
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);// 打开gps
			option.setCoorType("bd09ll"); // 设置坐标类型
			option.setScanSpan(120 * 1000);
			option.setIsNeedAddress(true);
			mLocClient.setLocOption(option);
		}
	}

	private long lastClickTime = 0;
	@Override
	public void onClick(View v) {
		if (System.currentTimeMillis() - lastClickTime < 500) {
			return;
		}
		lastClickTime = System.currentTimeMillis();
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.save:
			onSaveClick();
			break;
		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CodeConstants.REQUEST_CODE_TAKEPHOTO:
				if (compress) {
					new ImageCompressTask().execute(imgPath);
				} else {
					getImg(imgPath);
				}
				break;
			case CodeConstants.REQUEST_CODE_GETPHOTO:
				if (data != null && data.getData() != null) {// 从“文件浏览器”或者“Gallery相册”选择的图片
					String imagePath = "";
					Uri imageUri = data.getData();
					if (imageUri == null || !(imageUri instanceof Uri)) {
						return;
					}
					imagePath = AttchUtil.getPath(mContext, imageUri);
					if (imagePath == null || imagePath.equals("")) {
						return;
					}
					if (FilePathUtil.copyFile(imagePath, getImgPath())) {
						if (compress) {
							new ImageCompressTask().execute(getImgPath());
						} else {
							getImg(getImgPath());
						}
					} else {
						if (compress) {
							new ImageCompressTask().execute(imagePath);
						} else {
							getImg(imagePath);
						}
					}
				}
				break;
			default:
				break;
			}
		}
	}

	private class ImageCompressTask extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			AEProgressDialog.showLoadingDialog(mContext);
		}

		@Override
		protected String doInBackground(String... params) {
			String sourcePath = params[0];
			try {
				return BitmapUtil.zoomOutBitmap(sourcePath, screenW, screenH);
			} catch (Exception e) {
			} catch (OutOfMemoryError e) {
			}
			return sourcePath;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			getImg(result);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			AEProgressDialog.dismissLoadingDialog();
			getImg(getImgPath());
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		setImgPath(savedInstanceState.getString("imagePath"),
				savedInstanceState.getBoolean("compress"));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("imagePath", getImgPath());
		outState.putBoolean("compress", compress);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		mLocClient = null;
		AEProgressDialog.dismissLoadingDialog();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		stopLocation();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (needLocation() && mLocClient != null && !mLocClient.isStarted()) {
			startLocation();
		}
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			onActivityReceiveLocation(location);
		}

		public void onReceivePoi(BDLocation poiLocation) {
			onActivityReceivePoi(poiLocation);
		}
	}
}