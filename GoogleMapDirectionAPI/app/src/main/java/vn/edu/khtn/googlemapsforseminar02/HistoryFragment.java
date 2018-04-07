package vn.edu.khtn.googlemapsforseminar02;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HistoryFragment extends Fragment implements View.OnClickListener {
    private RecyclerView recyclerView;

    private HistoryViewAdapter adapter;
    private ArrayList<Position> listPositions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = view.findViewById(R.id.recycle_view_history);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        listPositions = new ArrayList<>();
        listPositions.add(new Position("10", "10"));
        listPositions.add(new Position("10", "10"));
        listPositions.add(new Position("10", "10"));
        listPositions.add(new Position("10", "10"));
        listPositions.add(new Position("10", "10"));
        listPositions.add(new Position("10", "10"));
        adapter = new HistoryViewAdapter(listPositions);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onClick(View v) {

    }
}
