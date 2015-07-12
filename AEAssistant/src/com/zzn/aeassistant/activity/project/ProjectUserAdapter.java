package com.zzn.aeassistant.activity.project;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.view.CircleImageView;
import com.zzn.aeassistant.view.pinnedsection.PinnedSectionListAdapter;
import com.zzn.aeassistant.vo.UserVO;

public class ProjectUserAdapter extends BaseAdapter implements
		PinnedSectionListAdapter, SectionIndexer {
	private Context mContext;
	private UserItem[] sections;
	private List<UserItem> datas = new ArrayList<UserItem>();

	public ProjectUserAdapter(Context context) {
		this.mContext = context;
	}

	public void setDatas(List<UserItem> items, int setionCount) {
		prepareSections(setionCount);
		int sectionPos = 0;
		for (int i = 0; i < items.size(); i++) {
			UserItem section = items.get(i);
			if (section.type == UserItem.SECTION) {
				section.sectionPosition = i;
				section.listPosition = i + 1;
				onSectionAdded(section, sectionPos);
				sectionPos++;
			}
		}
		this.datas = items;
	}

	public void clear() {
		datas.clear();
	}

	protected void prepareSections(int sectionsNumber) {
		sections = new UserItem[sectionsNumber];
	}

	protected void onSectionAdded(UserItem section, int sectionPosition) {
		sections[sectionPosition] = section;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public UserItem getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public UserItem[] getSections() {
		return sections;
	}

	@Override
	public int getPositionForSection(int section) {
		if (sections == null) {
			return 0;
		}
		if (section >= sections.length) {
			section = sections.length - 1;
		}
		return sections[section].listPosition;
	}

	@Override
	public int getSectionForPosition(int position) {
		if (position >= getCount()) {
			position = getCount() - 1;
		}
		return getItem(position).sectionPosition;
	}

	@Override
	public boolean isItemViewTypePinned(int viewType) {
		if (viewType == UserItem.SECTION) {
			return true;
		}
		return false;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position).type;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.item_user, null);
			holder.head = (CircleImageView) convertView
					.findViewById(R.id.user_head);
			holder.name = (TextView) convertView.findViewById(R.id.user_name);
			holder.phone = (TextView) convertView.findViewById(R.id.user_phone);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		UserItem item = getItem(position);
		if (isItemViewTypePinned(getItemViewType(position))) {
			holder.head.setVisibility(View.GONE);
			holder.phone.setVisibility(View.GONE);
			holder.name.setTextColor(mContext.getResources().getColorStateList(R.color.white));
			convertView.setBackgroundResource(R.color.theme_green);
		} else {
			holder.head.setVisibility(View.VISIBLE);
			holder.phone.setVisibility(View.VISIBLE);
			UserVO user = item.user;
			holder.phone.setText(user.getPHONE());
			holder.name.setTextColor(mContext.getResources().getColorStateList(R.drawable.text_normal));
			convertView.setBackgroundResource(R.drawable.btn_item);
		}
		holder.name.setText(item.lable);
		return convertView;
	}

	private class ViewHolder {
		private CircleImageView head;
		private TextView name;
		private TextView phone;
	}

	public static class UserItem {
		public static final int ITEM = 1;
		public static final int SECTION = 0;
		public final int type;
		public final String lable;
		public final UserVO user;
		public int sectionPosition;
		public int listPosition;
		public String sortLetter;

		public UserItem(int type, String lable, UserVO user) {
			this.type = type;
			this.lable = lable;
			this.user = user;
		}

		@Override
		public String toString() {
			return lable;
		}
	}
}