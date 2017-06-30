package com.hitomi.fungamerefreshdemo;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.BaseAdapter;

import com.hitomi.refresh.view.FunGameRefreshView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hitomi on 2016/12/2.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected FunGameRefreshView refreshView;
    protected BaseAdapter baseAdapter;

    protected List<String> dataList;

    public abstract void setContentView();
    public abstract void initView();
    public abstract void setViewListener();
    public abstract void processLogic();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        initView();
        setViewListener();
        processLogic();
    }

    protected List<String> createDate() {
        dataList = new ArrayList<>();
        dataList.add("A");
        dataList.add("B");
        dataList.add("C");
        dataList.add("D");
        dataList.add("E");
        dataList.add("F");
        dataList.add("G");
        return dataList;
    }

    protected void updateDataList() {
        String lastStr = dataList.get(dataList.size() - 1);
        char lastChar = lastStr.toCharArray()[0];
        int c = (int) lastChar;
        ++c;
        String str = String.valueOf((char) c);
        dataList.add(str);
    }
}
