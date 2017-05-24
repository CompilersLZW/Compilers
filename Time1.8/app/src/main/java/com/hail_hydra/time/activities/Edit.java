package com.hail_hydra.time.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.os.Bundle;

import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.hail_hydra.time.R;
import com.hail_hydra.time.adapters.DiaryAdapter;
import com.hail_hydra.time.dao.Explore;
import com.hail_hydra.time.dao.Upload;
import com.hail_hydra.time.entities.Diary;
import com.hail_hydra.time.entities.DiaryImage;
import com.hail_hydra.time.utils.GetPathFromUri4kitkat;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Edit extends Activity {

    private ImageView imageView;//图片预览
    private ImageButton backButton; //回退按钮
    private ImageButton picButton;  //添加图片
    private Button saveButton; //保存按钮
    private EditText editText; //文字内容
    private String imagePath=null;//图片路径
    private Uri imageUri=null;//图片URI
    private String content;//输入的文字
    private boolean flag;//是否已经上传过

    private static final String IMAGE_TYPE="image/*";
    private final int IMAGE_CODE=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit);

        flag=false;  //未上传过

        imageView=(ImageView)findViewById(R.id.preview);

        //文字内容
        editText=(EditText)findViewById(R.id.et_content);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);  //改为多行文本输入
        editText.setSingleLine(false);//改变默认的单行模式 
        editText.setHorizontallyScrolling(false);//水平滚动设置为False  

        //按钮
        backButton=(ImageButton)findViewById(R.id.back);
        picButton=(ImageButton)findViewById(R.id.pic);
        saveButton=(Button)findViewById(R.id.btn_ok);

        //退回到主界面
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Edit.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Edit.this.startActivity(intent);
                Edit.this.finish();
            }
        });

        //进入图片界面
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imagePath != null) {
                    Intent intent = new Intent();
                    intent.setClass(Edit.this, ImageDetail.class);
                    Edit.this.startActivity(intent);
                    //DiaryDetail.this.finish();
                }
            }
        });

        //从系统相册中选择图片
        picButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage();
            }
        });

        //保存并上传
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if(imagePath != null && !flag) {
                     flag=true;
                     Diary diaryUpload = new Diary();
                     diaryUpload.setPathName(imagePath);
                     content=editText.getText().toString();//获取文字
                     diaryUpload.setContent(content);
                     UploadTask uploadTask = new UploadTask(diaryUpload);
                     uploadTask.execute();
                }
                else{
                     //提示选择图片
                     Toast toast = Toast.makeText(Edit.this,
                             "请选择图片哦",
                             Toast.LENGTH_SHORT);
                     toast.show();
                 }
            }
        });
    }

    //从系统相册中选择图片
    private void setImage(){
        boolean isKitKat=Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT;
        Intent getAlbum;
        if(isKitKat){
            getAlbum=new Intent(Intent.ACTION_OPEN_DOCUMENT);
        }else{
            getAlbum=new Intent(Intent.ACTION_GET_CONTENT);
        }
        getAlbum.setType(IMAGE_TYPE);
        startActivityForResult(getAlbum,IMAGE_CODE);
    }

    @Override
    protected  void onActivityResult(int requestCode,int resultCode,Intent data){
        if(resultCode!=RESULT_OK){
            Log.e("TAG->onResult","ActivityResult resultCode error");
            return;
        }
        Bitmap bitmap;
        ContentResolver resolver=getContentResolver();
        if(requestCode==IMAGE_CODE){
            try{
                imageUri=data.getData();
                bitmap=MediaStore.Images.Media.getBitmap(resolver,imageUri);
                imageView.setImageBitmap(bitmap);
                DiaryImage.setDiaryImage(bitmap);
                imagePath=GetPathFromUri4kitkat.getPath(Edit.this,imageUri);
                imageView.setBackgroundResource(0);
            }catch(IOException e){
                Log.e("TAG-->Error",e.toString());
            }
        }
    }

    private class UploadTask extends AsyncTask<Void, Integer, Boolean> {
        private Diary diaryUpload;

        public UploadTask(Diary diaryUpload){
            this.diaryUpload = diaryUpload;
        }

        @Override
        protected Boolean doInBackground(Void... args0){

            if (Upload.sUpload(diaryUpload) == 0){
                return true;
            }
            else{
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if(result) {
                Intent intent = new Intent();
                intent.setClass(Edit.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Edit.this.startActivity(intent);
                Edit.this.finish();
            }
            else{
                Toast toast = Toast.makeText(Edit.this,
                        "上传失败，请检查网络连接",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }

    }
}
