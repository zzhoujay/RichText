package com.zzhoujay.richtext.ig;

import okhttp3.Call;

/**
 * Created by zhou on 2017/2/21.
 */
class CallCancelableWrapper implements Cancelable {
    private Call call;

    CallCancelableWrapper(Call call) {
        this.call = call;
    }

    @Override
    public void cancel() {
        if (call != null && !call.isCanceled()) {
            call.cancel();
            call = null;
        }
    }
}
