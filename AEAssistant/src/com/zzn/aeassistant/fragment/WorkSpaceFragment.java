package com.zzn.aeassistant.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.IndexActivity;
import com.zzn.aeassistant.activity.IndexActivity.SaveClickListener;
import com.zzn.aeassistant.activity.post.PostActivity;
import com.zzn.aeassistant.activity.post.PostAdapter;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.database.CommentDBHelper;
import com.zzn.aeassistant.database.CommentProvider;
import com.zzn.aeassistant.database.PostDBHelper;
import com.zzn.aeassistant.database.PostProvider;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.util.ToolsUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.Mode;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshListView;
import com.zzn.aeassistant.vo.CommentVO;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.PostVO;
import com.zzn.aeassistant.vo.ProjectVO;

/**
 * 工作圈
 * 
 * @author Shawn
 */
public class WorkSpaceFragment extends BaseFragment {
	private TextView projectTitle;
	private PullToRefreshListView mListView;
	private PostAdapter adapter;
	private boolean hasMore = true;
	private boolean dbHasMore = true;
	private InitProjectTask initProTask;
	private ProjectVO project = null;
	private PopupWindow projectMenu;
	private ListView projectList;
	private ProListAdapter proListAdapter;
	private RefreshPostTask postTask;
	private RefreshTask refreshTask;

	@Override
	protected int layoutResID() {
		return R.layout.fragment_work_space;
	}

	private PostObserver observer = new PostObserver(new Handler());
	private CommentObserver commentObserver = new CommentObserver(new Handler());

	private class PostObserver extends ContentObserver {
		public PostObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			if (project != null) {
				refreshFromDB("", true, true);
			}
		}
	}

	private class CommentObserver extends ContentObserver {
		public CommentObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			if (project != null) {
				refreshFromDB("", true, true);
			}
		}
	}

	@Override
	protected void initView(View container) {
		projectTitle = (TextView) container.findViewById(R.id.workspace_project);
		projectTitle.setOnClickListener(this);
		mListView = (PullToRefreshListView) container.findViewById(R.id.base_list);
		mListView.setEmptyView(View.inflate(mContext, R.layout.list_empty_view, null));
		adapter = new PostAdapter(mContext);
		mListView.setAdapter(adapter);
		((IndexActivity) getActivity()).setOnSaveClickListener(new SaveClickListener() {
			@Override
			public void onSaveClick() {
				if (project != null) {
					Intent intent = new Intent(mContext, PostActivity.class);
					intent.putExtra(CodeConstants.KEY_PROJECT_VO, project);
					startActivity(intent);
				}
			}
		});
		initMenuView();
		initPullToRefresh();
		getActivity().getContentResolver().registerContentObserver(PostProvider.CONTENT_URI, true, observer);
		getActivity().getContentResolver().registerContentObserver(CommentProvider.CONTENT_URI, true, commentObserver);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (project == null) {
			initProTask = new InitProjectTask();
			initProTask.execute();
		}
	}

	private void initMenuView() {
		View menuView = View.inflate(mContext, R.layout.menu_list, null);
		projectList = (ListView) menuView.findViewById(R.id.menu_list);
		proListAdapter = new ProListAdapter(mContext, AEApp.getCurrentUser().getPROJECTS());
		projectList.setAdapter(proListAdapter);
		projectMenu = new PopupWindow(menuView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		projectMenu.setBackgroundDrawable(getResources().getDrawable(R.color.transparent_lightslategray));
		projectMenu.setOutsideTouchable(true);
		projectMenu.setFocusable(true);
		projectList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (projectMenu != null && projectMenu.isShowing()) {
					projectMenu.dismiss();
				}
				project = proListAdapter.getItem(position);
				projectTitle.setText(project.getROOT_PROJECT_NAME() + "-" + project.getPROJECT_NAME());
				adapter.setProject(project);
				adapter.clear();
				adapter.notifyDataSetChanged();
				refreshFromDB("", true, true);
			}
		});
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.workspace_project:
			if (!projectMenu.isShowing()) {
				projectMenu.showAsDropDown(projectTitle);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroyView() {
		getActivity().getContentResolver().unregisterContentObserver(observer);
		getActivity().getContentResolver().unregisterContentObserver(commentObserver);
		if (initProTask != null) {
			initProTask.cancel(true);
			initProTask = null;
		}
		super.onDestroyView();
	}

	private void initPullToRefresh() {
		mListView.setMode(Mode.PULL_FROM_START);
		mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(final PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(AEApp.getInstance(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(label);
				if (postTask != null) {
					postTask.cancel(true);
				}
				postTask = new RefreshPostTask(true);
				postTask.execute(project.getPROJECT_ID(), "");
				hasMore = true;
			}

			@Override
			public void onPullUpToRefresh(final PullToRefreshBase<ListView> refreshView) {
				if (hasMore) {
					if (dbHasMore && adapter.getCount() > 0) {
						refreshFromDB(adapter.getItem(adapter.getCount() - 1).getTime(), false, true);
						return;
					}
					if (postTask != null) {
						postTask.cancel(true);
					}
					if (adapter.getCount() > 0) {
						postTask = new RefreshPostTask(false);
						postTask.execute(project.getPROJECT_ID(), adapter.getItem(adapter.getCount() - 1).getTime());
					}
				} else {
					refreshView.onRefreshComplete();
				}
			}
		});
	}

	private class InitProjectTask extends AsyncTask<ProjectVO, Integer, ProjectVO> {

		@Override
		protected ProjectVO doInBackground(ProjectVO... params) {
			ProjectVO currentProject = null;
			if (AEApp.getCurrentLoc() == null) {
				if (AEApp.getCurrentUser().getPROJECTS() != null && AEApp.getCurrentUser().getPROJECTS().size() > 0) {
					currentProject = AEApp.getCurrentUser().getPROJECTS().get(0);
				}
			} else {
				double currentLatitude = AEApp.getCurrentLoc().getLatitude();
				double currentLongitude = AEApp.getCurrentLoc().getLongitude();
				if (AEApp.getCurrentUser().getPROJECTS() != null && AEApp.getCurrentUser().getPROJECTS().size() > 0) {
					currentProject = AEApp.getCurrentUser().getPROJECTS().get(0);
				}
				for (ProjectVO projectVO : AEApp.getCurrentUser().getPROJECTS()) {
					double proLatitude = Double.parseDouble(projectVO.getLATITUDE());
					double proLongitude = Double.parseDouble(projectVO.getLONGITUDE());
					if (ToolsUtil.getDistance(currentLongitude, currentLatitude, proLongitude, proLatitude) < 500) {
						currentProject = projectVO;
						break;
					}
				}
			}
			return currentProject;
		}

		@Override
		protected void onPostExecute(ProjectVO result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			if (result == null) {
				projectTitle.setText(R.string.null_project);
				mListView.setMode(Mode.DISABLED);
				return;
			}
			project = result;
			adapter.setProject(project);
			projectTitle.setText(project.getROOT_PROJECT_NAME() + "-" + project.getPROJECT_NAME());
			if (refreshTask != null) {
				refreshTask.cancel(true);
				refreshTask = null;
			}
			refreshTask = new RefreshTask(true);
			refreshTask.execute(project.getPROJECT_ID(), "");
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			AEProgressDialog.dismissLoadingDialog();
		}
	}

	private class RefreshPostTask extends AsyncTask<String, Integer, HttpResult> {
		private boolean refresh = false;

		public RefreshPostTask(boolean refresh) {
			this.refresh = refresh;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			String project_id = params[0];
			String time = params[1];
			StringBuilder param = new StringBuilder("project_id=" + project_id);
			if (!StringUtil.isEmpty(time)) {
				param.append("&time=" + time);
			}
			HttpResult result = AEHttpUtil.doPost(URLConstants.URL_QUERY_POST, param.toString());
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				try {
					JSONObject json = new JSONObject(result.getRES_OBJ().toString());
					if (refresh) {
						PostDBHelper.deleteAll(project_id);
						CommentDBHelper.deleteAll(project_id);
					}
					List<PostVO> postList = new ArrayList<>();
					if (json.has("post")) {
						postList = GsonUtil.getInstance().fromJson(json.getString("post"),
								new TypeToken<List<PostVO>>() {
								}.getType());
					}
					List<CommentVO> tempCommentList = new ArrayList<>();
					if (json.has("comment")) {
						tempCommentList = GsonUtil.getInstance().fromJson(json.getString("comment"),
								new TypeToken<List<CommentVO>>() {
								}.getType());
					}
					PostDBHelper.insertPostList(postList, project_id);
					CommentDBHelper.insertCommentList(tempCommentList, project_id);
					Map<String, Object> map = new HashMap<>();
					map.put("post", postList);
					List<List<CommentVO>> commentList = new ArrayList<>();
					for (PostVO post : postList) {
						List<CommentVO> temp = new ArrayList<>();
						for (CommentVO comment : tempCommentList) {
							if (comment.getPost_id().equals(post.getPost_id())) {
								temp.add(comment);
							}
						}
						commentList.add(temp);
					}
					map.put("comment", commentList);
					result.setRES_OBJ(map);
					if (postList.size() < 20) {
						hasMore = false;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			mListView.onRefreshComplete();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				if (refresh) {
					adapter.clear();
				}
				try {
					Map<String, Object> map = (Map<String, Object>) result.getRES_OBJ();
					if (((List<PostVO>) map.get("post")).size() > 0) {
						adapter.addPost((List<PostVO>) map.get("post"));
						adapter.addComment((List<List<CommentVO>>) map.get("comment"));
					}
				} catch (Exception e) {
				}
				adapter.notifyDataSetChanged();
			} else {
				ToastUtil.show(result.getRES_MESSAGE());
			}
			if (!hasMore) {
				mListView.setMode(Mode.PULL_FROM_START);
			} else {
				mListView.setMode(Mode.BOTH);
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			AEProgressDialog.dismissLoadingDialog();
			mListView.onRefreshComplete();
		}
	}

	private class RefreshTask extends AsyncTask<String, Integer, Map<String, Object>> {
		private boolean refresh = false;

		public RefreshTask(boolean refresh) {
			this.refresh = refresh;
		}

		@Override
		protected Map<String, Object> doInBackground(String... params) {
			Map<String, Object> result = new HashMap<>();
			String project_id = params[0];
			String time = params[1];
			List<PostVO> postList = new ArrayList<>();
			if (StringUtil.isEmpty(time)) {
				postList = PostDBHelper.queryList(project_id);
			} else {
				postList = PostDBHelper.queryNextList(project_id, time);
			}
			result.put("post", postList);
			List<List<CommentVO>> commentList = new ArrayList<>();
			for (PostVO post : postList) {
				commentList.add(CommentDBHelper.queryList(post.getPost_id(), project_id));
			}
			result.put("comment", commentList);
			return result;
		}

		@Override
		protected void onPostExecute(Map<String, Object> result) {
			super.onPostExecute(result);
			if (refresh) {
				adapter.clear();
				adapter.notifyDataSetChanged();
			}
			adapter.addPost((List<PostVO>) result.get("post"));
			adapter.addComment((List<List<CommentVO>>) result.get("comment"));
			adapter.notifyDataSetChanged();
			if (adapter.getCount() < 20) {
				dbHasMore = false;
			}
			if (adapter.getCount() == 0) {
				mListView.setMode(Mode.PULL_FROM_START);
			} else if (hasMore) {
				mListView.setMode(Mode.BOTH);
			}
		}
	}

	private void refreshFromDB(String time, boolean refresh, boolean reset) {
		if (reset) {
			hasMore = true;
			dbHasMore = true;
			mListView.onRefreshComplete();
			mListView.setMode(Mode.PULL_FROM_START);
		}
		if (refreshTask != null) {
			refreshTask.cancel(true);
			refreshTask = null;
		}
		refreshTask = new RefreshTask(refresh);
		refreshTask.execute(project.getPROJECT_ID(), time);
	}
}