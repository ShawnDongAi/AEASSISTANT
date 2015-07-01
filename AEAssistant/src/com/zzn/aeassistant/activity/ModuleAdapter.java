package com.zzn.aeassistant.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.vo.Module;

public class ModuleAdapter extends BaseAdapter {
	private Context mContext;
	private List<Module> datas = new ArrayList<Module>();

	public ModuleAdapter(Context context) {
		this.mContext = context;
	}

	public void addItem(Module item) {
		datas.add(item);
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Module getItem(int position) {
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
			convertView = View.inflate(mContext, R.layout.item_home_module,
					null);
			holder.icon = (ImageView) convertView
					.findViewById(R.id.home_module_icon);
			holder.title = (TextView) convertView
					.findViewById(R.id.home_module_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Module item = getItem(position);
		holder.icon.setImageResource(item.getIconID());
		holder.title.setText(item.getTitleID());
		return convertView;
	}

	private class ViewHolder {
		ImageView icon;
		TextView title;
	}
}
