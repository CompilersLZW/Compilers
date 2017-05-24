package com.hail_hydra.time.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.hail_hydra.time.R;
import com.hail_hydra.time.entities.DiaryImage;

public class ImageDetail extends Activity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        imageView = (ImageView)findViewById(R.id.imageView);

        Bitmap bitmap= DiaryImage.getDiaryImage();
        imageView.setImageBitmap(bitmap);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageDetail.this.finish();
            }
        });
    }
}
