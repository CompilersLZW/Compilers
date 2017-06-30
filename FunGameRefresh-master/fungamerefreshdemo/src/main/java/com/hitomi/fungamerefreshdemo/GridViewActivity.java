package com.hitomi.fungamerefreshdemo;

import android.os.SystemClock;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.hitomi.refresh.view.FunGameRefreshView;

public class GridViewActivity extends BaseActivity {

    private GridView gridView;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_grid_view);
    }

    @Override
    public void initView() {
        refreshView = (FunGameRefreshView) findViewById(R.id.refresh_fun_game);
        gridView = (GridView) findViewById(R.id.grid_view);
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
        gridView.setAdapter(baseAdapter);
    }
}
