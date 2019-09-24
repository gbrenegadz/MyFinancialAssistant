package com.gbrenegadzdev.financeassistant.utils;

public class MathUtils {

    public float getRandom(float range, float start) {
        return (float) (Math.random() * range) + start;
    }
}
