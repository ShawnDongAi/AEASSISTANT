package com.zzn.aeassistant.activity.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.activity.TextEditActivity;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.view.CircleImageView;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.ProjectVO;
import com.zzn.aeassistant.vo.UserVO;

public class UserDetailActivity extends BaseActivity {
	public static final int REQUEST_PROJECT_NAME = 0;
	private View layoutProject, layoutIDCardImg, btnCall, layoutRate, rateIcon;
	private TextView project, name, phone, sex, remark, idcard, score;
	private View projectIcon;
	private CircleImageView head;
	private RatingBar rate;

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

	private ProjectVO projectVO;
	private UserVO userVO;
	private boolean changed = false;

	@Override
	protected int layoutResID() {
		return R.layout.activity_user_detail;
	}

	@Override
	protected int titleStringID() {
		return R.string.lable_user_detail;
	}

	@Override
	protected void initView() {
		projectVO = (ProjectVO) getIntent().getSerializableExtra(
				CodeConstants.KEY_PROJECT_VO);
		if (projectVO == null) {
			finish();
		}
		layoutProject = findViewById(R.id.user_layout_project);
		layoutIDCardImg = findViewById(R.id.user_layout_idcard_img);
		btnCall = findViewById(R.id.btn_call);
		layoutRate = findViewById(R.id.user_layout_rate);
		rateIcon = findViewById(R.id.user_rate_ic);
		rate = (RatingBar) findViewById(R.id.user_rate);
		score = (TextView) findViewById(R.id.user_score);
		projectIcon = findViewById(R.id.user_project_icon);
		String projectID = getIntent().getStringExtra(
				CodeConstants.KEY_PROJECT_ID);
		if (projectVO.getPARENT_ID().equals(projectID)) {
			layoutProject.setOnClickListener(this);
			layoutRate.setOnClickListener(this);
		} else {
			if (projectVO.getPROJECT_ID().equals(projectID)) {
				layoutProject.setOnClickListener(this);
			} else {
				projectIcon.setVisibility(View.INVISIBLE);
			}
			rateIcon.setVisibility(View.INVISIBLE);
		}
		layoutIDCardImg.setOnClickListener(this);
		btnCall.setOnClickListener(this);

		project = (TextView) findViewById(R.id.user_project);
		name = (TextView) findViewById(R.id.user_name);
		phone = (TextView) findViewById(R.id.user_phone);
		sex = (TextView) findViewById(R.id.user_sex);
		remark = (TextView) findViewById(R.id.user_remark);
		idcard = (TextView) findViewById(R.id.user_idcard);
		head = (CircleImageView) findViewById(R.id.user_head);
		initImageLoader();
		project.setText(projectVO.getPROJECT_NAME());
		name.setText(projectVO.getCREATE_USER_NAME());
		phone.setText(projectVO.getCREATE_USER_PHONE());
		if (!StringUtil.isEmpty(projectVO.getCREATE_USER_HEAD())) {
			imageLoader.displayImage(
					String.format(URLConstants.URL_DOWNLOAD,
							projectVO.getCREATE_USER_HEAD()), head, options);
		}
		new GetUserTask().execute(projectVO.getCREATE_USER());
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.user_layout_project:
			Intent projectintent = new Intent(mContext, TextEditActivity.class);
			projectintent.putExtra(CodeConstants.KEY_TITLE,
					getString(R.string.modify_project_name));
			projectintent.putExtra(CodeConstants.KEY_DEFAULT_TEXT,
					projectVO.getPROJECT_NAME());
			projectintent.putExtra(CodeConstants.KEY_HINT_TEXT,
					getString(R.string.project_hint_name));
			projectintent.putExtra(CodeConstants.KEY_SINGLELINE, true);
			startActivityForResult(projectintent, REQUEST_PROJECT_NAME);
			break;
		case R.id.user_layout_idcard_img:
			if (userVO != null) {
				Intent intent = new Intent(this, IDCardActivity.class);
				intent.putExtra(CodeConstants.KEY_EDITABLE, false);
				intent.putExtra(CodeConstants.KEY_IDCARD_FRONT,
						userVO.getIDCARD_FRONT());
				intent.putExtra(CodeConstants.KEY_IDCARD_BACK,
						userVO.getIDCARD_BACK());
				intent.putExtra(CodeConstants.KEY_IDCARD_HAND,
						userVO.getIDCARD_HAND());
				startActivity(intent);
			}
			break;
		case R.id.btn_call:
			try {
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
						+ projectVO.getCREATE_USER_PHONE()));
				startActivity(intent);
			} catch (Exception e) {
				ToastUtil.show(R.string.dial_error);
			}
			break;
		case R.id.user_layout_rate:
			if (userVO != null) {
				Intent intent = new Intent(this, RatingActivity.class);
				intent.putExtra(CodeConstants.KEY_USER_ID, userVO.getUSER_ID());
				intent.putExtra(CodeConstants.KEY_PROJECT_VO, projectVO);
				startActivity(intent);
			}
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
			case REQUEST_PROJECT_NAME:
				String nameString = data
						.getStringExtra(CodeConstants.KEY_TEXT_RESULT);
				if (project.getText().toString().trim().equals(nameString)) {
					break;
				}
				project.setText(nameString);
				new UpdateProjectTask().execute(nameString);
				changed = true;
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void onBackClick() {
		if (changed) {
			setResult(RESULT_OK);
		}
		super.onBackClick();
	}

	@Override
	public void onBackPressed() {
		if (changed) {
			setResult(RESULT_OK);
			finish();
		} else {
			super.onBackPressed();
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
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				.displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
				.build();// 构建完成
	}

	@Override
	protected boolean needLocation() {
		return false;
	}

	private class GetUserTask extends AsyncTask<String, Integer, HttpResult> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			AEProgressDialog.showLoadingDialog(mContext);
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			String user_id = params[0];
			String param = "user_id=" + user_id;
			HttpResult result = AEHttpUtil.doPost(
					URLConstants.URL_QUERY_USER_BY_ID, param);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				try {
					userVO = GsonUtil.getInstance().fromJson(
							result.getRES_OBJ().toString(), UserVO.class);
					sex.setText(!userVO.getSEX().trim().equals("1") ? R.string.male
							: R.string.female);
					String mRemark = userVO.getREMARK();
					if (!StringUtil.isEmpty(mRemark)) {
						remark.setText(mRemark);
					}
					String mIDCard = userVO.getIDCARD();
					if (!StringUtil.isEmpty(mIDCard)) {
						idcard.setText(mIDCard);
					}
					rate.setRating(userVO.getRATE());
					score.setText(getString(R.string.lable_score,
							userVO.getRATE() + ""));
				} catch (Exception e) {
					e.printStackTrace();
					ToastUtil.show(R.string.http_out);
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

	private class UpdateProjectTask extends
			AsyncTask<String, Integer, HttpResult> {
		@Override
		protected HttpResult doInBackground(String... params) {
			String nameString = params[0];
			String param = "project_id=" + projectVO.getPROJECT_ID()
					+ "&project_name=" + nameString;
			HttpResult result = AEHttpUtil.doPost(
					URLConstants.URL_UPDATE_PROJECT_NAME, param);
			return result;
		}
	}
}