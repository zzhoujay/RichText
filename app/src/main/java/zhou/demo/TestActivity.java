package zhou.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.zzhoujay.richtext.RichText;
import com.zzhoujay.richtext.callback.OnImageClickListener;

import java.util.Calendar;
import java.util.List;

/**
 * Created by zhou on 16-10-22.
 */

public class TestActivity extends AppCompatActivity {

    static final String test = "<h1>Test</h1><div class=\"topic_content\" itemprop=\"articleBody\">" +
            "<img alt=\"\" src=\"http://img10.360buyimg.com/imgzone/jfs/t2719/53/693438809/405912/957c1efa/5721e109N8ad86029.jpg\">" +
            "<img alt=\"\" src=\"http://img10.360buyimg.com/imgzone/jfs/t2836/30/707249522/270588/840d428a/5721e108Ne667230f.jpg\">" +
            "<img alt=\"\" src=\"http://img10.360buyimg.com/imgzone/jfs/t2305/211/1222246162/89571/4ce4f9a1/56496ac7N982aa001.jpg\">" +
            "<img alt=\"\" src=\"http://img10.360buyimg.com/imgzone/jfs/t2695/76/715579111/331050/cf2ae9f9/5721e10aNd690b026.jpg\">" +
            "<img alt=\"\" src=\"http://img10.360buyimg.com/imgzone/jfs/t2341/288/2958575364/740490/9678e90f/5721e10bNf923ebaa.jpg\">" +
            "<img alt=\"\" src=\"http://img10.360buyimg.com/imgzone/jfs/t2776/164/715581717/852142/2fa4714f/5721e10bN04e38f08.jpg\">" +
            "<img alt=\"\" src=\"http://img10.360buyimg.com/imgzone/jfs/t2104/197/2936780208/316761/f3051b63/5721e10cN1b74089c.jpg\">" +
            "<br>" +
            "</div>";

    private RichText richText;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        textView = findViewById(R.id.text);

        assert textView != null;

        richText = RichText.from(test)
                .imageClick(new OnImageClickListener() {
                    @Override
                    public void imageClicked(List<String> imageUrls, int position) {
                        Calendar calendar = Calendar.getInstance();
                        int m = calendar.get(Calendar.MINUTE);
                        int s = calendar.get(Calendar.SECOND);
                        Toast.makeText(TestActivity.this, "M:" + m + ",S:" + s, Toast.LENGTH_SHORT).show();
                    }
                }).into(textView);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        richText.clear();
        richText = null;
    }
}
