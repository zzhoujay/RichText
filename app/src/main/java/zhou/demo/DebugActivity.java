package zhou.demo;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichText;
import com.zzhoujay.richtext.callback.Callback;
import com.zzhoujay.richtext.callback.SimpleImageFixCallback;

/**
 * Created by zhou on 2017/10/8.
 */

public class DebugActivity extends Activity {

    private static final String TAG = "DebugActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        RichText.initCacheDir(this);
        RichText.debugMode = true;

        TextView textView = findViewById(R.id.test_text);
        ImageView imageView = findViewById(R.id.image_view);

        String test_text_2 = "<p>我是文本内容 <img src='http://wx1.sinaimg.cn/mw690/eaaf2affly1fihvjpekzwj21el0qotfq.jpg' /> 下一个" +
                "<img src='http://wx1.sinaimg.cn/mw690/eaaf2affly1fihvjpekzwj21el0qotfq.jpg' />似懂非懂撒范德萨咖啡机盛大开放惊世毒妃</p><p>我是文本内容 <img src='http://wx1.sinaimg.cn/mw690/eaaf2affly1fihvjpekzwj21el0qotfq.jpg' /> 下一个" +
                "<img src='http://wx1.sinaimg.cn/mw690/eaaf2affly1fihvjpekzwj21el0qotfq.jpg' />似懂非懂撒范德萨咖啡机盛大开放惊世毒妃</p><p>我是文本内容 <img src='http://wx1.sinaimg.cn/mw690/eaaf2affly1fihvjpekzwj21el0qotfq.jpg' /> 下一个" +
                "<img src='http://wx1.sinaimg.cn/mw690/eaaf2affly1fihvjpekzwj21el0qotfq.jpg' />似懂非懂撒范德萨咖啡机盛大开放惊世毒妃</p><p>我是文本内容 <img src='http://wx1.sinaimg.cn/mw690/eaaf2affly1fihvjpekzwj21el0qotfq.jpg' /> 下一个" +
                "<img src='http://wx1.sinaimg.cn/mw690/eaaf2affly1fihvjpekzwj21el0qotfq.jpg' />似懂非懂撒范德萨咖啡机盛大开放惊世毒妃</p><img src='http://wx1.sinaimg.cn/mw690/eaaf2affly1fihvjpekzwj21el0qotfq.jpg' />似懂非懂撒范德萨咖啡机盛大开放惊世毒妃";

        String test_text_1 = "<p><h1>Test1</h1><img src='file:///android_asset/doge.jpg'></p>";


        RichText.from(test_text_2)
                .showBorder(true)
                .fix(new SimpleImageFixCallback() {

                    @Override
                    public void onFailure(ImageHolder holder, Exception e) {
                        super.onFailure(holder, e);
                        e.printStackTrace();
                    }
                })
                .done(new Callback() {
                    @Override
                    public void done(boolean imageLoadDone) {
                        Log.d(TAG, "imageDownloadFinish() called with: imageLoadDone = [" + imageLoadDone + "]");
                    }
                })
                .into(textView);

    }
}
