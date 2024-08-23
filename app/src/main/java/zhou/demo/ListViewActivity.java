package zhou.demo;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichText;
import com.zzhoujay.richtext.callback.SimpleImageFixCallback;

/**
 * Created by zhou on 16-6-17.
 */
public class ListViewActivity extends AppCompatActivity {

//    private static final String[] testString = new String[]{
//            "<h3>Test1</h3><img src=\"https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/6567102e7b814573bafbc56ca1309b82~tplv-k3u1fbpfcp-zoom-crop-mark:3024:3024:3024:1702.awebp\" />",
//            "<h3>Test2</h3><img src=\"http://c.hiphotos.baidu.com/image/pic/item/f7246b600c3387448982f948540fd9f9d72aa0bb.jpg\" />",
//            "<h3>Test3</h3><img src=\"http://c.hiphotos.baidu.com/image/pic/item/267f9e2f070828382dcc0b20bd99a9014d08f1c5.jpg\" />",
//            "<h3>Test4</h3><img src=\"http://f.hiphotos.baidu.com/image/pic/item/32fa828ba61ea8d358824a0d950a304e251f5812.jpg\" />",
//            "<h3>Test5</h3><img src=\"http://f.hiphotos.baidu.com/image/pic/item/c2cec3fdfc039245831fa7498294a4c27c1e25c9.jpg\" />",
//            "<h3>Test6</h3><img src=\"http://e.hiphotos.baidu.com/image/pic/item/b999a9014c086e06613eab4b00087bf40ad1cb18.jpg\" />",
//            "<h3>Test7</h3><img src=\"http://a.hiphotos.baidu.com/image/pic/item/503d269759ee3d6d251670cb41166d224e4adeda.jpg\" />",
//            "<h3>Test8</h3><img src=\"http://f.hiphotos.baidu.com/image/pic/item/cb8065380cd791234275326baf345982b2b7801c.jpg\" />",
//            "<h3>Test9</h3><img src=\"http://a.hiphotos.baidu.com/image/pic/item/bba1cd11728b4710910b55c9c1cec3fdfc03238a.jpg\" />"
//    };

    private static final String gifTest = "<h3>Gif Test</h3><img src=\"http://ww4.sinaimg.cn/large/5cfc088ejw1f3jcujb6d6g20ap08mb2c.gif\"/>";

    private static final String[] testString = {
            "<h3>Test1</h3><img src=\"http://img.soogif.com/Xm3zewwGUgBRunDeS7Of8NwmQ4t3sakO.gif\" />",
            "<h3>Test2</h3><img src=\"http://img.soogif.com/iAYcT06yJ5q2nCMZWAA5EhsqIXctOt8y.gif\" />",
            "<h3>Test3</h3><img src=\"https://img.soogif.com/rSlMSm7msQagXhSSgIQ0LtqTusCK712l.gif\" />",
            "<h3>Test4</h3><img src=\"http://img.soogif.com/0KrElalx3tSiiL2vJGf1YWjIHUfiq73d.gif\" />",
            "<h3>Test5</h3><img src=\"http://img.soogif.com/s95N1KWG8fGeQHPdiwvmiy4SJUU5zTnr.gif\" />",
            "<h3>Test6</h3><img src=\"https://img.soogif.com/ECtUVa5UQsiDDU7og85Ue4NHfysXbeMm.gif\" />",
            "<h3>Test7</h3><img src=\"https://img.soogif.com/UPv53hBqhwuUKs5E5FDPh4gsRx5Ew02F.gif\" />",
            "<h3>Test8</h3><img src=\"https://img.soogif.com/osrtKN8i84Kq0EjKQeMaKpiba2n6oX7D.gif\" />",
            "<h3>Test9</h3><img src=\"https://img.soogif.com/O0o2yMSfsE5buC6o7DLNBgRWie3piZyA.gif\" />"
    };
    private static final String[] testString__ = {
            "<h3>Test1</h3><img src=\"http://img.soogif.com/0KrElalx3tSiiL2vJGf1YWjIHUfiq73d.gif\" />",
            "<h3>Test2</h3><img src=\"https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/d78f9d5f2fa14d1cae48dc6dee3ba6d1~tplv-k3u1fbpfcp-zoom-in-crop-mark:3024:0:0:0.awebp\" />",
            "<h3>Test9</h3><img src=\"https://img.soogif.com/UPv53hBqhwuUKs5E5FDPh4gsRx5Ew02F.gif\" />",
            "<h3>Test3</h3><img src=\"http://f.hiphotos.baidu.com/image/pic/item/3b87e950352ac65cad5ff279f9f2b21193138a66.jpg\" />",
            "<h3>Test4</h3><img src=\"http://c.hiphotos.baidu.com/image/pic/item/63d9f2d3572c11dfd3042623612762d0f603c2dd.jpg\" />",
            "<h3>Test5</h3><img src=\"http://d.hiphotos.baidu.com/image/pic/item/562c11dfa9ec8a13f075f10cf303918fa1ecc0eb.jpg\" />",
            "<h3>Test6</h3><img src=\"http://b.hiphotos.baidu.com/image/pic/item/cdbf6c81800a19d89d81380531fa828ba61e462b.jpg\" />",
//            "<h3>Test7</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/7.gif\" />",
//            "<h3>Test8</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/8.gif\" />",
//            "<h3>Test9</h3><img src=\"http://www.aikf.com/ask/resources/images/facialExpression/qq/9.gif\" />"
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return testString__.length + 1;
            }

            @Override
            public Object getItem(int position) {
                return testString[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                Holder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(ListViewActivity.this).inflate(R.layout.item_list, parent, false);
                    holder = new Holder(convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                Log.i("RichText", "position:" + position + ",textView:" + System.identityHashCode(holder.text));
                String text;
                if (position == 0) {
                    text = gifTest;
                } else {
                    text = testString__[position - 1];
                }
                RichText.from(text).autoPlay(true).singleLoad(false).fix(new SimpleImageFixCallback() {
                    @Override
                    public void onInit(ImageHolder holder) {
                        if (holder.isGif()) {
                            holder.setAutoFix(false);
                        }
                    }
                }).into(holder.text);
                return convertView;
            }

            class Holder {
                public final TextView text;

                public Holder(View view) {
                    text = (TextView) view.findViewById(R.id.text_item);
                }
            }
        });
    }
}
