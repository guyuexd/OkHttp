package com.example.okhttptest.activities;


import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.okhttptest.LBaseActivity;
import com.example.okhttptest.R;
import com.example.okhttptest.constant.Constants;
import com.example.okhttptest.entity.LoginInfo;
import com.example.okhttptest.http.LOkHttp3Utils;
import com.example.okhttptest.utils.ExecutorUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 普通GET/POST的同步/异步请求实例
 */

public class OkHttpDemoActivity extends LBaseActivity {

    @BindView(R.id.editText1)
    EditText editText1;

    @BindView(R.id.editText2)
    EditText editText2;

    @BindView(R.id.textView1)
    TextView textView1;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_okhttp_demo_normal;
    }

    @Override
    protected void initView() {
        setTitle(getIntent().getStringExtra("title"));
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6, R.id.button7, R.id.button8})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1: // 同步GET请求
                getSync();
                break;

            case R.id.button2: // 异步GET请求
                getAsync();
                break;

            case R.id.button3: // 同步POST请求
                postSync();
                break;

            case R.id.button4: // 异步POST请求
                postAsync();
                break;

            case R.id.button5: // 异步POST请求 string
                postStringAsync();
                break;

            case R.id.button6: // 异步POST请求 json
                postJSONAsync();
                break;

            case R.id.button7: // 异步POST请求 form
                postFormAsync();
                break;

            case R.id.button8: // 异步POST请求 form
                postMultiFormAsync();
                break;
        }
    }

    /**
     * 同步GET请求
     */
    private void getSync(){
        String url = Constants.BASE_URL + "/request1";
        url += "?username="+editText1.getText().toString() + "&password=" + editText2.getText().toString() + "&signcode=signcode";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("name", "value")
                .tag("getSync")
                .build();

        final Call call = LOkHttp3Utils.okHttpClient().newCall(request);
        ExecutorUtils.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    showDialog();
                    Message message = handler.obtainMessage();
                    message.what = 1;
                    message.obj = call.execute();
                    handler.sendMessage(message);
                }catch (Exception e){
                    e.printStackTrace();
                    hideDialog();
                }
            }
        });

    }

    /**
     * 异步GET请求
     */
    private void getAsync(){
        String url = Constants.BASE_URL + "/request1";
        url += "?username=" + editText1.getText().toString() + "&password=" + editText2.getText().toString() + "&signcode=signcode";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("name","value")
                .tag("getAsync")
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
     * 同步Post请求
     */
    private void postSync(){
        String url = Constants.BASE_URL + "/request2";
        url += "?username=" + editText1.getText().toString() + "&password=" + editText2.getText().toString() + "&signcode=signcode";

        RequestBody requestBody = RequestBody.create(MediaType.parse("text/html; charset=utf-8"), "123");

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("name","value")
                .tag("postSync")
                .build();

        final Call call = LOkHttp3Utils.okHttpClient().newCall(request);
        ExecutorUtils.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    showDialog();
                    Message message = handler.obtainMessage();
                    message.what = 1;
                    message.obj = call.execute();
                    handler.sendMessage(message);
                } catch (IOException e){
                    e.printStackTrace();
                    hideDialog();
                }

            }
        });

    }


    /**
     * 异步post请求
     */
    private void postAsync(){
        String url = Constants.BASE_URL + "/request2";
        url += "?username=" + editText1.getText().toString() + "&password=" + editText2.getText().toString() + "&signcode=signcode";

        RequestBody requestBody = RequestBody.create(MediaType.parse("text/html; charset=utf-8"), "123");

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("name", "value")
                .tag("postAsync")
                .build();

        final Call call= LOkHttp3Utils.okHttpClient().newCall(request);
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
     * 异步提交String
     */
    private void postStringAsync(){
        String url = Constants.BASE_URL + "/request3";

        RequestBody requestBody = RequestBody.create(MediaType.parse("text/html; charset=utf-8"), "this is ordinary String");

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("name", "value")
                .tag("postStringAsync")
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
     * 异步提交JSON
     */
    private void postJSONAsync(){
        String url = Constants.BASE_URL + "/request4";
        LoginInfo info = new LoginInfo();
        info.setUsername("username");
        info.setPassword("password");
        info.setSigncode("signcode");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(info));

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("name", "value")
                .tag("postJSONAsync")
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
     * 异步POST请求 form
     */
    private void postFormAsync() {
        String url = Constants.BASE_URL + "/request5";

        FormBody formBody = new FormBody.Builder()
                .add("username", editText1.getText().toString())
                .add("password", editText2.getText().toString())
                .add("signcode", "signcode")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .addHeader("name", "value")
                .tag("postFormAsync")
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
     * 异步请求 multiform 文本+file
     */
    private void postMultiFormAsync() {
        String url = Constants.BASE_URL + "/request6";

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test.txt");
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file, false);
            writer.append("this is file content");
            writer.flush();;
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);

        //方式 一
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", editText1.getText().toString())
                .addFormDataPart("password", editText2.getText().toString())
                .addFormDataPart("signcode", "signcode")
                .addFormDataPart("file1Id", "1")
                .addFormDataPart("file1", file.getName(), fileBody)
                .addFormDataPart("file2", file.getName(), fileBody)
                .build();

        //方式 二
        FormBody formBody1 = new FormBody.Builder()
                .add("username", editText1.getText().toString())
                .add("password", editText2.getText().toString())
                .add("signcode", "signcode")
                .add("file1Id", "1")
                .build();
        RequestBody requestBody1 = new MultipartBody.Builder()
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"params\""), formBody1)
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"file1\"; filename=\"" + file.getName() + "\""), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("name", "value")
                .tag("postMultiFormAsync")
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



    private Handler handler = new Handler(new Handler.Callback(){

        @Override
        public boolean handleMessage(Message message) {
            hideDialog();
            switch (message.what){
                case 0:
                    textView1.setText("error:"+message.obj);
                    break;
                case 1:
                    try{
                        dealResponse((Response) message.obj);
                    } catch (Exception e) {
                        textView1.setText("error:"+e.getMessage());
                        e.printStackTrace();
                    }
                    break;
            }
            return false;
        }
    });

    /**
     * 处理返回结果
     */
    private void dealResponse(Response response) throws Exception {
        int code = response.code();
        if(code == 200) {
            String result = response.body().string();
            textView1.setText(result);
            Log.i("r", result);
        } else {
            textView1.setText("error:" + response.message());
            Log.e("e", response.message());
        }
    }

}
