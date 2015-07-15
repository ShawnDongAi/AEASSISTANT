package com.zzn.aeassistant.activity.user;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.activity.MainActivity;
import com.zzn.aeassistant.activity.TextEditActivity;
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
import com.zzn.aeassistant.view.CircleImageView;
import com.zzn.aeassistant.view.cropimage.Crop;
import com.zzn.aeassistant.vo.AttchVO;
import com.zzn.aeassistant.vo.HttpResult;

public class UserActivity extends BaseActivity {
	public static final int REQUEST_PHOTOGRAPH = 0;
	public static final int REQUEST_ALBUM = 1;
	public static final int REQUEST_USER_NAME = 2;
	public static final int REQUEST_USER_REMARK = 3;

	private View layoutHead, layoutName, layoutPhone, layoutSex, layoutRemark;
	private TextView name, phone, sex, remark;
	private CircleImageView head;
	private PopupWindow menu;
	private Button photograph, album, cancel;

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

	@Override
	protected int layoutResID() {
		return R.layout.activity_user;
	}

	@Override
	protected int titleStringID() {
		return R.string.title_user_center;
	}

	@Override
	protected void initView() {
		layoutHead = findViewById(R.id.user_layout_head);
		layoutName = findViewById(R.id.user_layout_name);
		layoutPhone = findViewById(R.id.user_layout_phone);
		layoutSex = findViewById(R.id.user_layout_sex);
		layoutRemark = findViewById(R.id.user_layout_remark);
		layoutHead.setOnClickListener(this);
		layoutName.setOnClickListener(this);
		layoutPhone.setOnClickListener(this);
		layoutSex.setOnClickListener(this);
		layoutRemark.setOnClickListener(this);

		name = (TextView) findViewById(R.id.user_name);
		phone = (TextView) findViewById(R.id.user_phone);
		sex = (TextView) findViewById(R.id.user_sex);
		remark = (TextView) findViewById(R.id.user_remark);
		head = (CircleImageView) findViewById(R.id.user_head);

		name.setText(AEApp.getCurrentUser().getUSER_NAME());
		phone.setText(AEApp.getCurrentUser().getPHONE());
		sex.setText(!AEApp.getCurrentUser().getSEX().trim().equals("1") ? R.string.male
				: R.string.female);
		String mRemark = AEApp.getCurrentUser().getREMARK();
		if (!StringUtil.isEmpty(mRemark)) {
			remark.setText(mRemark);
		}
		initImageLoader();
		initMenu();
		if (!StringUtil.isEmpty(AEApp.getCurrentUser().getBIG_HEAD())) {
			imageLoader.displayImage(String.format(URLConstants.URL_DOWNLOAD,
					AEApp.getCurrentUser().getBIG_HEAD()), head, options);
		}
	}

	@SuppressWarnings("deprecation")
	private void initImageLoader() {
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_head) // 设置图片在下载期间显示的图片
				.showImageForEmptyUri(R.drawable.ic_head)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.ic_head) // 设置图片加载/解码过程中错误时候显示的图片
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
	}

	@SuppressWarnings("deprecation")
	private void initMenu() {
		View menuView = View.inflate(mContext, R.layout.menu_user_head, null);
		photograph = (Button) menuView.findViewById(R.id.menu_photograph);
		album = (Button) menuView.findViewById(R.id.menu_album);
		cancel = (Button) menuView.findViewById(R.id.menu_cancel);
		photograph.setOnClickListener(this);
		album.setOnClickListener(this);
		cancel.setOnClickListener(this);
		menuView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (menu != null && menu.isShowing()) {
					menu.dismiss();
				}
			}
		});
		menu = new PopupWindow(menuView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		menu.setBackgroundDrawable(getResources().getDrawable(
				R.color.transparent_lightslategray));
		menu.setAnimationStyle(R.style.bottommenu_anim_style);
		menu.setOutsideTouchable(false);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.user_layout_head:
			if (menu != null && !menu.isShowing()) {
				menu.showAtLocation(layoutHead, Gravity.BOTTOM, 0, 0);
			}
			break;
		case R.id.user_layout_name:
			Intent nameIntent = new Intent(mContext, TextEditActivity.class);
			nameIntent.putExtra(CodeConstants.KEY_TITLE,
					getString(R.string.modify_user_name));
			nameIntent.putExtra(CodeConstants.KEY_DEFAULT_TEXT, name.getText()
					.toString());
			nameIntent.putExtra(CodeConstants.KEY_HINT_TEXT,
					getString(R.string.hint_user));
			nameIntent.putExtra(CodeConstants.KEY_SINGLELINE, true);
			startActivityForResult(nameIntent, REQUEST_USER_NAME);
			break;
		case R.id.user_layout_phone:
			break;
		case R.id.user_layout_sex:
			break;
		case R.id.user_layout_remark:
			Intent remarkIntent = new Intent(mContext, TextEditActivity.class);
			remarkIntent.putExtra(CodeConstants.KEY_TITLE,
					getString(R.string.modify_user_remark));
			remarkIntent.putExtra(CodeConstants.KEY_DEFAULT_TEXT, remark
					.getText().toString());
			remarkIntent.putExtra(CodeConstants.KEY_HINT_TEXT,
					getString(R.string.hint_remark));
			remarkIntent.putExtra(CodeConstants.KEY_SINGLELINE, false);
			startActivityForResult(remarkIntent, REQUEST_USER_REMARK);
			break;
		case R.id.menu_photograph:
			if (menu != null && menu.isShowing()) {
				menu.dismiss();
			}
			setImgPath(
					FileCostants.DIR_HEAD + AEApp.getCurrentUser().getUSER_ID()
							+ "_" + System.currentTimeMillis() + ".jpg", false);
			AttchUtil.capture(this, getImgPath());
			break;
		case R.id.menu_album:
			if (menu != null && menu.isShowing()) {
				menu.dismiss();
			}
			setCompress(false);
			AttchUtil.getPictureFromGallery(this);
			break;
		case R.id.menu_cancel:
			if (menu != null && menu.isShowing()) {
				menu.dismiss();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		if (menu != null && menu.isShowing()) {
			menu.dismiss();
			return;
		}
		super.onBackPressed();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_USER_NAME:
				String nameString = data
						.getStringExtra(CodeConstants.KEY_TEXT_RESULT);
				if (name.getText().toString().trim().equals(nameString)) {
					break;
				}
				name.setText(nameString);
				AEApp.getCurrentUser().setUSER_NAME(nameString);
				sendBroadcast(new Intent(MainActivity.ACTION_USER_INFO_CHANGED));
				new UpdateNameTask().execute(nameString);
				break;
			case REQUEST_USER_REMARK:
				String remarkString = data
						.getStringExtra(CodeConstants.KEY_TEXT_RESULT);
				if (remark.getText().toString().trim().equals(remarkString)) {
					break;
				}
				remark.setText(remarkString);
				AEApp.getCurrentUser().setREMARK(remarkString);
				new UpdateRemarkTask().execute(remarkString);
				break;
			case Crop.REQUEST_CROP:
				new UpdateHeadTask().execute(AttchUtil.getPath(mContext,
						Crop.getOutput(data)));
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void getImg(String path) {
		Uri outputUri = Uri.fromFile(new File(FileCostants.DIR_HEAD, AEApp
				.getCurrentUser().getUSER_ID()
				+ "_"
				+ System.currentTimeMillis()));
		new Crop(path).output(outputUri).asSquare().start(this);
	}

	@Override
	protected boolean needLocation() {
		return false;
	}

	private class UpdateHeadTask extends AsyncTask<String, Integer, HttpResult> {
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
			List<String> files = new ArrayList<String>();
			files.add(filePath);
			HttpResult result = AEHttpUtil.doPostWithFile(
					URLConstants.URL_UPDATE_HEAD, files, param);
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
					AEApp.getCurrentUser().setBIG_HEAD(vo.getATTCH_ID());
					AEApp.getCurrentUser().setSMALL_HEAD(vo.getATTCH_ID());
					imageLoader.displayImage(String.format(
							URLConstants.URL_DOWNLOAD, AEApp.getCurrentUser()
									.getBIG_HEAD()), head, options);
					sendBroadcast(new Intent(
							MainActivity.ACTION_USER_INFO_CHANGED));
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

	private class UpdateNameTask extends AsyncTask<String, Integer, HttpResult> {
		@Override
		protected HttpResult doInBackground(String... params) {
			String nameString = params[0];
			String param = "user_id=" + AEApp.getCurrentUser().getUSER_ID()
					+ "&user_name=" + nameString;
			HttpResult result = AEHttpUtil.doPost(URLConstants.URL_UPDATE_NAME,
					param);
			return result;
		}
	}

	private class UpdateRemarkTask extends
			AsyncTask<String, Integer, HttpResult> {

		@Override
		protected HttpResult doInBackground(String... params) {
			String remarkString = params[0];
			String param = "user_id=" + AEApp.getCurrentUser().getUSER_ID()
					+ "&remark=" + remarkString;
			HttpResult result = AEHttpUtil.doPost(
					URLConstants.URL_UPDATE_REMARK, param);
			return result;
		}
	}
}
