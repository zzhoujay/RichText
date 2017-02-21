package com.zzhoujay.richtext.ig;

import java.util.concurrent.Future;

/**
 * Created by zhou on 2017/2/21.
 */
class FutureCancelableWrapper implements Cancelable {

    private Future future;

    FutureCancelableWrapper(Future future) {
        this.future = future;
    }

    @Override
    public void cancel() {
        if (future != null && !future.isDone() && !future.isCancelled()) {
            future.cancel(true);
            future = null;
        }
    }
}
