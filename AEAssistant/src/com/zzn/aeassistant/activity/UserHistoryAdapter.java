package com.zzn.aeassistant.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.vo.UserVO;

public class UserHistoryAdapter extends BaseAdapter {
	private Context mContext;
	private List<UserVO> users = new ArrayList<UserVO>();

	public UserHistoryAdapter(Context context) {
		this.mContext = context;
	}
	
	public void setUsers(List<UserVO> users) {
		this.users = users;
	}
	
	public void addUser(UserVO user) {
		this.users.add(0, user);
	}
	
	public void removeUser(int position) {
		this.users.remove(position);
	}

	@Override
	public int getCount() {
		return users.size();
	}

	@Override
	public UserVO getItem(int position) {
		return users.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.item_project, null);
			holder.name = (TextView) convertView.findViewById(R.id.project_name);
			holder.phone = (TextView) convertView.findViewById(R.id.project_address);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		UserVO user = getItem(position);
		holder.name.setText(user.getUSER_NAME());
		holder.phone.setText(user.getPHONE());
		return convertView;
	}

	private class ViewHolder {
		private TextView name, phone;
	}
}
