package com.zzn.aeassistant.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.user.UserActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.fragment.AttendanceRecordFragment;
import com.zzn.aeassistant.fragment.HomeFragment;
import com.zzn.aeassistant.fragment.ProjectManagerFragment;
import com.zzn.aeassistant.fragment.SettingFragment;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.CircleImageView;
import com.zzn.aeassistant.view.menudrawer.OverlayDrawer;
import com.zzn.aeassistant.vo.Module;

public class IndexActivity extends BaseActivity implements OnItemClickListener {

	public static final String ACTION_USER_INFO_CHANGED = "com.zzn.aeassistant.user_info_changed";
	private OverlayDrawer mDrawer;
	private ViewGroup mContainer;
	private View userLayout;
	private TextView mUserName;
	private CircleImageView mUserHead;
	private ListView menuList;
	private ModuleAdapter adapter;
	
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	private FragmentTransaction fragTrans;
	
	@Override
	protected int layoutResID() {
		return R.layout.activity_index;
	}

	@Override
	protected int titleStringID() {
		return R.string.title_home;
	}

	@SuppressLint("NewApi")
	@Override
	protected void initView() {
		setSwipeBackEnable(false);
		mContainer = (ViewGroup) findViewById(R.id.fragment_container);
		mDrawer = (OverlayDrawer) findViewById(R.id.menu_drawer);
		back.setVisibility(View.INVISIBLE);
//		mDrawer.setContentView(R.layout.fragment_home);
		View menuView = View.inflate(mContext, R.layout.menu_drawer, null);
		userLayout = menuView.findViewById(R.id.index_user);
		mUserName = (TextView) menuView.findViewById(R.id.index_name);
		mUserHead = (CircleImageView) menuView.findViewById(R.id.index_head);
		menuList = (ListView) menuView.findViewById(R.id.index_menu_list);
		mDrawer.setMenuView(menuView);
		userLayout.setOnClickListener(this);
		initImageLoader();
		initUserView();
		registerReceiver(userInfoReceiver, new IntentFilter(
				ACTION_USER_INFO_CHANGED));
		initModuleView();
		fragTrans = getSupportFragmentManager().beginTransaction();
		fragTrans.replace(R.id.fragment_container, adapter.getItem(0).getFragment());
		fragTrans.commit();
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.index_user:
			startActivity(new Intent(mContext, UserActivity.class));
			break;
		}
	}

	private void initModuleView() {
		adapter = new ModuleAdapter(mContext);
		// 主页
		adapter.addItem(new Module(R.drawable.ic_user_center,
				R.string.title_home, new HomeFragment()));
		// 项目管理
		adapter.addItem(new Module(R.drawable.ic_project_manager,
				R.string.title_project_manager, new ProjectManagerFragment()));
		// 考勤记录
		adapter.addItem(new Module(R.drawable.ic_attendance_record,
				R.string.title_attendance_record, new AttendanceRecordFragment()));
		// 设置
		adapter.addItem(new Module(R.drawable.ic_setting,
				R.string.title_setting, new SettingFragment()));
		menuList.setAdapter(adapter);
		menuList.setOnItemClickListener(this);
	}
	
	private void initUserView() {
		mUserName.setText(AEApp.getCurrentUser()
				.getUSER_NAME());
		imageLoader.displayImage(
				String.format(URLConstants.URL_DOWNLOAD,
						AEApp.getCurrentUser().getBIG_HEAD()),
				mUserHead, options);
	}
	
	@SuppressWarnings("deprecation")
	private void initImageLoader() {
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_head) // 设置图片在下载期间显示的图片
				.showImageForEmptyUri(R.drawable.ic_head)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.ic_head) // 设置图片加载/解码过程中错误时候显示的图片
				.cacheInMemory(true)// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
				.imageScaleType(ImageScaleType.EXACTLY)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				.displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
				.build();// 构建完成
	}
	
	/**
	 * 2秒内点击两次返回键退出
	 */
	private long lastPressTime = 0;

	@Override
	public void onBackPressed() {
		long time = System.currentTimeMillis();
		if (time - lastPressTime > 2000) {
			lastPressTime = time;
			ToastUtil.show(R.string.exit_toast);
		} else {
			super.onBackPressed();
			AEApp.getInstance().exit();
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(userInfoReceiver);
		super.onDestroy();
	}

	@Override
	protected boolean needLocation() {
		return true;
	}
	
	private BroadcastReceiver userInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			initUserView();
		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		fragTrans = getSupportFragmentManager().beginTransaction();
		fragTrans.replace(R.id.fragment_container, adapter.getItem(position).getFragment());
		fragTrans.commit();
		setTitle(getString(adapter.getItem(position).getTitleID()));
		mDrawer.closeMenu(true);
	}
}