package com.hitomi.fungamerefreshdemo;

import android.os.SystemClock;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hitomi.refresh.view.FunGameRefreshView;

public class ListViewActivity extends BaseActivity {

    private ListView listView;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_list_view);
    }

    @Override
    public void initView() {
        refreshView = (FunGameRefreshView) findViewById(R.id.refresh_fun_game);
        refreshView.setLoadingText("玩个游戏解解闷");
        refreshView.setGameOverText("游戏结束");
        refreshView.setLoadingFinishedText("加载完成");
        refreshView.setTopMaskText("下拉刷新");
        refreshView.setBottomMaskText("上下滑动控制游戏");

        listView = (ListView) findViewById(R.id.list_view);
    }

    @Override
    public void setViewListener() {
        refreshView.setOnRefreshListener(new FunGameRefreshView.FunGameRefreshListener() {
            @Override
            public void onPullRefreshing() {
                SystemClock.sleep(2000);
            }

            @Override
            public void onRefreshComplete() {
                updateDataList();
                baseAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void processLogic() {
        baseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, createDate());
        listView.setAdapter(baseAdapter);
    }

}
