package com.zzn.aeassistant.activity.task;

import java.util.ArrayList;
import java.util.List;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.vo.TaskDetailVO;
import com.zzn.aeassistant.vo.TaskVO;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TaskAdapter extends BaseAdapter {
	private Context mContext;
	private List<TaskDetailVO> datas = new ArrayList<>();

	public TaskAdapter(Context context) {
		this.mContext = context;
	}

	public void addData(TaskDetailVO task) {
		datas.add(task);
		notifyDataSetChanged();
	}

	public void addDatas(List<TaskDetailVO> tasks) {
		datas.addAll(tasks);
		notifyDataSetChanged();
	}

	public void setDatas(List<TaskDetailVO> tasks) {
		datas.clear();
		datas = tasks;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public TaskDetailVO getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.item_task_detail, null);
			holder.createUser = (TextView) convertView.findViewById(R.id.create_user);
			holder.startTime = (TextView) convertView.findViewById(R.id.start_time);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.date = (TextView) convertView.findViewById(R.id.date);
			holder.status = (TextView) convertView.findViewById(R.id.status);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		TaskDetailVO item = getItem(position);
		holder.createUser.setText(item.getCreate_user_name());
		holder.date.setText(item.getTime());
		holder.startTime.setText(item.getStart_time());
		holder.content.setText(item.getContent());
		if (item.getStatus().equals("0")) {
			holder.status.setText(R.string.task_status_pending_confirmation);
			holder.status.setBackgroundResource(R.color.red);
		} else if (item.getStatus().equals("1")) {
			holder.status.setText(R.string.task_status_completed);
			holder.status.setBackgroundResource(R.color.theme_green);
		} else {
			holder.status.setText(R.string.task_status_no_completed);
			holder.status.setBackgroundResource(R.color.darkgoldenrod);
		}
		return convertView;
	}

	class ViewHolder {
		TextView createUser;
		TextView date;
		TextView startTime;
		TextView content;
		TextView status;
	}
}