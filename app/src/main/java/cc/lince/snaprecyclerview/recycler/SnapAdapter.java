package cc.lince.snaprecyclerview.recycler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.lince.snaprecyclerview.R;

public class SnapAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> mList = new ArrayList<>();

    public SnapAdapter(List<String> list) {
        mList.clear();
        mList.addAll(list);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_test, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setScaleX(1f);
        holder.itemView.setScaleY(1f);
        TextView view = holder.itemView.findViewById(R.id.textView);
        view.setText(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}