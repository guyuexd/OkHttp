package com.example.okhttptest.listener;

import java.io.File;

public interface ProgressListner {

    void onStart();

    void onProgress(long total, float progress);

    void onFinish(File file);

    void onError(Exception e);
}
