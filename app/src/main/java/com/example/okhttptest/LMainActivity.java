package com.example.okhttptest;

import android.content.ComponentName;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import butterknife.BindView;

public class LMainActivity extends LBaseActivity {
    private String url = "";

    @BindView(R.id.listView1)
    protected ListView listView;

    private String[][] activities = {{
            "GET请求（同步/异步）",
            "POST请求（同步/异步）",
            "POST请求提交String",
            "POST请求提交实体/JSON",
            "POST请求提交普通Form表单",
            "POST请求提交混合Form表单",
            "POST请求提交单/多文件（带进度条）",
            "GET请求下载文件（带进度条）",
            "Cookie持久化",
    }, {
            "com.example.okhttptest.activities.OkHttpDemoActivity",
            "com.example.okhttptest.activities.OkHttpDemoActivity",
            "com.example.okhttptest.activities.",
            "com.example.okhttptest.activities.OkHttpDemoActivity",
            "com.example.okhttptest.activities.OkHttpDemoActivity",
            "com.example.okhttptest.activities.OkHttpDemoActivity",
            "com.example.okhttptest.activities.OkHttpDemoUploadFileActivity",
            "com.example.okhttptest.activities.OkHttpDemoDownloadFileActivity",
            "com.example.okhttptest.activities.OkHttpCookieActivity",
    }};


    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String target = activities[1][i];
                ComponentName name = new ComponentName(getPackageName(), target);
                Intent intent = new Intent();
                intent.putExtra("title", activities[0][i]);
                intent.setComponent(name);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void initData() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, activities[0]);
        listView.setAdapter(adapter);
    }
}
