package vn.edu.khtn.googlemapsforseminar02;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.io.IOException;

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
}
