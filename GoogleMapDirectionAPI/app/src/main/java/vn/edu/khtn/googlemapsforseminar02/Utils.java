package vn.edu.khtn.googlemapsforseminar02;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 10 pro 64bit on 09-Apr-18.
 */

public class Utils {
    public static void setDataSourceForMediaPlayer(Context context, MediaPlayer mediaPlayer, String fileName){
        try {
            AssetFileDescriptor descriptor = context.getAssets().openFd("sounds/" + fileName);
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveBooleanToPreference(Context context, String namePref, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(namePref, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(namePref, value);
        editor.apply();
    }

    public static boolean getBooleanFromPreference(Context context, String namePref) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(namePref, MODE_PRIVATE);
        return sharedPreferences.getBoolean(namePref, false);
    }
}
