package vn.edu.khtn.googlemapsforseminar02;

import android.os.Handler;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by 10 pro 64bit on 06-Apr-18.
 */

public class AnimateMarker implements Runnable {
    float tgtalpha;
    float delta;

    private Marker m;
    public AnimateMarker(Marker m, float tgt, float delta) {
        this.m = m;
        this.tgtalpha = tgt;
        this.delta = delta;
    }

    @Override
    public void run() {
        float a = m.getRotation();
        if (a >= 10) {
            a = -10;
        } else {
            a += 10.0F;
        }
        m.setRotation(a);

//        float a = m.getAlpha();
//        if (a <= tgtalpha || a >= 1.0F) {
//            delta *= -1.0F;
//        }
//        a += delta;
//        m.setAlpha(a);

        new Handler().postDelayed(new AnimateMarker(m, tgtalpha, delta), 500);
    }
}
