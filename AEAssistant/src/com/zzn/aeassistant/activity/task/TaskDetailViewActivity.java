package com.zzn.aeassistant.activity.task;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.view.AttachAdapter;
import com.zzn.aeassistant.view.CircleImageView;
import com.zzn.aeassistant.view.FastenGridView;
import com.zzn.aeassistant.vo.AttchVO;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.TaskDetailVO;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class TaskDetailViewActivity extends BaseActivity implements OnItemClickListener {
	private TextView mRootName, mCreateUser, mProcessUser, mContent, mStartTime, mProcessContent, mEndTime, mStatus;
	private FastenGridView mCreateAttach, mProcessAttach;
	private AttachAdapter mCreateAttachAdapter, mProcessAttachAdapter;
	private CircleImageView mCreateUserHead, mProcessUserHead;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private View mEndTimeLayout;
	private TaskDetailVO taskDetail;

	@Override
	protected int layoutResID() {
		return R.layout.activity_task_view;
	}

	@Override
	protected int titleStringID() {
		return R.string.lable_task_detail;
	}

	@Override
	protected void initView() {
		taskDetail = (TaskDetailVO) getIntent().getSerializableExtra(CodeConstants.KEY_TASK_DETAIL);
		if (taskDetail.getCreate_user_id().equals(AEApp.getCurrentUser().getUSER_ID())) {
			save.setVisibility(View.VISIBLE);
			save.setText(R.string.delete);
		}
		mRootName = (TextView) findViewById(R.id.task_root);
		mCreateUser = (TextView) findViewById(R.id.task_create_user);
		mCreateUserHead = (CircleImageView) findViewById(R.id.task_create_user_head);
		mProcessUser = (TextView) findViewById(R.id.task_process_user);
		mProcessUserHead = (CircleImageView) findViewById(R.id.task_process_user_head);
		mContent = (TextView) findViewById(R.id.task_create_content);
		mStartTime = (TextView) findViewById(R.id.task_start_time);
		mCreateAttach = (FastenGridView) findViewById(R.id.task_create_attach_list);
		mProcessContent = (TextView) findViewById(R.id.task_process_content);
		mEndTime = (TextView) findViewById(R.id.task_end_time);
		mProcessAttach = (FastenGridView) findViewById(R.id.task_process_attach_list);
		mEndTimeLayout = findViewById(R.id.task_end_time_layout);
		mStatus = (TextView) findViewById(R.id.status);
		mRootName.setText(taskDetail.getRoot_project_name());
		mCreateUser.setText(taskDetail.getCreate_user_name());
		mProcessUser.setText(taskDetail.getProcess_user_name());
		mContent.setText(taskDetail.getContent());
		mStartTime.setText(taskDetail.getStart_time());
		mProcessContent.setText(taskDetail.getProcess_content());
		mEndTime.setText(taskDetail.getEnd_time());
		if (!StringUtil.isEmpty(taskDetail.getEnd_time())) {
			mEndTimeLayout.setVisibility(View.VISIBLE);
		}
		if (taskDetail.getStatus().equals("0")) {
			mStatus.setText(R.string.task_status_pending_confirmation);
			mStatus.setBackgroundResource(R.color.red);
		} else if (taskDetail.getStatus().equals("1")) {
			mStatus.setText(R.string.task_status_completed);
			mStatus.setBackgroundResource(R.color.theme_green);
		} else {
			mStatus.setText(R.string.task_status_no_completed);
			mStatus.setBackgroundResource(R.color.darkgoldenrod);
		}
		initImageLoader();
		if (!StringUtil.isEmpty(taskDetail.getCreate_user_head())) {
			imageLoader.displayImage(String.format(URLConstants.URL_IMG, taskDetail.getCreate_user_head()),
					mCreateUserHead, options);
		}
		if (!StringUtil.isEmpty(taskDetail.getProcess_user_head())) {
			imageLoader.displayImage(String.format(URLConstants.URL_IMG, taskDetail.getProcess_user_head()),
					mProcessUserHead, options);
		}
		mCreateAttachAdapter = new AttachAdapter(mContext, false, null);
		String[] mCreateAttachArray = taskDetail.getAttch_id().split(",");
		for (String attachID : mCreateAttachArray) {
			if (!StringUtil.isEmpty(attachID)) {
				AttchVO vo = new AttchVO();
				vo.setATTCH_ID(attachID);
				vo.setTYPE(AttchVO.TYPE_IMG);
				mCreateAttachAdapter.addItem(vo);
			}
		}
		if (mCreateAttachAdapter.getCount() > 0) {
			mCreateAttach.setVisibility(View.VISIBLE);
			mCreateAttach.setAdapter(mCreateAttachAdapter);
			mCreateAttach.setOnItemClickListener(this);
		}
		mProcessAttachAdapter = new AttachAdapter(mContext, false, null);
		String[] mProcessAttachArray = taskDetail.getProcess_attch_id().split(",");
		for (String attachID : mProcessAttachArray) {
			if (!StringUtil.isEmpty(attachID)) {
				AttchVO vo = new AttchVO();
				vo.setATTCH_ID(attachID);
				vo.setTYPE(AttchVO.TYPE_IMG);
				mProcessAttachAdapter.addItem(vo);
			}
		}
		if (mProcessAttachAdapter.getCount() > 0) {
			mProcessAttach.setVisibility(View.VISIBLE);
			mProcessAttach.setAdapter(mProcessAttachAdapter);
			mProcessAttach.setOnItemClickListener(this);
		}
	}

	@SuppressWarnings("deprecation")
	private void initImageLoader() {
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_head) // 设置图片在下载期间显示的图片
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

	@Override
	protected void onSaveClick() {
		super.onSaveClick();
		new AlertDialog.Builder(mContext).setTitle(R.string.warning).setMessage(R.string.info_delete_task)
				.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new DeleteTask().execute(taskDetail.getTask_id(), taskDetail.getTask_detail_id());
					}
				}).setNegativeButton(R.string.cancel, null).show();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent.equals(mCreateAttach)) {
			mCreateAttachAdapter.onItemClick(position);
		} else if (parent.equals(mProcessAttach)) {
			mProcessAttachAdapter.onItemClick(position);
		}
	}

	private class DeleteTask extends AsyncTask<String, Integer, HttpResult> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			AEProgressDialog.showLoadingDialog(mContext);
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			String task_id = params[0];
			String task_detail_id = params[1];
			StringBuilder param = new StringBuilder();
			param.append("task_id=" + task_id);
			param.append("&task_detail_id=" + task_detail_id);
			return AEHttpUtil.doPost(URLConstants.URL_DELETE_TASK, param.toString());
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