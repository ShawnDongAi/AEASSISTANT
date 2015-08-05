package com.zzn.aeassistant.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.baidu.location.BDLocation;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.util.AttchUtil;
import com.zzn.aeassistant.util.BitmapUtil;
import com.zzn.aeassistant.util.FilePathUtil;
import com.zzn.aeassistant.view.AEProgressDialog;

public abstract class BaseFragment extends Fragment implements OnClickListener {
	public Context mContext;
	private String imgPath;
	private boolean compress = false;
	private float screenW;// 屏幕像素宽度
	private float screenH;// 屏幕像素高度
	private DisplayMetrics displayMetrics;
	private Bundle savedState;

	protected abstract int layoutResID();

	protected abstract void initView(View container);

	protected void getImg(String path) {
	}

	public boolean onBackPressed() {
		return false;
	}

	public void onActivityReceiveLocation(BDLocation location) {
	}

	public void onActivityReceivePoi(BDLocation poiLocation) {
	}

	protected void setImgPath(String imgPath, boolean compress) {
		this.imgPath = imgPath;
		this.compress = compress;
	}

	protected void setCompress(boolean compress) {
		this.compress = compress;
	}

	protected String getImgPath() {
		return imgPath;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (!restoreStateFromArguments()) {
			// First Time, Initialize something here
			onFirstTimeLaunched();
		}
	}

	protected void onFirstTimeLaunched() {
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Save State Here
		saveStateToArguments();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		// Save State Here
		saveStateToArguments();
	}

	private void saveStateToArguments() {
		if (getView() != null)
			savedState = saveState();
		if (savedState != null) {
			Bundle b = getArguments();
			if (b != null) {
				b.putBundle("savedState", savedState);
			}
		}
	}

	private boolean restoreStateFromArguments() {
		Bundle b = getArguments();
		if (b != null) {
			savedState = b.getBundle("savedState");
			if (savedState != null) {
				restoreState();
				return true;
			}
		}
		return false;
	}

	private void restoreState() {
		if (savedState != null) {
			onRestoreState(savedState);
		}
	}

	protected void onRestoreState(Bundle savedInstanceState) {
	}

	private Bundle saveState() {
		Bundle state = new Bundle();
		onSaveState(state);
		return state;
	}

	protected void onSaveState(Bundle outState) {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		screenW = displayMetrics.widthPixels;
		screenH = displayMetrics.heightPixels;
		View view = inflater.inflate(layoutResID(), null);
		initView(view);
		return view;
	}

	private long lastClickTime = 0;

	@Override
	public void onClick(View v) {
		if (System.currentTimeMillis() - lastClickTime < 500) {
			return;
		}
		lastClickTime = System.currentTimeMillis();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
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
}