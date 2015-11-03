package com.zzn.aeassistant.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.user.LoginActivity;
import com.zzn.aeassistant.app.PreConfig;

public class GuideActivity extends BaseActivity {
	private ViewPager pager;
	private View gotoLogin;
	private List<View> dotViewsList = new ArrayList<View>();
	private List<View> guideViewsList = new ArrayList<View>();

	@Override
	protected int layoutResID() {
		return R.layout.activity_guide;
	}

	@Override
	protected int titleStringID() {
		return 0;
	}

	@Override
	protected void initView() {
		findViewById(R.id.next).setOnClickListener(this);
		gotoLogin = findViewById(R.id.login);
		gotoLogin.setOnClickListener(this);
		View dot1 = findViewById(R.id.dot1);
		View guideView1 = View.inflate(mContext, R.layout.layout_guide, null);
		((ImageView) guideView1.findViewById(R.id.img))
				.setImageResource(R.drawable.ic_project_manager);
		((TextView) guideView1.findViewById(R.id.lable))
				.setText("项目管理：更快捷方便的创建项目、查看\n项目相关信息、建立组织架构");
		guideViewsList.add(guideView1);
		View dot2 = findViewById(R.id.dot2);
		View guideView2 = View.inflate(mContext, R.layout.layout_guide, null);
		((ImageView) guideView2.findViewById(R.id.img))
				.setImageResource(R.drawable.ic_contact);
		((TextView) guideView2.findViewById(R.id.lable))
				.setText("通讯录：清晰了解、建立、调整组织\n架构，快速联系项目成员");
		guideViewsList.add(guideView2);
		View dot3 = findViewById(R.id.dot3);
		View guideView3 = View.inflate(mContext, R.layout.layout_guide, null);
		((ImageView) guideView3.findViewById(R.id.img))
				.setImageResource(R.drawable.ic_user_center);
		((TextView) guideView3.findViewById(R.id.lable))
				.setText("工作圈：精准快速进行工作问题\n反馈、沟通交流");
		guideViewsList.add(guideView3);
		View dot4 = findViewById(R.id.dot4);
		View guideView4 = View.inflate(mContext, R.layout.layout_guide, null);
		((ImageView) guideView4.findViewById(R.id.img))
				.setImageResource(R.drawable.ic_attendance_record);
		((TextView) guideView4.findViewById(R.id.lable))
				.setText("签到：根据位置信息进行考勤签到，查询\n项目、个人签到记录方便快捷");
		guideViewsList.add(guideView4);
		dotViewsList.add(dot1);
		dotViewsList.add(dot2);
		dotViewsList.add(dot3);
		dotViewsList.add(dot4);
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new MyPagerAdapter());
		pager.setOnPageChangeListener(new MyPageChangeListener());
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.next:
			PreConfig.setFirstLoad();
			startActivity(new Intent(this, LoginActivity.class));
			finish();
			break;
		case R.id.login:
			PreConfig.setFirstLoad();
			startActivity(new Intent(this, LoginActivity.class));
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	protected boolean needLocation() {
		return false;
	}

	/**
	 * 填充ViewPager的页面适配器
	 */
	private class MyPagerAdapter extends PagerAdapter {

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(guideViewsList.get(position));
		}

		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(guideViewsList.get(position));
			return guideViewsList.get(position);
		}

		@Override
		public int getCount() {
			return guideViewsList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

		@Override
		public void finishUpdate(View arg0) {
		}
	}

	/**
	 * ViewPager的监听器 当ViewPager中页面的状态发生改变时调用
	 */
	private class MyPageChangeListener implements
			ViewPager.OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int pos) {
			gotoLogin.setVisibility(pos == 3 ? View.VISIBLE : View.INVISIBLE);
			gotoLogin.setClickable(pos == 3);
			for (int i = 0; i < dotViewsList.size(); i++) {
				if (i == pos) {
					((View) dotViewsList.get(pos))
							.setBackgroundResource(R.drawable.dot_focus);
				} else {
					((View) dotViewsList.get(i))
							.setBackgroundResource(R.drawable.dot_blur);
				}
			}
		}

	}
}
