package com.zzn.aeassistant.activity.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.CharacterParser;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.view.ClearEditText;
import com.zzn.aeassistant.view.pinnedsection.PinnedSectionListAdapter;
import com.zzn.aeassistant.view.pinnedsection.PinnedSectionListView;
import com.zzn.aeassistant.view.pinnedsection.SideBar;
import com.zzn.aeassistant.view.pinnedsection.SideBar.OnTouchingLetterChangedListener;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.PhoneContact;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class PhoneContactActivity extends BaseActivity {
	private PinnedSectionListView mListView;
	private FastScrollerAdapter mAdapter;
	private List<PhoneContact> allData = new ArrayList<PhoneContact>();
	private List<PhoneContact> itemList = new ArrayList<PhoneContact>();
	private SideBar mSideBar;
	private List<String> sectionList = new ArrayList<String>();
	private Map<String, String> selectDatas = new HashMap<>();
	private UpdateParentTask updateParentTask;
	private String projectID = "";

	@Override
	protected int layoutResID() {
		return R.layout.activity_contact_list;
	}

	@Override
	protected int titleStringID() {
		return R.string.lable_phone_contace;
	}

	@Override
	protected void initView() {
		projectID = getIntent().getStringExtra(CodeConstants.KEY_PROJECT_ID);
		save.setVisibility(View.VISIBLE);
		save.setText(R.string.import_contact);
		mListView = (PinnedSectionListView) findViewById(R.id.base_list);
		mSideBar = (SideBar) findViewById(R.id.side_bar);
		TextView mLetter = (TextView) findViewById(R.id.letter);
		View headView = View.inflate(this, R.layout.layout_search, null);
		ClearEditText filterInput = (ClearEditText) headView.findViewById(R.id.filter);
		filterInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				filterData(s.toString().toLowerCase());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		mSideBar.setPopView(mLetter);
		mSideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			@Override
			public void onTouchingLetterChanged(String s) {
				int position = mAdapter.getPositionForSection(sectionList.indexOf(s));
				if (position >= 0) {
					mListView.setSelection(position + 1);
				}
			}
		});
		mListView.addHeaderView(headView);
		mAdapter = new FastScrollerAdapter(mContext);
		mListView.setAdapter(mAdapter);
		init();
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				PhoneContact item = mAdapter.getItem(position - 1);
				if (selectDatas.containsKey(item.getName() + item.getPhone())) {
					selectDatas.remove(item.getName() + item.getPhone());
				} else {
					selectDatas.put(item.getName() + item.getPhone(), item.getPhone());
				}
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	protected boolean needLocation() {
		return false;
	}

	@Override
	protected void onSaveClick() {
		super.onSaveClick();
		if (selectDatas.size() == 0) {
			ToastUtil.show(R.string.null_phone_contace);
			return;
		}
		StringBuilder phone = new StringBuilder();
		for (String key : selectDatas.keySet()) {
			phone.append(selectDatas.get(key) + ",");
		}
		if (phone.length() > 0) {
			phone.deleteCharAt(phone.length() - 1);
		}
		// 迁移
		if (updateParentTask != null) {
			updateParentTask.cancel(true);
			updateParentTask = null;
		}
		updateParentTask = new UpdateParentTask();
		updateParentTask.execute(new String[] { projectID, phone.toString() });
	}

	class FastScrollerAdapter extends AppAdapter implements SectionIndexer {
		private PhoneContact[] sections;

		public FastScrollerAdapter(Context context) {
			super(context);
		}

		@Override
		protected void prepareSections(int sectionsNumber) {
			sections = new PhoneContact[sectionsNumber];
		}

		@Override
		protected void onSectionAdded(PhoneContact section, int sectionPosition) {
			sections[sectionPosition] = section;
		}

		@Override
		public PhoneContact[] getSections() {
			return sections;
		}

		@Override
		public int getPositionForSection(int section) {
			if (section >= sections.length) {
				section = sections.length - 1;
			}
			return sections[section].getListPosition();
		}

		@Override
		public int getSectionForPosition(int position) {
			if (position >= getCount()) {
				position = getCount() - 1;
			}
			return getItem(position).getSectionPosition();
		}
	}

	class AppAdapter extends BaseAdapter implements PinnedSectionListAdapter {
		private Context mContext;
		private List<PhoneContact> datas = new ArrayList<PhoneContact>();

		public AppAdapter(Context context) {
			this.mContext = context;
		}

		public void setDatas(List<PhoneContact> items, int setionCount) {
			prepareSections(setionCount);
			int sectionPos = 0;
			for (int i = 0; i < items.size(); i++) {
				PhoneContact section = items.get(i);
				if (section.getType() == PhoneContact.SECTION) {
					section.setSectionPosition(i);
					section.setListPosition(i + 1);
					onSectionAdded(section, sectionPos);
					sectionPos++;
				}
			}
			this.datas = items;
		}

		public void clear() {
			datas.clear();
		}

		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public PhoneContact getItem(int position) {
			return datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			if (getItem(position).getType() == PhoneContact.SECTION) {
				return PhoneContact.SECTION;
			}
			return PhoneContact.ITEM;
		}

		protected void prepareSections(int sectionsNumber) {
		}

		protected void onSectionAdded(PhoneContact section, int sectionPosition) {
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(mContext, R.layout.item_phone_contact, null);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.phone = (TextView) convertView.findViewById(R.id.phone);
				holder.checkbox = (CheckBox) convertView.findViewById(R.id.check_box);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final PhoneContact item = getItem(position);
			holder.name.setText(item.getName());
			holder.phone.setText(item.getPhone());
			if (isItemViewTypePinned(getItemViewType(position))) {
				holder.name.setTextSize(12f);
				holder.phone.setVisibility(View.GONE);
				holder.checkbox.setVisibility(View.GONE);
				convertView.setBackgroundColor(Color.GRAY);
			} else {
				holder.name.setTextSize(getResources().getDimension(R.dimen.menu_text_size));
				holder.phone.setVisibility(View.VISIBLE);
				holder.checkbox.setVisibility(View.VISIBLE);
			}
			holder.checkbox.setOnCheckedChangeListener(null);
			holder.checkbox.setChecked(selectDatas.containsKey(item.getName() + item.getPhone()));
			holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						selectDatas.put(item.getName() + item.getPhone(), item.getPhone());
					} else {
						selectDatas.remove(item.getName() + item.getPhone());
					}
				}
			});
			return convertView;
		}

		class ViewHolder {
			TextView name;
			TextView phone;
			CheckBox checkbox;
		}

		@Override
		public boolean isItemViewTypePinned(int viewType) {
			if (viewType == PhoneContact.SECTION) {
				return true;
			}
			return false;
		}
	}

	private List<PhoneContact> filledData(List<PhoneContact> data) {
		List<PhoneContact> mSortList = new ArrayList<PhoneContact>();
		for (int i = 0; i < data.size(); i++) {
			PhoneContact sortModel = new PhoneContact(PhoneContact.ITEM, data.get(i).getName(), data.get(i).getPhone());
			String pinyin = CharacterParser.getInstance().getSelling(data.get(i).getName());
			String sortString = pinyin.substring(0, 1).toUpperCase();
			if (!sortString.matches("[A-Z]")) {
				sortString = "#";
			}
			sortModel.setSortLetter(sortString.toUpperCase());
			mSortList.add(sortModel);
		}
		return mSortList;
	}

	private void setDatas() {
		itemList.clear();
		for (int i = 0; i < allData.size(); i++) {
			if (!sectionList.contains(allData.get(i).getSortLetter())) {
				sectionList.add(allData.get(i).getSortLetter());
				PhoneContact item = new PhoneContact(PhoneContact.SECTION, allData.get(i).getSortLetter(), "");
				itemList.add(item);
			}
			itemList.add(allData.get(i));
		}
		mSideBar.setSections(sectionList.toArray(new String[] {}));
		mAdapter.setDatas(itemList, sectionList.size());
		mAdapter.notifyDataSetChanged();
	}

	private synchronized void filterData(String key) {
		if (StringUtil.isEmpty(key)) {
			sectionList.clear();
			setDatas();
		} else {
			List<PhoneContact> filterList = new ArrayList<PhoneContact>();
			sectionList.clear();
			for (int i = 0; i < allData.size(); i++) {
				String text = allData.get(i).getName();
				if (text.toLowerCase().contains(key)
						|| CharacterParser.getInstance().getSelling(text).toLowerCase().contains(key)) {
					if (!sectionList.contains(allData.get(i).getSortLetter())) {
						sectionList.add(allData.get(i).getSortLetter());
						PhoneContact item = new PhoneContact(PhoneContact.SECTION, allData.get(i).getSortLetter(), "");
						filterList.add(item);
					}
					filterList.add(allData.get(i));
				}
			}
			mSideBar.setSections(sectionList.toArray(new String[] {}));
			mAdapter.setDatas(filterList, sectionList.size());
			mAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 初始化数据库查询参数
	 */
	private void init() {
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人Uri；
		// 查询的字段
		String[] projection = { ContactsContract.CommonDataKinds.Phone._ID,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.DATA1,
				"sort_key", ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
				ContactsContract.CommonDataKinds.Phone.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY };
		// 按照sort_key升序查詢
		new MyAsyncQueryHandler(getContentResolver()).startQuery(0, null, uri, projection, null, null,
				"sort_key COLLATE LOCALIZED asc");
	}

	private class MyAsyncQueryHandler extends AsyncQueryHandler {
		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			List<PhoneContact> data = new ArrayList<>();
			if (cursor != null && cursor.getCount() > 0) {
				// contactIdMap = new HashMap<Integer, ContactBean>();
				cursor.moveToFirst(); // 游标移动到第一项
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					String name = cursor.getString(1);
					String number = cursor.getString(2);
					String sortKey = cursor.getString(3);
					// if (contactIdMap.containsKey(contactId)) {
					// // 无操作
					// } else {
					// 创建联系人对象
					if (!StringUtil.isEmpty(number)) {
						PhoneContact contact = new PhoneContact(PhoneContact.ITEM);
						contact.setName(name);
						contact.setPhone(number);
						contact.setSortLetter(sortKey);
						data.add(contact);
					}
					// }
				}
			}
			allData = filledData(data);
			setDatas();
			super.onQueryComplete(token, cookie, cursor);
		}
	}

	private class UpdateParentTask extends AsyncTask<String, Integer, HttpResult> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			AEProgressDialog.showLoadingDialog(mContext);
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			String project_id = params[0];
			String phone = params[1];
			String param = "parent_project_id=" + project_id + "&leaf_user_phone=" + phone;
			HttpResult result = AEHttpUtil.doPost(URLConstants.URL_UPDATE_PARENT, param);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				ToastUtil.show(result.getRES_MESSAGE());
				setResult(RESULT_OK);
				finish();
			} else {
				ToastUtil.showImp(PhoneContactActivity.this, result.getRES_MESSAGE());
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			AEProgressDialog.dismissLoadingDialog();
		}
	}
}