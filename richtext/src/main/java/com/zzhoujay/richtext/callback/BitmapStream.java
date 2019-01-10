package com.zzhoujay.richtext.callback;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhou on 2017/11/17.
 */

public interface BitmapStream extends Closeable {

    InputStream getInputStream() throws IOException;

}
