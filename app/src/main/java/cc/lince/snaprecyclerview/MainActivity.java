package cc.lince.snaprecyclerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static List<String> mList = new ArrayList<>();

    static {
        mList.add("OR");
        mList.add("M01");
        mList.add("M02");
        mList.add("M03");
        mList.add("M04");
        mList.add("N01");
        mList.add("N02");
        mList.add("N03");
        mList.add("N04");
        mList.add("N05");
        mList.add("N06");
        mList.add("J01");
        mList.add("J02");
        mList.add("J03");
        mList.add("A01");
        mList.add("A02");
        mList.add("X01");
        mList.add("X02");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SnapRecyclerView recyclerView = findViewById(R.id.recyclerView);
//        SnapLinearLayoutManager layout = new SnapLinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        LinearLayoutManager layout = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
//        layout.setOnCallback(new SnapLinearLayoutManager.Callback() {
//            @Override
//            public void onIdle(View view) {
//                Log.e("fling", ((TextView) view).getText().toString());
//            }
//        });
//        recyclerView.addItemDecoration(new PaddingItemDecorator(RecyclerView.HORIZONTAL, 800));
        recyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(recyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder, final MotionEvent e, final int childLayoutPosition) {
                Log.e("fling", "position:" + childLayoutPosition);
//                recyclerView.smoothScrollToPosition(childLayoutPosition);
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
//                        final int deltaX = (int) (e.getX() - 800);
//                        recyclerView.smoothScrollBy(deltaX, 0, new DecelerateInterpolator(1f));
                        recyclerView.smoothScrollToPosition(childLayoutPosition);
                    }
                });
            }
        });
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(new SnapAdapter(mList));
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollToPosition(6);
            }
        });
//        recyclerView.setAnchorHorizontal(800);
    }
}