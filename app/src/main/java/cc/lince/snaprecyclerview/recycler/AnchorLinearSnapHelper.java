package cc.lince.snaprecyclerview.recycler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class AnchorLinearSnapHelper extends LinearSnapHelper {

    private int anchorX; // 锚点位置 (Horizontal)

    private int anchorY; // 锚点位置 (Vertical)

    private RecyclerView mRecyclerView;

    private View closestChild;

    private LinearLayoutManager layoutManager;

    private OnSnapListener onSnapListener;

    @Nullable
    private OrientationHelper mVerticalHelper;

    @Nullable
    private OrientationHelper mHorizontalHelper;


    public AnchorLinearSnapHelper() {
    }

    public void setAnchorVertical(int y) {
        this.anchorY = y;
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setPadding(0, anchorY, 0, mRecyclerView.getHeight() - anchorY);
                mRecyclerView.smoothScrollToPosition(0);
            }
        });
    }

    public void setAnchorHorizontal(int x) {
        this.anchorX = x;
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setPadding(anchorX, 0, mRecyclerView.getWidth() - anchorX, 0);
                mRecyclerView.smoothScrollToPosition(0);
            }
        });
    }

    public void setOnSnapListener(final OnSnapListener onSnapListener) {
        this.onSnapListener = onSnapListener;
    }

    @Override
    public void attachToRecyclerView(@Nullable final RecyclerView recyclerView)
            throws IllegalStateException {

        if (recyclerView != null) {
            mRecyclerView = recyclerView;
            layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            recyclerView.setClipToPadding(false);
            recyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener());
            recyclerView.addOnScrollListener(new OnRecyclerScrollListener(layoutManager.getOrientation()));
        }

        super.attachToRecyclerView(recyclerView);

    }

    @Nullable
    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,
                                              @NonNull View targetView) {
        int[] out = new int[2];
        if (layoutManager.canScrollHorizontally()) {
            out[0] = distanceToCenter(targetView, getHorizontalHelper(layoutManager), anchorX);
        } else {
            out[0] = 0;
        }

        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToCenter(targetView, getVerticalHelper(layoutManager), anchorY);
        } else {
            out[1] = 0;
        }
        return out;
    }

    private int distanceToCenter(@NonNull View targetView, OrientationHelper helper, int center) {
        final int childCenter = helper.getDecoratedStart(targetView)
                + (helper.getDecoratedMeasurement(targetView) / 2);
        return childCenter - center;
    }

    @Nullable
    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.canScrollHorizontally()) {
            return findSpecificView(layoutManager, getHorizontalHelper(layoutManager), anchorX);
        } else if (layoutManager.canScrollVertically()) {
            return findSpecificView(layoutManager, getVerticalHelper(layoutManager), anchorY);
        }
        return null;
    }

    private View findSpecificView(RecyclerView.LayoutManager layoutManager,
                                  OrientationHelper helper, final int center) {
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        }

        View closestChild = null;
        int absClosest = Integer.MAX_VALUE;

        for (int i = 0; i < childCount; i++) {
            final View child = layoutManager.getChildAt(i);
            int childCenter = helper.getDecoratedStart(child)
                    + (helper.getDecoratedMeasurement(child) / 2);
            int absDistance = Math.abs(childCenter - center);

            /* if child center is closer than previous closest, set it as closest  **/
            if (absDistance < absClosest) {
                absClosest = absDistance;
                closestChild = child;
            }
        }
        return closestChild;
    }

    @NonNull
    private OrientationHelper getVerticalHelper(@NonNull RecyclerView.LayoutManager layoutManager) {
        if (mVerticalHelper == null || mVerticalHelper.getLayoutManager() != layoutManager) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return mVerticalHelper;
    }

    @NonNull
    private OrientationHelper getHorizontalHelper(@NonNull RecyclerView.LayoutManager layoutManager) {
        if (mHorizontalHelper == null || mHorizontalHelper.getLayoutManager() != layoutManager) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return mHorizontalHelper;
    }

    private class OnRecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

        private final GestureDetectorCompat gestureDetectorCompat;

        OnRecyclerItemClickListener() {
            gestureDetectorCompat = new GestureDetectorCompat(mRecyclerView.getContext(),
                    new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onSingleTapUp(MotionEvent e) {

                            View child = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
                            if (child == null) {
                                return true;
                            }

                            if (layoutManager.getOrientation() == RecyclerView.HORIZONTAL) {
                                float viewCenter = child.getX() + child.getWidth() / 2;
                                final int deltaX = (int) (viewCenter - anchorX);
                                mRecyclerView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (deltaX != 0)
                                            mRecyclerView.smoothScrollBy(deltaX, 0);
                                    }
                                });
                            } else if (layoutManager.getOrientation() == RecyclerView.VERTICAL) {
                                float viewCenter = (int) (child.getY() + child.getHeight() / 2);
                                final int deltaY = (int) (viewCenter - anchorY);
                                mRecyclerView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (deltaY != 0)
                                            mRecyclerView.smoothScrollBy(0, deltaY);
                                    }
                                });
                            }
                            return true;
                        }
                    });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            gestureDetectorCompat.onTouchEvent(e);
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            gestureDetectorCompat.onTouchEvent(e);
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    private class OnRecyclerScrollListener extends RecyclerView.OnScrollListener {
        private int mOrientation;

        OnRecyclerScrollListener(int orientation) {
            this.mOrientation = orientation;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                /* TODO
                    IDLE 状态可能会回调两次
                    1. 滑动完成后
                    2. 1 完成后如果没有到达锚点，snapToTargetExistingView 方法调整位置之后会再次回调
                    此处需要在最后一次回调时更新选中状态
                 */
                int[] ints = calculateDistanceToFinalSnap(layoutManager, findSnapView(layoutManager));

                if (ints != null && ints.length == 2 && ints[0] == 0 && ints[1] == 0) {

                    if (onSnapListener != null) {
                        onSnapListener.onSnapStart(closestChild);
                    }

                    if (mOrientation == RecyclerView.VERTICAL) {
                        getClosestVerticalView(anchorY);
                    } else if (mOrientation == RecyclerView.HORIZONTAL) {
                        getClosestHorizontalView(anchorX);
                    }

                    if (onSnapListener != null) {
                        onSnapListener.onSnapEnd(closestChild);
                    }
                }
            }
        }
    }

    private void getClosestHorizontalView(int center) {

        int absClosest = Integer.MAX_VALUE;

        for (int i = 0; i < layoutManager.getChildCount(); i++) {
            final View child = layoutManager.getChildAt(i);

            int childCenter = getHorizontalHelper(layoutManager).getDecoratedStart(child)
                    + (getHorizontalHelper(layoutManager).getDecoratedMeasurement(child) / 2);
            int absDistance = Math.abs(childCenter - center);

            if (absDistance < absClosest) {
                absClosest = absDistance;
                closestChild = child;
            }
        }
    }

    private void getClosestVerticalView(int center) {

        int absClosest = Integer.MAX_VALUE;

        for (int i = 0; i < layoutManager.getChildCount(); i++) {
            final View child = layoutManager.getChildAt(i);

            int childCenter = getVerticalHelper(layoutManager).getDecoratedStart(child)
                    + (getVerticalHelper(layoutManager).getDecoratedMeasurement(child) / 2);
            int absDistance = Math.abs(childCenter - center);

            if (absDistance < absClosest) {
                absClosest = absDistance;
                closestChild = child;
            }
        }
    }

    public interface OnSnapListener {

        void onSnapStart(View preView);

        void onSnapEnd(View curView);
    }
}