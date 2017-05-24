package com.hail_hydra.time.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import android.widget.ListView;
import android.widget.Toast;

import com.hail_hydra.time.R;
import com.hail_hydra.time.adapters.DiaryAdapter;
import com.hail_hydra.time.dao.Explore;
import com.hail_hydra.time.entities.Diary;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity{

    DiaryAdapter diaryAdapter;
    ListView listView;

    private Context mContext;
    private Button addNote;  //编辑按钮


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //初始化一个adapter
//        diaryAdapter = new DiaryAdapter(this, R.layout.diary_item,Diary.getAllDiary());
        diaryAdapter = new DiaryAdapter(this, R.layout.diary_item,new ArrayList<Diary>());
        //通过ID获取listView
        listView = (ListView)findViewById(R.id.diary_listView);
        //设置listView的adapter
        listView.setAdapter(diaryAdapter);

        addNote = (Button) findViewById(R.id.btn_editnote);
        mContext = this;

        addNote.setOnClickListener(new MyButtonListener()); //点击事件 进入编辑页面
        ExploreTask exploreTask = new ExploreTask();
        exploreTask.execute();
    }

    class MyButtonListener implements View.OnClickListener {
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Edit.class);
            MainActivity.this.startActivity(intent);
            MainActivity.this.finish();
        }
    }

    private class ExploreTask extends AsyncTask<Void, Integer, List<Diary>>{
        @Override
        protected List<Diary> doInBackground(Void... arg0){

            return Explore.sExplore();
        }

        protected void onPostExecute(List<Diary> diaries) {
            if(diaries == null){
                Toast toast = Toast.makeText(MainActivity.this,
                        "未连接到服务器，请检查网络连接",
                        Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            diaryAdapter = new DiaryAdapter(MainActivity.this, R.layout.diary_item,diaries);
            listView.setAdapter(diaryAdapter);
        }

    }
}
