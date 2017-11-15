package com.zzhoujay.richtext;

/**
 * Created by zhou on 2016/12/5.
 * CacheType
 */
//@IntDef({CacheType.NONE, CacheType.LAYOUT, CacheType.ALL})
public enum CacheType {
    none(0), layout(1), all(2);

    int value;

    CacheType(int value) {
        this.value = value;
    }

    public int intValue() {
        return value;
    }
//    int NONE = 0; // 不进行缓存
//    int LAYOUT = 1; // 只缓存文字样式和图片大小信息
//    int ALL = 2; // 在LAYOUT的基础上还缓存图片，使用前需先设置缓存目录
}
