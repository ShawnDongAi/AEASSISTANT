package com.zzn.aeassistant.activity.task;

import java.util.ArrayList;
import java.util.List;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.vo.TaskDetailVO;
import com.zzn.aeassistant.vo.TaskVO;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpandTaskAdapter extends BaseExpandableListAdapter {
	private Context mContext;
	private List<TaskVO> datas = new ArrayList<>();

	public ExpandTaskAdapter(Context context) {
		this.mContext = context;
	}

	public void addData(TaskVO task) {
		datas.add(task);
		notifyDataSetChanged();
	}

	public void addDatas(List<TaskVO> tasks) {
		datas.addAll(tasks);
		notifyDataSetChanged();
	}

	public void setDatas(List<TaskVO> tasks) {
		datas.clear();
		datas = tasks;
		notifyDataSetChanged();
	}

	@Override
	public int getGroupCount() {
		return datas.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (datas.get(groupPosition).getTask_detail_list() != null) {
			return datas.get(groupPosition).getTask_detail_list().size();
		}
		return 0;
	}

	@Override
	public TaskVO getGroup(int groupPosition) {
		return datas.get(groupPosition);
	}

	@Override
	public TaskDetailVO getChild(int groupPosition, int childPosition) {
		if (datas.get(groupPosition).getTask_detail_list() != null) {
			return datas.get(groupPosition).getTask_detail_list().get(childPosition);
		}
		return null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		GroupHolder holder;
		if (convertView == null) {
			holder = new GroupHolder();
			convertView = View.inflate(mContext, R.layout.item_task_group, null);
			holder.arrow = (ImageView) convertView.findViewById(R.id.arrow);
			holder.createUser = (TextView) convertView.findViewById(R.id.create_user);
			holder.date = (TextView) convertView.findViewById(R.id.date);
			convertView.setTag(holder);
		} else {
			holder = (GroupHolder) convertView.getTag();
		}
		TaskVO group = getGroup(groupPosition);
		holder.createUser.setText(group.getCreate_user_name());
		holder.date.setText(group.getTime());
		holder.arrow.setImageResource(isExpanded ? R.drawable.ic_down : R.drawable.ic_to);
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		ChildHolder holder;
		if (convertView == null) {
			holder = new ChildHolder();
			convertView = View.inflate(mContext, R.layout.item_task_child, null);
			holder.processUser = (TextView) convertView.findViewById(R.id.process_user);
			holder.startTime = (TextView) convertView.findViewById(R.id.start_time);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.status = (TextView) convertView.findViewById(R.id.status);
			convertView.setTag(holder);
		} else {
			holder = (ChildHolder) convertView.getTag();
		}
		TaskDetailVO child = getChild(groupPosition, childPosition);
		holder.processUser.setText(child.getProcess_user_name());
		holder.startTime.setText(child.getStart_time());
		holder.content.setText(child.getContent());
		if (child.getStatus().equals("0")) {
			holder.status.setText(R.string.task_status_pending_confirmation);
			holder.status.setBackgroundResource(R.color.red);
		} else if (child.getStatus().equals("1")) {
			holder.status.setText(R.string.task_status_completed);
			holder.status.setBackgroundResource(R.color.theme_green);
		} else {
			holder.status.setText(R.string.task_status_no_completed);
			holder.status.setBackgroundResource(R.color.darkgoldenrod);
		}
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	class GroupHolder {
		ImageView arrow;
		TextView date;
		TextView createUser;
	}

	class ChildHolder {
		TextView processUser;
		TextView startTime;
		TextView content;
		TextView status;
	}
}