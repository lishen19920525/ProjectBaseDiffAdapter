package com.example.demo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.basediffadapter.InconsistencyLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private DemoAdapter demoAdapter;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        RecyclerView rvDemo = findViewById(R.id.rvDemo);
        rvDemo.setLayoutManager(new InconsistencyLinearLayoutManager(this));
//        rvDemo.addItemDecoration(new LinearOnlyInsideItemDecoration(this, 3,
//                LinearOnlyInsideItemDecoration.VERTICAL));
        demoAdapter = new DemoAdapter(this, DemoBean.class);
        rvDemo.setAdapter(demoAdapter);

        final SwipeRefreshLayout srlDemo = findViewById(R.id.srlDemo);
        srlDemo.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlDemo.setRefreshing(false);
                        refreshData();
                    }
                }, 500);
            }
        });

        initData();

        Toast t = Toast.makeText(this, "下拉刷新RecyclerView进行随机Item局部刷新", Toast.LENGTH_LONG);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();

        PermissionHandler.get().request(this, PermissionHandler.Type.STORAGE, new PermissionHandler.Callback() {
            @Override
            public void onResult(boolean grant) {
                PermissionHandler.get().request(MainActivity.this, PermissionHandler.Type.LOCATION, new PermissionHandler.Callback() {
                    @Override
                    public void onResult(boolean grant) {

                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHandler.get().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    private void initData() {
        List<DemoBean> data = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            DemoBean d = new DemoBean();
            d.generateData();
            data.add(0, d);
        }
        demoAdapter.setData(data);
    }


    /**
     * 增加数据
     */
    private void addData() {
        DemoBean d = new DemoBean();
        d.generateData();
        demoAdapter.addData(0, d);

        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RecyclerView rvDemo = findViewById(R.id.rvDemo);
                rvDemo.smoothScrollToPosition(0);
            }
        }, 100);
    }

    /**
     * 随机改变某一项item的数据
     */
    private void refreshData() {
        int max = demoAdapter.getItemCount();
        if (max > 0) {
            Random random = new Random();
            int chosen = random.nextInt(max - 1);
            DemoBean itemData = demoAdapter.getItemData(chosen);
            itemData.content = DemoUtil.generateRandomString();
            itemData.icon = DemoUtil.generateRandomIcon();
            // 此处不能调用 notifyDataSetChanged
            demoAdapter.notifyDataUpdated();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.itemMainAdd) {
            addData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}