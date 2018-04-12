package cc.lince.snaprecyclerview.view;

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
        SnapLinearLayoutManager layout = new SnapLinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        layout.setOnCallback(new SnapLinearLayoutManager.Callback() {
            @Override
            public void onIdle(View view) {
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
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(new SnapAdapter(mList));
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                int anchorX = (int) (tvAnchor.getWidth() / 2 + tvAnchor.getX());
                recyclerView.setAnchorHorizontal(anchorX);
            }
        });
    }
}