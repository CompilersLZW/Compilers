package com.hitomi.fungamerefreshdemo;

import android.os.SystemClock;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hitomi.refresh.view.FunGameRefreshView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

public class RecycleViewActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recycleAdapter;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_recycle_view);
    }

    @Override
    public void initView() {
        refreshView = (FunGameRefreshView) findViewById(R.id.refresh_fun_game);

        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
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
                recycleAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void processLogic() {
        recycleAdapter = new CommonAdapter<String>(this, R.layout.item_recyle_view, createDate()) {

            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                holder.setText(R.id.text, s);
            }

        };
        recyclerView.setAdapter(recycleAdapter);
    }

}
