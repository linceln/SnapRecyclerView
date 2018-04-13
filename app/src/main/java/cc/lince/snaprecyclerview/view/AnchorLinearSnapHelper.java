package cc.lince.snaprecyclerview.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class AnchorLinearSnapHelper extends LinearSnapHelper {

    private int x; // 锚点位置 (Horizontal)

    private int y; // 锚点位置 (Vertical)

    @Nullable
    private OrientationHelper mVerticalHelper;

    @Nullable
    private OrientationHelper mHorizontalHelper;

    public AnchorLinearSnapHelper() {
    }

    public void setAnchorVertical(int y) {
        this.y = y;
    }

    public void setAnchorHorizontal(int x) {
        this.x = x;
    }

    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView)
            throws IllegalStateException {
        
        super.attachToRecyclerView(recyclerView);
    }

    @Nullable
    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,
                                              @NonNull View targetView) {
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
        final int childCenter = helper.getDecoratedStart(targetView)
                + (helper.getDecoratedMeasurement(targetView) / 2);
        return childCenter - center;
    }

    @Nullable
    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.canScrollHorizontally()) {
            return findSpecificView(layoutManager, getHorizontalHelper(layoutManager), x);
        } else if (layoutManager.canScrollVertically()) {
            return findSpecificView(layoutManager, getVerticalHelper(layoutManager), y);
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

            /** if child center is closer than previous closest, set it as closest  **/
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
}