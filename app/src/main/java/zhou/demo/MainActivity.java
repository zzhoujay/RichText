package zhou.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.zzhoujay.richtext.ImageFixCallback;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity {


    private static final String IMAGE = "<img title=\"\" src=\"http://g.hiphotos.baidu.com/image/pic/item/241f95cad1c8a7866f726fe06309c93d71cf5087.jpg\"  style=\"cursor: pointer;\"><br><br>" +
            "<img src=\"http://img.ugirls.com/uploads/cooperate/baidu/20160519menghuli.jpg\" width=\"1080\" height=\"1620\"/>";
    private static final String IMAGE1 = "<h1>RichText</h1><p>Android平台下的富文本解析器</p><img title=\"\" src=\"http://image.tianjimedia.com/uploadImages/2015/129/56/J63MI042Z4P8.jpg\"  style=\"cursor: pointer;\"><br><br>" +
            "<h3>点击菜单查看更多Demo</h3><img src=\"http://ww2.sinaimg.cn/bmiddle/813a1fc7jw1ee4xpejq4lj20g00o0gnu.jpg\" />";

    private static final String TT = "<p>每天看你们发说说，我都好羡慕 。你们长得又好看 ，还用智能手机，又有钱，整天讨论一些好像很厉害的东西。随便拿个东西都顶我几个月的生活费，我读书少，又是乡下来的，没见过多少世面，所以我只能默默的看着你发，时不时点个赞。<img src=\"http://img.baidu.com/hi/jx2/j_0002.gif\"/></p><p><br/></p><p><br/></p><p><span style=\"text-decoration: line-through; color: rgb(255, 0, 0);\\\"><em><strong>上班路上还打醒精神，如今江湖险恶，到处都是坏人。</strong></em></span></p><p><img src=\"http://210.51.17.150:8090/files/lechu/20160528142627980669386.jpg\" title=\"20160528142627980669386.jpg\" alt=\"P60519-154052.jpg\"/></p><p><span style=\"color: rgb(146, 208, 80); text-decoration: none;\">在街上经常会遇到偷偷问你要不要手机的，现在太可怕了，直接明目张胆推销人体器官了，刚才走路无意碰了一女的，还没来得及说对不起，那女的竟然大声问我要不要脸！</span></p>";

    private static final String GIF_TEST = "<img src=\"http://ww4.sinaimg.cn/large/5cfc088ejw1f3jcujb6d6g20ap08mb2c.gif\">";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView) findViewById(R.id.text);

        final InputStream stream = getResources().openRawResource(R.raw.hello);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            RichText.from(IMAGE1).autoFix(false).fix(new ImageFixCallback() {
                @Override
                public void onFix(ImageHolder holder, boolean imageReady) {
                    if(holder.getSrc().equals("http://image.tianjimedia.com/uploadImages/2015/129/56/J63MI042Z4P8.jpg")){
                        holder.setShow(false);
                    }else {
                        holder.setAutoFix(true);
                    }
                }
            }).into(textView);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
}
