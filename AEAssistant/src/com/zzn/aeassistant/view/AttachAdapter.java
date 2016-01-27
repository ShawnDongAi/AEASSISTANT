package com.zzn.aeassistant.view;

import java.io.File;
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
import com.zzn.aeassistant.activity.ImageActivity;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.vo.AttchVO;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

public class AttachAdapter extends BaseAdapter {
	private Context mContext;
	private List<AttchVO> attchList = new ArrayList<AttchVO>();
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private boolean isDeleteMode = false;
	private boolean editable = false;
	private OnAddAttachCallBack onAddAttachCallBack;
	private AlertDialog chooseDialog;

	public AttachAdapter(Context context, boolean editable, OnAddAttachCallBack mOnAddAttachCallBack) {
		this.mContext = context;
		this.editable = editable;
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
		if (editable) {
			attchList.add(new AttchVO());
		}
		this.onAddAttachCallBack = mOnAddAttachCallBack;
		String items[] = new String[]{mContext.getString(R.string.photograph), mContext.getString(R.string.album)};
		chooseDialog = new AlertDialog.Builder(mContext).setTitle(R.string.title_attach_select)
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (onAddAttachCallBack != null) {
							switch (which) {
							case 0:
								onAddAttachCallBack.onAddCamera();
								break;
							case 1:
								onAddAttachCallBack.onAddPhoto();
								break;
							default:
								break;
							}
						}
					}
				}).create();
	}

	public void addItem(AttchVO vo) {
		if (editable) {
			attchList.add(getCount() - 1, vo);
		} else {
			attchList.add(vo);
		}
	}

	public void clear() {
		attchList.clear();
		if (editable) {
			attchList.add(new AttchVO());
		}
	}

	public String getAttachIDs() {
		StringBuilder ids = new StringBuilder();
		for (AttchVO vo : attchList) {
			if (!StringUtil.isEmpty(vo.getATTCH_ID())) {
				ids.append(vo.getATTCH_ID() + "#");
			}
		}
		if (ids.length() > 0) {
			ids.deleteCharAt(ids.length() - 1);
		}
		return ids.toString();
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
		if (position == getCount() - 1 && editable) {
			holder.delete.setVisibility(View.GONE);
			holder.image.setImageResource(R.drawable.ic_add);
		} else {
			AttchVO item = getItem(position);
			if (item.getTYPE().equals(AttchVO.TYPE_IMG)) {
				if (!StringUtil.isEmpty(item.getLOCAL_PATH())) {
					imageLoader.displayImage(Uri.fromFile(new File(item.getLOCAL_PATH())).toString(), holder.image,
							options, animateFirstListener);
				} else if (!StringUtil.isEmpty(item.getATTCH_ID())) {
					imageLoader.displayImage(String.format(URLConstants.URL_IMG, item.getATTCH_ID()), holder.image,
							options);
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
				holder.delete.setVisibility(isDeleteMode ? View.VISIBLE : View.GONE);
				holder.delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						attchList.remove(position);
						notifyDataSetChanged();
					}
				});
			}
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
		if (position == getCount() - 1) {
			if (getCount() >= 9) {
				ToastUtil.show(R.string.much_file);
				return;
			}
			if (chooseDialog != null && !chooseDialog.isShowing()) {
				chooseDialog.show();
			}
			return;
		}
		ArrayList<String> imgs = new ArrayList<String>();
		int count = editable ? getCount() - 1 : getCount();
		for (int i = 0; i < count; i++) {
			imgs.add(String.format(URLConstants.URL_IMG, getItem(i).getATTCH_ID()));
		}
		Intent intent = new Intent(mContext, ImageActivity.class);
		intent.putStringArrayListExtra(CodeConstants.KEY_IMG_URL, imgs);
		intent.putExtra(CodeConstants.KEY_POSITION, position);
		mContext.startActivity(intent);
		// AttchVO item = getItem(position);
		// String path = item.getLOCAL_PATH();
		// if (StringUtil.isEmpty(path)) {
		// path = imageLoader
		// .getInstance()
		// .getDiscCache()
		// .get(String.valueOf()).getPath();
		// }
		// if (FileUtils.exists(path)) {
		// FileUtils.openImg(mContext, new File(path));
		// }
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
	
	public interface OnAddAttachCallBack {
		public void onAddPhoto();
		public void onAddCamera();
	}
}