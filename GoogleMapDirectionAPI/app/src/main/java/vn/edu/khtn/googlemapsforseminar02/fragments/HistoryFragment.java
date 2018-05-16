package vn.edu.khtn.googlemapsforseminar02.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import vn.edu.khtn.googlemapsforseminar02.utils.Constant;
import vn.edu.khtn.googlemapsforseminar02.objects.HistoryObject;
import vn.edu.khtn.googlemapsforseminar02.utils.HistoryViewAdapter;
import vn.edu.khtn.googlemapsforseminar02.R;
import vn.edu.khtn.googlemapsforseminar02.objects.TreeObject;
import vn.edu.khtn.googlemapsforseminar02.utils.Utils;

public class HistoryFragment extends Fragment implements View.OnClickListener {
    private RecyclerView recyclerView;

    private HistoryViewAdapter adapter;
    private ArrayList<HistoryObject> historyObjects;
    private MediaPlayer mediaPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = view.findViewById(R.id.recycle_view_history);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        historyObjects = new ArrayList<>();
        historyObjects.add(new HistoryObject(new TreeObject(1, "Cây bàng", 10, 10, R.drawable.autumn_tree_silhouette), "25/03/2018 14:50"));
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

    @Override
    public void onResume() {
        super.onResume();
        mediaPlayer = new MediaPlayer();
        Utils.setDataSourceForMediaPlayer(getActivity(), mediaPlayer, "nhacthiennhien.mp3");
        if (Utils.getBooleanFromPreference(getActivity(), Constant.MUSIC_PREF)){
            mediaPlayer.setVolume(1.0f, 1.0f);
        } else {
            mediaPlayer.setVolume(0, 0);
        }
        mediaPlayer.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.release();
    }
}