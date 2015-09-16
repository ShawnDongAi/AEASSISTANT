package com.zzn.aeassistant.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.FileUtils;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.vo.AttchVO;

public class AttachAdapter extends BaseAdapter {
	private Context mContext;
	private List<AttchVO> attchList = new ArrayList<AttchVO>();
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private boolean isDeleteMode = false;
	private boolean editable = false;

	public AttachAdapter(Context context, boolean editable) {
		this.mContext = context;
		this.editable = editable;
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

	public void addItem(AttchVO vo) {
		attchList.add(vo);
	}

	@Override
	public int getCount() {
		return attchList.size();
	}

	@Override
	public AttchVO getItem(int position) {
		return attchList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.item_attach, null);
			holder.image = (ImageView) convertView.findViewById(R.id.img);
			holder.delete = (Button) convertView.findViewById(R.id.delete);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AttchVO item = getItem(position);
		if (item.getTYPE().equals(AttchVO.TYPE_IMG)) {
			if (!StringUtil.isEmpty(item.getLOCAL_PATH())) {
				imageLoader.displayImage(item.getLOCAL_PATH(), holder.image,
						options, animateFirstListener);
			} else if (!StringUtil.isEmpty(item.getURL())) {
				imageLoader
						.displayImage(
								String.format(URLConstants.URL_DOWNLOAD,
										item.getURL()), holder.image, options);
			}
		} else if (item.getTYPE().equals(AttchVO.TYPE_AUDIO)) {
			holder.image.setImageResource(R.drawable.ic_voice);
		} else if (item.getTYPE().equals(AttchVO.TYPE_DOC)) {
			holder.image.setImageResource(R.drawable.ic_word);
		} else if (item.getTYPE().equals(AttchVO.TYPE_EXCEL)) {
			holder.image.setImageResource(R.drawable.ic_excel);
		} else if (item.getTYPE().equals(AttchVO.TYPE_PDF)) {
			holder.image.setImageResource(R.drawable.ic_pdf);
		} else {
			holder.image.setImageResource(R.drawable.ic_file);
		}
		if (editable) {
			holder.delete
					.setVisibility(isDeleteMode ? View.VISIBLE : View.GONE);
			holder.delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					attchList.remove(position);
					notifyDataSetChanged();
				}
			});
		}
		return convertView;
	}

	private class ViewHolder {
		private ImageView image;
		private Button delete;
	}

	public void onItemLongClick() {
		isDeleteMode = !isDeleteMode;
		notifyDataSetChanged();
	}

	public void onItemClick(int position) {
		if (isDeleteMode) {
			isDeleteMode = false;
			notifyDataSetChanged();
			return;
		}
		AttchVO item = getItem(position);
		String path = item.getLOCAL_PATH();
		if (StringUtil.isEmpty(path) && StringUtil.isEmpty(item.getURL())) {
			path = String.valueOf(item.getURL())
					+ item.getNAME().substring(item.getNAME().lastIndexOf("."),
							item.getNAME().length());
		}
		if (FileUtils.exists(path)) {
			FileUtils.openFile(mContext, new File(path));
		}
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
}