package com.zzn.aeassistant.activity.post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.Mode;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshListView;
import com.zzn.aeassistant.vo.CommentVO;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.PostVO;
import com.zzn.aeassistant.vo.ProjectVO;

public class WorkSpaceActivity extends BaseActivity {
	private PullToRefreshListView mListView;
	private PostAdapter adapter;
	private boolean hasMore = true;
	private RefreshPostTask postTask;
	private ProjectVO projectVO;
	private String project_id = "";

	@Override
	protected int layoutResID() {
		return R.layout.activity_work_space;
	}

	@Override
	protected int titleStringID() {
		return R.string.title_work_space;
	}

	@Override
	protected void initView() {
		projectVO = (ProjectVO) getIntent().getSerializableExtra(
				CodeConstants.KEY_PROJECT_VO);
		project_id = getIntent().getStringExtra(CodeConstants.KEY_PROJECT_ID);
		mListView = (PullToRefreshListView) findViewById(R.id.base_list);
		mListView.setEmptyView(View.inflate(mContext, R.layout.list_empty_view,
				null));
		adapter = new PostAdapter(mContext);
		mListView.setAdapter(adapter);
		initPullToRefresh();
	}

	private void initPullToRefresh() {
		mListView.setMode(Mode.BOTH);
		mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(
					final PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(AEApp.getInstance(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy(true, false)
						.setLastUpdatedLabel(label);
				if (postTask != null) {
					postTask.cancel(true);
				}
				postTask = new RefreshPostTask(true);
				postTask.execute(project_id, "");
				hasMore = true;
			}

			@Override
			public void onPullUpToRefresh(
					final PullToRefreshBase<ListView> refreshView) {
				if (hasMore) {
					if (postTask != null) {
						postTask.cancel(true);
					}
					postTask = new RefreshPostTask(false);
					postTask.execute(project_id,
							adapter.getItem(adapter.getCount() - 1).getTime());
				} else {
					refreshView.onRefreshComplete();
				}
			}
		});
		AEProgressDialog.showLoadingDialog(this);
		postTask = new RefreshPostTask(true);
		postTask.execute(project_id, "");
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
	protected boolean needLocation() {
		return false;
	}

	private class RefreshPostTask extends
			AsyncTask<String, Integer, HttpResult> {
		private boolean refresh = false;

		public RefreshPostTask(boolean refresh) {
			this.refresh = refresh;
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			String project_id = params[0];
			String time = params[1];
			StringBuilder param = new StringBuilder("project_id=" + project_id);
			if (!StringUtil.isEmpty(time)) {
				param.append("&time=" + time);
			}
			HttpResult result = AEHttpUtil.doPost(URLConstants.URL_QUERY_POST,
					param.toString());
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				try {
					JSONObject json = new JSONObject(result.getRES_OBJ()
							.toString());
					List<PostVO> postList = new ArrayList<>();
					if (json.has("post")) {
						postList = GsonUtil.getInstance().fromJson(
								json.getString("post"),
								new TypeToken<List<PostVO>>() {
								}.getType());
					}
					List<CommentVO> tempCommentList = new ArrayList<>();
					if (json.has("comment")) {
						tempCommentList = GsonUtil.getInstance().fromJson(
								json.getString("comment"),
								new TypeToken<List<CommentVO>>() {
								}.getType());
					}
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
			if (refresh) {
				adapter.clear();
			}
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				Map<String, Object> map = (Map<String, Object>) result
						.getRES_OBJ();
				adapter.addPost((List<PostVO>) map.get("post"));
				adapter.addComment((List<List<CommentVO>>) map.get("comment"));
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
}