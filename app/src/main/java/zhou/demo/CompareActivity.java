package zhou.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.textview.QMUISpanTouchFixTextView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import zhou.parse.RichTextBaseKt;
import zhou.parse.ViewClick;

/**
 * 比较QMUISpanTouchFixTextView与TextView的点击处理
 */
public class CompareActivity extends AppCompatActivity {


    String linkReplace = "<a href=\"%d\" class=\"name\" target=\"_blank\"> %s </a>: ";
    String emotionReplace = "[em:%d]";
    String authorString = "[author]";
    String testClickString = "测试点击：" + "某英超名宿" + authorString + "回复：" + String.format(linkReplace, 333, "@英超利物浦") + "敢不敢把今赛季的联赛冠军给拿了" +
            String.format(emotionReplace, 5) + String.format(emotionReplace, 9) + String.format(emotionReplace, 10) + String.format(emotionReplace, 11) +
            "2019年竟然两连败！！";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        findViewById(R.id.ll_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "父View响应点击0", Toast.LENGTH_SHORT).show();
            }
        });
        final TextView tv = findViewById(R.id.tv);
        RichTextBaseKt.setRichText(tv, testClickString, "", new ViewClick() {
            @Override
            public void ItemClick(@NotNull Object item, @Nullable String clickTag, @Nullable String paras, @Nullable View view) {
                Toast.makeText(tv.getContext(), clickTag, Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.ll_stftv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "父View响应点击1", Toast.LENGTH_SHORT).show();
            }
        });
        final QMUISpanTouchFixTextView stftv = findViewById(R.id.apan_touch_fix_tv);
        stftv.setNeedForceEventToParent(true);  //设置父布局能接收事件
        RichTextBaseKt.setRichText(stftv, testClickString, "", new ViewClick() {
            @Override
            public void ItemClick(@NotNull Object item, @Nullable String clickTag, @Nullable String paras, @Nullable View view) {
                Toast.makeText(stftv.getContext(), clickTag, Toast.LENGTH_SHORT).show();
            }
        });

        TextView descTextView = findViewById(R.id.desc_tv);
        descTextView.setText("感谢作者开源这么好的富文本库。在项目里，用该库来显示文章详情时，可以很好的处理富文本内容，但是在评论列表里效果就不太好，" +
                "用户快速滑动时很容易出现Item重新调整高度的问题，这里我用了Android自带的Html解析类解析，然后针对项目来处理表情、链接，这样就不会出现这种情况。" +
                "但是只适用于评论列表没有包含图片的情况，如果哪天项目要求评论也要图片，就不能用了。做了个RecyclerViewOptimizeActivity界面，可以点击右上角菜单按钮查看对比");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "RecyclerView");
        menu.add(0, 1, 1, "RecyclerViewOptimizeActivity");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            startActivity(new Intent(this, RecyclerViewActivity.class));
        } else if (item.getItemId() == 1) {
            startActivity(new Intent(this, RecyclerViewOptimizeActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
