package cc.lince.snaprecyclerview.snap;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class OnRecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    private final GestureDetectorCompat gestureDetectorCompat;

    public OnRecyclerItemClickListener(final RecyclerView recyclerView) {
        gestureDetectorCompat = new GestureDetectorCompat(recyclerView.getContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                        if (child != null) {
                            RecyclerView.ViewHolder childViewHolder = recyclerView.getChildViewHolder(child);
                            final int childLayoutPosition = recyclerView.getChildLayoutPosition(child);
                            onItemClick(childViewHolder, e, childLayoutPosition);
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

    public abstract void onItemClick(RecyclerView.ViewHolder holder, MotionEvent e, int position);
}