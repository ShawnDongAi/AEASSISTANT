package com.zzn.aeassistant.activity.post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.view.AttachAdapter;
import com.zzn.aeassistant.view.CircleImageView;
import com.zzn.aeassistant.view.FastenGridView;
import com.zzn.aeassistant.vo.AttchVO;
import com.zzn.aeassistant.vo.CommentVO;
import com.zzn.aeassistant.vo.PostVO;
import com.zzn.aeassistant.vo.ProjectVO;

public class PostAdapter extends BaseExpandableListAdapter {
	private Context mContext;
	private ProjectVO project;
	private List<PostVO> postList = new ArrayList<PostVO>();
	private List<List<CommentVO>> commentList = new ArrayList<>();
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	public PostAdapter(Context context) {
		this.mContext = context;
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.icon_loading) // 设置图片在下载期间显示的图片
				.showImageForEmptyUri(R.drawable.icon_failed)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.icon_failed) // 设置图片加载/解码过程中错误时候显示的图片
				.cacheInMemory(true)// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(false)// 设置下载的图片是否缓存在SD卡中
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

	public void setProject(ProjectVO projectVO) {
		this.project = projectVO;
	}

	public List<PostVO> getPostList() {
		return postList;
	}

	public void addPost(List<PostVO> data) {
		postList.addAll(data);
	}

	public void addComment(List<List<CommentVO>> data) {
		commentList.addAll(data);
	}

	public void clear() {
		postList.clear();
		commentList.clear();
	}

	private class ViewHolder {
		CircleImageView head;
		TextView name;
		TextView content;
		FastenGridView attachList;
		TextView time;
		View comment;
		View arrow;
	}

	/** 图片加载监听事件 **/
	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {
		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
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

	private class CommentHolder {
		TextView content;
		FastenGridView attachList;
		TextView time;
	}

	@Override
	public int getGroupCount() {
		return postList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return commentList.get(groupPosition).size();
	}

	@Override
	public PostVO getGroup(int groupPosition) {
		return postList.get(groupPosition);
	}

	@Override
	public CommentVO getChild(int groupPosition, int childPosition) {
		return commentList.get(groupPosition).get(childPosition);
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

	@SuppressLint("NewApi")
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.item_post, null);
			holder.head = (CircleImageView) convertView
					.findViewById(R.id.user_head);
			holder.name = (TextView) convertView.findViewById(R.id.user_name);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.attachList = (FastenGridView) convertView
					.findViewById(R.id.attach_list);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.comment = convertView.findViewById(R.id.comment);
			holder.arrow = convertView.findViewById(R.id.arrow);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final PostVO item = getGroup(groupPosition);
		imageLoader.displayImage(
				String.format(URLConstants.URL_IMG, item.getUser_head()),
				holder.head, options);
		holder.name.setText(item.getProject_name());
		holder.content.setText(item.getContent());
		holder.time.setText(item.getTime());
		holder.comment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, CommentActivity.class);
				intent.putExtra(CodeConstants.KEY_POST_ID, item.getPost_id());
				intent.putExtra(CodeConstants.KEY_PROJECT_VO, project);
				mContext.startActivity(intent);
			}
		});
		AttachAdapter attachAdapter;
		if (holder.attachList.getTag() == null) {
			attachAdapter = new AttachAdapter(mContext, false);
		} else {
			attachAdapter = (AttachAdapter) holder.attachList.getTag();
			attachAdapter.clear();
		}
		if (item.getAttch_id() != null
				&& !StringUtil.isEmpty(item.getAttch_id())) {
			for (String id : item.getAttch_id().split("#")) {
				if (!StringUtil.isEmpty(id)) {
					AttchVO vo = new AttchVO();
					vo.setATTCH_ID(id);
					vo.setTYPE(AttchVO.TYPE_IMG);
					attachAdapter.addItem(vo);
				}
			}
		}
		holder.attachList.setAdapter(attachAdapter);
		holder.attachList.setTag(attachAdapter);
		holder.attachList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (parent.getTag() != null
						&& parent.getTag() instanceof AttachAdapter) {
					((AttachAdapter) parent.getTag()).onItemClick(position);
				}
			}
		});
		holder.arrow.setRotation(isExpanded ? 180 : 0);
		holder.arrow
				.setVisibility(getChildrenCount(groupPosition) > 0 ? View.VISIBLE
						: View.INVISIBLE);
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		CommentHolder holder;
		if (convertView == null) {
			holder = new CommentHolder();
			convertView = View.inflate(mContext, R.layout.item_comment, null);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.attachList = (FastenGridView) convertView
					.findViewById(R.id.attach_list);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(holder);
		} else {
			holder = (CommentHolder) convertView.getTag();
		}
		CommentVO item = getChild(groupPosition, childPosition);
		holder.content.setText(item.getProject_name() + "："
				+ item.getContent().trim());
		holder.time.setText(item.getTime());
		AttachAdapter attachAdapter;
		if (holder.attachList.getTag() == null) {
			attachAdapter = new AttachAdapter(mContext, false);
		} else {
			attachAdapter = (AttachAdapter) holder.attachList.getTag();
			attachAdapter.clear();
		}
		if (item.getAttch_id() != null
				&& !StringUtil.isEmpty(item.getAttch_id())) {
			for (String id : item.getAttch_id().split("#")) {
				if (!StringUtil.isEmpty(id)) {
					AttchVO vo = new AttchVO();
					vo.setATTCH_ID(id);
					vo.setTYPE(AttchVO.TYPE_IMG);
					attachAdapter.addItem(vo);
				}
			}
		}
		holder.attachList.setAdapter(attachAdapter);
		holder.attachList.setTag(attachAdapter);
		holder.attachList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (parent.getTag() != null
						&& parent.getTag() instanceof AttachAdapter) {
					((AttachAdapter) parent.getTag()).onItemClick(position);
				}
			}
		});
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
}