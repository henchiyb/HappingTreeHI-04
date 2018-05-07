package vn.edu.khtn.googlemapsforseminar02.animations;

import android.os.Handler;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by 10 pro 64bit on 06-Apr-18.
 */

public class AnimateMarker implements Runnable {
    float tgtalpha;
    float delta;
    int animation;

    private Marker m;

    public AnimateMarker(Marker m, float tgtalpha, float delta) {
        this.tgtalpha = tgtalpha;
        this.delta = delta;
        this.m = m;
        this.animation = 0;
    }
    public AnimateMarker(Marker m, int animation, float tgt, float delta) {
        this.tgtalpha = tgt;
        this.delta = delta;
        this.m = m;
        this.animation = animation;
    }



    @Override
    public void run() {
        if (animation == 0) {
            float a = m.getRotation();
            if (a >= 10) {
                a = -10;
            } else {
                a += 10.0F;
            }
            m.setRotation(a);
            new Handler().postDelayed(new AnimateMarker(m, 0, 0.5F, 0.1F), 500);
        } else  if (animation == 1) {
            float a = m.getAlpha();
            if (a <= tgtalpha || a >= 1.0F) {
                delta *= -1.0F;
            }
            a += delta;
            m.setAlpha(a);
            new Handler().postDelayed(new AnimateMarker(m, 1, 0.1F, 0.1F), 500);
        }
    }
}
