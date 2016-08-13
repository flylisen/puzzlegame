package com.lisen.android.pingtu.util;

import android.graphics.Bitmap;

import com.lisen.android.pingtu.bean.BitmapPieces;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/10.
 */
public class SplitBitmapUtil {

    public static List<BitmapPieces> splitBitmap(Bitmap bitmap, int pieces) {
        List<BitmapPieces> bitmapPiecesList = new ArrayList<>();
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int pieceWidth = Math.min(bitmapWidth, bitmapHeight) / pieces;
        for (int i = 0; i < pieces; i++) {
            for (int j = 0; j < pieces; j++) {
                BitmapPieces bitmapPieces = new BitmapPieces();
                int index = j + i * pieces;
                bitmapPieces.setIndex(index);
                int x = j * pieceWidth;
                int y = i * pieceWidth;
                bitmapPieces.setBitmap(Bitmap.createBitmap(bitmap, x, y, pieceWidth, pieceWidth));
                bitmapPiecesList.add(bitmapPieces);
            }
        }
        return bitmapPiecesList;
    }
}
