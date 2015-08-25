package com.zzn.aeassistant.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.vo.ProjectVO;

public class ProListAdapter extends BaseAdapter {
	private Context mContext;
	private List<ProjectVO> datas = new ArrayList<ProjectVO>();
	
	public ProListAdapter(Context context, List<ProjectVO> data) {
		this.mContext = context;
		this.datas = data;
	}
	
	public void setDatas(List<ProjectVO> data) {
		this.datas = data;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public ProjectVO getItem(int position) {
		return datas.get(position);
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
			convertView = View.inflate(mContext, R.layout.item_base_title, null);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.title.setText(getItem(position).getROOT_PROJECT_NAME());
		return convertView;
	}

	private class ViewHolder {
		private TextView title;
	}
}
