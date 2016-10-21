package zhou.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.zzhoujay.richtext.callback.ImageFixCallback;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichText;

/**
 * Created by zhou on 16-6-19.
 */
public class GifActivity extends AppCompatActivity {

    TextView textView;

    private static final String GIF_TEST = "<h3>Gif Test</h3><img src=\"http://ww4.sinaimg.cn/large/5cfc088ejw1f3jcujb6d6g20ap08mb2c.gif\"/>" +
            "<h3>Test2</h3><img src=\"http://ww4.sinaimg.cn/bmiddle/67bf1bb2gw1eznhegpqawg208c04onpg.gif\"/>";
//    private static final String GIF_TEST = "<h3>Gif Test</h3><img src=\"http://image.tianjimedia.com/uploadImages/2015/129/56/J63MI042Z4P8.jpg\"/>";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.text);

        RichText.from(GIF_TEST).autoFix(false).fix(new ImageFixCallback() {
            @Override
            public void onFix(ImageHolder holder, boolean imageReady) {
                if(holder.isGif()){
                    holder.setAutoFix(true);
                    holder.setAutoPlay(true);
                }
            }
        }).into(textView);



    }

}
