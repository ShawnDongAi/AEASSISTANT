package com.zzn.aeassistant.activity.post;

import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.constants.FileCostants;
import com.zzn.aeassistant.util.AttchUtil;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.view.AttachAdapter;
import com.zzn.aeassistant.view.HorizontalListView;
import com.zzn.aeassistant.vo.HttpResult;

public class PostActivity extends BaseActivity {
	private EditText content;
	private HorizontalListView attachGroup;
	private AttachAdapter adapter;
	private View photo, camera, voice, file, send;
	private PostTask postTask;

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
		attachGroup = (HorizontalListView) findViewById(R.id.attch_list);
		adapter = new AttachAdapter(this, true);
		attachGroup.setAdapter(adapter);
		photo = findViewById(R.id.photo);
		camera = findViewById(R.id.camera);
		voice = findViewById(R.id.voice);
		file = findViewById(R.id.file);
		send = findViewById(R.id.send);
		photo.setOnClickListener(this);
		camera.setOnClickListener(this);
		voice.setOnClickListener(this);
		file.setOnClickListener(this);
		send.setOnClickListener(this);
	}

	@Override
	protected void onSaveClick() {
		super.onSaveClick();
		if (StringUtil.isEmpty(content.getText().toString().trim())) {
			ToastUtil.show(content.getHint().toString());
			content.requestFocus();
			return;
		}
		postTask = new PostTask();
		postTask.execute(content.getText().toString());
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
		case R.id.voice:
			AttchUtil.record(this);
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
	protected void onDestroy() {
		if (postTask != null) {
			postTask.cancel(true);
			postTask = null;
		}
		super.onDestroy();
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

	private class PostTask extends AsyncTask<String, Integer, HttpResult> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			AEProgressDialog.showLoadingDialog(mContext);
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			AEProgressDialog.dismissLoadingDialog();
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
		}
	}
}