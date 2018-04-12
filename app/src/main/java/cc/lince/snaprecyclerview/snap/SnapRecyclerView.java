package cc.lince.snaprecyclerview.snap;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
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
                    View snapView = layoutManager.findSnapView(layoutManager);
                    if (snapView == viewHolder.itemView) {
                        return;
                    }
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
        if (layoutManager != null) {
            layoutManager.setAnchorHorizontal(x);

            snapHelper = new AnchorLinearSnapHelper();
            snapHelper.setAnchorHorizontal(x);
            snapHelper.attachToRecyclerView(SnapRecyclerView.this);

            addOnItemTouchListener(new OnRecyclerItemClickListener(SnapRecyclerView.this) {
                @Override
                public void onItemClick(RecyclerView.ViewHolder viewHolder, final MotionEvent e, final int childLayoutPosition) {
                    View snapView = layoutManager.findSnapView(layoutManager);
                    if (snapView == viewHolder.itemView) {
                        return;
                    }
                    final int deltaX = (int) (e.getX() - x);
                    post(new Runnable() {
                        @Override
                        public void run() {
                            smoothScrollBy(deltaX, 0, new DecelerateInterpolator(1f));
                        }
                    });
                }
            });

            post(new Runnable() {
                @Override
                public void run() {
                    setPadding(x, 0, getWidth() - x, 0);
                    smoothScrollToPosition(0);
                }
            });
        }
    }
}