package zhou.demo;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zzhoujay.richtext.RichText;
import com.zzhoujay.richtext.callback.Callback;
import com.zzhoujay.richtext.callback.EmotionGetter;

/**
 * Created by zhou on 16-6-17.
 */
public class RecyclerViewActivity extends AppCompatActivity {

    private static final String[] testString1 = new String[]{
            "<h3>Test1</h3><img src=\"http://h.hiphotos.baidu.com/image/h%3D200/sign=e72c850a09f3d7ca13f63876c21fbe3c/a2cc7cd98d1001e9460fd63bbd0e7bec54e797d7.jpg\" />",
            "<h3>Test2</h3><img src=\"http://pic28.photophoto.cn/20130818/0020033143720852_b.jpg\" />",
            "<h3>Test3</h3><img src=\"http://c.hiphotos.baidu.com/image/pic/item/267f9e2f070828382dcc0b20bd99a9014d08f1c5.jpg\" />",
            "<h3>Test4</h3><img src=\"http://f.hiphotos.baidu.com/image/pic/item/32fa828ba61ea8d358824a0d950a304e251f5812.jpg\" />",
            "<h3>Test5</h3><img src=\"http://f.hiphotos.baidu.com/image/pic/item/c2cec3fdfc039245831fa7498294a4c27c1e25c9.jpg\" />",
            "<h3>Test6</h3><img src=\"http://e.hiphotos.baidu.com/image/pic/item/b999a9014c086e06613eab4b00087bf40ad1cb18.jpg\" />",
            "<h3>Test7</h3><img src=\"http://a.hiphotos.baidu.com/image/pic/item/503d269759ee3d6d251670cb41166d224e4adeda.jpg\" />",
            "<h3>Test8</h3><img src=\"http://f.hiphotos.baidu.com/image/pic/item/cb8065380cd791234275326baf345982b2b7801c.jpg\" />",
            "<h3>Test9</h3><img src=\"http://a.hiphotos.baidu.com/image/pic/item/bba1cd11728b4710910b55c9c1cec3fdfc03238a.jpg\" />",
    };
//    private static final String[] testString1 = new String[]{
//            "<h3>Test1</h3><p>hello</p>",
//            "<h3>Test2</h3><p>hello</p>",
//            "<h3>Test3</h3><p>hello</p>",
//            "<h3>Test4</h3><p>hello</p>",
//            "<h3>Test5</h3><p>hello</p>",
//            "<h3>Test6</h3><p>hello</p>",
//            "<h3>Test7</h3><p>hello</p>",
//            "<h3>Test8</h3><p>hello</p>",
//            "<h3>Test9</h3><p>hello</p>",
//    };

    private static final String[] testStringImage = {
            "http://www.aikf.com/ask/resources/images/facialExpression/qq/1.gif",
            "http://www.aikf.com/ask/resources/images/facialExpression/qq/2.gif",
            "http://www.aikf.com/ask/resources/images/facialExpression/qq/3.gif",
            "http://www.aikf.com/ask/resources/images/facialExpression/qq/4.gif",
            "http://www.aikf.com/ask/resources/images/facialExpression/qq/5.gif",
            "http://www.aikf.com/ask/resources/images/facialExpression/qq/6.gif",
            "http://www.aikf.com/ask/resources/images/facialExpression/qq/7.gif",
            "http://www.aikf.com/ask/resources/images/facialExpression/qq/8.gif",
            "http://www.aikf.com/ask/resources/images/facialExpression/qq/9.gif",
    };

    private static final String[] testString = {
            "<h3>Test1</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/1.gif\" />",
            "<h3>Test2</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/2.gif\" />",
            "<h3>Test3</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/3.gif\" />",
            "<h3>Test4</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/4.gif\" />",
            "<h3>Test5</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/5.gif\" />",
            "<h3>Test6</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/6.gif\" />",
            "<h3>Test7</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/7.gif\" />",
            "<h3>Test8</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/8.gif\" />",
            "<h3>Test9</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/9.gif\" />",
    };

    static String replaceString = "<img src='file:///android_asset/qq%d.png'>";

    static String authorString = "<img src='file:///android_asset/iv_tag_author.png'>";

    static String author = "[author]";

    static String emoji = "[em:3]";

    static String nameString = "<a href=\"/%d\" class=\"name\" target=\"_blank\"> %s </a>: ";

    private static String getEmotion() {
        return String.format(replaceString, 4);
    }

    private static String getName(int id, String name) {
        return String.format(nameString, id, "@" + name);
    }

    private static final String[] myString = {
            getName(2, "kaka") + author + getName(4, "liner") + author + "[em:3]" + " JJ 监控 " + author + "[em:3]",
            author + "收到了三个人发GV的收费GV我问他要不二套房贷" + getName(2, "24563567567") + " JJ 监控 " + getName(4, "liner") + author + "[em:3]" + author + "[em:3]",
            getName(2, "5656756") + author + getName(4, "liner") + author + "[em:3]" + " JJ 监控 " + author + "[em:3]",
            author + "分公司给同行人容易" + getName(2, "24563567567") + " JJ 监控 " + getName(4, "liner") + author + "[em:3]" + author + "[em:3]",
            getName(2, "583567") + author + getName(4, "liner") + author + "[em:3]" + " JJ 监控 " + author + "[em:3]",
            author + "句酷研发部高三个号让他忽然好" + getName(2, "24563567567") + " JJ 监控 " + getName(4, "liner") + author + "[em:3]" + author + "[em:3]",
            getName(2, "hbsegy") + author + getName(4, "liner") + author + "[em:3]" + " JJ 监控 " + author + "[em:3]",
            author + "复活节能版图tu68i的回家要干活" + getName(2, "24563567567") + " JJ 监控 " + getName(4, "liner") + author + "[em:3]" + author + "[em:3]",
            getName(2, "e4wytgh") + author + getName(4, "liner") + author + "[em:3]" + " JJ 监控 " + author + "[em:3]",
            author + "喔让双方各 会让他恢复给你发个和回家" + getName(2, "24563567567") + " JJ 监控 " + getName(4, "liner") + author + "[em:3]" + author + "[em:3]",
            getName(2, "dbhfg") + author + getName(4, "liner") + author + "[em:3]" + " JJ 监控 " + author + "[em:3]",
            author + "你改好咯哦破碎厉害， " + getName(2, "24563567567") + " JJ 监控 " + getName(4, "liner") + author + "[em:3]" + author + "[em:3]",
            getName(2, "iyukyuk") + author + getName(4, "liner") + author + "[em:3]" + " JJ 监控 " + author + "[em:3]",
            author + "和你钛合金 他有几个号芙蓉厅 " + getName(2, "24563567567") + " JJ 监控 " + getName(4, "liner") + author + "[em:3]" + author + "[em:3]",
            getName(2, "rtdgbzdgb") + author + getName(4, "liner") + author + "[em:3]" + " JJ 监控 " + author + "[em:3]",
            author + "翻滚吧牛肉干好百服宁要国防生的" + getName(2, "24563567567") + " JJ 监控 " + getName(4, "liner") + author + "[em:3]" + author + "[em:3]",
            getName(2, "e4yw4ysg") + author + getName(4, "liner") + author + "[em:3]" + " JJ 监控 " + author + "[em:3]",
            author + "惹我吧见面呢黑寡妇你发退还给" + getName(2, "24563567567") + " JJ 监控 " + getName(4, "liner") + author + "[em:3]" + author + "[em:3]",
            getName(2, "fjnfgnxg") + author + getName(4, "liner") + author + "[em:3]" + " JJ 监控 " + author + "[em:3]",
            author + "湖南天雁欧佩克了对方深V 让他换个人头和" + getName(2, "24563567567") + " JJ 监控 " + getName(4, "liner") + author + "[em:3]" + author + "[em:3]",
            getName(2, "dfbxcbv") + author + getName(4, "liner") + author + "[em:3]" + " JJ 监控 " + author + "[em:3]",
            author + "容易你放好风华讲退" + getName(2, "24563567567") + " JJ 监控 " + getName(4, "liner") + author + "[em:3]" + author + "[em:3]",
            getName(2, "erter") + author + getName(4, "liner") + author + "[em:3]" + " JJ 监控 " + author + "[em:3]",
            author + "交换机刘老师京东方来扣欧锦" + getName(2, "24563567567") + " JJ 监控 " + getName(4, "liner") + author + "[em:3]" + author + "[em:3]",
            getName(2, "cvbtjkt") + author + getName(4, "liner") + author + "[em:3]" + " JJ 监控 " + author + "[em:3]",
            author + "我一哦哦好评；连接开户行离开了；剖" + getName(2, "24563567567") + " JJ 监控 " + getName(4, "liner") + author + "[em:3]" + author + "[em:3]",
            getName(2, "cvbstrhswt") + author + getName(4, "liner") + author + "[em:3]" + " JJ 监控 " + author + "[em:3]",
            author + "就开不开机欧尼后腰 连接 " + getName(2, "24563567567") + " JJ 监控 " + getName(4, "liner") + author + "[em:3]" + author + "[em:3]",
            getName(2, "eytysdgb") + author + getName(4, "liner") + author + "[em:3]" + " JJ 监控 " + author + "[em:3]",
            author + "哈哈哈；互换局一个个我koi" + getName(2, "24563567567") + " JJ 监控 " + getName(4, "liner") + author + "[em:3]" + author + "[em:3]",
            getName(2, "mjyryuk") + author + getName(4, "liner") + author + "[em:3]" + " JJ 监控 " + author + "[em:3]",
            author + "开工会图 一一个就好很快就黄金股雨刮臂" + getName(2, "24563567567") + " JJ 监控 " + getName(4, "liner") + author + "[em:3]" + author + "[em:3]",
            getName(2, "ert34g") + author + getName(4, "liner") + author + "[em:3]" + " JJ 监控 " + author + "[em:3]",
            author + "法国红酒退品如图三湾改编" + getName(2, "24563567567") + " JJ 监控 " + getName(4, "liner") + author + "[em:3]" + author + "[em:3]",
            getName(2, "bvgrqwert") + author + getName(4, "liner") + author + "[em:3]" + " JJ 监控 " + author + "[em:3]",
            author + "低功耗如果同一天与偶见魔法高校阿尔" + getName(2, "24563567567") + " JJ 监控 " + getName(4, "liner") + author + "[em:3]" + author + "[em:3]",
            getName(2, "mnrtjuye57y") + author + getName(4, "liner") + author + "[em:3]" + " JJ 监控 " + author + "[em:3]",
            author + "文本VB人与体温的表格就" + getName(2, "24563567567") + " JJ 监控 " + getName(4, "liner") + author + "[em:3]" + author + "[em:3]",
            getName(2, "68fggf") + author + getName(4, "liner") + author + "[em:3]" + " JJ 监控 " + author + "[em:3]",
            author + "发箍哟结构化你内酯豆腐而今天" + getName(2, "24563567567") + " JJ 监控 " + getName(4, "liner") + author + "[em:3]" + author + "[em:3]",

    };


    private static final String[] myString1 = {
            "收到了三个人发GV的收费GV我问他要不二套房贷",
            "5656756",
            "分公司给同行人容易",
            "583567",
            "句酷研发部高三个号让他忽然好"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new Holder(LayoutInflater.from(RecyclerViewActivity.this).inflate(R.layout.item_list, parent, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                if (holder instanceof Holder) {
                    Holder h = (Holder) holder;
                    final long time = System.currentTimeMillis();
                    RichText.from(myString[position]).singleLoad(false).done(new Callback() {
                        @Override
                        public void done(boolean imageLoadDone) {
                            Log.e("onBindViewHolder", "-----------------done: waste time = " + (System.currentTimeMillis() - time) + "---position = " + position);
                        }
                    }).into(h.text, new EmotionGetter() {
                        @Override
                        public Drawable getDrawable(String emotionKey) {
                            return null;
                        }
                    });
                }
            }

            @Override
            public int getItemCount() {
                return myString.length;
            }

            class Holder extends RecyclerView.ViewHolder {

                public TextView text;
                public TextView id;

                public Holder(View itemView) {
                    super(itemView);
                    text = (TextView) itemView.findViewById(R.id.text_item);
//                    id = (TextView) itemView.findViewById(R.id.text_id);
                }
            }
        });
    }
}
