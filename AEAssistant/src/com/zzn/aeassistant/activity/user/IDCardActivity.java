package com.zzn.aeassistant.activity.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.FileCostants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.AttchUtil;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.vo.AttchVO;
import com.zzn.aeassistant.vo.HttpResult;

public class IDCardActivity extends BaseActivity {
	private TextView editFront, editBack, editHand;
	private ImageView imgFront, imgBack, imgHand;
	private int imgType = 0;

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private String imgFrontID, imgBackID, imgHandID = "";
	private boolean editable = false;

	@Override
	protected int layoutResID() {
		return R.layout.activity_idcard;
	}

	@Override
	protected int titleStringID() {
		return R.string.lable_idcard_img;
	}

	@Override
	protected void initView() {
		editFront = (TextView) findViewById(R.id.idcard_front_edit);
		editBack = (TextView) findViewById(R.id.idcard_back_eidt);
		editHand = (TextView) findViewById(R.id.idcard_hand_edit);
		imgFront = (ImageView) findViewById(R.id.idcard_front);
		imgBack = (ImageView) findViewById(R.id.idcard_back);
		imgHand = (ImageView) findViewById(R.id.idcard_hand);
		editable = getIntent().getBooleanExtra(CodeConstants.KEY_EDITABLE,
				false);
		if (editable) {
			editFront.setOnClickListener(this);
			editBack.setOnClickListener(this);
			editHand.setOnClickListener(this);
		} else {
			editFront.setCompoundDrawables(null, null, null, null);
			editBack.setCompoundDrawables(null, null, null, null);
			editHand.setCompoundDrawables(null, null, null, null);
		}
		imgFrontID = getIntent().getStringExtra(CodeConstants.KEY_IDCARD_FRONT);
		imgBackID = getIntent().getStringExtra(CodeConstants.KEY_IDCARD_BACK);
		imgHandID = getIntent().getStringExtra(CodeConstants.KEY_IDCARD_HAND);
		initImageLoader();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.idcard_front_edit:
			imgType = 0;
			setImgPath(
					FileCostants.DIR_HEAD + AEApp.getCurrentUser().getUSER_ID()
							+ "_front_" + System.currentTimeMillis() + ".jpg",
					true);
			AttchUtil.capture(this, getImgPath());
			break;
		case R.id.idcard_back_eidt:
			imgType = 1;
			setImgPath(
					FileCostants.DIR_HEAD + AEApp.getCurrentUser().getUSER_ID()
							+ "_back_" + System.currentTimeMillis() + ".jpg",
					true);
			AttchUtil.capture(this, getImgPath());
			break;
		case R.id.idcard_hand_edit:
			imgType = 2;
			setImgPath(
					FileCostants.DIR_HEAD + AEApp.getCurrentUser().getUSER_ID()
							+ "_hand_" + System.currentTimeMillis() + ".jpg",
					true);
			AttchUtil.capture(this, getImgPath());
			break;
		default:
			break;
		}
	}

	@Override
	protected void getImg(String path) {
		new UpdateIDCardImgTask(imgType).execute(path);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		imgType = savedInstanceState.getInt("imgType");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("imgType", imgType);
	}

	private class UpdateIDCardImgTask extends
			AsyncTask<String, Integer, HttpResult> {
		private int imgType = 0;

		public UpdateIDCardImgTask(int imgType) {
			this.imgType = imgType;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			AEProgressDialog.showLoadingDialog(mContext);
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			String filePath = params[0];
			Map<String, String> param = new HashMap<String, String>();
			param.put("user_id", AEApp.getCurrentUser().getUSER_ID());
			param.put("img_type", imgType + "");
			List<String> files = new ArrayList<String>();
			files.add(filePath);
			HttpResult result = AEHttpUtil.doPostWithFile(
					URLConstants.URL_UPDATE_IDCARD_IMG, files, param);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				if (result.getRES_OBJ() != null
						&& !StringUtil.isEmpty(result.getRES_OBJ().toString())) {
					AttchVO vo = GsonUtil.getInstance().fromJson(
							result.getRES_OBJ().toString(), AttchVO.class);
					switch (imgType) {
					case 0:
						AEApp.getCurrentUser()
								.setIDCARD_FRONT(vo.getATTCH_ID());
						imageLoader.displayImage(String.format(
								URLConstants.URL_DOWNLOAD, AEApp
										.getCurrentUser().getIDCARD_FRONT()),
								imgFront, options);
						break;
					case 1:
						AEApp.getCurrentUser().setIDCARD_BACK(vo.getATTCH_ID());
						imageLoader.displayImage(String.format(
								URLConstants.URL_DOWNLOAD, AEApp
										.getCurrentUser().getIDCARD_BACK()),
								imgBack, options);
						break;
					case 2:
						AEApp.getCurrentUser().setIDCARD_HAND(vo.getATTCH_ID());
						imageLoader.displayImage(String.format(
								URLConstants.URL_DOWNLOAD, AEApp
										.getCurrentUser().getIDCARD_HAND()),
								imgHand, options);
						break;
					default:
						break;
					}
				}
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
		return false;
	}

	@SuppressWarnings("deprecation")
	private void initImageLoader() {
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.icon_loading) // 设置图片在下载期间显示的图片
				.showImageForEmptyUri(R.drawable.icon_failed)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.icon_failed) // 设置图片加载/解码过程中错误时候显示的图片
				.cacheInMemory(true)// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
				.imageScaleType(ImageScaleType.EXACTLY)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
				// .decodingOptions(android.graphics.BitmapFactory.Options
				// decodingOptions)//设置图片的解码配置
				// .delayBeforeLoading(int delayInMillis)//int
				// delayInMillis为你设置的下载前的延迟时间
				// 设置图片加入缓存前，对bitmap进行设置
				// .preProcessor(BitmapProcessor preProcessor)
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				// .displayer(new RoundedBitmapDisplayer(20))// 是否设置为圆角，弧度为多少
				.displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
				.build();// 构建完成
		if (!StringUtil.isEmpty(AEApp.getCurrentUser().getIDCARD_FRONT())) {
			imageLoader.displayImage(
					String.format(URLConstants.URL_DOWNLOAD, imgFrontID),
					imgFront, options);
		}
		if (!StringUtil.isEmpty(AEApp.getCurrentUser().getIDCARD_BACK())) {
			imageLoader.displayImage(
					String.format(URLConstants.URL_DOWNLOAD, imgBackID),
					imgBack, options);
		}
		if (!StringUtil.isEmpty(AEApp.getCurrentUser().getIDCARD_HAND())) {
			imageLoader.displayImage(
					String.format(URLConstants.URL_DOWNLOAD, imgHandID),
					imgHand, options);
		}
	}
}
