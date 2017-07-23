package zhou.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zzhoujay.richtext.RichText;

/**
 * Created by zhou on 16-6-17.
 */
public class RecyclerViewActivity extends AppCompatActivity {

    private static final String[] testString1 = new String[]{
            "<h3>Test1</h3><img src=\"http://h.hiphotos.baidu.com/image/h%3D200/sign=e72c850a09f3d7ca13f63876c21fbe3c/a2cc7cd98d1001e9460fd63bbd0e7bec54e797d7.jpg\" />",
            "<h3>Test2</h3><img src=\"http://c.hiphotos.baidu.com/image/pic/item/f7246b600c3387448982f948540fd9f9d72aa0bb.jpg\" />",
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

    private static final String test1 = "<p>试用员工提前三天，正式员工提前三十天以书面形式向所在部门离职申请，所有流程都是在NCOA上进行，首先是离职申请，通过之后是离职交接，NCOA离职交接流程如下：<br />" +
            "\r" +
            "\n<br />" +
            "\r\n" +
            "<img alt=\"\" src=\"http://mutone.fangte.com/V2/Manage/UpLoadFile/httpfile/10033073/image/png/2016/09/05/122805383318639.png\" style=\"height:256px; width:480px\" />" +
            "<br />\r\n<br />\r\n" +
            "员工未办理各项交接手续，不辞而别的，或超假旷工五个工作日以上者，视为自动离职，自动离职的员工不予发放工资，并且终生不予回聘。</p>" +
            "<a href=\"http://www.baidu.com\">baidu</a>";

    private static final String test = "<div class=\"topic_content\" itemprop=\"articleBody\">" +
            "rn " +
            "<div class=\"markdown-text\">" +
            "rn " +
            "<h2>前言</h2>" +
            "rn" +
            "<pre class=\"prettyprint\">" +
            "<code>本来要趁G20的喜庆气氛发布这个版本的,而且是nutz公开发布7周年,nutzcn社区上线1周年,但台风来了,被吹成了SB.rnrn月初,我(wendal)组织了一次长达4小时斗鱼直播(nutz.cn的内存泄漏排除),收看人数随着时间的推移正比例下降,rn最终收入鱼丸0个和鱼翅0根,妥妥的稳定0收入.最终,在睡醒一觉之后,怒删几个依赖库,解决了.rn随机调查了2名群众, 35%的群众表示,没有球没有肉,全是硬货太难啃,最终也没高潮,必须差评!!!rnrn另外,有小伙伴投诉说最近nutz刷版本很快啊,实不相瞒,当前的发布周期就是2-3个月,我觉得不算快枪手了.rnrn这次,我们集中力量完成了dao层的几个重要更新: #1116 读写分离,#1117 拦截器机制,#1119 支持存储过程的出参rn</code></pre><h2>与1.r.57.r3的兼容性</h2><p>这个版本的兼容性,主要是DaoRunner的实现类NutDaoRunner的变化导致的.</p>rn<ul>rn <li>判断是否开启自动事务,以前是NutDao负责,现在由NutDaoRunner负责 -- 如果自定义NutDaoRunner的话,改为复写其{_run}方法即可</li>rn <li>SQL日志的打印,现在由DaoLogInterceptor负责 -- 与daocache配合时的日志有变化,但是对功能没有任何影响. 详情看[issue1137 https://github.com/nutzam/nutz/issues/1137]</li>rn</ul><h2>主要变化:</h2>rn<ul>rn <li>add: #1116 Dao读写分离</li>rn <li>add: #1117 Dao拦截器机制</li>rn <li>add: #1119 支持存储过程的出参</li>rn <li>add: #1121 支持vue-resource的X-HTTP-Method-Override</li>rn <li>fix: #1134 SimpleDataSource不兼容Mysql6.0驱动</li>rn <li>fix: #1114 Http轻客户端的Session维持</li>rn <li>fix: #1109 Mvc前置表单列表的索引顺序不对</li>rn</ul><h2>关联项目更新:</h2>rn<ul>rn <li>add: daocache支持dao拦截器模式配置</li>rn <li>add: dubbo插件,兼容原生dubbo配置</li>rn <li>add: apidoc插件</li>rn <li>add: SfntTool插件,字体文件精简,用于PDF字体内嵌</li>rn <li>update: views插件支持pdf view和velocity layout</li>rn <li>update: sigar符合新版nutz插件的命名规则</li>rn <li>update: 大鲨鱼写的 <a href=\"https://github.com/Wizzercn/NutzWk\" target=\"_blank\">https://github.com/Wizzercn/NutzWk</a> 3.2.7</li>rn <li>update: 單純願望 <a href=\"https://github.com/Kerbores/NUTZ-ONEKEY\" target=\"_blank\">https://github.com/Kerbores/NUTZ-ONEKEY</a> 2.0</li>rn <li>update: 悟空 <a href=\"https://github.com/wukonggg/mz-g\" target=\"_blank\">https://github.com/wukonggg/mz-g</a> 0.6.3</li>rn</ul>rntt</div>rn </div><img width=\"258\" height=\"186\" title=\"点击查看源网页\" class=\"currentImg\" style=\"left: 330.5px; top: 24.5px; width: 258px; height: 186px; cursor: pointer;\" onload=\"alog &amp;&amp; alog('speed.set', 'c_firstPageComplete', +new Date); alog.fire &amp;&amp; alog.fire('mark');\" src=\"http://img2.imgtn.bdimg.com/it/u=2643681278,706636134&amp;fm=21&amp;gp=0.jpg\" log-rightclick=\"p=5.102\"><div id=\"J-detail-content\"><img alt=\"\" src=\"http://img10.360buyimg.com/imgzone/jfs/t2719/53/693438809/405912/957c1efa/5721e109N8ad86029.jpg\"> n<img alt=\"\" src=\"http://img10.360buyimg.com/imgzone/jfs/t2836/30/707249522/270588/840d428a/5721e108Ne667230f.jpg\"> n<img alt=\"\" src=\"http://img10.360buyimg.com/imgzone/jfs/t2305/211/1222246162/89571/4ce4f9a1/56496ac7N982aa001.jpg\"> n<img alt=\"\" src=\"http://img10.360buyimg.com/imgzone/jfs/t2695/76/715579111/331050/cf2ae9f9/5721e10aNd690b026.jpg\"> n<img alt=\"\" src=\"http://img10.360buyimg.com/imgzone/jfs/t2341/288/2958575364/740490/9678e90f/5721e10bNf923ebaa.jpg\"> n<img alt=\"\" src=\"http://img10.360buyimg.com/imgzone/jfs/t2776/164/715581717/852142/2fa4714f/5721e10bN04e38f08.jpg\"> n<img alt=\"\" src=\"http://img10.360buyimg.com/imgzone/jfs/t2104/197/2936780208/316761/f3051b63/5721e10cN1b74089c.jpg\"><br></div>";

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
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                if (holder instanceof Holder) {
                    Holder h = (Holder) holder;
                    if (position == 0) {
                        RichText.from(issue177).singleLoad(false).into(h.text);
                    } else {
                        RichText.from(testString1[position - 1]).singleLoad(false).into(h.text);
                    }
                }
            }

            @Override
            public int getItemCount() {
                return testString1.length + 1;
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
