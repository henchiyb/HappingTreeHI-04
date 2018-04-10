package vn.edu.khtn.googlemapsforseminar02;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.io.IOException;

/**
 * Created by 10 pro 64bit on 09-Apr-18.
 */

public class SoundEffects {
    private SoundPool soundPool;
    private int soundToggle;
    private int soundClick;
    private static SoundEffects instance;
    private Boolean soundEffectOn = true;

    private SoundEffects(Context context){
        this.soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        loadSoundToPool(context);
    }

    public static SoundEffects getInstance(Context context) {
        if (instance == null)
            instance = new SoundEffects(context);
        return instance;
    }

    private void loadSoundToPool(Context context){
        try {
            soundToggle = soundPool.load(context.getAssets().openFd("sounds/uitoggle.wav"), 0);
            soundClick = soundPool.load(context.getAssets().openFd("sounds/UIClick.wav"), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playSoundToggle(){
        if (soundEffectOn)
            soundPool.play(soundToggle, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    public void playSoundClick(){
        if (soundEffectOn)
            soundPool.play(soundClick, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    public void setSoundEffectOn(Boolean soundEffectOn) {
        this.soundEffectOn = soundEffectOn;
    }
}
