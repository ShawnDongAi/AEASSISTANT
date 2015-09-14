package com.zzn.aeassistant.activity.post;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.constants.FileCostants;
import com.zzn.aeassistant.util.AttchUtil;

public class PostActivity extends BaseActivity {
	private EditText content;
	private LinearLayout attachGroup;
	private View photo, camera, file, send;

	@Override
	protected int layoutResID() {
		return R.layout.activity_post;
	}

	@Override
	protected int titleStringID() {
		return R.string.post;
	}

	@Override
	protected void initView() {
		save.setText(R.string.send);
		save.setVisibility(View.VISIBLE);
		content = (EditText) findViewById(R.id.input_post);
		attachGroup = (LinearLayout) findViewById(R.id.attch_list);
		photo = findViewById(R.id.photo);
		camera = findViewById(R.id.camera);
		file = findViewById(R.id.file);
		send = findViewById(R.id.send);
		photo.setOnClickListener(this);
		camera.setOnClickListener(this);
		file.setOnClickListener(this);
		send.setOnClickListener(this);
	}

	@Override
	protected void onSaveClick() {
		super.onSaveClick();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.photo:
			setCompress(true);
			AttchUtil.getPictureFromGallery(this);
			break;
		case R.id.camera:
			setImgPath(
					FileCostants.DIR_HEAD + AEApp.getCurrentUser().getUSER_ID()
							+ "_" + System.currentTimeMillis() + ".jpg", true);
			AttchUtil.capture(this, getImgPath());
			break;
		case R.id.file:
			AttchUtil.getFile(this);
			break;
		case R.id.send:
			break;
		default:
			break;
		}
	}

	@Override
	protected void getImg(String path) {
		super.getImg(path);
	}

	@Override
	protected void getFile(String path) {
		super.getFile(path);
	}

	@Override
	protected boolean needLocation() {
		return false;
	}
}
