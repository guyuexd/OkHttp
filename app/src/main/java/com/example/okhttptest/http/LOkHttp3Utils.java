package com.example.okhttptest.http;


import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.example.okhttptest.entity.LoginInfo;
import com.example.okhttptest.http.cookie.CookieJarImpl;
import com.example.okhttptest.http.cookie.MemoryCookieStore;
import com.example.okhttptest.http.cookie.PersistentCookieStore;
import com.example.okhttptest.listener.ProgressListner;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * 提供okHttp单例，同时列举okhttp基本用法
 */
public class LOkHttp3Utils {

    private static final int HTTP_TIME_OUT = 30;

    private static OkHttpClient okHttpClient = null;

    /**
     * 构建okhttpClient对象，设置对应的全局参数
     */
    public static OkHttpClient okHttpClient(){
        if(okHttpClient == null){
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .readTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
                    .writeTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
                    .connectTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            Response response = chain.proceed(request);
                            return response;
                        }
                    })
                    .addNetworkInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            Response response = chain.proceed(request);
                            return response;
                        }
                    });
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    /**
     * 构建OkhttpClient对象，设置对应的全局参数，持久化cookie
     */
    public static OkHttpClient okHttpClient1(Context context){
        if(okHttpClient == null){
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .readTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
                    .writeTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
                    .connectTimeout(HTTP_TIME_OUT,TimeUnit.SECONDS)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            Response response = chain.proceed(request);
                            return response;
                        }
                    })
                    .addNetworkInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            Response response = chain.proceed(request);
                            return response;
                        }
                    })
//                    .cookieJar(new CookieJarImpl(new PersistentCookieStore(context)))
                    .cookieJar(new CookieJarImpl(new MemoryCookieStore()));

            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    /**
     * 清除所有缓存
     */
    public static void clearCookies(){
        if(okHttpClient != null && okHttpClient.cookieJar() instanceof CookieJarImpl){
            CookieJarImpl cookieJar = (CookieJarImpl) okHttpClient.cookieJar();
            cookieJar.getCookieStore().removeAll();
        }
    }

    /**
     * create file requestBody custom progress bar listener
     */
    public static RequestBody createProgressRequestBody(final MediaType mediaType, final File file, final ProgressListner listener) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return mediaType;
            }

            @Override
            public long contentLength() throws IOException {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                listener.onStart();
                Source source;
                try {
                    source = Okio.source(file);
                    //sink.writeAll(source);
                    Buffer buf = new Buffer();
                    Long remaining = contentLength();
                    for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                        sink.write(buf, readCount);
                        listener.onProgress(contentLength(), 1 - (float)(remaining -= readCount) / contentLength());
                    }
                    listener.onFinish(file);
                } catch (Exception e) {
                    listener.onError(e);
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * get async
     */
    public void getAsync() {
        Request request = new Request.Builder()
                .url("")
                .get()
                .addHeader("","")
                .tag("tag")
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("", "");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int code = response.code();
                if(code == 200) {
                    String result = response.body().string();
                    Log.i("r", result);
                }
                else {
                    Log.e("e", response.message());
                }
            }
        });
    }

    /**
     * getSync
     */
    public Response getSync() throws IOException {
        Request request = new Request.Builder()
                .url("")
                .get()
                .addHeader("", "")
                .tag("tag")
                .build();
        Call call = okHttpClient.newCall(request);
        return call.execute();
    }

    public void postStringAsync() {
        String bodyStr = "content";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),bodyStr);
        Request request = new Request.Builder()
                .url("")
                .post(requestBody)
                .addHeader("", "")
                .tag("tag")
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("","");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int code = response.code();
                if(code == 200) {
                    String ressult = response.body().string();
                    Log.i("r", ressult);
                } else {
                    Log.e("e", response.message());
                }
            }
        });
    }


    /**
     * post String sync
     */
    public Response postStringSync() throws IOException {
        String bodyStr = "content";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), bodyStr);
        Request request = new Request.Builder()
                .url("")
                .post(requestBody)
                .tag("postStringSync")
                .addHeader("", "")
                .build();
        Call call = okHttpClient.newCall(request);
        return call.execute();
    }


    public void postStreamAsync() {
        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse("application/octet-stream; charset=utf-8");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8("upload stream");

            }
        };
        Request request = new Request.Builder()
                .url("")
                .post(requestBody)
                .addHeader("", "")
                .tag("tag")
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("", "");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int code = response.code();
                if(code == 200){
                    String result = response.body().string();
                    Log.i("r", result);
                }
                else {
                    Log.e("e", response.message());
                }
            }
        });
    }

    /**
     * Async form sheet
     */
    public void postFormAsync() {
        FormBody formBody = new FormBody.Builder()
                .add("key1", "value1")
                .add("key2", "value2")
                .add("key3", "value3")
                .build();
        Request request = new Request.Builder()
                .url("")
                .post(formBody)
                .addHeader("", "")
                .tag("tag")
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("", "");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int code = response.code();
                if (code == 200){
                    String result = response.body().string();
                    Log.i("r", result);
                }
                else {
                    Log.e("e", response.message());
                }
            }
        });
    }


    /**
     * post file async
     */
    public void postFileAsync() {
        File file = new File("filePath");
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/actet; charset=utf-8"), file);

        Request request = new Request.Builder()
                .url("")
                .post(requestBody)
                .addHeader("", "")
                .tag("postFileAsync")
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("", "");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int code = response.code();
                if(code == 200) {
                    String result = response.body().string();
                    Log.i("r", result);
                }
                else {
                    Log.e("e", response.message());
                }
            }
        });
    }

public void postMultipartFormAsync() {
        File file = new File("filePath");
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream; charset=utf-8"),file);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("key1", "value1")
                .addFormDataPart("key2", "value2")
                .addFormDataPart("key3", "value3")
                .addFormDataPart("file1", "name1", fileBody)
                .build();

        FormBody formBody = new FormBody.Builder()
                .add("key1", "value1")
                .add("key2", "value2")
                .add("key3", "value3")
                .build();
        RequestBody requestBody1 = new MultipartBody.Builder()
                .addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"params\""),
                        formBody)
                .addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"file\"; filename=\"plans.xml\""),
                        fileBody)
                .build();


        Request request = new Request.Builder()
                .url("")
                .post(requestBody)
                .addHeader("", "")
                .tag("tag")
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("", "");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int code = response.code();
                if(code == 200) {
                    String result = response.body().string();
                    Log.i("r", result);
                }
                else {
                    Log.e("e", response.message());
                }
            }
        });
}


    public void cancelTag(Object tag) {
        if (tag == null) return;
        for(Call call : okHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }

        }
        for(Call call : okHttpClient.dispatcher().runningCalls()) {
            if(tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    public void cancelTag(OkHttpClient okHttpClient, Object tag) {
        if(okHttpClient == null || tag == null) return;
        for(Call call : okHttpClient.dispatcher().queuedCalls()) {
            if(tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for(Call call : okHttpClient.dispatcher().runningCalls()) {
            if(tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }


    public void cancelAll() {
        for(Call call : okHttpClient.dispatcher().queuedCalls()) {
            call.cancel();
        }
        for(Call call : okHttpClient.dispatcher().runningCalls()) {
            call.cancel();
        }
    }


    public void cancelAll(OkHttpClient client) {
        for(Call call : client.dispatcher().queuedCalls())
            call.cancel();
        for(Call call : client.dispatcher().runningCalls()) {
            call.cancel();
        }
    }

}
