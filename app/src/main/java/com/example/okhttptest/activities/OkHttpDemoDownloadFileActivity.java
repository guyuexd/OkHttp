package com.example.okhttptest.activities;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.example.okhttptest.LBaseActivity;
import com.example.okhttptest.R;
import com.example.okhttptest.http.FileCallBack;
import com.example.okhttptest.http.FileNoProgressCallback;
import com.example.okhttptest.http.LOkHttp3Utils;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 普通GET/POST的同步/异步请求实例
 */
public class OkHttpDemoDownloadFileActivity extends LBaseActivity {

    @BindView(R.id.progressBar1)
    ProgressBar progressBar;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_okhttp_demo_download_file;
    }

    @Override
    protected void initView() {
        setTitle(getIntent().getStringExtra("title"));
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.button1, R.id.button2})
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button1: // common download
                getFileAsync();
                break;
            case R.id.button2:
                getFileProgressAsync();
                break;
        }
    }

    /**
     * common downlaod
     */
    private void getFileAsync() {
        String url = "http://172.20.32.19:8000/oa/userfiles/image/xushijie/061901.png";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("name", "value")
                .tag("getFileAsync")
                .build();

        final Call call = LOkHttp3Utils.okHttpClient().newCall(request);
        showDialog();
        call.enqueue(new FileNoProgressCallback(Environment.getExternalStorageDirectory().getAbsolutePath(), "test.png") {
            @Override
            public void onFinish(File file) {
                Message message = handler.obtainMessage();
                message.what = 1;
                message.obj = "download success:" + file.getName();
                handler.sendMessage(message);
            }

            @Override
            public void onError(Exception e) {
                Message message = handler.obtainMessage();
                message.what = 0;
                message.obj = "error:" + e.getMessage();
                handler.sendMessage(message);
            }
        });
    }

    /**
     * progressbar download
     *
     */
    private void getFileProgressAsync() {
        String url = "http://172.20.32.19:8000/oa/userfiles/image/xushijie/061901.png";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("name","value")
                .tag("getFileProgressAsync")
                .build();

        final Call call = LOkHttp3Utils.okHttpClient().newCall(request);
        showDialog();
        call.enqueue(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "test.png") {
            @Override
            public void onStart() {

            }

            @Override
            public void onProgress(long total, float progress) {
                Message message = handler.obtainMessage();
                message.what = 2;
                message.obj = progress;
                handler.sendMessage(message);
            }

            @Override
            public void onFinish(File file) {
                Message message = handler.obtainMessage();
                message.what = 0;
                message.obj = "download success" + file.getName();
                handler.sendMessage(message);
            }

            @Override
            public void onError(Exception e) {
                Message message = handler.obtainMessage();
                message.what = 0;
                message.obj = "error:" + e.getMessage();
                handler.sendMessage(message);
            }
        });
    }


    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(@NonNull Message message) {
            hideDialog();
            switch (message.what) {
                case 0:
                    showToast(message.obj.toString());
                    break;
                case 1:
                    try {
                        dealResponse((Response) message.obj);
                    } catch (Exception e) {
                        showToast("error" + e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    progressBar.setProgress((int) ((float) message.obj * 100));
                    break;
            }
            return false;
        }
    });

    /**
     * deal with response
     */
    private void dealResponse(Response response) throws Exception {
        int code = response.code();
        if(code == 200) {
            String result = response.body().string();
            showToast(result);
            Log.i("r", result);
        } else {
            showToast("error:" + response.message());
            Log.e("e", response.message());
        }
    }
}
