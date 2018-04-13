package cc.lince.snaprecyclerview.snap;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SnapLinearLayoutManager extends LinearLayoutManager {

    private int x; // 锚点 (Horizontal)

    private int y; // 锚点 (Vertical)

    private View closestChild;

    private Callback callback;

    @Nullable
    private OrientationHelper mHorizontalHelper;

    @Nullable
    private OrientationHelper mVerticalHelper;

    public void setAnchorVertical(int y) {
        this.y = y;
    }

    public void setAnchorHorizontal(int x) {
        this.x = x;
    }

    public void setOnCallback(Callback callback) {
        this.callback = callback;
    }

    public SnapLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            int[] ints = calculateDistanceToFinalSnap(this, findSnapView(this));
            if (ints.length == 2 && ints[0] == 0 && ints[1] == 0) {

                applyScale(1f);
                if (getOrientation() == VERTICAL) {
                    getClosestVerticalView(y);
                } else if (getOrientation() == HORIZONTAL) {
                    getClosestHorizontalView(x);
                }
                applyScale(1.4f);
                if (callback != null) {
                    callback.onIdle(closestChild);
                }
            }
        }
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

            int childCenter = getHorizontalHelper(this).getDecoratedStart(child)
                    + (getHorizontalHelper(this).getDecoratedMeasurement(child) / 2);
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

            int childCenter = getVerticalHelper(this).getDecoratedStart(child)
                    + (getVerticalHelper(this).getDecoratedMeasurement(child) / 2);
            int absDistance = Math.abs(childCenter - center);

            if (absDistance < absClosest) {
                absClosest = absDistance;
                closestChild = child;
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

    public interface Callback {
        void onIdle(View view);
    }

    @NonNull
    private int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        int[] out = new int[2];
        if (layoutManager.canScrollHorizontally()) {
            out[0] = distanceToCenter(targetView, getHorizontalHelper(layoutManager), x);
        } else {
            out[0] = 0;
        }

        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToCenter(targetView, getVerticalHelper(layoutManager), y);
        } else {
            out[1] = 0;
        }
        return out;
    }

    private int distanceToCenter(@NonNull View targetView, OrientationHelper helper, int center) {
        final int childCenter = helper.getDecoratedStart(targetView) + (helper.getDecoratedMeasurement(targetView) / 2);
        return childCenter - center;
    }

    @Nullable
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.canScrollHorizontally()) {
            return findSpecificView(layoutManager, getHorizontalHelper(layoutManager), x);
        } else if (layoutManager.canScrollVertically()) {
            return findSpecificView(layoutManager, getVerticalHelper(layoutManager), y);
        }
        return null;
    }

    private View findSpecificView(RecyclerView.LayoutManager layoutManager, OrientationHelper helper, final int center) {
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

            /** if child center is closer than previous closest, set it as closest  **/
            if (absDistance < absClosest) {
                absClosest = absDistance;
                closestChild = child;
            }
        }
        return closestChild;
    }
}
