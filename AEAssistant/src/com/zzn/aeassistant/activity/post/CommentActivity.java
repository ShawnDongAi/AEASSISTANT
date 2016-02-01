package com.zzn.aeassistant.activity.post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.FileCostants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.database.CommentDBHelper;
import com.zzn.aeassistant.database.CommentProvider;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.AttchUtil;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.view.AttachAdapter;
import com.zzn.aeassistant.view.AttachAdapter.OnAddAttachCallBack;
import com.zzn.aeassistant.view.FastenGridView;
import com.zzn.aeassistant.vo.AttchVO;
import com.zzn.aeassistant.vo.CommentVO;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.ProjectVO;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;

public class CommentActivity extends BaseActivity {
	private EditText content;
	private FastenGridView attachGroup;
	private AttachAdapter adapter;
	private View photo, camera, voice, file;
	private CommentTask commentTask;
	private String post_id;
	private ProjectVO project;
	private String currentPath = "";

	@Override
	protected int layoutResID() {
		return R.layout.activity_comment;
	}

	@Override
	protected int titleStringID() {
		return R.string.comment;
	}

	@Override
	protected void initView() {
		post_id = getIntent().getStringExtra(CodeConstants.KEY_POST_ID);
		project = (ProjectVO) getIntent().getSerializableExtra(CodeConstants.KEY_PROJECT_VO);
		save.setText(R.string.comment);
		save.setVisibility(View.VISIBLE);
		content = (EditText) findViewById(R.id.input_comment);
		attachGroup = (FastenGridView) findViewById(R.id.attch_list);
		adapter = new AttachAdapter(this, true, new OnAddAttachCallBack() {
			@Override
			public void onAddPhoto() {
				if (adapter.getCount() >= 9) {
					ToastUtil.show(R.string.much_file);
					return;
				}
				setCompress(true);
				AttchUtil.getPictureFromGallery(mContext);
			}
			@Override
			public void onAddCamera() {
				if (adapter.getCount() >= 9) {
					ToastUtil.show(R.string.much_file);
					return;
				}
				setCompress(true);
				setImgPath(FileCostants.DIR_HEAD + AEApp.getCurrentUser().getUSER_ID() + "_" + System.currentTimeMillis()
						+ ".jpg", true);
				AttchUtil.capture(mContext, getImgPath());
			}
		});
		attachGroup.setAdapter(adapter);
		photo = findViewById(R.id.photo);
		camera = findViewById(R.id.camera);
		voice = findViewById(R.id.voice);
		file = findViewById(R.id.file);
		photo.setOnClickListener(this);
		camera.setOnClickListener(this);
		voice.setOnClickListener(this);
		file.setOnClickListener(this);
		attachGroup.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				adapter.onItemClick(position);
			}
		});
		attachGroup.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				adapter.onItemLongClick();
				return true;
			}
		});
	}

	@Override
	protected void onSaveClick() {
		super.onSaveClick();
		if (StringUtil.isEmpty(content.getText().toString().trim())) {
			ToastUtil.show(content.getHint().toString());
			content.requestFocus();
			return;
		}
		commentTask = new CommentTask();
		commentTask.execute(content.getText().toString(), post_id);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.photo:
			if (adapter.getCount() >= 9) {
				ToastUtil.show(R.string.much_file);
				return;
			}
			setCompress(true);
			AttchUtil.getPictureFromGallery(this);
			break;
		case R.id.camera:
			if (adapter.getCount() >= 9) {
				ToastUtil.show(R.string.much_file);
				return;
			}
			setImgPath(FileCostants.DIR_HEAD + AEApp.getCurrentUser().getUSER_ID() + "_" + System.currentTimeMillis()
					+ ".jpg", true);
			AttchUtil.capture(this, getImgPath());
			break;
		case R.id.voice:
			if (adapter.getCount() >= 9) {
				ToastUtil.show(R.string.much_file);
				return;
			}
			AttchUtil.record(this);
			break;
		case R.id.file:
			if (adapter.getCount() >= 9) {
				ToastUtil.show(R.string.much_file);
				return;
			}
			AttchUtil.getFile(this);
			break;
		case R.id.send:
			int position = content.getSelectionEnd();
			if (position < 0) {
				position = 0;
			}
			content.getEditableText().insert(position, "@");
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		if (commentTask != null) {
			commentTask.cancel(true);
			commentTask = null;
		}
		super.onDestroy();
	}

	@Override
	protected void getImg(String path) {
		super.getImg(path);
		currentPath = path;
		new UploadFileTask().execute(path);
	}

	@Override
	protected void getFile(String path) {
		super.getFile(path);
		currentPath = path;
		new UploadFileTask().execute(path);
	}

	@Override
	protected boolean needLocation() {
		return false;
	}

	private class CommentTask extends AsyncTask<String, Integer, HttpResult> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			AEProgressDialog.showLoadingDialog(mContext);
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			StringBuilder param = new StringBuilder();
			param.append("project=" + GsonUtil.getInstance().toJson(project));
			param.append("&attach_ids=" + adapter.getAttachIDs());
			param.append("&content=" + params[0]);
			param.append("&post_id=" + params[1]);
			return AEHttpUtil.doPost(URLConstants.URL_COMMENT, param.toString());
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
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				CommentVO commentVo = GsonUtil.getInstance().fromJson(result.getRES_OBJ().toString(), CommentVO.class);
				ContentValues values = new ContentValues();
				values.put(CommentDBHelper.COMMENT_ID, commentVo.getComment_id());
				values.put(CommentDBHelper.POST_ID, commentVo.getPost_id());
				values.put(CommentDBHelper.USER_ID, commentVo.getUser_id());
				values.put(CommentDBHelper.USER_NAME, commentVo.getUser_name());
				values.put(CommentDBHelper.USER_HEAD, commentVo.getUser_head());
				values.put(CommentDBHelper.CONTENT, commentVo.getContent());
				values.put(CommentDBHelper.ATTCH_ID, commentVo.getAttch_id());
				values.put(CommentDBHelper.PROJECT_ID, commentVo.getProject_id());
				values.put(CommentDBHelper.PROJECT_NAME, commentVo.getProject_name());
				values.put(CommentDBHelper.ROOT_ID, commentVo.getRoot_id());
				values.put(CommentDBHelper.TIME, commentVo.getTime());
				values.put(CommentDBHelper.IS_NEW, "1");
				values.put(CommentDBHelper.CURRENT_PROJECT, project.getPROJECT_ID());
				getContentResolver().insert(CommentProvider.CONTENT_URI, values);
				finish();
			} else {
				ToastUtil.show(result.getRES_MESSAGE());
			}
		}
	}

	private class UploadFileTask extends AsyncTask<String, Integer, HttpResult> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			AEProgressDialog.showLoadingDialog(mContext);
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			List<String> files = new ArrayList<>();
			for (String path : params) {
				files.add(path);
			}
			return AEHttpUtil.doPostWithFile(URLConstants.URL_UPLOAD_FILE, files, new HashMap<String, String>());
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				AttchVO vo = GsonUtil.getInstance().fromJson(result.getRES_OBJ().toString(), AttchVO.class);
				vo.setLOCAL_PATH(currentPath);
				adapter.addItem(vo);
				adapter.notifyDataSetChanged();
				attachGroup.setVisibility(View.VISIBLE);
			} else {
				ToastUtil.show(result.getRES_MESSAGE());
			}
			currentPath = "";
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			AEProgressDialog.dismissLoadingDialog();
		}
	}
}