package vn.edu.khtn.googlemapsforseminar02;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nhan on 10/31/2016.
 */

public class HistoryViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.image_tree) CircleImageView imageView;
    @BindView(R.id.tv_detail) TextView tvNameTree;
    @BindView(R.id.tv_name_tree) TextView tvDetail;
    public HistoryViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setData(Position position){
        Picasso.with(itemView.getContext())
                .load(R.drawable.ic_big_pine_tree_shape)
                .fit()
                .centerCrop()
                .into(imageView);
        tvDetail.setText("DES LAT: " + position.getDesLat());
        tvNameTree.setText("DES LONG: " + position.getDesIng());
        itemView.setTag(position);
    }
}
