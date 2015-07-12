package com.zzn.aeassistant.activity.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.activity.project.ProjectUserAdapter.UserItem;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.CharacterParser;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.PinyinComparator;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.view.pinnedsection.PinnedSectionListView;
import com.zzn.aeassistant.view.pinnedsection.SideBar;
import com.zzn.aeassistant.view.pinnedsection.SideBar.OnTouchingLetterChangedListener;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.Mode;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshPinnedListView;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.UserVO;

public class ProjectUsersActivity extends BaseActivity {
	private PullToRefreshPinnedListView pullListView;
	private PinnedSectionListView listView;
	private ProjectUserAdapter adapter;
	private SideBar sideBar;
	private TextView mTextDialog;
	private ListUsersTask listUserTask;
	private String project_id;
	private List<String> sectionList = new ArrayList<String>();
	private long lastClickTime = 0;

	@Override
	protected int layoutResID() {
		return R.layout.activity_project_user_list;
	}

	@Override
	protected int titleStringID() {
		return R.string.project_user;
	}

	@Override
	protected void initView() {
		pullListView = (PullToRefreshPinnedListView) findViewById(R.id.base_list);
		listView = pullListView.getRefreshableView();
		sideBar = (SideBar) findViewById(R.id.side_bar);
		mTextDialog = (TextView) findViewById(R.id.text_dialog);
		sideBar.setPopView(mTextDialog);
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			@Override
			public void onTouchingLetterChanged(String s) {
				int position = adapter.getPositionForSection(sectionList
						.indexOf(s));
				if (position >= 0) {
					listView.setSelection(position + 1);
				}
			}
		});
		project_id = getIntent().getStringExtra(CodeConstants.KEY_PROJECT_ID);
		adapter = new ProjectUserAdapter(mContext);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		initPullToRefresh();
	}

	private void initPullToRefresh() {
		pullListView.setMode(Mode.PULL_FROM_START);
		pullListView
				.setOnRefreshListener(new OnRefreshListener<PinnedSectionListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<PinnedSectionListView> refreshView) {
						String label = DateUtils.formatDateTime(
								getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						listUserTask = new ListUsersTask();
						listUserTask.execute(project_id);
					}
				});
		pullListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (System.currentTimeMillis() - lastClickTime < 500) {
					return;
				}
				lastClickTime = System.currentTimeMillis();
				position -= 1;
				if (position < 0) {
					position = 0;
				}
				UserItem item = adapter.getItem(position);
				if (!adapter.isItemViewTypePinned(item.type)) {
					try {
						Intent intent = new Intent(Intent.ACTION_CALL, Uri
								.parse("tel:" + item.user.getPHONE()));
						startActivity(intent);
					} catch (Exception e) {
						ToastUtil.show(R.string.dial_error);
					}
				}
			}
		});
		AEProgressDialog.showLoadingDialog(mContext);
		listUserTask = new ListUsersTask();
		listUserTask.execute(project_id);
	}

	@Override
	protected boolean needLocation() {
		return false;
	}

	@Override
	protected void onDestroy() {
		if (listUserTask != null) {
			listUserTask.cancel(true);
			listUserTask = null;
		}
		super.onDestroy();
	}

	private class ListUsersTask extends AsyncTask<String, Integer, HttpResult> {

		@Override
		protected HttpResult doInBackground(String... params) {
			String project_id = params[0];
			String param = "project_id=" + project_id;
			HttpResult result = AEHttpUtil.doPost(
					URLConstants.URL_PROJECT_USERS, param);
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				if (result.getRES_OBJ() != null
						&& !StringUtil.isEmpty(result.getRES_OBJ().toString())) {
					List<UserVO> userList = GsonUtil.getInstance().fromJson(
							result.getRES_OBJ().toString(),
							new TypeToken<List<UserVO>>() {
							}.getType());
					result.setRES_OBJ(filledData(userList));
				}
			} else {
				ToastUtil.show(result.getRES_MESSAGE());
			}
			return result;
		}

		private List<UserItem> filledData(List<UserVO> userList) {
			List<UserItem> mSortList = new ArrayList<UserItem>();
			for (int i = 0; i < userList.size(); i++) {
				UserVO user = userList.get(i);
				UserItem sortModel = new UserItem(UserItem.ITEM,
						user.getUSER_NAME(), user);
				String pinyin = CharacterParser.getInstance().getSelling(
						user.getUSER_NAME());
				String sortString = pinyin.substring(0, 1).toUpperCase();
				if (!sortString.matches("[A-Z]")) {
					sortString = "#";
				}
				sortModel.sortLetter = sortString.toUpperCase();
				mSortList.add(sortModel);
			}
			Collections.sort(mSortList, new PinyinComparator());
			return mSortList;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				try {
					List<UserItem> userItems = (List<UserItem>) result
							.getRES_OBJ();
					List<UserItem> userItemList = new ArrayList<UserItem>();
					adapter.clear();
					sectionList.clear();
					for (int i = 0; i < userItems.size(); i++) {
						if (!sectionList.contains(userItems.get(i).sortLetter)) {
							sectionList.add(userItems.get(i).sortLetter);
							UserItem item = new UserItem(UserItem.SECTION,
									userItems.get(i).sortLetter, null);
							userItemList.add(item);
						}
						userItemList.add(userItems.get(i));
					}
					sideBar.setSections(sectionList.toArray(new String[] {}));
					adapter.setDatas(userItemList, sectionList.size());
					adapter.notifyDataSetChanged();
				} catch (Exception e) {
				}
			} else {
				ToastUtil.show(result.getRES_MESSAGE());
			}
			pullListView.onRefreshComplete();
			AEProgressDialog.dismissLoadingDialog();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			pullListView.onRefreshComplete();
			AEProgressDialog.dismissLoadingDialog();
		}
	}
}