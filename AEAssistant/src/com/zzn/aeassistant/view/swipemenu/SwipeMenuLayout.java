package com.zzn.aeassistant.view.swipemenu;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;

public class SwipeMenuLayout extends FrameLayout {

	private static final int CONTENT_VIEW_ID = 1;
	private static final int MENU_VIEW_ID = 2;

	private static final int STATE_CLOSE = 0;
	private static final int STATE_OPEN = 1;

	private View mContentView;
	private SwipeMenuView mMenuView;
	private int mDownX;
	private int state = STATE_CLOSE;
	private GestureDetectorCompat mGestureDetector;
	private OnGestureListener mGestureListener;
	private boolean isFling;
	private int MIN_FLING = dp2px(15);
	private int MAX_VELOCITYX = -dp2px(500);
	private ScrollerCompat mOpenScroller;
	private ScrollerCompat mCloseScroller;
	private int mBaseX;
	private int position;
	private Interpolator mCloseInterpolator;
	private Interpolator mOpenInterpolator;

	public SwipeMenuLayout(View contentView, SwipeMenuView menuView) {
		this(contentView, menuView, null, null);
	}

	public SwipeMenuLayout(View contentView, SwipeMenuView menuView,
			Interpolator closeInterpolator, Interpolator openInterpolator) {
		super(contentView.getContext());
		mCloseInterpolator = closeInterpolator;
		mOpenInterpolator = openInterpolator;
		mContentView = contentView;
		mMenuView = menuView;
		mMenuView.setLayout(this);
		init();
	}

	private SwipeMenuLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private SwipeMenuLayout(Context context) {
		super(context);
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
		mMenuView.setPosition(position);
	}

	private void init() {
		setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		mGestureListener = new SimpleOnGestureListener() {
			@Override
			public boolean onDown(MotionEvent e) {
				isFling = false;
				return true;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				if ((e1.getX() - e2.getX()) > MIN_FLING
						&& velocityX < MAX_VELOCITYX) {
					isFling = true;
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		};
		mGestureDetector = new GestureDetectorCompat(getContext(),
				mGestureListener);
		if (mCloseInterpolator != null) {
			mCloseScroller = ScrollerCompat.create(getContext(),
					mCloseInterpolator);
		} else {
			mCloseScroller = ScrollerCompat.create(getContext());
		}
		if (mOpenInterpolator != null) {
			mOpenScroller = ScrollerCompat.create(getContext(),
					mOpenInterpolator);
		} else {
			mOpenScroller = ScrollerCompat.create(getContext());
		}

		LayoutParams contentParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mContentView.setLayoutParams(contentParams);
		if (mContentView.getId() < 1) {
			mContentView.setId(CONTENT_VIEW_ID);
		}

		mMenuView.setId(MENU_VIEW_ID);
		mMenuView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		addView(mContentView);
		addView(mMenuView);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	public boolean onSwipe(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownX = (int) event.getX();
			isFling = false;
			break;
		case MotionEvent.ACTION_MOVE:
			// Log.i("byz", "downX = " + mDownX + ", moveX = " + event.getX());
			int dis = (int) (mDownX - event.getX());
			if (state == STATE_OPEN) {
				dis += mMenuView.getWidth();
			}
			swipe(dis);
			break;
		case MotionEvent.ACTION_UP:
			if (isFling || (mDownX - event.getX()) > (mMenuView.getWidth() / 2)) {
				// open
				smoothOpenMenu();
			} else {
				// close
				smoothCloseMenu();
				return false;
			}
			break;
		}
		return true;
	}

	public boolean isOpen() {
		return state == STATE_OPEN;
	}

	private void swipe(int dis) {
		int menuWidth = mMenuView.getWidth();
		int delta = menuWidth / 10;
		int menuDis = dis;
		if (dis > menuWidth) {
			menuDis = menuWidth;
		}
		if (dis < 0) {
			menuDis = 0;
		}
		if (dis > menuWidth + delta) {
			mDownX -= (dis - menuWidth - delta);
			dis = menuDis + delta;
		}
		if (dis < -delta) {
			mDownX -= dis + delta;
			dis = -delta;
		}
		mContentView.layout(-dis, mContentView.getTop(),
				mContentView.getWidth() - dis, getMeasuredHeight());
		if (dis <= menuWidth || dis >= 0) {
			mMenuView.layout(mContentView.getWidth() - menuDis,
					mMenuView.getTop(),
					mContentView.getWidth() + mMenuView.getWidth() - menuDis,
					mMenuView.getBottom());
		}
	}

	@Override
	public void computeScroll() {
		if (state == STATE_OPEN) {
			if (mOpenScroller.computeScrollOffset()) {
				swipe(mOpenScroller.getCurrX());
				postInvalidate();
			}
		} else {
			if (mCloseScroller.computeScrollOffset()) {
				swipe(mBaseX - mCloseScroller.getCurrX());
				postInvalidate();
			}
		}
	}

	public void smoothCloseMenu() {
		state = STATE_CLOSE;
		mBaseX = -mContentView.getLeft();
		int menuWidth = mMenuView.getWidth();
		int duration = (int) (200 * Math.abs(mBaseX * 1f / menuWidth));
		if (mBaseX < 0) {
			duration = (int) (200 * Math.abs(mBaseX * 5f / menuWidth));
		}
		mCloseScroller.startScroll(0, 0, mBaseX, 0, duration);
		postInvalidate();
	}

	public void smoothOpenMenu() {
		state = STATE_OPEN;
		int menuWidth = mMenuView.getWidth();
		int contentLeft = mContentView.getLeft();
		int duration = (int) (200 * Math.abs(1 - contentLeft * 1f / menuWidth));
		if (contentLeft < -menuWidth) {
			duration = (int) (200 * Math.abs((contentLeft + menuWidth) * 5f
					/ menuWidth));
		}
		mOpenScroller.startScroll(-contentLeft, 0, menuWidth + contentLeft, 0,
				duration);
		postInvalidate();
	}

	public void closeMenu() {
		if (mCloseScroller.computeScrollOffset()) {
			mCloseScroller.abortAnimation();
		}
		if (state == STATE_OPEN) {
			state = STATE_CLOSE;
			swipe(0);
		}
	}

	public void openMenu() {
		if (state == STATE_CLOSE) {
			state = STATE_OPEN;
			swipe(mMenuView.getWidth());
		}
	}

	public View getContentView() {
		return mContentView;
	}

	public SwipeMenuView getMenuView() {
		return mMenuView;
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getContext().getResources().getDisplayMetrics());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mMenuView.measure(MeasureSpec.makeMeasureSpec(0,
				MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(
				getMeasuredHeight(), MeasureSpec.EXACTLY));
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		mContentView.layout(0, 0, getMeasuredWidth(),
				mContentView.getMeasuredHeight());
		mMenuView.layout(getMeasuredWidth(), 0,
				getMeasuredWidth() + mMenuView.getMeasuredWidth(),
				mContentView.getMeasuredHeight());
		// setMenuHeight(mContentView.getMeasuredHeight());
		// bringChildToFront(mContentView);
	}

	public void setMenuHeight(int measuredHeight) {
		Log.i("byz", "pos = " + position + ", height = " + measuredHeight);
		LayoutParams params = (LayoutParams) mMenuView.getLayoutParams();
		if (params.height != measuredHeight) {
			params.height = measuredHeight;
			mMenuView.setLayoutParams(mMenuView.getLayoutParams());
		}
	}
}