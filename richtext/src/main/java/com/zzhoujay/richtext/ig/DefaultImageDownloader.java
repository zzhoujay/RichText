package com.zzhoujay.richtext.ig;

import android.annotation.SuppressLint;

import com.zzhoujay.richtext.callback.BitmapStream;
import com.zzhoujay.richtext.exceptions.HttpResponseCodeException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by zhou on 2017/11/5.
 * 默认的图片下载器，使用HttpUrlConnect发起连接
 */

public class DefaultImageDownloader implements ImageDownloader {

    public static final String GLOBAL_ID = DefaultImageDownloader.class.getName();

    @Override
    public BitmapStream download(String source) throws IOException {
        return new BitmapStreamImpl(source);
    }

    private static class BitmapStreamImpl implements BitmapStream {

        private final String url;
        private HttpURLConnection connection;
        private InputStream inputStream;

        private BitmapStreamImpl(String url) {
            this.url = url;
        }

        @Override
        public void close() throws IOException {
            if (inputStream != null) {
                inputStream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        @Override
        public InputStream getInputStream() throws IOException {
            URL url = new URL(this.url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setDoInput(true);
            connection.addRequestProperty("Connection", "Keep-Alive");

            if (connection instanceof HttpsURLConnection) {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) connection;
                httpsURLConnection.setHostnameVerifier(DO_NOT_VERIFY);
                httpsURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());
            }

            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                inputStream = connection.getInputStream();
                return inputStream;
            } else {
                throw new HttpResponseCodeException(responseCode);
            }
        }
    }

    private static SSLContext sslContext;

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
    }
}
