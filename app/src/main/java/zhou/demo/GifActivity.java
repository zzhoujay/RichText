package zhou.demo;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.zzhoujay.richtext.RichText;

/**
 * Created by zhou on 16-6-19.
 */
public class GifActivity extends AppCompatActivity {

    private static final String GIF_TEST = "<h3>Gif Test</h3><img src=\"http://ww4.sinaimg.cn/large/5cfc088ejw1f3jcujb6d6g20ap08mb2c.gif\"/>" +
            "<h3>Test2</h3><img src=\"http://ww4.sinaimg.cn/bmiddle/67bf1bb2gw1eznhegpqawg208c04onpg.gif\"/>";
    private static final String GIF_TEST1 = "'<h1 class=\\\"article-title\\\" style=\\\"margin: 0px; padding: 0px; font-size: 34px; line-height: 48px; color: rgb(34, 34, 34); font-family: &quot;PingFang SC&quot;, &quot;Hiragino Sans GB&quot;, &quot;Microsoft YaHei&quot;, &quot;WenQuanYi Micro Hei&quot;, &quot;Helvetica Neue&quot;, Arial, sans-serif; white-space: normal; background-color: rgb(255, 255, 255);\\\">蒋劲夫，黑人劲爆扣篮，潘玮柏犀利突破，娱乐圈男星谁篮球水平高</h1><p><span class=\\\"src\\\" style=\\\"color: rgb(119, 119, 119); margin-right: 2px;\\\">体育热评&nbsp;</span><span class=\\\"time\\\" style=\\\"color: rgb(119, 119, 119); margin-right: 2px;\\\">2017-03-17 21:53</span></p><p style=\\\"margin-top: 18px; margin-bottom: 0px; padding: 0px;\\\">“黑人”陈建州，16岁就代表中华台北参加“亚洲青年锦标赛”，21岁就成为了职业篮球运动员，身体素质就不用说了，身高189cm，体重90kg，职业球员的身体条件，娱乐圈估计也很难找出第二个了。<br/></p><p style=\\\"margin-top: 18px; margin-bottom: 0px; padding: 0px;\\\"><img src=\\\"http://news-develop.oss-cn-qingdao.aliyuncs.com/upload/ueditor/image/20170318/1489828101774725.gif\\\" title=\\\"1489828101774725.gif\\\" alt=\\\"192000023d95200c4479.gif\\\"/></p><p style=\\\"margin-top: 18px; margin-bottom: 0px; padding: 0px;\\\">随便虐篮筐，这力量，爆发力都很足啊。</p><p style=\\\"margin-top: 18px; margin-bottom: 0px; padding: 0px;\\\"><img src=\\\"http://news-develop.oss-cn-qingdao.aliyuncs.com/upload/ueditor/image/20170318/1489828118457864.gif\\\" title=\\\"1489828118457864.gif\\\" alt=\\\"192000023da900931bcd.gif\\\"/></p><p style=\\\"margin-top: 18px; margin-bottom: 0px; padding: 0px;\\\">潘玮柏，这是他高中时期比赛的画面，滞空能力非常出色，而且据说潘玮柏获得了NCAA的奖学金，是有机会，有能力打NCAA联赛的。</p><p style=\\\"margin-top: 18px; margin-bottom: 0px; padding: 0px;\\\"><img src=\\\"http://news-develop.oss-cn-qingdao.aliyuncs.com/upload/ueditor/image/20170318/1489828143655519.gif\\\" title=\\\"1489828143655519.gif\\\" alt=\\\"191e00024266fa8e3f5c.gif\\\"/></p><p style=\\\"margin-top: 18px; margin-bottom: 0px; padding: 0px;\\\"><img src=\\\"http://news-develop.oss-cn-qingdao.aliyuncs.com/upload/ueditor/image/20170318/1489828150797206.gif\\\" title=\\\"1489828150797206.gif\\\" alt=\\\"191c000243cf27494877.gif\\\"/></p><p style=\\\"margin-top: 18px; margin-bottom: 0px; padding: 0px;\\\">身体很灵活，很像NBA马刺队的托尼-帕克有没有？</p><p style=\\\"margin-top: 18px; margin-bottom: 0px; padding: 0px;\\\"><img src=\\\"http://news-develop.oss-cn-qingdao.aliyuncs.com/upload/ueditor/image/20170318/1489828161166072.gif\\\" title=\\\"1489828161166072.gif\\\" alt=\\\"191f00029aba6488b6a0.gif\\\"/></p><p style=\\\"margin-top: 18px; margin-bottom: 0px; padding: 0px;\\\">霍建华，文质彬彬的，没想到篮球打得也很厉害吗，急停跳投也是可以的。</p><p style=\\\"margin-top: 18px; margin-bottom: 0px; padding: 0px;\\\"><img src=\\\"http://news-develop.oss-cn-qingdao.aliyuncs.com/upload/ueditor/image/20170318/1489828171421177.gif\\\" title=\\\"1489828171421177.gif\\\" alt=\\\"191400048a4b4277b9f3.gif\\\"/></p><p style=\\\"margin-top: 18px; margin-bottom: 0px; padding: 0px;\\\">连续交叉胯下，这基本功也很扎实啊。</p><p style=\\\"margin-top: 18px; margin-bottom: 0px; padding: 0px;\\\"><img src=\\\"http://news-develop.oss-cn-qingdao.aliyuncs.com/upload/ueditor/image/20170318/1489828184769994.gif\\\" title=\\\"1489828184769994.gif\\\" alt=\\\"191c0002a9fe59c5afba.gif\\\"/></p><p style=\\\"margin-top: 18px; margin-bottom: 0px; padding: 0px;\\\">蒋劲夫，可以称得上是娱乐圈的飞人了，也被称为打球最好的男星之一，这双手扣篮也很劲爆啊，弹跳非常出色。<br/></p><p style=\\\"margin-top: 18px; margin-bottom: 0px; padding: 0px;\\\"><img src=\\\"http://news-develop.oss-cn-qingdao.aliyuncs.com/upload/ueditor/image/20170318/1489828196403788.gif\\\" title=\\\"1489828196403788.gif\\\" alt=\\\"191c0002abb1d0028c71.gif\\\"/></p><p style=\\\"margin-top: 18px; margin-bottom: 0px; padding: 0px;\\\">单臂灌篮，一个被演艺事业耽误了的篮球少年的。<br/></p><p style=\\\"margin-top: 18px; margin-bottom: 0px; padding: 0px;\\\"><img src=\\\"http://news-develop.oss-cn-qingdao.aliyuncs.com/upload/ueditor/image/20170318/1489828205588085.gif\\\" title=\\\"1489828205588085.gif\\\" alt=\\\"19190002a33d671fc6b2.gif\\\"/></p><p style=\\\"margin-top: 18px; margin-bottom: 0px; padding: 0px;\\\">作为一个非职业的选手，这投篮能力也不错了，能投，能突，能扣，这水平打野球，称霸野球赛场应该没有任何压力吧。<br/></p><p style=\\\"margin-top: 18px; margin-bottom: 0px; padding: 0px;\\\"><img src=\\\"http://news-develop.oss-cn-qingdao.aliyuncs.com/upload/ueditor/image/20170318/1489828215512035.gif\\\" title=\\\"1489828215512035.gif\\\" alt=\\\"191e0002ab1644a67801.gif\\\"/></p><p><br/></p>'";
    TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.text);
        textView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        RichText.from(GIF_TEST).autoFix(false).showBorder(true).autoPlay(true).into(textView);
    }

}
