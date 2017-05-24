package com.hail_hydra.time.entities;

import android.graphics.Bitmap;

/**
 * Created by J_Zhen on 2017/4/7.
 */
public class DiaryImage {

    private static Bitmap bitmap;

    public static Bitmap getDiaryImage() {
        return bitmap;
    }

    public static void setDiaryImage(Bitmap bm){
        bitmap=bm;
    }

}
