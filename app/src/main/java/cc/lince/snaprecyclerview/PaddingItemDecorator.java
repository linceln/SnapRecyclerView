package cc.lince.snaprecyclerview;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class PaddingItemDecorator extends RecyclerView.ItemDecoration {
    private final int mSize;
    private final int mOrientation;

    public PaddingItemDecorator(int orientation, int size) {
        mSize = size;
        mOrientation = orientation;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        parent.setClipToPadding(false);
        if (mOrientation == LinearLayoutManager.HORIZONTAL) {
            int current = parent.getChildLayoutPosition(view);
            if (current == 0) {
                outRect.set(mSize - view.getWidth() / 2, 0, 0, 0);
            } else if (current == state.getItemCount() - 1) {
                outRect.set(0, 0, parent.getWidth() - mSize - view.getWidth() / 2, 0);
            }
        } else if (mOrientation == LinearLayoutManager.VERTICAL) {
            outRect.set(0, 0, 0, mSize);
        }
    }
}
