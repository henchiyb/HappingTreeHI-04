package vn.edu.khtn.googlemapsforseminar02;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Nhan on 10/31/2016.
 */

public class HistoryViewAdapter extends RecyclerView.Adapter<HistoryViewHolder> {
    private List<HistoryObject> historyObjects;

    public HistoryViewAdapter(List<HistoryObject> historyObjects) {
        this.historyObjects = historyObjects;
    }

    private View.OnClickListener onItemClickListener;

    public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_recycle_view_history, parent, false);

        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        holder.setData(historyObjects.get(position));
        holder.itemView.setOnClickListener(onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return historyObjects.size();
    }
}
