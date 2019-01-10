package com.zzhoujay.richtext.ig;

import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.cache.BitmapPool;
import com.zzhoujay.richtext.callback.BitmapStream;
import com.zzhoujay.richtext.exceptions.ImageDownloadTaskAddFailureException;
import com.zzhoujay.richtext.exceptions.ImageLoadCancelledException;
import com.zzhoujay.richtext.ext.Debug;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhou on 2017/10/9.
 * 图片下载管理器
 */

class ImageDownloaderManager {


    private final HashMap<String, Task> tasks;


    private ImageDownloaderManager() {
        tasks = new HashMap<>();
    }


    static ImageDownloaderManager getImageDownloaderManager() {
        return ImageDownloaderManagerHolder.IMAGE_DOWNLOADER_MANAGER;
    }


    Cancelable addTask(ImageHolder holder, ImageDownloader imageDownloader, CallbackImageLoader callbackImageLoader) {
        String key = holder.getKey();
        synchronized (tasks) {
            Task task = tasks.get(key);
            if (task == null) {
                task = new Task(holder.getSource(), key, imageDownloader, IMAGE_READY_CALLBACK);
                tasks.put(key, task);
            }
            return task.exec(getExecutorService(), callbackImageLoader);
        }
    }

    private final ImageDownloadFinishCallback IMAGE_READY_CALLBACK = new ImageDownloadFinishCallback() {
        @Override
        public void imageDownloadFinish(String key) {
            synchronized (tasks) {
                tasks.remove(key);
            }
        }
    };

    public interface ImageDownloadFinishCallback {

        void imageDownloadFinish(String key);

    }


    private static class TaskCancelable implements Cancelable {

        private WeakReference<Task> taskWeakReference;
        private WeakReference<CallbackImageLoader> callbackImageLoaderWeakReference;

        TaskCancelable(Task task, CallbackImageLoader callbackImageLoader) {
            this.taskWeakReference = new WeakReference<>(task);
            this.callbackImageLoaderWeakReference = new WeakReference<>(callbackImageLoader);
        }

        @Override
        public void cancel() {
            Task task = taskWeakReference.get();
            if (task != null) {
                CallbackImageLoader callbackImageLoader = callbackImageLoaderWeakReference.get();
                if (callbackImageLoader != null) {
                    task.removeCallback(callbackImageLoader);
                    callbackImageLoader.onFailure(new ImageLoadCancelledException());
                }
            }
        }
    }

    private static class Task implements Runnable {

        private static final int STATE_INIT = 0;
        private static final int STATE_WORK = 1;
        private static final int STATE_CALLBACK = 2;
        private static final int STATE_FINISHED = 3;

        private final String key;
        private final String imageUrl;
        private final ImageDownloader imageDownloader;

        private volatile int state;

        private final Object stateLock = new Object();

        private final ArrayList<CallbackImageLoader> callbackList;
        private final ImageDownloadFinishCallback imageDownloadFinishCallback;

        Task(String imageUrl, String key, ImageDownloader imageDownloader, ImageDownloadFinishCallback imageDownloadFinishCallback) {
            this.imageUrl = imageUrl;
            this.imageDownloader = imageDownloader;
            this.imageDownloadFinishCallback = imageDownloadFinishCallback;
            this.state = STATE_INIT;

            this.callbackList = new ArrayList<>();

            this.key = key;
        }

        @Override
        public void run() {

            synchronized (stateLock) {
                state = STATE_WORK;
            }

            Exception exception = null;

            try {
                BitmapStream bitmapStream = imageDownloader.download(imageUrl);
                BitmapPool.getPool().writeBitmapToTemp(key, bitmapStream.getInputStream());
                bitmapStream.close();
            } catch (Exception e) {
                exception = e;
            }

            synchronized (stateLock) {

                imageDownloadFinishCallback.imageDownloadFinish(key);

                if (state != STATE_WORK) {
                    return;
                }

                state = STATE_CALLBACK;

                synchronized (callbackList) {

                    for (CallbackImageLoader imageLoader : callbackList) {
                        try {
                            imageLoader.onImageDownloadFinish(key, exception);
                        } catch (Throwable e) {
                            Debug.e(e);
                        }
                    }

                }
                state = STATE_FINISHED;
            }
        }

        private void removeCallback(CallbackImageLoader callbackImageLoader) {
            synchronized (callbackList) {
                callbackList.remove(callbackImageLoader);
            }
        }

        private Cancelable exec(ExecutorService executorService, CallbackImageLoader callbackImageLoader) {
            Cancelable cancelable = null;
            synchronized (stateLock) {
                if (state == STATE_WORK) {
                    synchronized (callbackList) {
                        callbackList.add(callbackImageLoader);
                        cancelable = new TaskCancelable(this, callbackImageLoader);
                    }
                }
                if (state == STATE_INIT) {
                    state = STATE_WORK;
                    executorService.submit(this);

                    synchronized (callbackList) {
                        callbackList.add(callbackImageLoader);
                        cancelable = new TaskCancelable(this, callbackImageLoader);
                    }
                }
            }
            if (cancelable == null) {
                callbackImageLoader.onFailure(new ImageDownloadTaskAddFailureException());
            }
            return cancelable;
        }

    }


    private static ExecutorService getExecutorService() {
        return ExecutorServiceHolder.EXECUTOR_SERVICE;
    }


    private static class ExecutorServiceHolder {

        private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    }

    private static class ImageDownloaderManagerHolder {

        private static final ImageDownloaderManager IMAGE_DOWNLOADER_MANAGER = new ImageDownloaderManager();

    }

}
