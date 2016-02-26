package com.zzn.aeassistant.activity.attendance;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.view.CircleImageView;
import com.zzn.aeassistant.view.tree.Node;
import com.zzn.aeassistant.view.tree.TreeListViewAdapter;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.ProjectVO;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class LeafProjectActivity extends BaseActivity {
	private ListView mListView;
	private LeafProjectAdapter<ProjectVO> adapter;
	private ProjectVO projectVO;
	private ListLeafTask listLeafTask = null;
	private ScanningTask scanningTask = null;
	private List<String> selectDatas = new ArrayList<String>();
	private int selectMode = CodeConstants.STATUS_SCANNING_LEAF;

	@Override
	protected int layoutResID() {
		return R.layout.activity_base_pull_list;
	}

	@Override
	protected int titleStringID() {
		return R.string.scanning_leaf_project;
	}

	@Override
	protected void initView() {
		if (getIntent().hasExtra(CodeConstants.KEY_TITLE)) {
			title.setText(getIntent().getStringExtra(CodeConstants.KEY_TITLE));
		}
		if (getIntent().hasExtra(CodeConstants.KEY_SELECT_LEAF_MODE)) {
			selectMode = getIntent().getIntExtra(CodeConstants.KEY_SELECT_LEAF_MODE,
					CodeConstants.STATUS_SCANNING_LEAF);
		}
		projectVO = (ProjectVO) getIntent().getSerializableExtra(CodeConstants.KEY_PROJECT_VO);
		projectVO.setPARENT_ID(projectVO.getPROJECT_ID());
		projectVO.setROOT_ID(projectVO.getPROJECT_ID());
		if (selectMode == CodeConstants.STATUS_SCANNING_LEAF) {
			save.setVisibility(View.VISIBLE);
			save.setText(R.string.confirm);
		}
		mListView = (ListView) findViewById(R.id.base_list);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ProjectVO vo = (ProjectVO) adapter.getItem(position).getData();
				if (selectMode == CodeConstants.STATUS_SCANNING_LEAF) {
					if (selectDatas.contains(vo.getPROJECT_ID())) {
						selectDatas.remove(vo.getPROJECT_ID());
					} else {
						if (!vo.getPROJECT_ID().equals(projectVO.getPROJECT_ID())) {
							selectDatas.add(vo.getPROJECT_ID());
						}
					}
					adapter.notifyDataSetChanged();
				} else {
					Intent intent = new Intent();
					intent.putExtra(CodeConstants.KEY_PROJECT_VO, vo);
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});
		listLeafTask = new ListLeafTask();
		listLeafTask.execute(projectVO.getPROJECT_ID());
		AEProgressDialog.showLoadingDialog(mContext);
	}

	@Override
	protected void onSaveClick() {
		super.onSaveClick();
		if (selectDatas.size() == 0) {
			ToastUtil.show(R.string.null_leaf);
			return;
		}
		StringBuilder projectIds = new StringBuilder();
		for (String id : selectDatas) {
			projectIds.append(id + ",");
		}
		if (projectIds.length() > 0) {
			projectIds.deleteCharAt(projectIds.length() - 1);
		}
		scanningTask = new ScanningTask();
		scanningTask.execute(projectIds.toString());
	}

	@Override
	protected boolean needLocation() {
		return false;
	}

	@Override
	protected void onDestroy() {
		if (listLeafTask != null) {
			listLeafTask.cancel(true);
			listLeafTask = null;
		}
		if (scanningTask != null) {
			scanningTask.cancel(true);
			scanningTask = null;
		}
		super.onDestroy();
	}

	private class ListLeafTask extends AsyncTask<String, Integer, HttpResult> {

		@Override
		protected HttpResult doInBackground(String... params) {
			String project_id = params[0];
			String param = "project_id=" + project_id;
			HttpResult result = AEHttpUtil.doPost(URLConstants.URL_LEAF_PROJECT, param);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				if (result.getRES_OBJ() != null && !StringUtil.isEmpty(result.getRES_OBJ().toString())) {
					try {
						List<ProjectVO> projectList = GsonUtil.getInstance().fromJson(result.getRES_OBJ().toString(),
								new TypeToken<List<ProjectVO>>() {
								}.getType());
						projectList.add(0, projectVO);
						adapter = new LeafProjectAdapter<ProjectVO>(mListView, mContext, projectList, true,
								projectVO.getPROJECT_ID());
						mListView.setAdapter(adapter);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				ToastUtil.show(result.getRES_MESSAGE());
			}
		}

		@Override
		protected void onCancelled() {
			AEProgressDialog.dismissLoadingDialog();
			super.onCancelled();
		}
	}

	public class LeafProjectAdapter<T> extends TreeListViewAdapter<T> {
		private Context mContext;
		private String project_id;
		private ImageLoader imageLoader = ImageLoader.getInstance();
		private DisplayImageOptions options;

		public LeafProjectAdapter(ListView mTree, Context context, List<T> datas, boolean expand, String project_id)
				throws IllegalArgumentException, IllegalAccessException {
			super(mTree, context, datas, expand);
			this.mContext = context;
			this.project_id = project_id;
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

		@SuppressWarnings("unchecked")
		@Override
		public View getConvertView(Node node, int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_tree_list, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.icon = (ImageView) convertView.findViewById(R.id.treenode_icon);
				viewHolder.label = (TextView) convertView.findViewById(R.id.treenode_label);
				viewHolder.user = (TextView) convertView.findViewById(R.id.treenode_user);
				viewHolder.head = (CircleImageView) convertView.findViewById(R.id.treenode_head);
				viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.treenode_checkbox);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (node.getIcon() == -1) {
				viewHolder.icon.setVisibility(View.INVISIBLE);
			} else {
				viewHolder.icon.setVisibility(View.VISIBLE);
				viewHolder.icon.setImageResource(node.getIcon());
			}
			viewHolder.label.setText(node.getName());
			final ProjectVO vo = (ProjectVO) node.getData();
			viewHolder.checkBox.setVisibility(
					selectMode == CodeConstants.STATUS_SELECT_PROCESS_USER || vo.getPROJECT_ID().equals(project_id)
							? View.GONE : View.VISIBLE);
			viewHolder.checkBox.setOnCheckedChangeListener(null);
			viewHolder.checkBox
					.setChecked(!vo.getPROJECT_ID().equals(project_id) && selectDatas.contains(vo.getPROJECT_ID()));
			viewHolder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						if (!selectDatas.contains(vo.getPROJECT_ID())) {
							selectDatas.add(vo.getPROJECT_ID());
						}
					} else {
						if (selectDatas.contains(vo.getPROJECT_ID())) {
							selectDatas.remove(vo.getPROJECT_ID());
						}
					}
				}
			});
			viewHolder.user.setText(vo.getCREATE_USER_NAME());
			imageLoader.displayImage(String.format(URLConstants.URL_IMG, vo.getCREATE_USER_HEAD()), viewHolder.head,
					options);
			return convertView;
		}

		@Override
		public int getItemViewType(int position) {
			ProjectVO vo = (ProjectVO) getItem(position).getData();
			return vo.getPARENT_ID().equals(project_id) && !vo.getPROJECT_ID().equals(project_id) ? 0 : 1;
		}

		private final class ViewHolder {
			ImageView icon;
			CircleImageView head;
			TextView label;
			TextView user;
			CheckBox checkBox;
		}
	}

	/**
	 * 打卡异步任务
	 * 
	 * @author Shawn
	 *
	 */
	private class ScanningTask extends AsyncTask<String, Integer, HttpResult> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			AEProgressDialog.showLoadingDialog(mContext);
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			String param = "project_ids=" + params[0];
			return AEHttpUtil.doPost(URLConstants.URL_SCANNING_LEAF, param);
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			if (result != null && result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				ToastUtil.show(result.getRES_MESSAGE());
				finish();
			} else {
				ToastUtil.showImp(LeafProjectActivity.this, result.getRES_MESSAGE());
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			AEProgressDialog.dismissLoadingDialog();
		}
	}
}