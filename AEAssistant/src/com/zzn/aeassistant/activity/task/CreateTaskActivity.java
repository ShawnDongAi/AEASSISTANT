package com.zzn.aeassistant.activity.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.activity.attendance.LeafProjectActivity;
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
import com.zzn.aeassistant.view.AttachAdapter;
import com.zzn.aeassistant.view.AttachAdapter.OnAddAttachCallBack;
import com.zzn.aeassistant.view.AttachAdapter.OnDeleteListener;
import com.zzn.aeassistant.view.FastenGridView;
import com.zzn.aeassistant.vo.AttchVO;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.ProjectVO;
import com.zzn.aeassistant.vo.TaskDetailVO;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

public class CreateTaskActivity extends BaseActivity {
	private DatePickerDialog datePicker;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private ProjectVO project;
	private ListView listView;
	private EditTaskAdapter adapter;
	private int currentPos = 0;
	private CreateTask createTask;

	@Override
	protected int layoutResID() {
		return R.layout.activity_base_pull_list;
	}

	@Override
	protected int titleStringID() {
		return R.string.lable_task_new;
	}

	@Override
	protected void initView() {
		save.setVisibility(View.VISIBLE);
		save.setText(R.string.save);
		project = (ProjectVO) getIntent().getSerializableExtra(CodeConstants.KEY_PROJECT_VO);
		listView = (ListView) findViewById(R.id.base_list);
		listView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
		adapter = new EditTaskAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == adapter.getCount() - 1) {
					adapter.addItem();
				}
			}
		});
	}

	@Override
	protected void onSaveClick() {
		super.onSaveClick();
		List<TaskDetailVO> taskDetailList = new ArrayList<>();
		for (int i = 0; i < adapter.getCount(); i++) {
			TaskItem item = adapter.getItem(i);
			if (item.type == TaskItem.TYPE_DETAIL && !StringUtil.isEmpty(item.content)) {
				TaskDetailVO taskDetail = new TaskDetailVO();
				taskDetail.setProcess_user_id(item.processUser.getCREATE_USER());
				taskDetail.setProcess_project_id(item.processUser.getPROJECT_ID());
				taskDetail.setContent(item.content);
				taskDetail.setStart_time(item.startTime);
				taskDetail.setStatus("0");
				StringBuilder attachIDs = new StringBuilder();
				for (String attachID : item.attachList) {
					attachIDs.append(attachID + ",");
				}
				if (attachIDs.length() > 0) {
					attachIDs.deleteCharAt(attachIDs.length() - 1);
				}
				taskDetail.setAttch_id(attachIDs.toString());
				taskDetailList.add(taskDetail);
			}
		}
		if (taskDetailList.size() == 0) {
			ToastUtil.show(R.string.toast_empty_task_content);
			return;
		}
		// 开始提交任务
		if (createTask != null) {
			createTask.cancel(true);
			createTask = null;
		}
		createTask = new CreateTask();
		createTask.execute(project.getCREATE_USER(), project.getPROJECT_ID(), project.getROOT_ID(),
				GsonUtil.getInstance().toJson(taskDetailList));
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CodeConstants.REQUEST_CODE_PROJECT:
				ProjectVO processUser = (ProjectVO) data.getSerializableExtra(CodeConstants.KEY_PROJECT_VO);
				adapter.getItem(currentPos).processUser = processUser;
				adapter.notifyDataSetChanged();
				break;
			}
		}
	}

	@Override
	protected boolean needLocation() {
		return false;
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
	protected void onDestroy() {
		if (createTask != null) {
			createTask.cancel(true);
			createTask = null;
		}
		super.onDestroy();
	}

	private class EditTaskAdapter extends BaseAdapter {
		private List<TaskItem> datas = new ArrayList<>();

		public EditTaskAdapter() {
			TaskItem item = new TaskItem(TaskItem.TYPE_DETAIL);
			item.processUser = project;
			Calendar now = Calendar.getInstance();
			String currentDate = dateFormat.format(now.getTime());
			item.startTime = currentDate;
			datas.add(item);
			datas.add(new TaskItem(TaskItem.TYPE_ADD));
		}

		private void addItem() {
			TaskItem lastItem = getItem(datas.size() - 2);
			if (StringUtil.isEmpty(lastItem.content)) {
				ToastUtil.show(R.string.toast_empty_task_content);
				return;
			}
			if (getCount() > 10) {
				ToastUtil.show(R.string.toast_out_of_ten_task);
				return;
			}
			TaskItem item = new TaskItem(TaskItem.TYPE_DETAIL);
			item.processUser = project;
			Calendar now = Calendar.getInstance();
			String currentDate = dateFormat.format(now.getTime());
			item.startTime = currentDate;
			datas.add(datas.size() - 1, item);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public TaskItem getItem(int position) {
			return datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			return getItem(position).type;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			TaskItem item = getItem(position);
			if (item.type == TaskItem.TYPE_ADD) {
				AddHolder holder;
				if (convertView == null || !(convertView.getTag() instanceof AddHolder)) {
					holder = new AddHolder();
					convertView = View.inflate(mContext, R.layout.item_add, null);
					holder.add = (Button) convertView.findViewById(R.id.add);
					convertView.setTag(holder);
				} else {
					holder = (AddHolder) convertView.getTag();
				}
				holder.add.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						addItem();
					}
				});
			} else {
				ViewHolder holder;
				if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
					holder = new ViewHolder();
					convertView = View.inflate(mContext, R.layout.item_task_detail_edit, null);
					holder.user = (Button) convertView.findViewById(R.id.task_process_user);
					holder.time = (Button) convertView.findViewById(R.id.task_start_time);
					holder.content = (EditText) convertView.findViewById(R.id.task_create_content);
					holder.attach = (FastenGridView) convertView.findViewById(R.id.task_create_attach_list);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				holder.user.setOnClickListener(null);
				holder.content.removeTextChangedListener((TextWatcher) holder.content.getTag());
				holder.time.setOnClickListener(null);
				holder.user.setText(item.processUser.getCREATE_USER_NAME());
				holder.user.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						currentPos = position;
						Intent intent = new Intent(mContext, LeafProjectActivity.class);
						intent.putExtra(CodeConstants.KEY_PROJECT_VO, project);
						intent.putExtra(CodeConstants.KEY_TITLE, getString(R.string.lable_select_process_user));
						intent.putExtra(CodeConstants.KEY_SELECT_LEAF_MODE, CodeConstants.STATUS_SELECT_PROCESS_USER);
						startActivityForResult(intent, CodeConstants.REQUEST_CODE_PROJECT);
					}
				});
				holder.content.setText(item.content);
				ContentWatcher watcher = new ContentWatcher(position);
				holder.content.addTextChangedListener(watcher);
				holder.content.setTag(watcher);
				holder.time.setText(item.startTime);
				holder.time.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						selectDate((Button) v);
					}
				});
				AttachAdapter attachAdapter;
				if (holder.attach.getTag() == null) {
					attachAdapter = new AttachAdapter(mContext, true, new OnAddAttachCallBack() {
						@Override
						public void onAddPhoto() {
							currentPos = position;
							setCompress(true);
							setImgPath(FileCostants.DIR_HEAD + AEApp.getCurrentUser().getUSER_ID() + "_"
									+ System.currentTimeMillis() + ".jpg", true);
							AttchUtil.getPictureFromGallery(mContext);
						}

						@Override
						public void onAddCamera() {
							currentPos = position;
							setCompress(true);
							setImgPath(FileCostants.DIR_HEAD + AEApp.getCurrentUser().getUSER_ID() + "_"
									+ System.currentTimeMillis() + ".jpg", true);
							AttchUtil.capture(mContext, getImgPath());
						}
					});
				} else {
					attachAdapter = (AttachAdapter) holder.attach.getTag();
					attachAdapter.clear();
				}
				attachAdapter.setOnDeleteListener(new OnDeleteListener() {
					@Override
					public void onDelete(int position) {
						getItem(position).attachList.remove(position);
					}
				});
				if (item.attachList != null && item.attachList.size() > 0) {
					for (String id : item.attachList) {
						if (!StringUtil.isEmpty(id)) {
							AttchVO vo = new AttchVO();
							vo.setATTCH_ID(id);
							vo.setTYPE(AttchVO.TYPE_IMG);
							attachAdapter.addItem(vo);
						}
					}
				}
				holder.attach.setAdapter(attachAdapter);
				holder.attach.setTag(attachAdapter);
				holder.attach.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						if (parent.getTag() != null && parent.getTag() instanceof AttachAdapter) {
							((AttachAdapter) parent.getTag()).onItemClick(position);
						}
					}
				});
				holder.attach.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
						if (parent.getTag() != null && parent.getTag() instanceof AttachAdapter) {
							((AttachAdapter) parent.getTag()).onItemLongClick();
						}
						return true;
					}
				});
			}
			return convertView;
		}
	}

	class ViewHolder {
		Button user;
		EditText content;
		Button time;
		FastenGridView attach;
	}

	class AddHolder {
		Button add;
	}

	class TaskItem {
		public static final int TYPE_ADD = 0;
		public static final int TYPE_DETAIL = 1;
		public int type = TYPE_DETAIL;
		public ProjectVO processUser;
		public String startTime;
		public String content = "";
		public List<String> attachList = new ArrayList<>();

		public TaskItem(int type) {
			this.type = type;
		}
	}

	class ContentWatcher implements TextWatcher {
		private int pos;

		public ContentWatcher(int position) {
			pos = position;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			adapter.getItem(pos).content = s.toString();
		}
	}

	private void selectDate(final Button button) {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(dateFormat.parse(button.getText().toString()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (datePicker != null) {
			datePicker.dismiss();
			datePicker = null;
		}
		datePicker = new DatePickerDialog(mContext, new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(year, monthOfYear, dayOfMonth);
				button.setText(dateFormat.format(calendar.getTime()));
			}
		}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		datePicker.setTitle(R.string.select_date);
		datePicker.show();
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
				adapter.getItem(currentPos).attachList.add(vo.getATTCH_ID());
				adapter.notifyDataSetChanged();
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

	private class CreateTask extends AsyncTask<String, Integer, HttpResult> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			AEProgressDialog.showLoadingDialog(mContext);
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			String create_user_id = params[0];
			String create_project_id = params[1];
			String root_id = params[2];
			String taskDetails = params[3];
			StringBuilder param = new StringBuilder();
			param.append("create_user_id=" + create_user_id);
			param.append("&create_project_id=" + create_project_id);
			param.append("&root_id=" + root_id);
			param.append("&taskDetails=" + taskDetails);
			return AEHttpUtil.doPost(URLConstants.URL_CREATE_TASK, param.toString());
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			ToastUtil.show(result.getRES_MESSAGE());
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				setResult(RESULT_OK);
				finish();
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			AEProgressDialog.dismissLoadingDialog();
		}
	}
}