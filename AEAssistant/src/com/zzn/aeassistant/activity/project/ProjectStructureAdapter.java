package com.zzn.aeassistant.activity.project;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.view.tree.Node;
import com.zzn.aeassistant.view.tree.TreeListViewAdapter;
import com.zzn.aeassistant.vo.ProjectVO;

public class ProjectStructureAdapter<T> extends TreeListViewAdapter<T> {
	private Context mContext;

	public ProjectStructureAdapter(ListView mTree, Context context,
			List<T> datas, boolean expand) throws IllegalArgumentException,
			IllegalAccessException {
		super(mTree, context, datas, expand);
		this.mContext = context;
	}

	@SuppressWarnings("unchecked")
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
			viewHolder.myPosition = (ImageView) convertView
					.findViewById(R.id.my_position);
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
		ProjectVO vo = (ProjectVO) node.getData();
		viewHolder.user.setText(vo.getCREATE_USER_NAME() + ":");
		viewHolder.phone.setText(vo.getCREATE_USER_PHONE());
		viewHolder.myPosition
				.setVisibility(vo.getCREATE_USER().equals(
						AEApp.getCurrentUser((Activity) mContext).getUSER_ID()) ? View.VISIBLE
						: View.GONE);
		return convertView;
	}

	private final class ViewHolder {
		ImageView icon;
		TextView label;
		TextView user;
		TextView phone;
		ImageView myPosition;
	}
}
