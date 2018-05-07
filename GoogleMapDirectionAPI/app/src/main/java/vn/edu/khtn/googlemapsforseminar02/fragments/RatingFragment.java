package vn.edu.khtn.googlemapsforseminar02.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import vn.edu.khtn.googlemapsforseminar02.R;

/**
 * Created by 10 pro 64bit on 07-May-18.
 */

public class RatingFragment extends Fragment {
    private Button submitButton;
    private EditText edtFeedback;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rating, container, false);
        submitButton = view.findViewById(R.id.btn_submit_fb);
        edtFeedback = view.findViewById(R.id.edt_feddback);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Gửi phản hồi thành công", Toast.LENGTH_SHORT).show();
                edtFeedback.setText("");
            }
        });
        return view;
    }
}
