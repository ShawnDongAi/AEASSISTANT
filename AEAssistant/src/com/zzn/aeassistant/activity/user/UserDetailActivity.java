package com.zzn.aeassistant.activity.user;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.activity.TextEditActivity;
import com.zzn.aeassistant.activity.post.WorkSpaceActivity;
import com.zzn.aeassistant.activity.task.TaskActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.FileCostants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.fragment.SettingFragment;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.AttchUtil;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.view.CircleImageView;
import com.zzn.aeassistant.view.cropimage.Crop;
import com.zzn.aeassistant.view.tree.Node;
import com.zzn.aeassistant.vo.AttchVO;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.ProjectVO;
import com.zzn.aeassistant.vo.UserVO;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;

public class UserDetailActivity extends BaseActivity {
	public static final int REQUEST_USER_IDCARD = 0;
	public static final int REQUEST_PROJECT_NAME = 1;
	public static final int REQUEST_RATE = 2;
	public static final String ACTION_UPDATE_IDCARD_IMG = "com.zzn.aeassistant.update_idcard_img";
	private View layoutHead, userHeadIcon, layoutProject, layoutTask, /*layoutIDCard, layoutIDCardImg,*/ btnCall,
			layoutRate, rateIcon, /*diverIDCard, diverIDCardImg, idCardIcon,*/
			layoutWorkSpace, diverWorkSpace;
	private TextView project, name, phone, sex, remark, idcard, score;
	private View projectIcon;
	private CircleImageView head;
	private RatingBar rate;

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

	private ProjectVO projectVO;
	private String projectID;
	private UserVO userVO;
	private boolean changed = false;
	
	private PopupWindow headMenu;
	private Button photograph, album, cancel;

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
		Node node = null;
		try {
			node = (Node) getIntent().getSerializableExtra(
					CodeConstants.KEY_PROJECT_VO);
		} catch (Exception e) {
			finish();
		}
		if (node == null || node.getData() == null
				|| !(node.getData() instanceof ProjectVO)) {
			finish();
		}
		projectVO = (ProjectVO) (node.getData());
//		diverIDCard = findViewById(R.id.diver_idcard);
//		diverIDCardImg = findViewById(R.id.diver_idcard_img);
//		idCardIcon = findViewById(R.id.user_idcard_ic);
		layoutHead = findViewById(R.id.user_layout_head);
		userHeadIcon = findViewById(R.id.user_head_icon);
		layoutProject = findViewById(R.id.user_layout_project);
		layoutTask = findViewById(R.id.user_layout_task);
//		layoutIDCard = findViewById(R.id.user_layout_idcard);
//		layoutIDCardImg = findViewById(R.id.user_layout_idcard_img);
		layoutWorkSpace = findViewById(R.id.user_layout_workspace);
		diverWorkSpace = findViewById(R.id.diver_workspace);
		btnCall = findViewById(R.id.btn_call);
		layoutRate = findViewById(R.id.user_layout_rate);
		rateIcon = findViewById(R.id.user_rate_ic);
		rate = (RatingBar) findViewById(R.id.user_rate);
		score = (TextView) findViewById(R.id.user_score);
		projectIcon = findViewById(R.id.user_project_icon);
		projectID = getIntent().getStringExtra(CodeConstants.KEY_PROJECT_ID);
		new CheckRateTask().execute(node);
		if (projectVO.getPARENT_ID().equals(projectID)
				|| projectVO.getPROJECT_ID().equals(projectID)) {
			layoutProject.setOnClickListener(this);
//			layoutIDCard.setOnClickListener(this);
//			layoutIDCardImg.setOnClickListener(this);
		} else {
			projectIcon.setVisibility(View.INVISIBLE);
//			layoutIDCard.setVisibility(View.GONE);
//			diverIDCard.setVisibility(View.GONE);
//			layoutIDCardImg.setVisibility(View.GONE);
//			diverIDCardImg.setVisibility(View.GONE);
		}
		if (!projectVO.getPROJECT_ID().equals(projectID)) {
			layoutRate.setOnClickListener(UserDetailActivity.this);
			rateIcon.setVisibility(View.VISIBLE);
		}
//		layoutIDCardImg.setOnClickListener(this);
		layoutWorkSpace.setOnClickListener(this);
		layoutTask.setOnClickListener(this);
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
					String.format(URLConstants.URL_IMG,
							projectVO.getCREATE_USER_HEAD()), head, options);
		}
		new GetUserTask().execute(projectVO.getCREATE_USER());
		registerReceiver(userInfoReceiver, new IntentFilter(
				ACTION_UPDATE_IDCARD_IMG));
		initMenu();
	}
	
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
				if (headMenu != null && headMenu.isShowing()) {
					headMenu.dismiss();
				}
			}
		});
		headMenu = new PopupWindow(menuView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		headMenu.setBackgroundDrawable(getResources().getDrawable(
				R.color.transparent_lightslategray));
		headMenu.setAnimationStyle(R.style.bottommenu_anim_style);
		headMenu.setOutsideTouchable(false);
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
		case R.id.user_layout_idcard:
			if (userVO != null) {
				Intent intent = new Intent(this, TextEditActivity.class);
				intent.putExtra(CodeConstants.KEY_TITLE,
						getString(R.string.modify_user_idcard));
				intent.putExtra(CodeConstants.KEY_DEFAULT_TEXT, idcard
						.getText().toString());
				intent.putExtra(CodeConstants.KEY_HINT_TEXT,
						getString(R.string.hint_idcard));
				intent.putExtra(CodeConstants.KEY_SINGLELINE, true);
				intent.putExtra(CodeConstants.KEY_INPUT_TYPE,
						InputType.TYPE_NUMBER_FLAG_SIGNED);
				startActivityForResult(intent, REQUEST_USER_IDCARD);
			}
			break;
		case R.id.user_layout_idcard_img:
			if (userVO != null) {
				Intent intent = new Intent(this, IDCardActivity.class);
				intent.putExtra(CodeConstants.KEY_EDITABLE, true);
				intent.putExtra(CodeConstants.KEY_IDCARD_FRONT,
						userVO.getIDCARD_FRONT());
				intent.putExtra(CodeConstants.KEY_IDCARD_BACK,
						userVO.getIDCARD_BACK());
				intent.putExtra(CodeConstants.KEY_IDCARD_HAND,
						userVO.getIDCARD_HAND());
				intent.putExtra(CodeConstants.KEY_USER_ID, userVO.getUSER_ID());
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
				Intent rateIntent = new Intent(this, RatingActivity.class);
				rateIntent.putExtra(CodeConstants.KEY_USER_ID, userVO.getUSER_ID());
				rateIntent.putExtra(CodeConstants.KEY_PROJECT_VO, projectVO);
				startActivityForResult(rateIntent, REQUEST_RATE);
			}
			break;
		case R.id.user_layout_workspace:
			Intent intent = new Intent(this, WorkSpaceActivity.class);
			intent.putExtra(CodeConstants.KEY_PROJECT_ID,
					projectVO.getPROJECT_ID());
			for (ProjectVO currentPro : AEApp.getCurrentUser().getPROJECTS()) {
				if (currentPro.getPROJECT_ID().equals(projectID)) {
					intent.putExtra(CodeConstants.KEY_PROJECT_VO, currentPro);
				}
			}
			startActivity(intent);
			break;
		case R.id.user_layout_task:
			Intent taskIntent = new Intent(this, TaskActivity.class);
			taskIntent.putExtra(CodeConstants.KEY_PROJECT_VO, projectVO);
			startActivity(taskIntent);
			break;
		case R.id.user_layout_head:
			if (headMenu != null && !headMenu.isShowing()) {
				headMenu.showAtLocation(layoutHead, Gravity.BOTTOM, 0, 0);
			}
			break;
		case R.id.menu_photograph:
			if (headMenu != null && headMenu.isShowing()) {
				headMenu.dismiss();
			}
			setImgPath(
					FileCostants.DIR_HEAD + AEApp.getCurrentUser().getUSER_ID()
							+ "_" + System.currentTimeMillis() + ".jpg", true);
			AttchUtil.capture(this, getImgPath());
			break;
		case R.id.menu_album:
			if (headMenu != null && headMenu.isShowing()) {
				headMenu.dismiss();
			}
			setCompress(false);
			AttchUtil.getPictureFromGallery(this);
			break;
		case R.id.menu_cancel:
			if (headMenu != null && headMenu.isShowing()) {
				headMenu.dismiss();
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
			case REQUEST_USER_IDCARD:
				String idcardString = data
						.getStringExtra(CodeConstants.KEY_TEXT_RESULT);
				if (idcard.getText().toString().trim().equals(idcardString)) {
					break;
				}
				new UpdateIDCardTask().execute(idcardString);
				break;
			case REQUEST_PROJECT_NAME:
				String nameString = data
						.getStringExtra(CodeConstants.KEY_TEXT_RESULT);
				if (project.getText().toString().trim().equals(nameString)) {
					break;
				}
				new UpdateProjectTask().execute(nameString);
				changed = true;
				break;
			case REQUEST_RATE:
				new GetUserTask().execute(projectVO.getCREATE_USER());
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
	protected void onBackClick() {
		if (changed) {
			setResult(RESULT_OK);
		}
		super.onBackClick();
	}

	@Override
	public void onBackPressed() {
		if (headMenu != null && headMenu.isShowing()) {
			headMenu.dismiss();
			return;
		}
		if (changed) {
			setResult(RESULT_OK);
			finish();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(userInfoReceiver);
		super.onDestroy();
	}
	
	@Override
	protected void getImg(String path) {
		Uri outputUri = Uri.fromFile(new File(FileCostants.DIR_HEAD, AEApp
				.getCurrentUser().getUSER_ID()
				+ "_"
				+ System.currentTimeMillis()));
		new Crop(path).output(outputUri).asSquare().start(this);
	}

	private BroadcastReceiver userInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (userVO != null) {
				String idcard_img_front = intent
						.getStringExtra(CodeConstants.KEY_IDCARD_FRONT);
				String idcard_img_back = intent
						.getStringExtra(CodeConstants.KEY_IDCARD_BACK);
				String idcard_img_hand = intent
						.getStringExtra(CodeConstants.KEY_IDCARD_HAND);
				if (!StringUtil.isEmpty(idcard_img_front)) {
					userVO.setIDCARD_FRONT(idcard_img_front);
				}
				if (!StringUtil.isEmpty(idcard_img_back)) {
					userVO.setIDCARD_BACK(idcard_img_back);
				}
				if (!StringUtil.isEmpty(idcard_img_hand)) {
					userVO.setIDCARD_HAND(idcard_img_hand);
				}
			}
		}
	};

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

	private class CheckRateTask extends AsyncTask<Node, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(Node... params) {
			Node node = params[0];
			boolean canRate = false;
			while (node != null && node.getId() != null
					&& node.getParent() != null
					&& node.getParent().getId() != null
					&& !node.getParent().getId().equals(node.getId())
					&& !StringUtil.isEmpty(node.getParent().getId())) {
				if (node.getParent().getId().equals(projectID)) {
					canRate = true;
					break;
				}
				node = node.getParent();
			}
			return canRate;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result || projectVO.getPROJECT_ID().equals(projectID)) {
				layoutWorkSpace.setVisibility(View.VISIBLE);
				diverWorkSpace.setVisibility(View.VISIBLE);
				layoutHead.setOnClickListener(UserDetailActivity.this);
				userHeadIcon.setVisibility(View.VISIBLE);
			}
		}
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
//					String mIDCard = userVO.getIDCARD();
//					if (!StringUtil.isEmpty(mIDCard)) {
//						idcard.setText(mIDCard);
//						idCardIcon.setVisibility(View.INVISIBLE);
//					} else {
//						idCardIcon.setVisibility(View.VISIBLE);
//					}
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
		protected void onPreExecute() {
			super.onPreExecute();
			AEProgressDialog.showLoadingDialog(mContext);
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			String nameString = params[0];
			String param = "project_id=" + projectVO.getPROJECT_ID()
					+ "&project_name=" + nameString;
			HttpResult result = AEHttpUtil.doPost(
					URLConstants.URL_UPDATE_PROJECT_NAME, param);
			result.setRES_OBJ(nameString);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				project.setText(result.getRES_OBJ().toString());
				AEApp.getCurrentUser()
						.setIDCARD(result.getRES_OBJ().toString());
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

	private class UpdateIDCardTask extends
			AsyncTask<String, Integer, HttpResult> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			AEProgressDialog.showLoadingDialog(mContext);
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			String idcardString = params[0];
			String param = "user_id=" + userVO.getUSER_ID() + "&idcard="
					+ idcardString;
			HttpResult result = AEHttpUtil.doPost(
					URLConstants.URL_UPDATE_IDCARD, param);
			result.setRES_OBJ(idcardString);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				idcard.setText(result.getRES_OBJ().toString());
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
			param.put("user_id", projectVO.getCREATE_USER());
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
					imageLoader.displayImage(String.format(
							URLConstants.URL_IMG, vo.getURL()), head, options);
					if (projectVO.getPROJECT_ID().equals(projectID)) {
						AEApp.getCurrentUser().setBIG_HEAD(vo.getATTCH_ID());
						AEApp.getCurrentUser().setSMALL_HEAD(vo.getATTCH_ID());
						sendBroadcast(new Intent(
								SettingFragment.ACTION_USER_INFO_CHANGED));
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
}