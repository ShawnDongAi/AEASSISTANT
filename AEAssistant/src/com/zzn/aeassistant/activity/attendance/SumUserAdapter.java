package com.zzn.aeassistant.activity.attendance;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.vo.AttendanceVO;

public class SumUserAdapter extends BaseAdapter {
	private Context mContext;
	private List<AttendanceVO> datas = new ArrayList<AttendanceVO>();
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

	public SumUserAdapter(Context context) {
		this.mContext = context;
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_download) // 设置图片在下载期间显示的图片
				.showImageForEmptyUri(R.drawable.ic_launcher)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.ic_launcher) // 设置图片加载/解码过程中错误时候显示的图片
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
//				.displayer(new RoundedBitmapDisplayer(20))// 是否设置为圆角，弧度为多少
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
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View
					.inflate(mContext, R.layout.item_attendance, null);
			holder.address = (TextView) convertView.findViewById(R.id.address);
			holder.time = (TextView) convertView.findViewById(R.id.date);
			holder.photo = (ImageView) convertView.findViewById(R.id.thumbnail);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AttendanceVO vo = getItem(position);
		holder.address.setText(vo.getAddress());
		holder.time.setText(vo.getDate());
		try {
			imageLoader.displayImage(
					String.format(URLConstants.URL_DOWNLOAD,
							URLEncoder.encode(vo.getImgURL(), "UTF-8")),
					holder.photo, options);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return convertView;
	}

	private class ViewHolder {
		private TextView time;
		private TextView address;
		private ImageView photo;
	}
}
