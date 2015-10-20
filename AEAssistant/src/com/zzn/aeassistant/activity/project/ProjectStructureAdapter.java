package com.zzn.aeassistant.activity.project;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.view.CircleImageView;
import com.zzn.aeassistant.view.tree.Node;
import com.zzn.aeassistant.view.tree.TreeListViewAdapter;
import com.zzn.aeassistant.vo.ProjectVO;

public class ProjectStructureAdapter<T> extends TreeListViewAdapter<T> {
	private Context mContext;
	private String project_id;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

	public ProjectStructureAdapter(ListView mTree, Context context,
			List<T> datas, boolean expand, String project_id)
			throws IllegalArgumentException, IllegalAccessException {
		super(mTree, context, datas, expand);
		this.mContext = context;
		this.project_id = project_id;
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_head) // 设置图片在下载期间显示的图片
				.showImageForEmptyUri(R.drawable.ic_head)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.ic_head) // 设置图片加载/解码过程中错误时候显示的图片
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
			viewHolder.head = (CircleImageView) convertView
					.findViewById(R.id.treenode_head);
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
		viewHolder.user.setText(vo.getCREATE_USER_NAME());
		imageLoader.displayImage(
				String.format(URLConstants.URL_IMG, vo.getCREATE_USER_HEAD()),
				viewHolder.head, options);
		return convertView;
	}

	@Override
	public int getItemViewType(int position) {
		ProjectVO vo = (ProjectVO) getItem(position).getData();
		return vo.getPARENT_ID().equals(project_id)
				&& !vo.getPROJECT_ID().equals(project_id) ? 0 : 1;
	}

	private final class ViewHolder {
		ImageView icon;
		CircleImageView head;
		TextView label;
		TextView user;
	}
}
