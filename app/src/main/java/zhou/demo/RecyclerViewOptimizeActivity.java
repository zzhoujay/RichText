package zhou.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.textview.QMUISpanTouchFixTextView;

import org.jetbrains.annotations.NotNull;

import zhou.parse.RichTextBaseKt;
import zhou.parse.ViewClick;

import static zhou.demo.RecyclerViewActivity.myString;

/**
 * 这个Activity展示了在列表中显示含有表情、@标签、自定义drawable的场景，适用于列表，
 * 先用android自带的Html类解析内容，然后再针对项目需求来解析和处理表情、@标签等，这种方案解析速度快，不会出现界面闪动的情况
 */
public class RecyclerViewOptimizeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new Holder(LayoutInflater.from(RecyclerViewOptimizeActivity.this).inflate(R.layout.item_list, parent, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                if (holder instanceof Holder) {
                    final Holder h = (Holder) holder;
                    RichTextBaseKt.setSimpleRichText(h.text, myString[position], true, new ViewClick() {
                        @Override
                        public void ItemClick(@NotNull Object item, @Nullable String clickTag, @Nullable String paras, @Nullable View view) {
                            Toast.makeText(h.text.getContext(), clickTag, Toast.LENGTH_SHORT).show();
                        }
                    });
                    h.ll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(v.getContext(), "点击了Item", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public int getItemCount() {
                return myString.length;
            }

            class Holder extends RecyclerView.ViewHolder {
                public LinearLayout ll;
                public QMUISpanTouchFixTextView text;

                public Holder(View itemView) {
                    super(itemView);
                    ll = itemView.findViewById(R.id.ll);
                    text = itemView.findViewById(R.id.text_item);
                }
            }
        });
    }
}
