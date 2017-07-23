package zhou.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.zzhoujay.richtext.RichText;


public class MainActivity extends AppCompatActivity {


    private static final String IMAGE = "<img title=\"\" src=\"http://g.hiphotos.baidu.com/image/pic/item/241f95cad1c8a7866f726fe06309c93d71cf5087.jpg\"  style=\"cursor: pointer;\"><br><br>" +
            "<img src=\"http://img.ugirls.com/uploads/cooperate/baidu/20160519menghuli.jpg\" width=\"1080\" height=\"1620\"/><a href=\"http://www.baidu.com\">baidu</a>" +
            "hello asdkjfgsduk <a href=\"http://www.jd.com\">jd</a>";
    private static final String IMAGE1 = "<h1>RichText</h1><p>Android平台下的富文本解析器</p><img title=\"\" src=\"http://g.hiphotos.baidu.com/image/pic/item/241f95cad1c8a7866f726fe06309c93d71cf5087.jpg\"  style=\"cursor: pointer;\"><br><br>" +
            "<h3>点击菜单查看更多Demo</h3><img src=\"http://ww2.sinaimg.cn/bmiddle/813a1fc7jw1ee4xpejq4lj20g00o0gnu.jpg\" /><p><a href=\"http://www.baidu.com\">baidu</a>" +
            "hello asdkjfgsduk <a href=\"http://www.jd.com\">jd</a></p><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>bottom";

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

    private final String issue142 = "<p><img src=\"http://image.wangchao.net.cn/it/1233190350268.gif?size=528*388\" width=\"528\" height=\"388\" /></p>";

    private final String issue143 = "<img src=\"file:///C:\\Users\\ADMINI~1\\AppData\\Local\\Temp\\ksohtml\\wpsB8DD.tmp.png\">";

    private final String issue147 = "<div class=\"pictureBox\"> \n" +
            " <img src=\"http://static.storeer.com/wlwb/productDetail/234be0ec-e90a-4eda-90bd-98d64b55280a_580x4339.jpeg\">\n" +
            "</div>";

    private final String issue149 = null;

    private final String issue150 = "<img src='http://cuncxforum-10008003.image.myqcloud.com/642def77-373f-434f-8e68-42dedbd9f880'/><br><img src='http://cuncxforum-10008003.image.myqcloud.com/bf153d9f-e8c3-4dcc-a23e-bfe0358cb429'/>";


    private final String issue177 = "<i class=\"pstatus\"> 本帖最后由 CS古月 于 2017-07-17 14:15 编辑 </i><br /><br /><p>  	  " +
            "这段时间经过多位龙空好友的建议反馈，龙的天空安卓版app终于要发布正式版本了，" +
            "在此先谢谢那些为这个app辛苦测试并提供多方建议的龙友们，感谢！  " +
            "</p>  <h3><strong><span style=\"color:rgb(0,0,255);\">正式版V1.0.0功能介绍：</span></strong></h3>  <ul>  	" +
            "<li><strong>首页帖子：</strong>对应龙空网站首页，分有信息流、主题、精华和热门列表；</li>  	" +
            "<li><strong>版块：</strong>暂时有滑动和九空格两种模式，可以在界面右上角切换；</li>  	<li><strong>消息：" +
            "</strong>可以查看评分、私信、通知及@我的所有消息；</li>  	<li><strong>个人我的资料：" +
            "</strong>可以进行签到，查看主题、收藏、帖子、关注、粉丝及浏览历史资料，点击头像还<span style=\"color:rgb(255,0,0);\">" +
            "可以设置用户头像</span>、性别、头衔、个人介绍及<span style=\"color:rgb(0,0,255);\">小尾巴</span>内容；</li>  " +
            "	<li><strong>搜索：</strong>可以进行帖子/用户/位面的搜索，" +
            "而且可以像网站一样进行热度、时间及相关度的排序选择，还能保存搜索历史；</li>  	" +
            "<li><strong>回复评论及发帖：</strong>回复评论和发贴可以<strong><span style=\"color:rgb(255,0,0);\">" +
            "发表情图片</span></strong>，而且还可以像网站一样字体加粗斜体、分段及发超链接等；</li>  " +
            "	<li><strong>帖子详情：</strong>除了帖子正常的样式外，还有评分、回复及编辑功能；而且还可以调整字体的大小，" +
            "分享帖子内容及选择用浏览器打开帖子；</li>  	<li><strong><span style=\"color:rgb(84,141,212);\">优书网：" +
            "</span></strong>app还接入了优书网的内容，帖子内点击书名可以直接跳到优书网，" +
            "我的界面也可以进入优书网首页，<strong>优书网只要登录一次，则可以长期免登</strong>；</li>  <li><strong>设置：" +
            "</strong>可以切换日夜间模式，可以设置自动签到、清除缓存及版块显示模式；</li>  </ul>  <h4><strong>预定下一版功能：" +
            "</strong></h4>  <ul>  	<li>版块列表区别关注和未关注版块；</li>  	<li>多账号切换；</li>  	<li>增加主题；</li>  	" +
            "<li>优化消息提醒模式；</li>  	<li>位面功能；</li>  </ul>  <p>  	<strong><span " +
            "style=\"background-color:rgb(255,255,255);\">百度网盘下载地址：<a target=\"_blank\"href=\"https://pan.baidu.com/s/1hsACaRq\">" +
            "https://pan.baidu.com/s/1hsACaRq</a></span></strong>  </p>  <p>  	<strong><span style=\"background-color: rgb(255, 255, 255);\">" +
            "蒲公英下载地址：<a target=\"_blank\"href=\"https://www.pgyer.com/1duS\">https://www.pgyer.com/1duS</a></span></strong>  </p>  <p> " +
            " 	<strong>各大市场因为需要账号申请及审核，可能要过两天才能在市场上看到下载。  </strong>  </p>  <p>  	<strong><br>  	</strong> " +
            " </p>  <p>  	如果大家还有什么功能及建议想在之后的版块迭代中出现的，可以反馈给我，<strong><span style=\"color: rgb(255, 0, 0);\">" +
            "反馈Q群：650097719</span></strong><strong><br>  	</strong>  </p> ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Glide.with(this).from("").sizeMultiplier(
        RichText.initCacheDir(this);
        final TextView textView = (TextView) findViewById(R.id.text);


        RichText.from(issue177).into(textView);


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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RichText.recycle();
    }
}
