package vn.edu.khtn.googlemapsforseminar02;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;

import java.io.IOException;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 10 pro 64bit on 09-Apr-18.
 */

public class Utils {
    private static TextToSpeech t1;
    private static String googleTtsPackage = "com.google.android.tts";
    public static TextToSpeech textToSpeech(Context context) {
        t1 = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setSpeechRate(1.2f);
                }
            }
        }, googleTtsPackage);
        return t1;
    }
    public static void setDataSourceForMediaPlayer(Context context, MediaPlayer mediaPlayer, String fileName){
        try {
            AssetFileDescriptor descriptor = context.getAssets().openFd("sounds/" + fileName);
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Utils.getBooleanFromPreference(context, "Music")) {
            mediaPlayer.setVolume(1.0f, 1.0f);
        } else {
            mediaPlayer.setVolume(0, 0);
        }
    }

    public static void saveBooleanToPreference(Context context, String namePref, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(namePref, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(namePref, value);
        editor.commit();
    }

    public static boolean getBooleanFromPreference(Context context, String namePref) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(namePref, MODE_PRIVATE);
        return sharedPreferences.getBoolean(namePref, false);
    }
}
