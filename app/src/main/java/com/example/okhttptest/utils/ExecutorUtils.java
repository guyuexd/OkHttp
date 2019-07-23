package com.example.okhttptest.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ExecutorUtils {

    private static final int FIXED_THREAD_NUM = 5;

    private static Executor executor = Executors.newFixedThreadPool(FIXED_THREAD_NUM);

    private static Executor executor(){
        return executor;
    }

    public static void execute(Runnable runnable) {
        executor().execute(runnable);
    }
}
