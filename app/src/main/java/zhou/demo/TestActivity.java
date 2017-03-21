package zhou.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.zzhoujay.richtext.RichText;

/**
 * Created by zhou on 16-10-22.
 */

public class TestActivity extends AppCompatActivity {

    private static final String test = "<div class=\"topic_content\" itemprop=\"articleBody\">" +
            "<div class=\"markdown-text\">" +
            "<h2>前言</h2>" +
            "<pre class=\"prettyprint\">" +
            "<code>本来要趁G20的喜庆气氛发布这个版本的,而且是公开发布7周年,社区上线1周年,但台风来了,被吹成了SB.月初,我(wendal)组织了一次长达4小时斗鱼直播(nutz.cn的内存泄漏排除),收看人数随着时间的推移正比例下降,rn最终收入鱼丸0个和鱼翅0根,妥妥的稳定0收入.最终,在睡醒一觉之后,怒删几个依赖库,解决了.rn随机调查了2名群众, 35%的群众表示,没有球没有肉,全是硬货太难啃,最终也没高潮,必须差评!!!rnrn另外,有小伙伴投诉说最近nutz刷版本很快啊,实不相瞒,当前的发布周期就是2-3个月,我觉得不算快枪手了.rnrn这次,我们集中力量完成了dao层的几个重要更新: #1116 读写分离,#1117 拦截器机制,#1119 支持存储过程的出参</code></pre>" +
            "<h2>与1.r.57.r3的兼容性</h2><p>这个版本的兼容性,主要是DaoRunner的实现类NutDaoRunner的变化导致的.</p>" +
            "<ul>" +
            "<li>判断是否开启自动事务,以前是NutDao负责,现在由NutDaoRunner负责 -- 如果自定义NutDaoRunner的话,改为复写其{_run}方法即可</li>" +
            "<li>SQL日志的打印,现在由DaoLogInterceptor负责 -- 与daocache配合时的日志有变化,但是对功能没有任何影响. 详情看[issue1137 https://github.com/nutzam/nutz/issues/1137]</li>" +
            "</ul>" +
            "<h2>主要变化:</h2>" +
            "<ul>" +
            "<li>add: #1116 Dao读写分离</li>" +
            "<li>add: #1117 Dao拦截器机制</li>" +
            "<li>add: #1119 支持存储过程的出参</li>" +
            "<li>add: #1121 支持vue-resource的X-HTTP-Method-Override</li>" +
            "<li>fix: #1134 SimpleDataSource不兼容Mysql6.0驱动</li>" +
            "<li>fix: #1114 Http轻客户端的Session维持</li>" +
            "<li>fix: #1109 Mvc前置表单列表的索引顺序不对</li>" +
            "</ul>" +
            "<h2>关联项目更新:</h2>" +
            "<ul>" +
            "<li>add: daocache支持dao拦截器模式配置</li>" +
            "<li>add: dubbo插件,兼容原生dubbo配置</li>" +
            "<li>add: apidoc插件</li>" +
            "<li>add: SfntTool插件,字体文件精简,用于PDF字体内嵌</li>" +
            "<li>update: views插件支持pdf view和velocity layout</li>" +
            "<li>update: sigar符合新版nutz插件的命名规则</li>" +
            "<li>update: 大鲨鱼写的 " +
            "<a href=\"https://github.com/Wizzercn/NutzWk\" target=\"_blank\">https://github.com/Wizzercn/NutzWk</a> 3.2.7</li>" +
            "<li>update: 單純願望 <a href=\"https://github.com/Kerbores/NUTZ-ONEKEY\" target=\"_blank\">https://github.com/Kerbores/NUTZ-ONEKEY</a> 2.0</li>" +
            "<li>update: 悟空 <a href=\"https://github.com/wukonggg/mz-g\" target=\"_blank\">https://github.com/wukonggg/mz-g</a> 0.6.3</li>" +
            "</ul>" +
            "</div>" +
            "</div>" +
            "<img width=\"258\" height=\"186\" title=\"点击查看源网页\" class=\"currentImg\" style=\"left: 330.5px; top: 24.5px; width: 258px; height: 186px; cursor: pointer;\" onload=\"alog &amp;&amp; alog('speed.set', 'c_firstPageComplete', +new Date); alog.fire &amp;&amp; alog.fire('mark');\" src=\"http://img2.imgtn.bdimg.com/it/u=2643681278,706636134&amp;fm=21&amp;gp=0.jpg\" log-rightclick=\"p=5.102\"><div id=\"J-detail-content\">" +
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        TextView textView = (TextView) findViewById(R.id.text);

        assert textView != null;

        richText = RichText.from(test).into(textView);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        richText.clear();
        richText = null;
    }
}
