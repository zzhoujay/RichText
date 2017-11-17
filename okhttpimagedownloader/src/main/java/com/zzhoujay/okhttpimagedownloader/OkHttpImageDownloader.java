package com.zzhoujay.okhttpimagedownloader;

import android.annotation.SuppressLint;

import com.zzhoujay.richtext.callback.BitmapStream;
import com.zzhoujay.richtext.ig.ImageDownloader;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
    public BitmapStream download(String source) throws IOException {
        return new BitmapStreamWrapper(source);
    }

    private static class BitmapStreamWrapper implements BitmapStream {

        private final String url;
        private Response response;
        private InputStream inputStream;

        private BitmapStreamWrapper(String url) {
            this.url = url;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            Request request = new Request.Builder().url(url).get().build();
            response = getClient().newCall(request).execute();
            inputStream = response.body().byteStream();
            return inputStream;
        }

        @Override
        public void close() throws IOException {
            if (inputStream != null) {
                inputStream.close();
            }
            if (response != null) {
                response.close();
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
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .sslSocketFactory(sslContext.getSocketFactory(), xtm)
                    .hostnameVerifier(DO_NOT_VERIFY)
                    .build();
        }

    }
}
