package com.zzn.aeassistant.activity.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.view.CircleImageView;
import com.zzn.aeassistant.vo.TaskDetailVO;
import com.zzn.aeassistant.vo.TaskVO;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpandTaskAdapter extends BaseExpandableListAdapter {
	private Context mContext;
	private List<TaskVO> datas = new ArrayList<>();
	private boolean mShowCreateUser = true;

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	public ExpandTaskAdapter(Context context, boolean showCreateUser) {
		this.mContext = context;
		this.mShowCreateUser = showCreateUser;
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.icon_loading) // 设置图片在下载期间显示的图片
				.showImageForEmptyUri(R.drawable.icon_failed)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.icon_failed) // 设置图片加载/解码过程中错误时候显示的图片
				.cacheInMemory(true)// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
				.imageScaleType(ImageScaleType.EXACTLY)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
				// .decodingOptions(android.graphics.BitmapFactory.Options
				// decodingOptions)//设置图片的解码配置
				// .delayBeforeLoading(int delayInMillis)//int
				// delayInMillis为你设置的下载前的延迟时间
				// 设置图片加入缓存前，对bitmap进行设置
				// .preProcessor(BitmapProcessor preProcessor)
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				// .displayer(new RoundedBitmapDisplayer(20))// 是否设置为圆角，弧度为多少
				.displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
				.build();// 构建完成
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
		holder.createUser.setVisibility(mShowCreateUser ? View.VISIBLE : View.GONE);
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
			holder.processUserHead = (CircleImageView) convertView.findViewById(R.id.process_user_head);
			holder.startTime = (TextView) convertView.findViewById(R.id.start_time);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.status = (TextView) convertView.findViewById(R.id.status);
			convertView.setTag(holder);
		} else {
			holder = (ChildHolder) convertView.getTag();
		}
		TaskDetailVO child = getChild(groupPosition, childPosition);
		holder.processUser.setText(child.getProcess_user_name());
		if (!StringUtil.isEmpty(child.getProcess_user_head())) {
			imageLoader.displayImage(String.format(URLConstants.URL_IMG, child.getProcess_user_head()),
					holder.processUserHead, options, animateFirstListener);
		}
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
		return true;
	}

	class GroupHolder {
		ImageView arrow;
		TextView date;
		TextView createUser;
	}

	class ChildHolder {
		TextView processUser;
		CircleImageView processUserHead;
		TextView startTime;
		TextView content;
		TextView status;
	}

	/** 图片加载监听事件 **/
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500); // 设置image隐藏动画500ms
					displayedImages.add(imageUri); // 将图片uri添加到集合中
				}
			}
		}
	}
}