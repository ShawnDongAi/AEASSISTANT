package com.zzn.aeassistant.activity.attendance;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.view.tree.Node;
import com.zzn.aeassistant.view.tree.TreeListViewAdapter;
import com.zzn.aeassistant.vo.ProAttendanceVO;

public class ProAttendanceAdapter<T> extends TreeListViewAdapter<T> {

	public ProAttendanceAdapter(ListView mTree, Context context, List<T> datas,
			boolean expand) throws IllegalArgumentException,
			IllegalAccessException {
		super(mTree, context, datas, expand);
	}

	@Override
	public View getConvertView(Node node, int position, View convertView,
			ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_tree_list, parent,
					false);
			viewHolder = new ViewHolder();
			viewHolder.icon = (ImageView) convertView
					.findViewById(R.id.treenode_icon);
			viewHolder.label = (TextView) convertView
					.findViewById(R.id.treenode_label);
			viewHolder.user = (TextView) convertView
					.findViewById(R.id.treenode_user);
			viewHolder.phone = (TextView) convertView
					.findViewById(R.id.treenode_phone);
			viewHolder.phone.setVisibility(View.GONE);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (node.getIcon() == -1) {
			viewHolder.icon.setVisibility(View.INVISIBLE);
		} else {
			viewHolder.icon.setVisibility(View.VISIBLE);
			viewHolder.icon.setImageResource(node.getIcon());
		}
		viewHolder.label.setText(node.getName());
		ProAttendanceVO vo = (ProAttendanceVO) node.getData();
		viewHolder.user.setText(mContext.getString(R.string.sum_pro_total,
				vo.getCount(), vo.getException_count()));
		return convertView;
	}

	private final class ViewHolder {
		ImageView icon;
		TextView label;
		TextView user;
		TextView phone;
	}
}
