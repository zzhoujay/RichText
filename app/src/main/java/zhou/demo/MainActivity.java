package zhou.demo;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TypefaceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichText;
import com.zzhoujay.richtext.callback.ImageFixCallback;
import com.zzhoujay.richtext.callback.OnImageClickListener;
import com.zzhoujay.richtext.callback.OnImageLongClickListener;
import com.zzhoujay.richtext.callback.OnURLClickListener;
import com.zzhoujay.richtext.callback.OnUrlLongClickListener;
import com.zzhoujay.richtext.ext.HtmlTagHandler;

import org.xml.sax.XMLReader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Stack;


public class MainActivity extends AppCompatActivity {


    private static final String IMAGE = "<img title=\"\" src=\"http://g.hiphotos.baidu.com/image/pic/item/241f95cad1c8a7866f726fe06309c93d71cf5087.jpg\"  style=\"cursor: pointer;\"><br><br>" +
            "<img src=\"http://img.ugirls.com/uploads/cooperate/baidu/20160519menghuli.jpg\" width=\"1080\" height=\"1620\"/><a href=\"http://www.baidu.com\">baidu</a>" +
            "hello asdkjfgsduk <a href=\"http://www.jd.com\">jd</a>";
    private static final String IMAGE1 = "<h1>RichText</h1><p>Android平台下的富文本解析器</p><img title=\"\" src=\"http://image.tianjimedia.com/uploadImages/2015/129/56/J63MI042Z4P8.jpg\"  style=\"cursor: pointer;\"><br><br>" +
            "<h3>点击菜单查看更多Demo</h3><img src=\"http://ww2.sinaimg.cn/bmiddle/813a1fc7jw1ee4xpejq4lj20g00o0gnu.jpg\" /><p><a href=\"http://www.baidu.com\">baidu</a>" +
            "hello asdkjfgsduk <a href=\"http://www.jd.com\">jd</a></p>";

    private static final String TT = "<p>每天看你们发说说，我都好羡慕 。你们长得又好看 ，还用智能手机，又有钱，整天讨论一些好像很厉害的东西。随便拿个东西都顶我几个月的生活费，我读书少，又是乡下来的，没见过多少世面，所以我只能默默的看着你发，时不时点个赞。<img src=\"http://img.baidu.com/hi/jx2/j_0002.gif\"/></p><p><br/></p><p><br/></p><p><span style=\"text-decoration: line-through; color: rgb(255, 0, 0);\\\"><em><strong>上班路上还打醒精神，如今江湖险恶，到处都是坏人。</strong></em></span></p><p><img src=\"http://210.51.17.150:8090/files/lechu/20160528142627980669386.jpg\" title=\"20160528142627980669386.jpg\" alt=\"P60519-154052.jpg\"/></p><p><span style=\"color: rgb(146, 208, 80); text-decoration: none;\">在街上经常会遇到偷偷问你要不要手机的，现在太可怕了，直接明目张胆推销人体器官了，刚才走路无意碰了一女的，还没来得及说对不起，那女的竟然大声问我要不要脸！</span></p>";

    private static final String GIF_TEST = "<img src=\"http://ww4.sinaimg.cn/large/5cfc088ejw1f3jcujb6d6g20ap08mb2c.gif\">";

    private static final String markdown_test = "image:![image](http://image.tianjimedia.com/uploadImages/2015/129/56/J63MI042Z4P8.jpg)\n[link](https://github.com/zzhoujay/RichText/issues)";

    private static final String gif_test = "<h3>Test1</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/1.gif\" />" +
            "            <h3>Test2</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/2.gif\" />" +
            "            <h3>Test3</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/3.gif\" />" +
            "            <h3>Test4</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/4.gif\" />" +
            "            <h3>Test5</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/5.gif\" />" +
            "            <h3>Test6</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/6.gif\" />" +
            "            <h3>Test7</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/7.gif\" />" +
            "            <h3>Test8</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/8.gif\" />" +
            "            <h3>Test9</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/9.gif\" />";

    private static final String list_test = "<ol>\n" +
            "   <li>Coffee</li>\n" +
            "   <li>Tea</li>\n" +
            "   <li>Milk</li>\n" +
            "</ol>\n" +
            "\n" +
            "<ul>\n" +
            "   <li>Coffee</li>\n" +
            "   <li>Tea</li>\n" +
            "   <li>Milk</li>\n" +
            "</ul>";

    private static final String large_image = "<img src=\"http://static.tme.im/article_1_1471686391302fue\"/>";

    private static final String text = "";

    private static final String big_image_test = "汉仪喵魂体！<br>自己写了小半年的手写字体刚上线，自我感觉还是值得推荐的！<br>我先给大家图文并茂地讲讲这字儿怎么值得推荐！<br><img src=\"https://pic3.zhimg.com/b6a1334f561eb6f388d5321402ee6c0e_b.jpg\" data-rawwidth=\"800\" data-rawheight=\"3266\" class=\"origin_image zh-lightbox-thumb\" width=\"800\" data-original=\"https://pic3.zhimg.com/b6a1334f561eb6f388d5321402ee6c0e_r.jpg\"/><img src=\"https://pic3.zhimg.com/13fc1f61deeea1e4d5b1fd4d36b30b82_b.jpg\" data-rawwidth=\"801\" data-rawheight=\"6071\" class=\"origin_image zh-lightbox-thumb\" width=\"801\" data-original=\"https://pic3.zhimg.com/13fc1f61deeea1e4d5b1fd4d36b30b82_r.jpg\"/><img src=\"https://pic3.zhimg.com/07fcb9ad57c568d2d41466616fdc6022_b.jpg\" data-rawwidth=\"801\" data-rawheight=\"5475\" class=\"origin_image zh-lightbox-thumb\" width=\"801\" data-original=\"https://pic3.zhimg.com/07fcb9ad57c568d2d41466616fdc6022_r.jpg\"/><img src=\"https://pic1.zhimg.com/a5d631cc0a6c9c5b38c6243ef89e1794_b.jpg\" data-rawwidth=\"801\" data-rawheight=\"7352\" class=\"origin_image zh-lightbox-thumb\" width=\"801\" data-original=\"https://pic1.zhimg.com/a5d631cc0a6c9c5b38c6243ef89e1794_r.jpg\"/>";

    private static final String big_image_test_2 = "<img src=\"https://pic1.zhimg.com/00f14a0147fd336a6c34c7817f4f096c_b.jpg\" data-rawwidth=\"801\" data-rawheight=\"3178\" class=\"origin_image zh-lightbox-thumb\" width=\"801\" data-original=\"https://pic1.zhimg.com/00f14a0147fd336a6c34c7817f4f096c_r.jpg\"><img src=\"https://pic2.zhimg.com/0d1a5113e3ea1bb379b56faeba908f71_b.jpg\" data-rawwidth=\"600\" data-rawheight=\"600\" class=\"origin_image zh-lightbox-thumb\" width=\"600\" data-original=\"https://pic2.zhimg.com/0d1a5113e3ea1bb379b56faeba908f71_r.jpg\">啊……<br>最近又有一个字体的想法……<br>还蛮好看……<br>但是写一套太累辣！";

    final String html = "<div>汉仪喵魂体！<br>自己写了小半年的手写字体刚上线，自我感觉还是值得推荐的！<br>我先给大家图文并茂地讲讲这字儿怎么值得推荐！<br><img src=\"https://pic3.zhimg.com/b6a1334f561eb6f388d5321402ee6c0e_b.jpg\" data-rawwidth=\"800\" data-rawheight=\"3266\" class=\"origin_image zh-lightbox-thumb\" width=\"800\" data-original=\"https://pic3.zhimg.com/b6a1334f561eb6f388d5321402ee6c0e_r.jpg\"><img src=\"https://pic3.zhimg.com/13fc1f61deeea1e4d5b1fd4d36b30b82_b.jpg\" data-rawwidth=\"801\" data-rawheight=\"6071\" class=\"origin_image zh-lightbox-thumb\" width=\"801\" data-original=\"https://pic3.zhimg.com/13fc1f61deeea1e4d5b1fd4d36b30b82_r.jpg\"><img src=\"https://pic3.zhimg.com/07fcb9ad57c568d2d41466616fdc6022_b.jpg\" data-rawwidth=\"801\" data-rawheight=\"5475\" class=\"origin_image zh-lightbox-thumb\" width=\"801\" data-original=\"https://pic3.zhimg.com/07fcb9ad57c568d2d41466616fdc6022_r.jpg\"><img src=\"https://pic1.zhimg.com/a5d631cc0a6c9c5b38c6243ef89e1794_b.jpg\" data-rawwidth=\"801\" data-rawheight=\"7352\" class=\"origin_image zh-lightbox-thumb\" width=\"801\" data-original=\"https://pic1.zhimg.com/a5d631cc0a6c9c5b38c6243ef89e1794_r.jpg\"><img src=\"https://pic4.zhimg.com/47188b45390ec15b1fd290c2a472d043_b.jpg\" data-rawwidth=\"801\" data-rawheight=\"5358\" class=\"origin_image zh-lightbox-thumb\" width=\"801\" data-original=\"https://pic4.zhimg.com/47188b45390ec15b1fd290c2a472d043_r.jpg\"><br>字库加标点接近一万字<br><br>我的极速是除了吃饭睡觉<br>一刻不停的写<br>一天最多300字<br>极速一天之后第二天就要最少崩溃半天<br>哈哈哈哈哈<br><br>上半年因为出书有点拖延<br><br>到临交稿的两个月剩的有点多<br>我只好回老家闭关写<br>（有人做饭啊没有干扰还）<br>有一个半月的时间里<br>每天都<br>从天亮写到天黑<br>从睁眼写到闭眼<br><br>微信朋友圈还合作什么的<br>啥也不管了不回了<br><br>特别的酸爽<br>坚持下来之后感觉耐力提升了一百倍<br><br>之后谁再说什么事儿忍不了抛不下<br>我就特别想说<br>你去抛下一切功名利禄社交合作<br>写俩月字库试试<br>之后什么矫情都没了<br><br>云淡风轻的<br><img src=\"https://pic1.zhimg.com/00f14a0147fd336a6c34c7817f4f096c_b.jpg\" data-rawwidth=\"801\" data-rawheight=\"3178\" class=\"origin_image zh-lightbox-thumb\" width=\"801\" data-original=\"https://pic1.zhimg.com/00f14a0147fd336a6c34c7817f4f096c_r.jpg\"><img src=\"https://pic2.zhimg.com/0d1a5113e3ea1bb379b56faeba908f71_b.jpg\" data-rawwidth=\"600\" data-rawheight=\"600\" class=\"origin_image zh-lightbox-thumb\" width=\"600\" data-original=\"https://pic2.zhimg.com/0d1a5113e3ea1bb379b56faeba908f71_r.jpg\">啊……<br>最近又有一个字体的想法……<br>还蛮好看……<br>但是写一套太累辣！<br><br>粉丝的话可以加我私人微信号：catsoulmini<br>（同行或职业达人之类加了备注一下身份即可）</div>";


    private final String tt = "hello zz <code>gg</code>" +
            "<ul>" +
            "<li>hello</li>" +
            "<li>world</li>" +
            "<li>world</li>" +
            "<li>world</li>" +
            "<li>world</li>" +
            "<li>world</li>" +
            "<li>world</li>" +
            "<li>gg</li>" +
            "</ul>\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Glide.with(this).from("").sizeMultiplier(
        final TextView textView = (TextView) findViewById(R.id.text);

//        textView.post(new Runnable() {
//            @Override
//            public void run() {
//                Spanned c = Html.fromHtml("hello zz <code>gg</code>" +
//                        "<ul>" +
//                        "<li>hello</li>" +
//                        "<li>world</li>" +
//                        "<li>world</li>" +
//                        "<li>world</li>" +
//                        "<li>world</li>" +
//                        "<li>world</li>" +
//                        "<li>world</li>" +
//                        "<li>gg</li>" +
//                        "</ul>\n", null, new HtmlTagHandler(textView));
//                textView.setText(c);
//            }
//        });

        RichText.from(IMAGE1).clickable(true).urlClick(new OnURLClickListener() {
            @Override
            public boolean urlClicked(String url) {
                Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.putExtra(Browser.EXTRA_APPLICATION_ID, getPackageName());
                try {
                    startActivity(intent);
                    Log.i("RichText","zz:"+url);
                    return true;
                } catch (ActivityNotFoundException e) {
                    Log.w("URLSpan", "Actvity was not found for intent, " + intent.toString());
                }
                return false;
            }
        }).into(textView);

//        try {
//            RichText.from(loadFile(R.raw.large)).into(textView);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        RichText.from(html).into(textView);


//        RichText.fromMarkdown(markdown_test).clickable(true).urlLongClick(new OnUrlLongClickListener() {
//            @Override
//            public boolean urlLongClick(String url) {
//                Toast.makeText(getApplicationContext(),url,Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        }).into(textView);
//        RichText.from(IMAGE1).clickable(true).autoFix(false).imageClick(new OnImageClickListener() {
//            @Override
//            public void imageClicked(List<String> imageUrls, int position) {
//                Toast.makeText(getApplicationContext(), imageUrls.get(position), Toast.LENGTH_SHORT).show();
//            }
//        }).imageLongClick(new OnImageLongClickListener() {
//            @Override
//            public boolean imageLongClicked(List<String> imageUrls, int position) {
//                Toast.makeText(getApplicationContext(), imageUrls.get(position), Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        }).into(textView);
//        RichText.from(base64test3).into(textView);
//        RichText.from(gif_test).autoFix(false).fix(new ImageFixCallback() {
//            @Override
//            public void onFix(ImageHolder holder, boolean imageReady) {
//                holder.setWidth(400);
//                holder.setHeight(400);
//                holder.setAutoPlay(true);
//            }
//        }).into(textView);

//        RichText.from(text).urlClick(new OnURLClickListener() {
//            @Override
//            public boolean urlClicked(String url) {
//                Log.i("RichText", url);
//                return false;
//            }
//        }).into(textView);
//
//        RichText.from(text).urlLongClick(new OnUrlLongClickListener() {
//            @Override
//            public boolean urlLongClick(String url) {
//                Log.i("RichText", url);
//                return false;
//            }
//        }).into(textView);
//
//        RichText.from(text).imageClick(new OnImageClickListener() {
//            @Override
//            public void imageClicked(List<String> imageUrls, int position) {
//                Log.i("RichText", imageUrls.get(position));
//            }
//        }).into(textView);
//
//        RichText.from(text).imageLongClick(new OnImageLongClickListener() {
//            @Override
//            public boolean imageLongClicked(List<String> imageUrls, int position) {
//                Log.i("RichText", imageUrls.get(position));
//                return false;
//            }
//        }).into(textView);

//        SpannableStringBuilder ssb = new SpannableStringBuilder();
//        ssb.append("skdfgasduk").append('\n').append("sdakfgbsdk").append('\n').append("asdkfjj");
//        textView.setText(ssb);

//        RichText.from(IMAGE1).autoFix(false).fix(new ImageFixCallback() {
//            @Override
//            public void onFix(ImageHolder holder, boolean imageReady) {
//                holder.setWidth(500);
//                holder.setHeight(500);
//            }
//        }).imageLongClick(new OnImageLongClickListener() {
//            @Override
//            public boolean imageLongClicked(List<String> imageUrls, int position) {
//                Toast.makeText(getApplicationContext(), "longClick:" + imageUrls.get(position), Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        }).imageClick(new OnImageClickListener() {
//            @Override
//            public void imageClicked(List<String> imageUrls, int position) {
//                Toast.makeText(getApplicationContext(), "click:" + imageUrls.get(position), Toast.LENGTH_SHORT).show();
//            }
//        }).urlClick(new OnURLClickListener() {
//            @Override
//            public boolean urlClicked(String url) {
//                Toast.makeText(getApplicationContext(), "urlClick:" + url, Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        }).urlLongClick(new OnUrlLongClickListener() {
//            @Override
//            public boolean urlLongClick(String url) {
//                Toast.makeText(getApplicationContext(), "urlLongClick:" + url, Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        }).into(textView);
//        RichText.from(IMAGE1).into(textView);
//
//        final InputStream stream = getResources().openRawResource(R.raw.image);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
//        StringBuilder sb = new StringBuilder();
//        String line;
//
//        try {
//            while ((line = reader.readLine()) != null) {
//                sb.append(line).append('\n');
//            }
//            RichText.fromMarkdown(sb.toString()).autoFix(false).fix(new ImageFixCallback() {
//                @Override
//                public void onFix(ImageHolder holder, boolean imageReady) {
//                    holder.setWidth(700);
//                    holder.setHeight(700);
//                }
//            }).into(textView);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        RichText.fromHtml(large_image).into(textView);

//        RichText.fromHtml(IMAGE1).autoFix(false).fix(new ImageFixCallback() {
//            @Override
//            public void onFix(ImageHolder holder, boolean imageReady) {
//                holder.setWidth(400);
//                holder.setHeight(400);
//
//            }
//        }).into(textView);

//        RichText.from(GIF_TEST).error(R.drawable.test).into(textView);
//        Glide.with(this).load(GIF_TEST).asGif().skipMemoryCache(true).preload();

//        RichText.from(TEXT).autoFix(false).fix(new ImageFixCallback() {
//            @Override
//            public void onFix(ImageHolder holder, boolean imageReady) {
//                if (holder.getWidth() > 200 && holder.getHeight() > 200) {
//                    holder.setAutoFix(true);
//                }
//            }
//        }).into(textView);


//        RichText
//                .from(TEXT)
//                .autoFix(true)
//                .async(true)
//                .fix(new ImageFixCallback() {
//                    @Override
//                    public void onFix(ImageHolder holder) {
//                        if(holder.getImageType()==ImageHolder.GIF){
//                            holder.setWidth(400);
//                            holder.setHeight(400);
//                        }else {
//                            holder.setAutoFix(true);
//                        }
//                    }
//                })
//                .imageClick(new OnImageClickListener() {
//                    @Override
//                    public void imageClicked(List<String> imageUrls, int position) {
//
//                    }
//                })
//                .urlClick(new OnURLClickListener() {
//                    @Override
//                    public boolean urlClicked(String url) {
//                        return false;
//                    }
//                })
//                .into(textView);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "RecyclerView");
        menu.add(0, 1, 1, "ListView");
        menu.add(0, 2, 2, "Gif");
        menu.add(0, 3, 3, "Test");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            startActivity(new Intent(this, RecyclerViewActivity.class));
        } else if (item.getItemId() == 1) {
            startActivity(new Intent(this, ListViewActivity.class));
        } else if (item.getItemId() == 2) {
            startActivity(new Intent(this, GifActivity.class));
        } else if (item.getItemId() == 3) {
            startActivity(new Intent(this, TestActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics.widthPixels;
    }

    public String loadFile(int res) throws IOException {
        InputStream is = getResources().openRawResource(res);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append('\n');
        }
        br.close();
        is.close();
        return sb.toString();
    }
}
