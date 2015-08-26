package com.zzn.aeassistant.activity.user;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.zzn.aeassistant.vo.RateVO;

public class RateAdapter extends BaseAdapter {
	private Context mContext;
	private List<RateVO> datas = new ArrayList<RateVO>();
	
	public RateAdapter(Context context) {
		this.mContext = context;
	}
	
	public void addData(List<RateVO> datas) {
		this.datas.addAll(datas);
		notifyDataSetChanged();
	}

	public void setData(List<RateVO> datas) {
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
	public RateVO getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

	class ViewHolder {
		private TextView name;
		private TextView phone;
		private RatingBar rate;
		private TextView score;
		private TextView content;
		private TextView project;
		private TextView time;
	}
}
