package com.zzn.aeassistant.activity.attendance;

import java.util.List;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.Rotate3dAnimation;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.view.pla.MultiColumnListView;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.Mode;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshListView;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshPLAListView;
import com.zzn.aeassistant.vo.AttendanceVO;
import com.zzn.aeassistant.vo.HttpResult;

public class SumByUsersActivity extends BaseActivity {
	private PullToRefreshPLAListView pullGridView;
	private PullToRefreshListView pullListView;
	private TextView headerLable;
	private SumUserGridAdapter gridAdapter;
	private SumUserListAdapter listAdapter;
	private String startDate, endDate;
	private SumByUserTask sumByUserTask;
	private int page = 0;
	private boolean hasMore = true;
	private Animation visibleAnim, goneAnim;

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
		save.setVisibility(View.VISIBLE);
		save.setText(R.string.grid);
		pullGridView = (PullToRefreshPLAListView) findViewById(R.id.base_grid);
		pullListView = (PullToRefreshListView) findViewById(R.id.base_list);
		visibleAnim = new Rotate3dAnimation(-90f, 0f, screenW / 2.0f, 0.5f,
				0.5f, false);
		visibleAnim.setDuration(300);
		goneAnim = new Rotate3dAnimation(0f, 90f, screenW / 2.0f, 0.5f, 0.5f,
				false);
		goneAnim.setDuration(300);
		pullGridView.setEmptyView(View.inflate(mContext,
				R.layout.list_empty_view, null));
		pullListView.setEmptyView(View.inflate(mContext,
				R.layout.list_empty_view, null));
		headerLable = (TextView) findViewById(R.id.lable);

		gridAdapter = new SumUserGridAdapter(mContext);
		listAdapter = new SumUserListAdapter(mContext);
		pullGridView.setAdapter(gridAdapter);
		pullListView.setAdapter(listAdapter);
		startDate = getIntent().getStringExtra(CodeConstants.KEY_START_DATE);
		endDate = getIntent().getStringExtra(CodeConstants.KEY_END_DATE);
		initPullToRefresh();
	}

	@Override
	protected void onSaveClick() {
		super.onSaveClick();
		pullListView.clearAnimation();
		pullGridView.clearAnimation();
		if (pullListView.getVisibility() == View.GONE) {
			save.setText(R.string.grid);
			visibleAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
				}
			});
			goneAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					pullGridView.setVisibility(View.GONE);
					pullListView.setVisibility(View.VISIBLE);
					pullListView.startAnimation(visibleAnim);
				}
			});
			pullGridView.startAnimation(goneAnim);
		} else {
			save.setText(R.string.list);
			visibleAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
				}
			});
			goneAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					pullListView.setVisibility(View.GONE);
					pullGridView.setVisibility(View.VISIBLE);
					pullGridView.startAnimation(visibleAnim);
				}
			});
			pullListView.startAnimation(goneAnim);
		}
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
		pullGridView.setMode(Mode.BOTH);
		pullListView.setMode(Mode.BOTH);
		pullGridView
				.setOnRefreshListener(new OnRefreshListener2<MultiColumnListView>() {
					@Override
					public void onPullDownToRefresh(
							final PullToRefreshBase<MultiColumnListView> refreshView) {
						String label = DateUtils.formatDateTime(
								getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						page = 0;
						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy(true, false)
								.setLastUpdatedLabel(label);
						sumByUserTask = new SumByUserTask();
						sumByUserTask
								.execute(new String[] { startDate, endDate });
					}

					@Override
					public void onPullUpToRefresh(
							final PullToRefreshBase<MultiColumnListView> refreshView) {
						if (hasMore) {
							sumByUserTask = new SumByUserTask();
							sumByUserTask.execute(new String[] { startDate,
									endDate });
						} else {
							refreshView.onRefreshComplete();
						}
					}
				});
		pullListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
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
				sumByUserTask = new SumByUserTask();
				sumByUserTask.execute(new String[] { startDate, endDate });
			}

			@Override
			public void onPullUpToRefresh(
					final PullToRefreshBase<ListView> refreshView) {
				if (hasMore) {
					sumByUserTask = new SumByUserTask();
					sumByUserTask.execute(new String[] { startDate, endDate });
				} else {
					refreshView.onRefreshComplete();
				}
			}
		});
		AEProgressDialog.showLoadingDialog(mContext);
		sumByUserTask = new SumByUserTask();
		sumByUserTask.execute(new String[] { startDate, endDate });
	}

	private class SumByUserTask extends AsyncTask<String, Integer, HttpResult> {

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

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			pullGridView.onRefreshComplete();
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
								gridAdapter.clear();
								listAdapter.clear();
							}
							gridAdapter.addData(attendances);
							listAdapter.addData(attendances);
							if (attendances.size() < 20) {
								hasMore = false;
							}
							int count = object.getInt("count");
							headerLable.setText(getString(
									R.string.sum_user_total, startDate,
									endDate, count));
							page++;
						} else {
							headerLable
									.setText(getString(R.string.sum_user_null,
											startDate, endDate));
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
			if (!hasMore) {
				pullGridView
						.getLoadingLayoutProxy(false, true)
						.setPullLabel(
								getString(R.string.pull_to_refresh_from_bottom_null_data));
				pullListView
						.getLoadingLayoutProxy(false, true)
						.setPullLabel(
								getString(R.string.pull_to_refresh_from_bottom_null_data));
				pullGridView
						.getLoadingLayoutProxy(false, true)
						.setRefreshingLabel(
								getString(R.string.pull_to_refresh_from_bottom_null_data));
				pullListView
						.getLoadingLayoutProxy(false, true)
						.setRefreshingLabel(
								getString(R.string.pull_to_refresh_from_bottom_null_data));
				pullGridView
						.getLoadingLayoutProxy(false, true)
						.setReleaseLabel(
								getString(R.string.pull_to_refresh_from_bottom_null_data));
				pullListView
						.getLoadingLayoutProxy(false, true)
						.setReleaseLabel(
								getString(R.string.pull_to_refresh_from_bottom_null_data));
				pullGridView.getLoadingLayoutProxy(false, true)
						.setLoadingDrawable(null);
				pullListView.getLoadingLayoutProxy(false, true)
						.setLoadingDrawable(null);
			} else {
				pullGridView
						.getLoadingLayoutProxy(false, true)
						.setPullLabel(
								getString(R.string.pull_to_refresh_from_bottom_pull_label));
				pullListView
						.getLoadingLayoutProxy(false, true)
						.setPullLabel(
								getString(R.string.pull_to_refresh_from_bottom_pull_label));
				pullGridView
						.getLoadingLayoutProxy(false, true)
						.setRefreshingLabel(
								getString(R.string.pull_to_refresh_from_bottom_refreshing_label));
				pullListView
						.getLoadingLayoutProxy(false, true)
						.setRefreshingLabel(
								getString(R.string.pull_to_refresh_from_bottom_refreshing_label));
				pullGridView
						.getLoadingLayoutProxy(false, true)
						.setReleaseLabel(
								getString(R.string.pull_to_refresh_from_bottom_release_label));
				pullListView
						.getLoadingLayoutProxy(false, true)
						.setReleaseLabel(
								getString(R.string.pull_to_refresh_from_bottom_release_label));
				pullGridView.getLoadingLayoutProxy(false, true)
						.setLoadingDrawable(
								getResources().getDrawable(
										R.drawable.default_ptr_rotate));
				pullListView.getLoadingLayoutProxy(false, true)
						.setLoadingDrawable(
								getResources().getDrawable(
										R.drawable.default_ptr_rotate));
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			AEProgressDialog.dismissLoadingDialog();
			pullGridView.onRefreshComplete();
		}
	}
}