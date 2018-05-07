package vn.edu.khtn.googlemapsforseminar02.fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import vn.edu.khtn.googlemapsforseminar02.utils.Constant;
import vn.edu.khtn.googlemapsforseminar02.R;
import vn.edu.khtn.googlemapsforseminar02.utils.Utils;

/**
 * Created by 10 pro 64bit on 09-Apr-18.
 */

public class SettingFragment extends Fragment implements View.OnClickListener{
    private ToggleButton tgVoice, tgMusic, tgTimer;
    private MediaPlayer mediaPlayer;
    private Context context;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        context = this.getActivity();
        tgVoice = (ToggleButton) view.findViewById(R.id.toggle_voice);
        tgMusic = (ToggleButton) view.findViewById(R.id.toggle_music);
        tgTimer = (ToggleButton) view.findViewById(R.id.toggle_timer);
        initData();
        addListeners();
        return view;
    }

    private void initData() {
        tgMusic.setChecked(Utils.getBooleanFromPreference(context, Constant.MUSIC_PREF));
        tgVoice.setChecked(Utils.getBooleanFromPreference(context, Constant.VOICE_PREF));
    }

    private void addListeners() {
        tgVoice.setOnClickListener(this);
        tgMusic.setOnClickListener(this);
        tgTimer.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mediaPlayer = new MediaPlayer();
        Utils.setDataSourceForMediaPlayer(context, mediaPlayer, "nhacthiennhien.mp3");
        if (Utils.getBooleanFromPreference(context, Constant.MUSIC_PREF)){
            mediaPlayer.setVolume(1.0f, 1.0f);
            tgMusic.setChecked(true);
        } else {
            mediaPlayer.setVolume(0, 0);
            tgMusic.setChecked(false);
        }
        tgVoice.setChecked(Utils.getBooleanFromPreference(context, Constant.VOICE_PREF));
        mediaPlayer.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.release();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggle_voice: {
                Utils.saveBooleanToPreference(context, Constant.VOICE_PREF, tgVoice.isChecked());
                Log.d("abcd", "tgvoice" + tgVoice.isChecked());
                break;
            }
            case R.id.toggle_music: {
                if (tgMusic.isChecked()) {
                    Utils.saveBooleanToPreference(getContext(), Constant.MUSIC_PREF, true);
                    mediaPlayer.setVolume(1.0f, 1.0f);
                } else {
                    Utils.saveBooleanToPreference(getContext(), Constant.MUSIC_PREF, false);
                    mediaPlayer.setVolume(0, 0);
                }
                break;
            }
        }
    }
}
