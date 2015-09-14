package com.zzn.aeassistant.activity.post;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zzn.aeassistant.vo.PostVO;

public class PostAdapter extends BaseAdapter {
	private Context mContext;
	private List<PostVO> datas = new ArrayList<PostVO>();
	
	public PostAdapter(Context context) {
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public PostVO getItem(int position) {
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
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		return convertView;
	}
	
	private class ViewHolder {
		ImageView head;
		TextView name;
		TextView content;
		TextView time;
	}
}
