package com.zzn.aeassistant.activity.attendance;

import java.util.List;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.widget.TextView;

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
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshStaggeredGridView;
import com.zzn.aeassistant.view.staggered.StaggeredGridView;
import com.zzn.aeassistant.view.staggered.StaggeredGridView.OnLoadmoreListener;
import com.zzn.aeassistant.vo.AttendanceVO;
import com.zzn.aeassistant.vo.HttpResult;

public class SumByUsersActivity extends BaseActivity {
	private PullToRefreshStaggeredGridView pullListView;
	// private View footerView;
	private TextView headerLable;
	private SumUserAdapter adapter;
	private String startDate, endDate;
	private SumByUserTask sumByUserTask;
	private int page = 0;
	private boolean hasMore = true;

	@Override
	protected int layoutResID() {
		return R.layout.activity_staggered_list;
	}

	@Override
	protected int titleStringID() {
		return R.string.sum_by_user;
	}

	@Override
	protected void initView() {
		pullListView = (PullToRefreshStaggeredGridView) findViewById(R.id.base_list);
		headerLable = (TextView) findViewById(R.id.lable);

		/*footerView = View.inflate(mContext, R.layout.item_list_footer, null);
		ImageView spaceshipImage = (ImageView) footerView
				.findViewById(R.id.img);
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				mContext, R.anim.loading_anim);
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		pullListView.getRefreshableView().setFooterView(footerView);
		footerView.setVisibility(View.GONE);*/

		adapter = new SumUserAdapter(mContext);
		pullListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		startDate = getIntent().getStringExtra(CodeConstants.KEY_START_DATE);
		endDate = getIntent().getStringExtra(CodeConstants.KEY_END_DATE);
		initPullToRefresh();
	}

	@Override
	protected boolean needLocation() {
		return false;
	}

	@Override
	protected void onDestroy() {
		if (sumByUserTask != null) {
			sumByUserTask.cancel(true);
			sumByUserTask = null;
		}
		super.onDestroy();
	}

	private void initPullToRefresh() {
		pullListView.setMode(Mode.PULL_FROM_START);
		pullListView
				.setOnRefreshListener(new OnRefreshListener<StaggeredGridView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<StaggeredGridView> refreshView) {
						String label = DateUtils.formatDateTime(
								getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						page = 0;
						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						sumByUserTask = new SumByUserTask();
						sumByUserTask
								.execute(new String[] { startDate, endDate });
					}
				});
		pullListView.setOnLoadmoreListener(new OnLoadmoreListener() {
			@Override
			public void onLoadmore() {
				if (hasMore) {
					sumByUserTask = new SumByUserTask();
					sumByUserTask.execute(new String[] { startDate, endDate });
				}
			}
		});
		AEProgressDialog.showLoadingDialog(mContext);
		sumByUserTask = new SumByUserTask();
		sumByUserTask.execute(new String[] { startDate, endDate });
	}

	private class SumByUserTask extends AsyncTask<String, Integer, HttpResult> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			String startDate = params[0];
			String endDate = params[1];
			String param = "start_date=" + startDate + "&end_date=" + endDate
					+ "&user_id=" + AEApp.getCurrentUser().getUSER_ID()
					+ "&page=" + page;
			HttpResult result = AEHttpUtil.doPost(
					URLConstants.URL_SUM_BY_USERS, param);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			pullListView.onRefreshComplete();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				if (result.getRES_OBJ() != null
						&& !StringUtil.isEmpty(result.getRES_OBJ().toString())) {
					JSONObject object;
					try {
						object = new JSONObject(result.getRES_OBJ().toString());
						if (object.has("count")
								&& object.has("attendance_list")) {
							List<AttendanceVO> attendances = GsonUtil
									.getInstance()
									.fromJson(
											object.getString("attendance_list"),
											new TypeToken<List<AttendanceVO>>() {
											}.getType());
							if (page == 0) {
								adapter.clear();
							}
							adapter.addData(attendances);
							if (attendances.size() < 20) {
								hasMore = false;
							}
							int count = object.getInt("count");
							headerLable.setText(getString(
									R.string.sum_user_total, count));
							page++;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					hasMore = false;
				}
			} else {
				ToastUtil.show(result.getRES_MESSAGE());
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			AEProgressDialog.dismissLoadingDialog();
			pullListView.onRefreshComplete();
		}
	}
}
