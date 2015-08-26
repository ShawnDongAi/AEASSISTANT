package com.zzn.aeassistant.activity.user;

import java.util.List;

import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.Mode;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshListView;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.ProjectVO;
import com.zzn.aeassistant.vo.RateVO;

public class RatingActivity extends BaseActivity {
	private RatingBar rateToday, rateHistory;
	private TextView scoreToday, scoreHistory;
	private EditText content;
	private PullToRefreshListView listView;
	private RateAdapter adapter;
	private String user_id = "";
	private ProjectVO project;
	private LoadRateTask loadRateTask;
	private TodayRateTask todayRateTask;
	private RatingTask ratingTask;
	private int page = 0;
	private boolean hasMore = true;

	@Override
	protected int layoutResID() {
		return R.layout.activity_rating;
	}

	@Override
	protected int titleStringID() {
		return R.string.lable_rate;
	}

	@Override
	protected void initView() {
		rateToday = (RatingBar) findViewById(R.id.rate_today);
		rateHistory = (RatingBar) findViewById(R.id.rate_history);
		scoreToday = (TextView) findViewById(R.id.score_today);
		scoreHistory = (TextView) findViewById(R.id.score_history);
		content = (EditText) findViewById(R.id.content);
		listView = (PullToRefreshListView) findViewById(R.id.base_list);
		user_id = getIntent().getStringExtra(CodeConstants.KEY_USER_ID);
		project = (ProjectVO) getIntent().getSerializableExtra(CodeConstants.KEY_PROJECT_VO);
		initListView();
	}

	private void initListView() {
		listView.setEmptyView(View.inflate(mContext, R.layout.list_empty_view,
				null));
		adapter = new RateAdapter(this);
		listView.setAdapter(adapter);
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(
					final PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(
						getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
				page = 0;
				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy(true, false)
						.setLastUpdatedLabel(label);
				loadRateTask = new LoadRateTask();
				loadRateTask.execute(new String[] { user_id });
			}

			@Override
			public void onPullUpToRefresh(
					final PullToRefreshBase<ListView> refreshView) {
				if (hasMore) {
					loadRateTask = new LoadRateTask();
					loadRateTask.execute(new String[] { user_id });
				} else {
					refreshView.onRefreshComplete();
				}
			}
		});
	}

	@Override
	protected boolean needLocation() {
		return false;
	}

	@Override
	protected void onDestroy() {
		if (todayRateTask != null) {
			todayRateTask.cancel(true);
			todayRateTask = null;
		}
		if (loadRateTask != null) {
			loadRateTask.cancel(true);
			loadRateTask = null;
		}
		if (ratingTask != null) {
			ratingTask.cancel(true);
			ratingTask = null;
		}
		super.onDestroy();
	}

	/**
	 * 获取今日评价信息
	 * 
	 * @author Shawn 2015年8月26日
	 */
	private class TodayRateTask extends AsyncTask<String, Integer, HttpResult> {
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
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			AEProgressDialog.dismissLoadingDialog();
		}

	}

	/**
	 * 提交评价信息
	 * 
	 * @author Shawn 2015年8月26日
	 */
	private class RatingTask extends AsyncTask<String, Integer, HttpResult> {
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
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			AEProgressDialog.dismissLoadingDialog();
		}

	}

	/**
	 * 获取历史评价信息
	 * 
	 * @author Shawn 2015年8月26日
	 */
	private class LoadRateTask extends AsyncTask<String, Integer, HttpResult> {

		@Override
		protected HttpResult doInBackground(String... params) {
			return null;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			listView.onRefreshComplete();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				if (result.getRES_OBJ() != null
						&& !StringUtil.isEmpty(result.getRES_OBJ().toString())) {
					List<RateVO> rates = GsonUtil.getInstance().fromJson(
							result.getRES_OBJ().toString(),
							new TypeToken<List<RateVO>>() {
							}.getType());
					if (page == 0) {
						adapter.clear();
					}
					adapter.addData(rates);
					if (rates.size() < 20) {
						hasMore = false;
					}
					page++;
				} else {
					hasMore = false;
				}
			} else {
				ToastUtil.show(result.getRES_MESSAGE());
			}
			if (!hasMore) {
				listView.getLoadingLayoutProxy(false, true)
						.setPullLabel(
								getString(R.string.pull_to_refresh_from_bottom_null_data));
				listView.getLoadingLayoutProxy(false, true)
						.setRefreshingLabel(
								getString(R.string.pull_to_refresh_from_bottom_null_data));
				listView.getLoadingLayoutProxy(false, true)
						.setReleaseLabel(
								getString(R.string.pull_to_refresh_from_bottom_null_data));
				listView.getLoadingLayoutProxy(false, true).setLoadingDrawable(
						null);
			} else {
				listView.getLoadingLayoutProxy(false, true)
						.setPullLabel(
								getString(R.string.pull_to_refresh_from_bottom_pull_label));
				listView.getLoadingLayoutProxy(false, true)
						.setRefreshingLabel(
								getString(R.string.pull_to_refresh_from_bottom_refreshing_label));
				listView.getLoadingLayoutProxy(false, true)
						.setReleaseLabel(
								getString(R.string.pull_to_refresh_from_bottom_release_label));
				listView.getLoadingLayoutProxy(false, true).setLoadingDrawable(
						getResources().getDrawable(
								R.drawable.default_ptr_rotate));
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			listView.onRefreshComplete();
		}
	}
}
