package vn.edu.khtn.googlemapsforseminar02.utils;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.khtn.googlemapsforseminar02.R;
import vn.edu.khtn.googlemapsforseminar02.objects.HistoryObject;

/**
 * Created by Nhan on 10/31/2016.
 */

public class HistoryViewHolder extends RecyclerView.ViewHolder {
    private CircleImageView imageView;
    private TextView tvNameTree;
    private TextView tvDetail;
    public HistoryViewHolder(View itemView) {
        super(itemView);
        imageView = (CircleImageView) itemView.findViewById(R.id.image_tree);
        tvDetail = itemView.findViewById(R.id.tv_name_tree);
        tvNameTree = itemView.findViewById(R.id.tv_detail);
    }

    public void setData(HistoryObject historyObject){
        imageView.setImageResource(R.drawable.ic_big_pine_tree_shape);
        imageView.setBorderColor(Color.BLACK);
        imageView.setBackgroundColor(Color.WHITE);
        tvDetail.setText("Tên cây: " + historyObject.getTree().getName());
        tvNameTree.setText("Thời gian tưới: " + historyObject.getDateTimeWater());
        imageView.setImageResource(historyObject.getTree().getImageID());
        itemView.setTag(historyObject);
    }
}
