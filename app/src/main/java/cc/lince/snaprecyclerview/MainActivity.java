package cc.lince.snaprecyclerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
        SnapLinearLayoutManager layout = new SnapLinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        layout.setOnCallback(new SnapLinearLayoutManager.Callback() {
            @Override
            public void onIdle(View view) {
                Log.e("fling", ((TextView) view).getText().toString());
            }
        });
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(new SnapAdapter(mList));
        recyclerView.setAnchorHorizontal(800);
    }
}