package com.zzn.aeassistant.activity.attendance;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.view.pla.STGVImageView;
import com.zzn.aeassistant.view.squareprogressbar.SquareProgressView;
import com.zzn.aeassistant.vo.AttendanceVO;

public class SumUserAdapter extends BaseAdapter {
	private Context mContext;
	private List<AttendanceVO> datas = new ArrayList<AttendanceVO>();
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

	@SuppressWarnings("deprecation")
	public SumUserAdapter(Context context) {
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

	public void addData(List<AttendanceVO> datas) {
		this.datas.addAll(datas);
		notifyDataSetChanged();
	}

	public void setData(List<AttendanceVO> datas) {
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
	public AttendanceVO getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View
					.inflate(mContext, R.layout.item_attendance, null);
			holder.address = (TextView) convertView.findViewById(R.id.address);
			holder.time = (TextView) convertView.findViewById(R.id.date);
			holder.photo = (STGVImageView) convertView
					.findViewById(R.id.thumbnail);
			holder.progress = (SquareProgressView) convertView
					.findViewById(R.id.progress);
			holder.progress.setColor(mContext.getResources().getColor(
					R.color.theme_green_pressed));
			holder.progress.setWidthInDp(2);
			holder.project = (TextView) convertView.findViewById(R.id.project);
			holder.normal = (ImageView) convertView.findViewById(R.id.normal);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AttendanceVO vo = getItem(position);
		holder.address.setText(vo.getAddress());
		holder.time.setText(vo.getDate());
		holder.project.setText(vo.getProject_name());
		holder.normal.setVisibility(vo.getNormal() != null
				&& vo.getNormal().equals("1") ? View.VISIBLE : View.GONE);
		holder.photo.mHeight = vo.getPhoto_height();
		holder.photo.mWidth = vo.getPhoto_width();
		imageLoader.displayImage(
				String.format(URLConstants.URL_DOWNLOAD, vo.getImgURL()),
				holder.photo, options, new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						holder.progress.setProgress(0);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason arg2) {
						holder.progress.setProgress(0);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap arg2) {
						holder.progress.setProgress(0);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						holder.progress.setProgress(0);
					}
				}, new ImageLoadingProgressListener() {
					@Override
					public void onProgressUpdate(String imageUri, View view,
							int current, int total) {
						holder.progress.setProgress(current * 100.0 / total);
					}
				});
		return convertView;
	}

	private class ViewHolder {
		private TextView time;
		private TextView address;
		private STGVImageView photo;
		private SquareProgressView progress;
		private TextView project;
		private ImageView normal;
	}
}
