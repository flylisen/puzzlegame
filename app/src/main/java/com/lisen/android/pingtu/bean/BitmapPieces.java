package com.lisen.android.pingtu.bean;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2016/8/10.
 */
public class BitmapPieces {
    private int index;
    private Bitmap bitmap;

    public int getIndex() {
        return index;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public String toString() {
        return "BitmapPieces{" +
                "index=" + index +
                ", bitmap=" + bitmap +
                '}';
    }
}
