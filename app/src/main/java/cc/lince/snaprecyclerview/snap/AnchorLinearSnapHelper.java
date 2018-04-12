package cc.lince.snaprecyclerview.snap;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.DisplayMetrics;
import android.view.View;

public class AnchorLinearSnapHelper extends SnapHelper {

    private static final float MILLISECONDS_PER_INCH = 100f; // 影响滑动速度

    private static final float INVALID_DISTANCE = 1f;

    private int x; // 锚点位置 (Horizontal)

    private int y; // 锚点位置 (Vertical)

    @Nullable
    private OrientationHelper mVerticalHelper;

    @Nullable
    private OrientationHelper mHorizontalHelper;

    @Nullable
    private RecyclerView mRecyclerView;

    public AnchorLinearSnapHelper() {
    }

    public void setAnchorVertical(int y) {
        this.y = y;
    }

    public void setAnchorHorizontal(int x) {
        this.x = x;
    }

    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) throws IllegalStateException {
        this.mRecyclerView = recyclerView;
        super.attachToRecyclerView(recyclerView);
    }

    @Nullable
    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
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
        int width = targetView.getWidth();
        int decoratedStart = helper.getDecoratedStart(targetView);
        int decoratedMeasurement = helper.getDecoratedMeasurement(targetView);
        final int childCenter = decoratedStart + (decoratedMeasurement / 2);
//        int leftDecorationWidth = mRecyclerView.getLayoutManager().getLeftDecorationWidth(targetView);
//        int childCenter = targetView.getLeft() + leftDecorationWidth + targetView.getWidth() / 2;
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

    private View findSpecificView(RecyclerView.LayoutManager layoutManager, OrientationHelper helper, final int center) {
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        }

        View closestChild = null;
        int absClosest = Integer.MAX_VALUE;

        for (int i = 0; i < childCount; i++) {
            final View child = layoutManager.getChildAt(i);
            int leftDecorationWidth = layoutManager.getLeftDecorationWidth(child);
            int decoratedMeasurement = helper.getDecoratedMeasurement(child);
            int rightDecorationWidth = layoutManager.getRightDecorationWidth(child);
//            int childCenter = helper.getDecoratedStart(child) + (helper.getDecoratedMeasurement(child)  / 2);
//            int childCenter = helper.getDecoratedStart(child) + helper.getDecoratedMeasurement(child) - child.getWidth() / 2;
            int childCenter = helper.getDecoratedStart(child) + leftDecorationWidth + child.getWidth() / 2;

            int absDistance = Math.abs(childCenter - center);

            /** if child center is closer than previous closest, set it as closest  **/
            if (absDistance < absClosest) {
                absClosest = absDistance;
                closestChild = child;
            }
        }
        return closestChild;
    }

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return RecyclerView.NO_POSITION;
        }

        final int itemCount = layoutManager.getItemCount();
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION;
        }

        final View currentView = findSnapView(layoutManager);
        if (currentView == null) {
            return RecyclerView.NO_POSITION;
        }

        final int currentPosition = layoutManager.getPosition(currentView);
        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }

        RecyclerView.SmoothScroller.ScrollVectorProvider vectorProvider =
                (RecyclerView.SmoothScroller.ScrollVectorProvider) layoutManager;
        // deltaJumps sign comes from the velocity which may not match the order of children in
        // the LayoutManager. To overcome this, we ask for a vector from the LayoutManager to
        // get the direction.
        PointF vectorForEnd = vectorProvider.computeScrollVectorForPosition(itemCount - 1);
        if (vectorForEnd == null) {
            // cannot get a vector for the given position.
            return RecyclerView.NO_POSITION;
        }

        int vDeltaJump, hDeltaJump;
        if (layoutManager.canScrollHorizontally()) {
            hDeltaJump = estimateNextPositionDiffForFling(layoutManager,
                    getHorizontalHelper(layoutManager), velocityX, 0);

//            //计算一屏的item数
//            int deltaThreshold = layoutManager.getWidth() / getHorizontalHelper(layoutManager).getDecoratedMeasurement(currentView);
//
//            // 对估算出来的位置偏移量进行阈值判断，最多只能滚动一屏的 item 数
//            if (hDeltaJump > deltaThreshold) {
//                hDeltaJump = deltaThreshold;
//            }
//            if (hDeltaJump < -deltaThreshold) {
//                hDeltaJump = -deltaThreshold;
//            }
            if (vectorForEnd.x < 0) {
                hDeltaJump = -hDeltaJump;
            }
        } else {
            hDeltaJump = 0;
        }
        if (layoutManager.canScrollVertically()) {
            vDeltaJump = estimateNextPositionDiffForFling(layoutManager,
                    getVerticalHelper(layoutManager), 0, velocityY);

//            //计算一屏的item数
//            int deltaThreshold = layoutManager.getWidth() / getVerticalHelper(layoutManager).getDecoratedMeasurement(currentView);
//
//            // 对估算出来的位置偏移量进行阈值判断，最多只能滚动一屏的 item 数
//            if (vDeltaJump > deltaThreshold) {
//                vDeltaJump = deltaThreshold;
//            }
//            if (vDeltaJump < -deltaThreshold) {
//                vDeltaJump = -deltaThreshold;
//            }
            if (vectorForEnd.y < 0) {
                vDeltaJump = -vDeltaJump;
            }
        } else {
            vDeltaJump = 0;
        }

        int deltaJump = layoutManager.canScrollVertically() ? vDeltaJump : hDeltaJump;
        if (deltaJump == 0) {
            return RecyclerView.NO_POSITION;
        }

        int targetPos = currentPosition + deltaJump;
        if (targetPos < 0) {
            targetPos = 0;
        }
        if (targetPos >= itemCount) {
            targetPos = itemCount - 1;
        }
        return targetPos;
    }

    /**
     * Estimates a position to which SnapHelper will try to scroll to in response to a fling.
     *
     * @param layoutManager The {@link RecyclerView.LayoutManager} associated with the attached
     *                      {@link RecyclerView}.
     * @param helper        The {@link OrientationHelper} that is created from the LayoutManager.
     * @param velocityX     The velocity on the x axis.
     * @param velocityY     The velocity on the y axis.
     * @return The diff between the target scroll position and the current position.
     */
    private int estimateNextPositionDiffForFling(RecyclerView.LayoutManager layoutManager,
                                                 OrientationHelper helper, int velocityX, int velocityY) {
        int[] distances = calculateScrollDistance(velocityX, velocityY);
        float distancePerChild = computeDistancePerChild(layoutManager, helper);
        if (distancePerChild <= 0) {
            return 0;
        }
        int distance =
                Math.abs(distances[0]) > Math.abs(distances[1]) ? distances[0] : distances[1];
        return Math.round(distance / distancePerChild);
    }

    private float computeDistancePerChild(RecyclerView.LayoutManager layoutManager,
                                          OrientationHelper helper) {
        View minPosView = null;
        View maxPosView = null;
        int minPos = Integer.MAX_VALUE;
        int maxPos = Integer.MIN_VALUE;
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return INVALID_DISTANCE;
        }

        for (int i = 0; i < childCount; i++) {
            View child = layoutManager.getChildAt(i);
            final int pos = layoutManager.getPosition(child);
            if (pos == RecyclerView.NO_POSITION) {
                continue;
            }
            if (pos < minPos) {
                minPos = pos;
                minPosView = child;
            }
            if (pos > maxPos) {
                maxPos = pos;
                maxPosView = child;
            }
        }
        if (minPosView == null || maxPosView == null) {
            return INVALID_DISTANCE;
        }
        int start = Math.min(helper.getDecoratedStart(minPosView),
                helper.getDecoratedStart(maxPosView));
        int end = Math.max(helper.getDecoratedEnd(minPosView),
                helper.getDecoratedEnd(maxPosView));
        int distance = end - start;
        if (distance == 0) {
            return INVALID_DISTANCE;
        }
        return 1f * distance / ((maxPos - minPos) + 1);
    }

    @Nullable
    @Override
    protected RecyclerView.SmoothScroller createScroller(RecyclerView.LayoutManager layoutManager) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return null;
        }
        return new LinearSmoothScroller(mRecyclerView.getContext()) {
            @Override
            protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
                if (mRecyclerView == null) {
                    // The associated RecyclerView has been removed so there is no action to take.
                    return;
                }
                int[] snapDistances = calculateDistanceToFinalSnap(mRecyclerView.getLayoutManager(), targetView);
                final int dx = snapDistances[0];
                final int dy = snapDistances[1];
                final int time = calculateTimeForDeceleration(Math.max(Math.abs(dx), Math.abs(dy)));
                if (time > 0) {
                    action.update(dx, dy, time, mDecelerateInterpolator);
                }
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
            }
        };
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