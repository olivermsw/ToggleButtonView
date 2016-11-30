package me.olimsw.togglebuttonlibrary;

import android.animation.TimeInterpolator;

/**
 * Created by musiwen on 2016/11/30.
 */

public class BackOutInterpolator implements TimeInterpolator {
    @Override
    public float getInterpolation(float input) {
        float factor = 1f;
        return ((input = input - 1) * input * ((factor + 1) * input + factor) + 1);
    }

}
