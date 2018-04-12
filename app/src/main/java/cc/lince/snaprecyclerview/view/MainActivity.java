package cc.lince.snaprecyclerview.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.lince.snaprecyclerview.R;
import cc.lince.snaprecyclerview.snap.SnapLinearLayoutManager;
import cc.lince.snaprecyclerview.snap.SnapRecyclerView;

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

        final TextView tvAnchor = findViewById(R.id.tvAnchor);

        final SnapRecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new SnapLinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(new SnapAdapter(mList));
        recyclerView.setOnAnchorListener(new SnapRecyclerView.OnAnchorListener() {
            @Override
            public void onAnchor(View view) {
                applyScale(view);
                // 选中回调
                final Snackbar snackbar = Snackbar.make(tvAnchor, ((TextView) view).getText().toString(), Snackbar.LENGTH_SHORT);
                snackbar.setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
            }
        });
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                int anchorX = (int) (tvAnchor.getWidth() / 2 + tvAnchor.getX());
                recyclerView.setAnchorHorizontal(anchorX);
            }
        });
    }

    private void applyScale(View view) {
        if (view != null) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.4f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.4f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(80);
            animatorSet.playTogether(scaleX, scaleY);
            animatorSet.start();
        }
    }
}