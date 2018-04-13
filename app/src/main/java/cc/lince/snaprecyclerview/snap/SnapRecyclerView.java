package cc.lince.snaprecyclerview.snap;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class SnapRecyclerView extends RecyclerView {

    private LinearLayoutManager layoutManager;
    private AnchorLinearSnapHelper snapHelper;
    private OnAnchorListener onAnchorListener;

    private View closestChild;

    @Nullable
    private OrientationHelper mHorizontalHelper;

    @Nullable
    private OrientationHelper mVerticalHelper;

    public SnapRecyclerView(Context context) {
        this(context, null, 0);
    }

    public SnapRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SnapRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setClipToPadding(false);
    }

    public void setOnAnchorListener(final OnAnchorListener onAnchorListener) {
        this.onAnchorListener = onAnchorListener;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        layoutManager = (LinearLayoutManager) layout;
        super.setLayoutManager(layout);
    }

    public void setAnchorVertical(final int anchorY) {
        if (snapHelper == null) {
            snapHelper = new AnchorLinearSnapHelper();
            snapHelper.attachToRecyclerView(SnapRecyclerView.this);
        }
        snapHelper.setAnchorVertical(anchorY);

        addOnItemTouchListener(new OnRecyclerItemClickListener(anchorY));

        addOnScrollListener(new OnRecyclerScrollListener(layoutManager.getOrientation(), anchorY));

        post(new Runnable() {
            @Override
            public void run() {
                setPadding(0, anchorY, 0, getHeight() - anchorY);
                smoothScrollToPosition(0);
            }
        });
    }

    public void setAnchorHorizontal(final int anchorX) {
        if (snapHelper == null) {
            snapHelper = new AnchorLinearSnapHelper();
            snapHelper.attachToRecyclerView(SnapRecyclerView.this);
        }
        snapHelper.setAnchorHorizontal(anchorX);

        addOnItemTouchListener(new OnRecyclerItemClickListener(anchorX));

        addOnScrollListener(new OnRecyclerScrollListener(layoutManager.getOrientation(), anchorX));

        post(new Runnable() {
            @Override
            public void run() {
                setPadding(anchorX, 0, getWidth() - anchorX, 0);
                smoothScrollToPosition(0);
            }
        });
    }

    private void applyScale(float scale) {
        if (closestChild != null) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(closestChild, "scaleX", scale);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(closestChild, "scaleY", scale);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(80);
            animatorSet.playTogether(scaleX, scaleY);
            animatorSet.start();
        }
    }

    private void getClosestHorizontalView(int center) {

        int absClosest = Integer.MAX_VALUE;

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);

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

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);

            int childCenter = getVerticalHelper(layoutManager).getDecoratedStart(child)
                    + (getVerticalHelper(layoutManager).getDecoratedMeasurement(child) / 2);
            int absDistance = Math.abs(childCenter - center);

            if (absDistance < absClosest) {
                absClosest = absDistance;
                closestChild = child;
            }
        }
    }

    private class OnRecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

        private final GestureDetectorCompat gestureDetectorCompat;

        OnRecyclerItemClickListener(final int offset) {
            gestureDetectorCompat = new GestureDetectorCompat(getContext(),
                    new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onSingleTapUp(MotionEvent e) {

                            View child = findChildViewUnder(e.getX(), e.getY());
                            if (child == null) {
                                return true;
                            }

                            if (layoutManager.getOrientation() == HORIZONTAL) {
                                float viewCenter = child.getX() + child.getWidth() / 2;
                                final int deltaX = (int) (viewCenter - offset);
                                post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (deltaX != 0)
                                            smoothScrollBy(deltaX, 0);
                                    }
                                });
                            } else if (layoutManager.getOrientation() == VERTICAL) {
                                float viewCenter = (int) (child.getY() + child.getHeight() / 2);
                                final int deltaY = (int) (viewCenter - offset);
                                post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (deltaY != 0)
                                            smoothScrollBy(0, deltaY);
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
        private int mAnchor;

        OnRecyclerScrollListener(int orientation, int anchor) {
            this.mOrientation = orientation;
            this.mAnchor = anchor;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                /* TODO
                    IDLE 状态可能会回调两次
                    1. 滑动完成后
                    2. SnapHelper 调整位置后
                    此处需要在最后一次回调时更新选中状态
                 */
                int[] ints = snapHelper.calculateDistanceToFinalSnap(layoutManager, snapHelper.findSnapView(layoutManager));

                if (onAnchorListener != null && ints != null && ints.length == 2 && ints[0] == 0 && ints[1] == 0) {
                    applyScale(1f);
                    if (mOrientation == VERTICAL) {
                        getClosestVerticalView(mAnchor);
                    } else if (mOrientation == HORIZONTAL) {
                        getClosestHorizontalView(mAnchor);
                    }
                    applyScale(1.4f);
                    onAnchorListener.onAnchor(closestChild);
                }
            }
        }
    }

    @NonNull
    private OrientationHelper getHorizontalHelper(@NonNull RecyclerView.LayoutManager
                                                          layoutManager) {
        if (mHorizontalHelper == null || mHorizontalHelper.getLayoutManager() != layoutManager) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return mHorizontalHelper;
    }

    @NonNull
    private OrientationHelper getVerticalHelper(@NonNull RecyclerView.LayoutManager
                                                        layoutManager) {
        if (mVerticalHelper == null || mVerticalHelper.getLayoutManager() != layoutManager) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return mVerticalHelper;
    }

    public interface OnAnchorListener {
        void onAnchor(View view);
    }
}