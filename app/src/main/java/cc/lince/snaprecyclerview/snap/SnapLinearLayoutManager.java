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
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int orientation = getOrientation();
        if (orientation == VERTICAL) {
            for (int i = 0; i < getChildCount(); i++) {
                final View child = getChildAt(i);
                child.setScaleX(1f);
                child.setScaleY(1f);
            }
            return super.scrollVerticallyBy(dy, recycler, state);
        } else {
            return 0;
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.
            State state) {
        int orientation = getOrientation();
        if (orientation == HORIZONTAL) {
            for (int i = 0; i < getChildCount(); i++) {
                final View child = getChildAt(i);
                child.setScaleX(1f);
                child.setScaleY(1f);
            }
            return super.scrollHorizontallyBy(dx, recycler, state);
        } else {
            return 0;
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
//        if (state == RecyclerView.SCROLL_STATE_IDLE) {
//            int[] ints = calculateDistanceToFinalSnap(this, findSnapView(this));
//            if (ints.length == 2 && ints[0] == 0 && ints[1] == 0) {
//                if (getOrientation() == VERTICAL) {
//                    getClosestVerticalView(y);
//                } else if (getOrientation() == HORIZONTAL) {
//                    getClosestHorizontalView(x);
//                }
//                applyScale();
//                if (callback != null) {
//                    callback.onIdle(closestChild);
//                }
//            }
//        }
    }

    private void applyScale() {
        if (closestChild != null) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(closestChild, "scaleX", 1.4f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(closestChild, "scaleY", 1.4f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(80);
            animatorSet.playTogether(scaleX, scaleY);
            animatorSet.start();
        }
    }

    public interface Callback {
        void onIdle(View view);
    }
}
