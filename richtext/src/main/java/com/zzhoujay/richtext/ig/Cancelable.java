package com.zzhoujay.richtext.ig;

/**
 * Created by zhou on 2016/12/11.
 * 可取消的任务（标记）
 */
public interface Cancelable {

    /**
     * 尝试取消（不一定能成功取消）
     */
    void cancel();

}
