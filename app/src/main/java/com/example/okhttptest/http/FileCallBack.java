package com.example.okhttptest.http;


import com.example.okhttptest.listener.ProgressListner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class FileCallBack implements Callback, ProgressListner {

    private String destFileDir;

    private String destFileName;

    public FileCallBack(String destFileDir, String destFileName) {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        onError(e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        this.saveFile(response);
    }

    private void saveFile(Response response) {
        onStart();
        InputStream is = null;
        byte[] buf = new byte[2048];
        FileOutputStream fos = null;

        try {
            is = response.body().byteStream();
            final long total = response.body().contentLength();
            long sum = 0L;
            File dir = new File(this.destFileDir);
            if(!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, this.destFileName);
            fos = new FileOutputStream(file);

            int len = 0;
            while ((len = is.read(buf)) != -1) {
                sum += (long) len;
                fos.write(buf, 0, len);
                onProgress(total, (float) sum * 1.0F / (float) total);
            }
            fos.flush();
            onFinish(file);
        } catch (Exception e) {
            onError(e);
        } finally {
            try {
                response.body().close();
                if(is != null) {
                    is.close();
                }

                } catch (IOException var23) {

            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException var22) {

            }
        }
    }
}
