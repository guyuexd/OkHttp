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
import com.example.okhttptest.constant.Constants;
import com.example.okhttptest.http.LOkHttp3Utils;
import com.example.okhttptest.listener.ProgressListner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * post request upload form files with progress
 */
public class OkHttpDemoUploadFileActivity extends LBaseActivity {

    @Override
    protected int getContentViewId() {
        return R.layout.activity_okhttp_demo_upload_file;
    }

    @Override
    protected void initView() {
        setTitle(getIntent().getStringExtra("title"));

    }

    @Override
    protected void initData() {

    }

    @BindView(R.id.progressBar1)
    ProgressBar progressBar;

    @OnClick({R.id.button1, R.id.button2, R.id.button3})
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.button1: // upload single file
                postFileAsync();
                break;
            case R.id.button2:  // upload single file with progress
                postFileProgressAsync();
                break;
            case R.id.button3:
                postFilesAsync();
            break;
        }
    }

    /**
     * post single file Async
     */
    private void postFileAsync() {
        String url = Constants.BASE_URL + "/request7";

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "test.txt");
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.append("this is file content");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);

        // method 1
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file1", file.getName(), fileBody)
                .build();

        // method 2
        RequestBody requestBody1 = new MultipartBody.Builder()
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"file1\"; filename= \"" + file.getName() + "\""), fileBody)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("name", "value")
                .tag("postFileAsync")
                .build();
        final Call call = LOkHttp3Utils.okHttpClient().newCall(request);
        showDialog();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = handler.obtainMessage();
                message.what = 0;
                message.obj = e.getMessage();
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = handler.obtainMessage();
                message.what = 1;
                message.obj = response;
                handler.sendMessage(message);
            }
        });
    }


    private void postFileProgressAsync() {
        String url = Constants.BASE_URL + "/request7";
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test.txt");
        try{
            if(!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file, false);
            writer.append("this is file content");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        RequestBody fileBody = LOkHttp3Utils.createProgressRequestBody(MediaType.parse("application/octet-stream"), file, new ProgressListner() {
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

            }

            @Override
            public void onError(Exception e) {

            }
        });

        //method 1
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file1", file.getName(), fileBody)
                .build();

        // method 2
        RequestBody requestBody1 = new MultipartBody.Builder()
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"file1\"; filename=\"" + file.getName() + "\""), fileBody)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("name", "value")
                .tag("postFileAsync")
                .build();
        final Call call = LOkHttp3Utils.okHttpClient().newCall(request);
        showDialog();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = handler.obtainMessage();
                message.what = 0;
                message.obj = e.getMessage();
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = handler.obtainMessage();
                message.what = 1;
                message.obj = response;
                handler.sendMessage(message);
            }
        });

    }


    /**
     * post update files
     */
    private void postFilesAsync() {
        String url = Constants.BASE_URL + "/request8";

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test.txt");
        try{
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file, false);
            writer.append("this is file content post files Async");
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);

        //method 1
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file1", file.getName(), fileBody)
                .addFormDataPart("file2", file.getName(), fileBody)
                .build();

        //method 2
        RequestBody requestBody1 = new MultipartBody.Builder()
                .addPart(Headers.of("Content-Disposition","form-data; name=\"file1\"; filename=\"" + file.getName() + "\""), fileBody)
                .addPart(Headers.of("Content-Disposition", "form-data; name\"file2\"; filename=\"" +file.getName() + "\""),fileBody)
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("name", "value")
                .tag("postFilesAsync")
                .build();
        final Call call = LOkHttp3Utils.okHttpClient().newCall(request);
        showDialog();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = handler.obtainMessage();
                message.what = 0;
                message.obj = e.getMessage();
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = handler.obtainMessage();
                message.what = 1;
                message.obj = request;
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
                    showToast("error" + message.obj);
                case 1:
                    try {
                        dealResponse((Response) message.obj);

                    } catch (Exception e) {

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
    private void dealResponse(Response response) throws IOException {
        int code = response.code();
        if(code == 200) {
            String result = response.body().string();
            showToast(result);
            Log.i("r", result);
        } else {
            showToast("error"  + response.message());
            Log.e("e", response.message());
        }
    }

}
