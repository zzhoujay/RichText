package com.zzhoujay.okhttpimagedownloader;

import android.annotation.SuppressLint;

import com.zzhoujay.richtext.ig.Cancelable;
import com.zzhoujay.richtext.ig.ImageDownloadCallback;
import com.zzhoujay.richtext.ig.ImageDownloader;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhou on 2017/9/11.
 * 使用OkHttp实现的图片下载器
 */

@SuppressWarnings("unused")
public class OkHttpImageDownloader implements ImageDownloader {

    @Override
    public Cancelable download(String source, ImageDownloadCallback callback) {
        OkHttpClient client = getClient();
        Request request = new Request.Builder().url(source).get().build();
        Call call = client.newCall(request);
        call.enqueue(new OkHttpCallback(callback));
        return new CallCancelableWrapper(call);
    }

    private static class OkHttpCallback implements Callback {

        private WeakReference<ImageDownloadCallback> imageDownloadCallbackWeakReference;

        OkHttpCallback(ImageDownloadCallback imageDownloadCallback) {
            this.imageDownloadCallbackWeakReference = new WeakReference<>(imageDownloadCallback);
        }

        @Override
        public void onFailure(Call call, IOException e) {
            ImageDownloadCallback imageDownloadCallback = imageDownloadCallbackWeakReference.get();
            if (imageDownloadCallback != null) {
                imageDownloadCallback.failure(e);
            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            ImageDownloadCallback imageDownloadCallback = imageDownloadCallbackWeakReference.get();
            if (imageDownloadCallback != null) {
                imageDownloadCallback.success(response.body().byteStream());
            }
        }
    }


    private static OkHttpClient getClient() {
        return OkHttpClientHolder.CLIENT;
    }


    private static class OkHttpClientHolder {
        private static final OkHttpClient CLIENT;
        private static SSLContext sslContext = null;

        private static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            @SuppressLint("BadHostnameVerifier")
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        static {
            // 设置https为全部信任
            X509TrustManager xtm = new X509TrustManager() {
                @SuppressLint("TrustAllX509TrustManager")
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };


            try {
                sslContext = SSLContext.getInstance("SSL");

                sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                e.printStackTrace();
            }

            CLIENT = new OkHttpClient().newBuilder()
                    .sslSocketFactory(sslContext.getSocketFactory(), xtm)
                    .hostnameVerifier(DO_NOT_VERIFY)
                    .build();
        }

    }
}
