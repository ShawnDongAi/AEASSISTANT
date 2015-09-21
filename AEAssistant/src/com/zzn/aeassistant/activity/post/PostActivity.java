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
import com.zzn.aeassistant.database.PostDBHelper;
import com.zzn.aeassistant.database.PostProvider;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.AttchUtil;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.view.AttachAdapter;
import com.zzn.aeassistant.view.HorizontalListView;
import com.zzn.aeassistant.vo.AttchVO;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.PostVO;
import com.zzn.aeassistant.vo.ProjectVO;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.CheckBox;
import android.widget.EditText;

public class PostActivity extends BaseActivity {
	private EditText content;
	private HorizontalListView attachGroup;
	private AttachAdapter adapter;
	private View photo, camera, voice, file, send;
	private CheckBox privateBox;
	private PostTask postTask;
	private ProjectVO project;
	private List<ProjectVO> sendProjectVOs = new ArrayList<>();

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
		project = (ProjectVO) getIntent().getSerializableExtra(CodeConstants.KEY_PROJECT_VO);
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
		privateBox = (CheckBox) findViewById(R.id.is_private);
		photo.setOnClickListener(this);
		camera.setOnClickListener(this);
		voice.setOnClickListener(this);
		file.setOnClickListener(this);
		send.setOnClickListener(this);
		content.addTextChangedListener(new TextWatcher() {
			@Override
			public synchronized void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public synchronized void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public synchronized void afterTextChanged(Editable s) {
				int position = content.getSelectionStart();
				if (position >= content.length()) {
					position = content.length() - 1;
				}
				if (position < 0) {
					position = 0;
				}
				if (s.toString().charAt(position) == '@') {
					// 选择联系人
					ProjectVO projectVO = new ProjectVO();
					projectVO.setPROJECT_NAME("test");
					sendProjectVOs.add(projectVO);
					content.getEditableText().insert(position + 1, projectVO.getPROJECT_NAME());
				}
			}
		});
		/** 监听删除按键，执行删除动作 */
		content.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) { // 当为删除键并且是按下动作时执行
					int selectionStart = content.getSelectionStart();
					int lastPos = 0;
					for (int i = 0; i < sendProjectVOs.size(); i++) { // 循环遍历整个输入框的所有字符
						if ((lastPos = content.getText().toString().indexOf(sendProjectVOs.get(i).getPROJECT_NAME(),
								lastPos)) != -1) {
							if (selectionStart != 0 && selectionStart >= lastPos
									&& selectionStart <= (lastPos + sendProjectVOs.get(i).getPROJECT_NAME().length())) {
								String sss = content.getText().toString();
								content.setText(sss.substring(0, lastPos)
										+ sss.substring(lastPos + sendProjectVOs.get(i).getPROJECT_NAME().length())); // 字符串替换，删掉符合条件的字符串
								sendProjectVOs.remove(i); // 删除对应实体
								content.setSelection(lastPos); // 设置光标位置
								return true;
							}
						} else {
							lastPos += ("@" + sendProjectVOs.get(i).getPROJECT_NAME()).length();
						}
					}
				}
				return false;
			}
		});
		content.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int selectionStart = ((EditText) v).getSelectionStart();
				int lastPos = 0;
				for (int i = 0; i < sendProjectVOs.size(); i++) {
					if ((lastPos = content.getText().toString().indexOf(sendProjectVOs.get(i).getPROJECT_NAME(),
							lastPos)) != -1) {
						if (selectionStart >= lastPos
								&& selectionStart <= (lastPos + sendProjectVOs.get(i).getPROJECT_NAME().length())) {
							content.setSelection(lastPos + sendProjectVOs.get(i).getPROJECT_NAME().length());
						}
					} else {
						lastPos += ("@" + sendProjectVOs.get(i).getPROJECT_NAME()).length();
					}
				}
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
		postTask = new PostTask();
		String is_private = privateBox.isChecked() ? "0" : "1";
		postTask.execute(content.getText().toString(), is_private);
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
			setImgPath(FileCostants.DIR_HEAD + AEApp.getCurrentUser().getUSER_ID() + "_" + System.currentTimeMillis()
					+ ".jpg", true);
			AttchUtil.capture(this, getImgPath());
			break;
		case R.id.voice:
			AttchUtil.record(this);
			break;
		case R.id.file:
			AttchUtil.getFile(this);
			break;
		case R.id.send:
			int position = content.getSelectionEnd() - 1;
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
		if (postTask != null) {
			postTask.cancel(true);
			postTask = null;
		}
		super.onDestroy();
	}

	@Override
	protected void getImg(String path) {
		super.getImg(path);
		new UploadFileTask().execute(path);
	}

	@Override
	protected void getFile(String path) {
		super.getFile(path);
		new UploadFileTask().execute(path);
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
			StringBuilder param = new StringBuilder();
			param.append("project=" + GsonUtil.getInstance().toJson(project));
			param.append("&is_private=" + params[1]);
			param.append("&send_project=" + GsonUtil.getInstance().toJson(sendProjectVOs));
			param.append("&attach_ids=" + adapter.getAttachIDs());
			param.append("&content=" + params[0]);
			return AEHttpUtil.doPost(URLConstants.URL_POST, param.toString());
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
				PostVO postVo = GsonUtil.getInstance().fromJson(result.getRES_OBJ().toString(), PostVO.class);
				ContentValues values = new ContentValues();
				values.put(PostDBHelper.POST_ID, postVo.getPost_id());
				values.put(PostDBHelper.USER_ID, postVo.getUser_id());
				values.put(PostDBHelper.USER_NAME, postVo.getUser_name());
				values.put(PostDBHelper.USER_HEAD, postVo.getUser_head());
				values.put(PostDBHelper.CONTENT, postVo.getContent());
				values.put(PostDBHelper.ATTCH_ID, postVo.getAttch_id());
				values.put(PostDBHelper.PROJECT_ID, postVo.getProject_id());
				values.put(PostDBHelper.PROJECT_NAME, postVo.getProject_name());
				values.put(PostDBHelper.ROOT_ID, postVo.getRoot_id());
				values.put(PostDBHelper.ROOT_PROJECT_NAME, postVo.getRoot_project_name());
				values.put(PostDBHelper.TIME, postVo.getTime());
				values.put(PostDBHelper.SEND_USER_ID, postVo.getSend_user_id());
				values.put(PostDBHelper.SEND_USER_NAME, postVo.getSend_user_name());
				values.put(PostDBHelper.SEND_PROJECT_ID, postVo.getSend_project_id());
				values.put(PostDBHelper.SEND_PROJECT_NAME, postVo.getSend_project_name());
				values.put(PostDBHelper.IS_PRIVATE, postVo.getIs_private());
				values.put(PostDBHelper.IS_NEW, "1");
				getContentResolver().insert(PostProvider.CONTENT_URI, values);
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
				adapter.addItem(vo);
				adapter.notifyDataSetChanged();
				attachGroup.setVisibility(View.VISIBLE);
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