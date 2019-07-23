package com.example.okhttptest.http;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public abstract class FileNoProgressCallback implements Callback {

    private String destFileDir;
    private String destFileName;

    public FileNoProgressCallback(String destFileDir, String destFileName) {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
    }

    /**
     * Called when the request could not be executed due to cancellation, a connectivity problem or
     * timeout. Because networks can fail during an exchange, it is possible that the remote server
     * accepted the request before the failure.
     *
     * @param call
     * @param e
     */
    @Override
    public void onFailure(Call call, IOException e) {

    }

    /**
     * Called when the HTTP response was successfully returned by the remote server. The callback may
     * proceed to read the response body with {@link Response#body}. The response is still live until
     * its response body is {@linkplain ResponseBody closed}. The recipient of the callback may
     * consume the response body on another thread.
     *
     * <p>Note that transport-layer success (receiving a HTTP response code, headers and body) does
     * not necessarily indicate application-layer success: {@code response} may still indicate an
     * unhappy HTTP response code like 404 or 500.
     *
     * @param call
     * @param response
     */
    @Override
    public void onResponse(Call call, Response response) throws IOException {
        this.saveFile(response);
    }

    private void saveFile(Response response) throws IOException {
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
                if(fos != null) {
                    fos.close();
                }
            } catch (IOException var22) {

            }
        }
    }

    public abstract void onFinish(File file);

    public abstract void onError(Exception e);
}
