package zhou.demo;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.zzhoujay.richtext.RichText;
import com.zzhoujay.richtext.callback.EmotionGetter;
import com.zzhoujay.richtext.callback.OnUrlClickListener;

public class MyTestActivity extends AppCompatActivity {

    private static final String html = "<article class=\"markdown-body entry-content\" itemprop=\"text\"><h1><a href=\"#richtext\" aria-hidden=\"true\" class=\"anchor\" id=\"user-content-richtext\"><svg aria-hidden=\"true\" class=\"octicon octicon-link\" height=\"16\" version=\"1.1\" viewBox=\"0 0 16 16\" width=\"16\"><path fill-rule=\"evenodd\" d=\"M4 9h1v1H4c-1.5 0-3-1.69-3-3.5S2.55 3 4 3h4c1.45 0 3 1.69 3 3.5 0 1.41-.91 2.72-2 3.25V8.59c.58-.45 1-1.27 1-2.09C10 5.22 8.98 4 8 4H4c-.98 0-2 1.22-2 2.5S3 9 4 9zm9-3h-1v1h1c1 0 2 1.22 2 2.5S13.98 12 13 12H9c-.98 0-2-1.22-2-2.5 0-.83.42-1.64 1-2.09V6.25c-1.09.53-2 1.84-2 3.25C6 11.31 7.55 13 9 13h4c1.45 0 3-1.69 3-3.5S14.5 6 13 6z\"></path></svg></a>RichText</h1>\n" +
            "<blockquote>\n" +
            "<p style=\"background-color:rgba(255,0,0,1);\">Android平台下的富文本解析器</p>\n" +
            "</blockquote>\n" +
            "<ul>\n" +
            "<li>流式操作</li>\n" +
            "<li>低侵入性</li>\n" +
            "<li>依赖少，只依赖了<code>disklrucache</code>和<code>support v4</code></li>\n" +
            "<li>支持Html和Markdown格式文本</li>\n" +
            "<li>支持图片点击和长按事件</li>\n" +
            "<li>链接点击事件和长按事件</li>\n" +
            "<li>支持设置加载中和加载错误时的图片</li>\n" +
            "<li>支持自定义超链接的点击回调</li>\n" +
            "<li>支持修正图片宽高</li>\n" +
            "<li>支持GIF图片</li>\n" +
            "<li>支持Base64编码、本地图片和Assets目录图片</li>\n" +
            "<li>自持自定义图片加载器、图片加载器</li>\n" +
            "<li>支持内存和磁盘双缓存</li>\n" +
            "</ul>\n" +
            "<h3><a href=\"#效果\" aria-hidden=\"true\" class=\"anchor\" id=\"user-content-效果\"><svg aria-hidden=\"true\" class=\"octicon octicon-link\" height=\"16\" version=\"1.1\" viewBox=\"0 0 16 16\" width=\"16\"><path fill-rule=\"evenodd\" d=\"M4 9h1v1H4c-1.5 0-3-1.69-3-3.5S2.55 3 4 3h4c1.45 0 3 1.69 3 3.5 0 1.41-.91 2.72-2 3.25V8.59c.58-.45 1-1.27 1-2.09C10 5.22 8.98 4 8 4H4c-.98 0-2 1.22-2 2.5S3 9 4 9zm9-3h-1v1h1c1 0 2 1.22 2 2.5S13.98 12 13 12H9c-.98 0-2-1.22-2-2.5 0-.83.42-1.64 1-2.09V6.25c-1.09.53-2 1.84-2 3.25C6 11.31 7.55 13 9 13h4c1.45 0 3-1.69 3-3.5S14.5 6 13 6z\"></path></svg></a>效果</h3>\n" +
            "<p><a href=\"/zzhoujay/RichText/blob/master/image/image.jpg\" target=\"_blank\"><img src=\"http://a.hiphotos.baidu.com/image/pic/item/bba1cd11728b4710910b55c9c1cec3fdfc03238a.jpg\" alt=\"演示\" title=\"演示\" style=\"max-width:100%;\"></a></p>\n" +
            "<h3><a href=\"#gradle中引用的方法\" aria-hidden=\"true\" class=\"anchor\" id=\"user-content-gradle中引用的方法\"><svg aria-hidden=\"true\" class=\"octicon octicon-link\" height=\"16\" version=\"1.1\" viewBox=\"0 0 16 16\" width=\"16\"><path fill-rule=\"evenodd\" d=\"M4 9h1v1H4c-1.5 0-3-1.69-3-3.5S2.55 3 4 3h4c1.45 0 3 1.69 3 3.5 0 1.41-.91 2.72-2 3.25V8.59c.58-.45 1-1.27 1-2.09C10 5.22 8.98 4 8 4H4c-.98 0-2 1.22-2 2.5S3 9 4 9zm9-3h-1v1h1c1 0 2 1.22 2 2.5S13.98 12 13 12H9c-.98 0-2-1.22-2-2.5 0-.83.42-1.64 1-2.09V6.25c-1.09.53-2 1.84-2 3.25C6 11.31 7.55 13 9 13h4c1.45 0 3-1.69 3-3.5S14.5 6 13 6z\"></path></svg></a>gradle中引用的方法</h3>\n" +
            "<pre><code>compile 'com.zzhoujay.richtext:richtext:3.0.5'\n" +
            "</code></pre>\n" +
            "<h3><a href=\"#关于issue\" aria-hidden=\"true\" class=\"anchor\" id=\"user-content-关于issue\"><svg aria-hidden=\"true\" class=\"octicon octicon-link\" height=\"16\" version=\"1.1\" viewBox=\"0 0 16 16\" width=\"16\"><path fill-rule=\"evenodd\" d=\"M4 9h1v1H4c-1.5 0-3-1.69-3-3.5S2.55 3 4 3h4c1.45 0 3 1.69 3 3.5 0 1.41-.91 2.72-2 3.25V8.59c.58-.45 1-1.27 1-2.09C10 5.22 8.98 4 8 4H4c-.98 0-2 1.22-2 2.5S3 9 4 9zm9-3h-1v1h1c1 0 2 1.22 2 2.5S13.98 12 13 12H9c-.98 0-2-1.22-2-2.5 0-.83.42-1.64 1-2.09V6.25c-1.09.53-2 1.84-2 3.25C6 11.31 7.55 13 9 13h4c1.45 0 3-1.69 3-3.5S14.5 6 13 6z\"></path></svg></a>关于issue</h3>\n" +
            "<p style=\"text-indent:50px;\">最近一段时间会比较忙，issue不能及时处理，一般会定时抽空集中解决issue，但时间有限解决速度上不敢保证。</p>\n" +
            "<p>欢迎提交pull request帮助完善这个项目</p>\n" +
            "<h3><a href=\"#注意\" aria-hidden=\"true\" class=\"anchor\" id=\"user-content-注意\"><svg aria-hidden=\"true\" class=\"octicon octicon-link\" height=\"16\" version=\"1.1\" viewBox=\"0 0 16 16\" width=\"16\"><path fill-rule=\"evenodd\" d=\"M4 9h1v1H4c-1.5 0-3-1.69-3-3.5S2.55 3 4 3h4c1.45 0 3 1.69 3 3.5 0 1.41-.91 2.72-2 3.25V8.59c.58-.45 1-1.27 1-2.09C10 5.22 8.98 4 8 4H4c-.98 0-2 1.22-2 2.5S3 9 4 9zm9-3h-1v1h1c1 0 2 1.22 2 2.5S13.98 12 13 12H9c-.98 0-2-1.22-2-2.5 0-.83.42-1.64 1-2.09V6.25c-1.09.53-2 1.84-2 3.25C6 11.31 7.55 13 9 13h4c1.45 0 3-1.69 3-3.5S14.5 6 13 6z\"></path></svg></a>注意</h3>\n" +
            "<p>在第一次调用RichText之前先调用<code>RichText.initCacheDir()</code>方法设置缓存目录</p>\n" +
            "<p>ImageFixCallback的回调方法不一定是在主线程回调，注意不要进行UI操作</p>\n" +
            "<p>本地图片由根路径<code>\\</code>开头，Assets目录图片由<code>file:///android_asset/</code>开头</p>\n" +
            "<p>Gif图片播放不支持硬件加速，若要使用Gif图片请先关闭TextView的硬件加速</p>\n" +
            "<pre><code>textView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);\n" +
            "</code></pre>\n" +
            "<h3><a href=\"#使用方式\" aria-hidden=\"true\" class=\"anchor\" id=\"user-content-使用方式\"><svg aria-hidden=\"true\" class=\"octicon octicon-link\" height=\"16\" version=\"1.1\" viewBox=\"0 0 16 16\" width=\"16\"><path fill-rule=\"evenodd\" d=\"M4 9h1v1H4c-1.5 0-3-1.69-3-3.5S2.55 3 4 3h4c1.45 0 3 1.69 3 3.5 0 1.41-.91 2.72-2 3.25V8.59c.58-.45 1-1.27 1-2.09C10 5.22 8.98 4 8 4H4c-.98 0-2 1.22-2 2.5S3 9 4 9zm9-3h-1v1h1c1 0 2 1.22 2 2.5S13.98 12 13 12H9c-.98 0-2-1.22-2-2.5 0-.83.42-1.64 1-2.09V6.25c-1.09.53-2 1.84-2 3.25C6 11.31 7.55 13 9 13h4c1.45 0 3-1.69 3-3.5S14.5 6 13 6z\"></path></svg></a>使用方式</h3>\n" +
            "<p><a href=\"https://github.com/zzhoujay/RichText/wiki\">多看wiki</a>、<a href=\"https://github.com/zzhoujay/RichText/wiki\">多看wiki</a>、<a href=\"https://github.com/zzhoujay/RichText/wiki\">多看wiki</a>，重要的事情说三遍</p>\n" +
            "<h3><a href=\"#后续计划\" aria-hidden=\"true\" class=\"anchor\" id=\"user-content-后续计划\"><svg aria-hidden=\"true\" class=\"octicon octicon-link\" height=\"16\" version=\"1.1\" viewBox=\"0 0 16 16\" width=\"16\"><path fill-rule=\"evenodd\" d=\"M4 9h1v1H4c-1.5 0-3-1.69-3-3.5S2.55 3 4 3h4c1.45 0 3 1.69 3 3.5 0 1.41-.91 2.72-2 3.25V8.59c.58-.45 1-1.27 1-2.09C10 5.22 8.98 4 8 4H4c-.98 0-2 1.22-2 2.5S3 9 4 9zm9-3h-1v1h1c1 0 2 1.22 2 2.5S13.98 12 13 12H9c-.98 0-2-1.22-2-2.5 0-.83.42-1.64 1-2.09V6.25c-1.09.53-2 1.84-2 3.25C6 11.31 7.55 13 9 13h4c1.45 0 3-1.69 3-3.5S14.5 6 13 6z\"></path></svg></a>后续计划</h3>\n" +
            "<ul>\n" +
            "<li><del>添加自定义标签的支持</del> (已添加对少部分自定义标签的支持)</li>\n" +
            "</ul>\n" +
            "<h3><a href=\"#关于markdown\" aria-hidden=\"true\" class=\"anchor\" id=\"user-content-关于markdown\"><svg aria-hidden=\"true\" class=\"octicon octicon-link\" height=\"16\" version=\"1.1\" viewBox=\"0 0 16 16\" width=\"16\"><path fill-rule=\"evenodd\" d=\"M4 9h1v1H4c-1.5 0-3-1.69-3-3.5S2.55 3 4 3h4c1.45 0 3 1.69 3 3.5 0 1.41-.91 2.72-2 3.25V8.59c.58-.45 1-1.27 1-2.09C10 5.22 8.98 4 8 4H4c-.98 0-2 1.22-2 2.5S3 9 4 9zm9-3h-1v1h1c1 0 2 1.22 2 2.5S13.98 12 13 12H9c-.98 0-2-1.22-2-2.5 0-.83.42-1.64 1-2.09V6.25c-1.09.53-2 1.84-2 3.25C6 11.31 7.55 13 9 13h4c1.45 0 3-1.69 3-3.5S14.5 6 13 6z\"></path></svg></a>关于Markdown</h3>\n" +
            "<p>Markdown源于子项目：<a href=\"https://github.com/zzhoujay/Markdown\">Markdown</a></p>\n" +
            "<p>若在markdown解析过程中发现什么问题可以在该项目中反馈</p>\n" +
            "<h3><a href=\"#富文本编辑器\" aria-hidden=\"true\" class=\"anchor\" id=\"user-content-富文本编辑器\"><svg aria-hidden=\"true\" class=\"octicon octicon-link\" height=\"16\" version=\"1.1\" viewBox=\"0 0 16 16\" width=\"16\"><path fill-rule=\"evenodd\" d=\"M4 9h1v1H4c-1.5 0-3-1.69-3-3.5S2.55 3 4 3h4c1.45 0 3 1.69 3 3.5 0 1.41-.91 2.72-2 3.25V8.59c.58-.45 1-1.27 1-2.09C10 5.22 8.98 4 8 4H4c-.98 0-2 1.22-2 2.5S3 9 4 9zm9-3h-1v1h1c1 0 2 1.22 2 2.5S13.98 12 13 12H9c-.98 0-2-1.22-2-2.5 0-.83.42-1.64 1-2.09V6.25c-1.09.53-2 1.84-2 3.25C6 11.31 7.55 13 9 13h4c1.45 0 3-1.69 3-3.5S14.5 6 13 6z\"></path></svg></a>富文本编辑器</h3>\n" +
            "<p>编辑功能目前正在开发中，<a href=\"https://github.com/zzhoujay/RichEditor\">RichEditor</a></p>\n" +
            "<h3><a href=\"#具体使用请查看demo\" aria-hidden=\"true\" class=\"anchor\" id=\"user-content-具体使用请查看demo\"><svg aria-hidden=\"true\" class=\"octicon octicon-link\" height=\"16\" version=\"1.1\" viewBox=\"0 0 16 16\" width=\"16\"><path fill-rule=\"evenodd\" d=\"M4 9h1v1H4c-1.5 0-3-1.69-3-3.5S2.55 3 4 3h4c1.45 0 3 1.69 3 3.5 0 1.41-.91 2.72-2 3.25V8.59c.58-.45 1-1.27 1-2.09C10 5.22 8.98 4 8 4H4c-.98 0-2 1.22-2 2.5S3 9 4 9zm9-3h-1v1h1c1 0 2 1.22 2 2.5S13.98 12 13 12H9c-.98 0-2-1.22-2-2.5 0-.83.42-1.64 1-2.09V6.25c-1.09.53-2 1.84-2 3.25C6 11.31 7.55 13 9 13h4c1.45 0 3-1.69 3-3.5S14.5 6 13 6z\"></path></svg></a>具体使用请查看demo</h3>\n" +
            "<p><a href=\"https://github.com/zzhoujay/RichText/blob/master/app/src/main/java/zhou/demo/ListViewActivity.java\">ListView Demo</a>、\n" +
            "<a href=\"https://github.com/zzhoujay/RichText/blob/master/app/src/main/java/zhou/demo/RecyclerViewActivity.java\">RecyclerView Demo</a>、\n" +
            "<a href=\"https://github.com/zzhoujay/RichText/blob/master/app/src/main/java/zhou/demo/GifActivity.java\">Gif Demo</a></p>\n" +
            "<h3><a href=\"#license\" aria-hidden=\"true\" class=\"anchor\" id=\"user-content-license\"><svg aria-hidden=\"true\" class=\"octicon octicon-link\" height=\"16\" version=\"1.1\" viewBox=\"0 0 16 16\" width=\"16\"><path fill-rule=\"evenodd\" d=\"M4 9h1v1H4c-1.5 0-3-1.69-3-3.5S2.55 3 4 3h4c1.45 0 3 1.69 3 3.5 0 1.41-.91 2.72-2 3.25V8.59c.58-.45 1-1.27 1-2.09C10 5.22 8.98 4 8 4H4c-.98 0-2 1.22-2 2.5S3 9 4 9zm9-3h-1v1h1c1 0 2 1.22 2 2.5S13.98 12 13 12H9c-.98 0-2-1.22-2-2.5 0-.83.42-1.64 1-2.09V6.25c-1.09.53-2 1.84-2 3.25C6 11.31 7.55 13 9 13h4c1.45 0 3-1.69 3-3.5S14.5 6 13 6z\"></path></svg></a>License</h3>\n" +
            "<pre><code>The MIT License (MIT)\n" +
            "\n" +
            "Copyright (c) 2016 zzhoujay\n" +
            "\n" +
            "Permission is hereby granted, free of charge, to any person obtaining a copy\n" +
            "of this software and associated documentation files (the \"Software\"), to deal\n" +
            "in the Software without restriction, including without limitation the rights\n" +
            "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n" +
            "copies of the Software, and to permit persons to whom the Software is\n" +
            "furnished to do so, subject to the following conditions:\n" +
            "\n" +
            "The above copyright notice and this permission notice shall be included in all\n" +
            "copies or substantial portions of the Software.\n" +
            "\n" +
            "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n" +
            "\n" +
            "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n" +
            "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n" +
            "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n" +
            "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n" +
            "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\n" +
            "SOFTWARE.\n" +
            "</code></pre>\n" +
            "<p><em>by zzhoujay</em></p>\n" +
            "</article>";

    private static final String gif_test = "<h3>Test1</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/1.gif\" />" +
            "            <h3>Test2</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/2.gif\" />" +
            "            <h3>Test3</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/3.gif\" />" +
            "            <h3>Test4</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/4.gif\" />" +
            "            <h3>Test5</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/5.gif\" />" +
            "            <h3>Test6</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/6.gif\" />" +
            "            <h3>Test7</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/7.gif\" />" +
            "            <h3>Test8</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/8.gif\" />" +
            "            <h3>Test9</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/9.gif\" />";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = findViewById(R.id.text);

        RichText.from(gif_test)
                .urlClick(new OnUrlClickListener() {
                    @Override
                    public boolean urlClicked(String url) {
                        if (url.startsWith("code://")) {
                            Toast.makeText(MyTestActivity.this, url.replaceFirst("code://", ""), Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        return false;
                    }
                })
                .into(textView, new EmotionGetter() {
                    @Override
                    public Drawable getDrawable(String emotionKey) {
                        return null;
                    }
                });

    }
}
