package com.hail_hydra.time.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hail_hydra.time.R;
import com.hail_hydra.time.adapters.DiaryAdapter;
import com.hail_hydra.time.dao.Download;
import com.hail_hydra.time.dao.Explore;
import com.hail_hydra.time.dao.Remove;
import com.hail_hydra.time.dao.Upload;
import com.hail_hydra.time.entities.Diary;
import com.hail_hydra.time.entities.DiaryImage;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public class DiaryDetail extends Activity {
    private Button deleteButton;
    private ImageView imageView;
    private TextView textView;
    private ImageButton backButton; //回退按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_detail);

        //按钮
        deleteButton = (Button)findViewById(R.id.btn_delete);
        backButton=(ImageButton)findViewById(R.id.back);

        //从Intent处获取数据
        String content = getIntent().getStringExtra("diary_content");
        final String dateTime = getIntent().getStringExtra("diary_date");

        //获取视图
        imageView = (ImageView)findViewById(R.id.imageView);
        textView = (TextView)findViewById(R.id.content);

        //设置视图内容
        textView.setText(content);

        //删除日记
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveTask removeTask = new RemoveTask(dateTime);
                removeTask.execute();
            }
        });

        //退回到主界面
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(DiaryDetail.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                DiaryDetail.this.startActivity(intent);
                DiaryDetail.this.finish();
            }
        });

        //进入图片界面
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(DiaryDetail.this, ImageDetail.class);
                DiaryDetail.this.startActivity(intent);
                //DiaryDetail.this.finish();
            }
        });

        DownloadTask downloadTask = new DownloadTask(dateTime);
        downloadTask.execute();
    }

    private class RemoveTask extends AsyncTask<Void, Integer, Boolean> {
        private String date_time;

        public RemoveTask(String date_time) {
            this.date_time = date_time;
        }

        @Override
        protected Boolean doInBackground(Void... args0) {
            if(Remove.sRemove(date_time) == 0){
                return true;
            }
            else{
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result) {
                Intent intent = new Intent();                   //跳转回并重新加载主页面
                intent.setClass(DiaryDetail.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                DiaryDetail.this.startActivity(intent);
                DiaryDetail.this.finish();
            }
            else{
                Toast toast = Toast.makeText(DiaryDetail.this,
                        "删除失败，请检查网络连接",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private class DownloadTask extends AsyncTask<Void, Integer, Bitmap>{
        private String date_time;

        DownloadTask(String date_time){
            this.date_time = date_time;
        }
        @Override
        protected Bitmap doInBackground(Void... arg0){
            byte[] bitPic = Download.sDownload(date_time);
            if(bitPic==null){
                return null;
            }
            else {
                return BitmapFactory.decodeByteArray(bitPic, 0, bitPic.length);
            }
        }

        protected void onPostExecute(Bitmap bm) {
            if(bm != null) {
                imageView.setImageBitmap(bm);
                DiaryImage.setDiaryImage(bm);
            }
            else{
                Toast toast = Toast.makeText(DiaryDetail.this,
                        "下载图片失败，请检查网络连接",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
