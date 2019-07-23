package com.example.okhttptest.activities;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.okhttptest.LBaseActivity;
import com.example.okhttptest.R;
import com.example.okhttptest.http.LOkHttp3Utils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * cookie
 */
public class OkHttpCookieActivity extends LBaseActivity {

    @BindView(R.id.textView)
    TextView textView1;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_okhttp_demo_cookie;
    }

    @Override
    protected void initView() {
        setTitle(getIntent().getStringExtra("title"));
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.button, R.id.button1})
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.button :
                requestWithCookie();
                break;
            case R.id.button1:
                LOkHttp3Utils.clearCookies();
                break;
        }
    }

    private void requestWithCookie() {
        Request request = new Request.Builder()
                .get()
                .url("https://blog.csdn.net/u012527802/article/details/81013772")
                .tag("requestWithCookies")
                .build();

        final Call call = LOkHttp3Utils.okHttpClient1(this).newCall(request);
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
        public boolean handleMessage(@NonNull Message message) {
            hideDialog();
            switch (message.what) {
                case 0:
                    textView1.setText("error" + message.obj);
                    break;
                case 1:
                    try{
                        dealResponse((Response) message.obj);
                    } catch (Exception e) {
                        textView1.setText("error" + e.getMessage());
                        e.printStackTrace();
                    }
                    break;
            }
            return false;
        }
    });

    /**
     * deal with response
     */
    private void dealResponse(Response response) throws IOException{
        int code = response.code();
        if(code == 200) {
            String result = response.body().string();
            textView1.setText(result);
            Log.i("r", result);
        } else {
            textView1.setText("error" + response.message());
            Log.e("e", response.message());
        }
    }

}
