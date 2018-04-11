package cc.lince.snaprecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class SnapRecyclerView extends RecyclerView {

    private SnapLinearLayoutManager layoutManager;
    private AnchorLinearSnapHelper snapHelper;

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

    @Override
    public void setLayoutManager(LayoutManager layout) {
        if (layout instanceof SnapLinearLayoutManager) {
            layoutManager = (SnapLinearLayoutManager) layout;
        }
        super.setLayoutManager(layout);
    }

    public void setAnchorVertical(final int y) {
        if (layoutManager != null) {
            layoutManager.setAnchorVertical(y);

            snapHelper = new AnchorLinearSnapHelper();
            snapHelper.setAnchorVertical(y);
            snapHelper.attachToRecyclerView(SnapRecyclerView.this);

            addOnItemTouchListener(new OnRecyclerItemClickListener(SnapRecyclerView.this) {
                @Override
                public void onItemClick(RecyclerView.ViewHolder viewHolder, MotionEvent e, final int childLayoutPosition) {
                    final int deltaY = (int) (e.getY() - y);
                    post(new Runnable() {
                        @Override
                        public void run() {
                            smoothScrollBy(0, deltaY);
                        }
                    });
                }
            });

            post(new Runnable() {
                @Override
                public void run() {
                    setPadding(0, y, 0, getHeight() - y);
                    smoothScrollToPosition(0);
                }
            });
        }
    }

    public void setAnchorHorizontal(final int x) {

        // 添加 RecyclerView 前后 Padding
        addItemDecoration(new PaddingItemDecorator(RecyclerView.HORIZONTAL, x));

        if (layoutManager != null) {
            layoutManager.setAnchorHorizontal(x);
            snapHelper = new AnchorLinearSnapHelper();
            snapHelper.setAnchorHorizontal(x);
//            snapHelper.attachToRecyclerView(SnapRecyclerView.this);

            addOnItemTouchListener(new OnRecyclerItemClickListener(SnapRecyclerView.this) {
                @Override
                public void onItemClick(RecyclerView.ViewHolder viewHolder, final MotionEvent e, final int childLayoutPosition) {
//                    View snapView = layoutManager.findSnapView(layoutManager);
//                    Log.e("fling", "onclick: " + snapView.hashCode() + " " + viewHolder.itemView.hashCode());
//                    if (snapView == viewHolder.itemView) {
//                        return;
//                    }
                    post(new Runnable() {
                        @Override
                        public void run() {
                            final int deltaX = (int) (e.getX() - x);
                            smoothScrollBy(deltaX, 0, new DecelerateInterpolator(1f));
                            Log.e("fling", "position:" + childLayoutPosition);
//                            smoothScrollToPosition(childLayoutPosition);
                        }
                    });
                }
            });
        }
    }
}