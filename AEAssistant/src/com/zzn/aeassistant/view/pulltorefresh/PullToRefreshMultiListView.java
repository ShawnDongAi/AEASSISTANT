package com.zzn.aeassistant.view.pulltorefresh;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.View;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.view.multicolumn.MultiColumnListView;

public class PullToRefreshMultiListView extends PullToRefreshBase<MultiColumnListView> {

	public PullToRefreshMultiListView(Context context) {
		super(context);
	}

	public PullToRefreshMultiListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullToRefreshMultiListView(Context context, Mode mode) {
		super(context, mode);
	}

	public PullToRefreshMultiListView(Context context, Mode mode, AnimationStyle style) {
		super(context, mode, style);
	}

	@Override
	public final Orientation getPullToRefreshScrollDirection() {
		return Orientation.VERTICAL;
	}

	@Override
	protected MultiColumnListView createRefreshableView(Context context, AttributeSet attrs) {
		MultiColumnListView multiColumnListView;
		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
			multiColumnListView = new InternalMultiColumnListViewSDK9(context, attrs);
		} else {
			multiColumnListView = new MultiColumnListView(context, attrs);
		}

		multiColumnListView.setId(R.id.scrapped_view);
		return multiColumnListView;
	}

	@Override
	protected boolean isReadyForPullStart() {
		return mRefreshableView.getScrollY() == 0;
	}

	@Override
	protected boolean isReadyForPullEnd() {
		View multiColumnListViewChild = mRefreshableView.getChildAt(0);
		if (null != multiColumnListViewChild) {
			return mRefreshableView.getScrollY() >= (multiColumnListViewChild.getHeight() - getHeight());
		}
		return false;
	}

	@TargetApi(9)
	final class InternalMultiColumnListViewSDK9 extends MultiColumnListView {

		public InternalMultiColumnListViewSDK9(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
				int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

			final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
					scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

			// Does all of the hard work...
			OverscrollHelper.overScrollBy(PullToRefreshMultiListView.this, deltaX, scrollX, deltaY, scrollY,
					getScrollRange(), isTouchEvent);

			return returnValue;
		}

		/**
		 * Taken from the AOSP MultiColumnListView source
		 */
		private int getScrollRange() {
			int scrollRange = 0;
			if (getChildCount() > 0) {
				View child = getChildAt(0);
				scrollRange = Math.max(0, child.getHeight() - (getHeight() - getPaddingBottom() - getPaddingTop()));
			}
			return scrollRange;
		}
	}
}
