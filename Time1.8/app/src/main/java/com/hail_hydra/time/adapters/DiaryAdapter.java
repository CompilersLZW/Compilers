package com.hail_hydra.time.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.hail_hydra.time.R;
import com.hail_hydra.time.activities.DiaryDetail;
import com.hail_hydra.time.activities.MainActivity;
import com.hail_hydra.time.entities.Diary;
import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by J_Zhen on 2017/3/29.
 */
public class DiaryAdapter extends ArrayAdapter<Diary> {
    public DiaryAdapter(Context context, int resource, List<Diary> objects){super(context,resource,objects);}

    @Override
    public View getView(int position,View convertView, ViewGroup parent){
        //获取日记数据
        final Diary diary = getItem(position);
        //创建布局
        View oneDiaryView = LayoutInflater.from(getContext()).inflate(R.layout.diary_item,parent,false);
        //获取日期和内容
        TextView dateTime=(TextView)oneDiaryView.findViewById(R.id.diary_small_dateTime);
        TextView content=(TextView)oneDiaryView.findViewById(R.id.diary_small_content);
        //设置两个TextView的展现
        dateTime.setText(diary.getDateTime());
        content.setText(diary.getContent());

        oneDiaryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //根据日记时间获取图片
                String dateTime = diary.getDateTime();

                //初始化一个准备跳转到DiaryDetailActivity的Intent
                Intent intent=new Intent(getContext(),DiaryDetail.class);
                //往Intent中传入Teacher相关的数据
                intent.putExtra("diary_date",dateTime);
                intent.putExtra("diary_content",diary.getContent());
                //准备跳转
                getContext().startActivity(intent);
                //((Activity)getContext()).finish();
            }
        });

        return oneDiaryView;

    }
}
