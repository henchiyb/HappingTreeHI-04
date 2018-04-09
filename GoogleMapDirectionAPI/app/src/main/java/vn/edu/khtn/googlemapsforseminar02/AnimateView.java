package vn.edu.khtn.googlemapsforseminar02;

import android.os.Handler;
import android.view.View;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by 10 pro 64bit on 06-Apr-18.
 */

public class AnimateView implements Runnable {
    float tgtalpha;
    float delta;
    int animation;

    private View m;

    public AnimateView(View m, float tgtalpha, float delta) {
        this.tgtalpha = tgtalpha;
        this.delta = delta;
        this.m = m;
    }

    @Override
    public void run() {
            float a = m.getAlpha();
            if (a <= tgtalpha || a >= 1.0F) {
                delta *= -1.0F;
            }
            a += delta;
            m.setAlpha(a);
            new Handler().postDelayed(new AnimateView(m, 0.1F, 0.1F), 500);
    }
}
