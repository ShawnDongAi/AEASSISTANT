package com.zzn.aeassistant.activity.attendance;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.vo.AttendanceVO;

public class SumUserListAdapter extends BaseAdapter {
	private Context mContext;
	private List<AttendanceVO> datas = new ArrayList<AttendanceVO>();

	public SumUserListAdapter(Context context) {
		this.mContext = context;
	}

	public void addData(List<AttendanceVO> datas) {
		this.datas.addAll(datas);
		notifyDataSetChanged();
	}

	public void setData(List<AttendanceVO> datas) {
		this.datas = datas;
		notifyDataSetChanged();
	}

	public void clear() {
		this.datas.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public AttendanceVO getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.item_list_attendance, null);
			holder.time = (TextView) convertView.findViewById(R.id.date);
			holder.project = (TextView) convertView.findViewById(R.id.project);
			holder.root = (TextView) convertView.findViewById(R.id.root);
			holder.status = (ImageView) convertView.findViewById(R.id.normal);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AttendanceVO vo = getItem(position);
		holder.time.setText(vo.getDate().replace(" ", "\n"));
		holder.project.setText(vo.getProject_name());
		holder.status
				.setVisibility(vo.getNormal() != null && !vo.getNormal().equals("0") ? View.VISIBLE : View.INVISIBLE);
		holder.status.setImageResource(vo.getNormal() != null && vo.getNormal().equals("1") ? R.drawable.ic_warning_red
				: R.drawable.ic_warning_green);
		if (vo.getRoot_project_name() != null) {
			holder.root.setText(vo.getRoot_project_name());
		}
		return convertView;
	}

	private class ViewHolder {
		private TextView time;
		private TextView project;
		private TextView root;
		private ImageView status;
	}
}
