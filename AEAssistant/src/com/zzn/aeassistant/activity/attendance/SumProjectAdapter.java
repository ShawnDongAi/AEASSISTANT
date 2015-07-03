package com.zzn.aeassistant.activity.attendance;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.view.pinnedsection.PinnedSectionListAdapter;

public class SumProjectAdapter extends BaseAdapter implements
		PinnedSectionListAdapter, SectionIndexer {
	private Context mContext;
	private List<AttendanceItem> sections = new ArrayList<AttendanceItem>();
	private List<AttendanceItem> datas = new ArrayList<AttendanceItem>();

	public SumProjectAdapter(Context context) {
		this.mContext = context;
	}

	public void addItem(AttendanceItem item) {
		if (item.type == AttendanceItem.SECTION) {
			item.sectionPosition = datas.size();
			item.listPosition = datas.size() + 1;
			sections.add(item);
		}
		datas.add(item);
	}

	public void setItem(List<AttendanceItem> items) {
		for (int i = 0; i < items.size(); i++) {
			AttendanceItem section = items.get(i);
			if (section.type == AttendanceItem.SECTION) {
				section.sectionPosition = i;
				section.listPosition = i + 1;
				sections.add(section);
			}
		}
		datas = items;
	}

	public void clear() {
		sections.clear();
		datas.clear();
	}

	public void remove(int position) {
		datas.remove(position);
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public AttendanceItem getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position).type;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.item_sum_project,
					null);
			holder.date = (TextView) convertView.findViewById(R.id.sum_time);
			holder.sumResult = (TextView) convertView
					.findViewById(R.id.sum_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AttendanceItem item = getItem(position);
		boolean isSection = isItemViewTypePinned(item.type);
		holder.date.setVisibility(isSection ? View.GONE : View.VISIBLE);
		holder.sumResult.setText(item.toString());
		if (isSection) {
			holder.sumResult.setPadding(10, 5, 5, 10);
			holder.sumResult.setTextSize(15f);
			holder.sumResult.setTextColor(mContext.getResources().getColor(
					R.color.white));
			convertView.setBackgroundResource(R.color.theme_green);
		} else {
			holder.date.setText(item.date);
			convertView.setBackgroundResource(R.color.transparent);
		}
		return convertView;
	}

	private class ViewHolder {
		private TextView date;
		private TextView sumResult;
	}

	@Override
	public AttendanceItem[] getSections() {
		return sections.toArray(new AttendanceItem[] {});
	}

	@Override
	public int getPositionForSection(int section) {
		if (section >= sections.size()) {
			section = sections.size() - 1;
		}
		return sections.get(section).listPosition;
	}

	@Override
	public int getSectionForPosition(int position) {
		return datas.get(position).sectionPosition;
	}

	@Override
	public boolean isItemViewTypePinned(int viewType) {
		return viewType == AttendanceItem.SECTION;
	}

	@Override
	public int getViewTypeCount() {
		return 5;
	}

	public static class AttendanceItem {
		public static final int SECTION = 0;
		public static final int EXEPTION_ATTENDANCE = 1;
		public static final int EXEPTION_ATTENDANCE_NULL = 2;
		public static final int NORMAL_ATTENDANCE = 3;
		public static final int NORMAL_ATTENDANCE_NULL = 4;

		public int type;
		public String title;
		public String date;
		public int sectionPosition;
		public int listPosition;

		public AttendanceItem(int type, String title, String date) {
			this.type = type;
			this.title = title;
		}

		public String toString() {
			return title;
		}
	}
}
