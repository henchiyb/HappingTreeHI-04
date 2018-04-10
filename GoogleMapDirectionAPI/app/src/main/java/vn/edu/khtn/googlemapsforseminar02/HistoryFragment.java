package vn.edu.khtn.googlemapsforseminar02;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class HistoryFragment extends Fragment implements View.OnClickListener {
    private RecyclerView recyclerView;

    private HistoryViewAdapter adapter;
    private ArrayList<HistoryObject> historyObjects;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = view.findViewById(R.id.recycle_view_history);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        historyObjects = new ArrayList<>();
        historyObjects.add(new HistoryObject(new TreeObject(1, "Cây bàng", 10, 10, R.drawable.avt), "25/03/2018 14:50"));
        historyObjects.add(new HistoryObject(new TreeObject(2, "Cây bàng", 10, 10, R.drawable.autumn_tree_silhouette), "25/03/2018 14:50"));
        historyObjects.add(new HistoryObject(new TreeObject(3, "Cây bàng", 10, 10, R.drawable.tree_curved_to_left_with_few_leaves_and_branches), "25/03/2018 14:50"));
        historyObjects.add(new HistoryObject(new TreeObject(4, "Cây bàng", 10, 10, R.drawable.tree_of_oval_horizontal_foliage), "25/03/2018 14:50"));
        historyObjects.add(new HistoryObject(new TreeObject(5, "Cây bàng", 10, 10, R.drawable.ic_tree_with_three_circles_of_foliage), "25/03/2018 14:50"));
        historyObjects.add(new HistoryObject(new TreeObject(6, "Cây bàng", 10, 10, R.drawable.tree_irregular_silhouette), "25/03/2018 14:50"));
        adapter = new HistoryViewAdapter(historyObjects);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onClick(View v) {

    }
}
