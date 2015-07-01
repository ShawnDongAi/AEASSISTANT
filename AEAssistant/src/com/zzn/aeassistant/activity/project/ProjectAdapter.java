package com.zzn.aeassistant.activity.project;

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
import com.zzn.aeassistant.vo.ProjectVO;

public class ProjectAdapter extends BaseAdapter implements
		PinnedSectionListAdapter, SectionIndexer {
	private Context mContext;
	private List<ProjectItem> sections = new ArrayList<ProjectItem>();
	private List<ProjectItem> datas = new ArrayList<ProjectItem>();

	public ProjectAdapter(Context context) {
		this.mContext = context;
	}

	public void addItem(ProjectItem item) {
		if (item.type == ProjectItem.SECTION) {
			item.sectionPosition = datas.size();
			item.listPosition = datas.size() + 1;
			sections.add(item);
		}
		datas.add(item);
	}

	public void setItem(List<ProjectItem> items) {
		for (int i = 0; i < items.size(); i++) {
			ProjectItem section = items.get(i);
			if (section.type == ProjectItem.SECTION) {
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
	public ProjectItem getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position).type;
		// return super.getItemViewType(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.item_project, null);
			holder.projectName = (TextView) convertView
					.findViewById(R.id.project_name);
			holder.address = (TextView) convertView
					.findViewById(R.id.project_address);
			holder.time = (TextView) convertView
					.findViewById(R.id.project_create_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ProjectItem item = getItem(position);
		boolean isSection = isItemViewTypePinned(item.type);
		holder.address.setVisibility(isSection ? View.GONE : View.VISIBLE);
		holder.time.setVisibility(isSection ? View.GONE : View.VISIBLE);
		holder.projectName.setText(item.toString());
		if (isSection) {
			holder.projectName.setPadding(10, 5, 5, 10);
			holder.projectName.setTextSize(15f);
			holder.projectName.setTextColor(mContext.getResources().getColor(
					R.color.white));
			convertView.setBackgroundResource(R.color.theme_green);
		} else {
			ProjectVO project = item.project;
			if (project != null) {
				holder.address.setText(project.getADDRESS() != null ? project
						.getADDRESS() : "");
				holder.time.setText(project.getCREATE_TIME());
			}
			holder.address
					.setVisibility(project != null ? View.VISIBLE
							: View.GONE);
			holder.time
					.setVisibility(project != null ? View.VISIBLE
							: View.GONE);
			convertView.setBackgroundResource(R.color.transparent);
		}
		return convertView;
	}

	private class ViewHolder {
		TextView projectName;
		TextView address;
		TextView time;
	}

	@Override
	public ProjectItem[] getSections() {
		return sections.toArray(new ProjectItem[] {});
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

	public static class ProjectItem {
		public static final int SECTION = 0;
		public static final int CURRENT_PROJECT = 1;
		public static final int CURRENT_PROJECT_NULL = 2;
		public static final int MANAGER_PROJECT = 3;
		public static final int MANAGER_PROJECT_NULL = 4;

		public int type;
		public String title;
		public ProjectVO project;
		public int sectionPosition;
		public int listPosition;

		public ProjectItem(int type, String title) {
			this.type = type;
			this.title = title;
		}

		public String toString() {
			return title;
		}
	}

	@Override
	public boolean isItemViewTypePinned(int viewType) {
		return viewType == ProjectItem.SECTION;
	}

	@Override
	public int getViewTypeCount() {
		return 5;
		// return super.getViewTypeCount();
	}
}
