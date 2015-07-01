package com.zzn.aeassistant.activity.user;

import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.constants.FileCostants;
import com.zzn.aeassistant.util.AttchUtil;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.view.CircleImageView;

public class UserActivity extends BaseActivity {
	public static final int REQUEST_PHOTOGRAPH = 0;
	public static final int REQUEST_ALBUM = 1;

	private View layoutHead, layoutName, layoutPhone, layoutSex,
			layoutRemark;
	private TextView name, phone, sex, remark;
	private CircleImageView head;
	private PopupWindow menu;
	private Button photograph, album, cancel;
	
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
		// head
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
				if (menu != null && menu.isShowing()) {
					menu.dismiss();
				}
			}
		});
		menu = new PopupWindow(menuView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		menu.setBackgroundDrawable(getResources().getDrawable(
				R.color.transparent));
		menu.setAnimationStyle(R.style.bottommenu_anim_style);
		menu.setOutsideTouchable(false);
		menu.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				WindowManager.LayoutParams params = getWindow()
						.getAttributes();
				params.alpha = 1.0f;
				getWindow().setAttributes(params);
			}
		});
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.user_layout_head:
			if (menu != null && !menu.isShowing()) {
				WindowManager.LayoutParams params = getWindow()
						.getAttributes();
				params.alpha = 0.7f;
				getWindow().setAttributes(params);
				menu.showAtLocation(layoutHead, Gravity.BOTTOM, 0, 0);
			}
			break;
		case R.id.user_layout_name:
			break;
		case R.id.user_layout_phone:
			break;
		case R.id.user_layout_sex:
			break;
		case R.id.user_layout_remark:
			break;
		case R.id.menu_photograph:
			if (menu != null && menu.isShowing()) {
				menu.dismiss();
			}
			setImgPath(FileCostants.DIR_HEAD + AEApp.getCurrentUser().getUSER_ID() + "_"
					+ System.currentTimeMillis() + ".jpg", false);
			AttchUtil.capture(this, getImgPath());
			break;
		case R.id.menu_album:
			if (menu != null && menu.isShowing()) {
				menu.dismiss();
			}
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
	protected void getImg(String path) {
//		head.setImageBitmap(bm);
	}

	@Override
	protected boolean needLocation() {
		return false;
	}
}
