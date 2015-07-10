package com.zzn.aeassistant.view.pulltorefresh;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.BaseAdapter;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.view.pla.MultiColumnListView;

public class PullToRefreshPLAListView extends
		PullToRefreshBase<MultiColumnListView> {

	public PullToRefreshPLAListView(Context context) {
		super(context);
	}

	public PullToRefreshPLAListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullToRefreshPLAListView(Context context, Mode mode) {
		super(context, mode);
	}

	public PullToRefreshPLAListView(Context context, Mode mode,
			AnimationStyle style) {
		super(context, mode, style);
	}

	@Override
	public Orientation getPullToRefreshScrollDirection() {
		return Orientation.VERTICAL;
	}

	@Override
	protected MultiColumnListView createRefreshableView(Context context,
			AttributeSet attrs) {
		MultiColumnListView multiColumnListView;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			multiColumnListView = new InternalMultiColumnListViewSDK9(context,
					attrs);
		} else {
			multiColumnListView = new MultiColumnListView(context, attrs);
		}

		multiColumnListView.setId(R.id.scrapped_view);
		return multiColumnListView;
	}

	@Override
	protected boolean isReadyForPullStart() {
		return isFirstItemVisible();
	}

	@Override
	protected boolean isReadyForPullEnd() {
		return isLastItemVisible();
	}

	public void setAdapter(BaseAdapter adapter) {
		mRefreshableView.setAdapter(adapter);
	}

	@TargetApi(9)
	final class InternalMultiColumnListViewSDK9 extends MultiColumnListView {

		public InternalMultiColumnListViewSDK9(Context context,
				AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
				int scrollY, int scrollRangeX, int scrollRangeY,
				int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

			final boolean returnValue = super.overScrollBy(deltaX, deltaY,
					scrollX, scrollY, scrollRangeX, scrollRangeY,
					maxOverScrollX, maxOverScrollY, isTouchEvent);

			// Does all of the hard work...
			OverscrollHelper.overScrollBy(PullToRefreshPLAListView.this,
					deltaX, scrollX, deltaY, scrollY, getScrollRange(),
					isTouchEvent);

			return returnValue;
		}

		/**
		 * Taken from the AOSP ScrollView source
		 */
		private int getScrollRange() {
			int scrollRange = 0;
			if (getChildCount() > 0) {
				View child = getChildAt(0);
				scrollRange = Math.max(0, child.getHeight()
						- (getHeight() - getPaddingBottom() - getPaddingTop()));
			}
			return scrollRange;
		}
	}

	private boolean isFirstItemVisible() {
		final Adapter adapter = mRefreshableView.getAdapter();
		if (null == adapter || adapter.isEmpty()) {
			if (DEBUG) {
				Log.d(LOG_TAG, "isFirstItemVisible. Empty View.");
			}
			return true;
		} else {
			/**
			 * This check should really just be:
			 * mRefreshableView.getFirstVisiblePosition() == 0, but PtRListView
			 * internally use a HeaderView which messes the positions up. For
			 * now we'll just add one to account for it and rely on the inner
			 * condition which checks getTop().
			 */
			if (mRefreshableView.getFirstVisiblePosition() <= 1) {
				final View firstVisibleChild = mRefreshableView.getChildAt(0);
				if (firstVisibleChild != null) {
					return firstVisibleChild.getTop() >= mRefreshableView
							.getTop();
				}
			}
		}
		return false;
	}

	private boolean isLastItemVisible() {
		final Adapter adapter = mRefreshableView.getAdapter();

		if (null == adapter || adapter.isEmpty()) {
			if (DEBUG) {
				Log.d(LOG_TAG, "isLastItemVisible. Empty View.");
			}
			return true;
		} else {
			final int lastItemPosition = mRefreshableView.getCount() - 1;
			final int lastVisiblePosition = mRefreshableView
					.getLastVisiblePosition();
			if (DEBUG) {
				Log.d(LOG_TAG, "isLastItemVisible. Last Item Position: "
						+ lastItemPosition + " Last Visible Pos: "
						+ lastVisiblePosition);
			}
			/**
			 * This check should really just be: lastVisiblePosition ==
			 * lastItemPosition, but PtRListView internally uses a FooterView
			 * which messes the positions up. For me we'll just subtract one to
			 * account for it and rely on the inner condition which checks
			 * getBottom().
			 */
			if (lastVisiblePosition >= lastItemPosition - 1) {
				final int childIndex = lastVisiblePosition
						- mRefreshableView.getFirstVisiblePosition();
				final View lastVisibleChild = mRefreshableView
						.getChildAt(childIndex);
				if (lastVisibleChild != null) {
					return lastVisibleChild.getBottom() <= mRefreshableView
							.getBottom();
				}
			}
		}
		return false;
	}
}
