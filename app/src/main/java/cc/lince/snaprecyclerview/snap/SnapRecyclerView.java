package cc.lince.snaprecyclerview.snap;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
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

    public void setAnchorVertical(final int anchorY) {
        if (layoutManager != null) {
            layoutManager.setAnchorVertical(anchorY);

            if (snapHelper == null) {
                snapHelper = new AnchorLinearSnapHelper();
                snapHelper.attachToRecyclerView(SnapRecyclerView.this);
            }
            snapHelper.setAnchorVertical(anchorY);

            addOnItemTouchListener(new OnRecyclerItemClickListener(anchorY));

            post(new Runnable() {
                @Override
                public void run() {
                    setPadding(0, anchorY, 0, getHeight() - anchorY);
                    smoothScrollToPosition(0);
                }
            });
        }
    }

    public void setAnchorHorizontal(final int anchorX) {
        if (layoutManager != null) {
            layoutManager.setAnchorHorizontal(anchorX);

            if (snapHelper == null) {
                snapHelper = new AnchorLinearSnapHelper();
                snapHelper.attachToRecyclerView(SnapRecyclerView.this);
            }
            snapHelper.setAnchorHorizontal(anchorX);

            addOnItemTouchListener(new OnRecyclerItemClickListener(anchorX));

            post(new Runnable() {
                @Override
                public void run() {
                    setPadding(anchorX, 0, getWidth() - anchorX, 0);
                    smoothScrollToPosition(0);
                }
            });
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
                            View snapView = layoutManager.findSnapView(layoutManager);
                            if (snapView == child) {
                                /*
                                需要锚定的 item 和点击的 item 是同一个
                                即重复点击同一个 item
                                */
                                return true;
                            }

                            if (layoutManager.getOrientation() == HORIZONTAL) {
                                float viewCenter = child.getX() + child.getWidth() / 2;
                                final int deltaX = (int) (viewCenter - offset);
                                post(new Runnable() {
                                    @Override
                                    public void run() {
                                        smoothScrollBy(deltaX, 0, new DecelerateInterpolator(1f));
                                    }
                                });
                            } else if (layoutManager.getOrientation() == VERTICAL) {
                                float viewCenter = (int) (child.getX() + child.getWidth() / 2);
                                final int deltaY = (int) (viewCenter - offset);
                                post(new Runnable() {
                                    @Override
                                    public void run() {
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
}