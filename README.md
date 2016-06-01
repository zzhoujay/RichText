# RichText

> Android平台下的富文本解析器

* 流式操作
* 低侵入性
* 支持Html格式文本
* 支持图片点击事件
* 链接自动回调
* 支持设置加载中和加载错误时的图片
* 支持自定义超链接的点击回调
* 支持修正图片宽高

### 效果

![演示](image/image.jpg "演示")


### gradle中引用的方法

```
compile 'com.zzhoujay.richtext:richtext:1.1.1'
```


### 使用方式

```
RichText.from(text).into(textView);
```

高级

```
RichText
       .from(text) // 数据源
       .autoFix(true) // 是否自动修复，默认true
       .async(true) // 是否异步，默认false
       .fix(imageFixCallback) // 设置自定义修复图片宽高
       .imageClick(onImageClickListener) // 设置图片点击回调
       .urlClick(onURLClickListener) // 设置链接点击回调
       .placeHolder(placeHolder) // 设置加载中显示的占位图
       .error(errorImage) // 设置加载失败的错误图
       .into(textView); // 设置目标TextView
```

### 自定义修复宽高

```
RichText.from(text).fix(new ImageFixCallback() {
                @Override
                public void onFix(ImageHolder holder,boolean imageReady) {
                     if(imageReady){
                          return;
                     }
                     if(holder.getImageType()==ImageHolder.GIF){
                          holder.setWidth(400);
                          holder.setHeight(400);
                     }else {
                          holder.setAutoFix(true);
                     }
                }
             })
```

通过设置`holder.setAutoFix(true)`设置该图片为自动修复,自动修复的效果是图片按宽度充满，所有如果有些小的图片设置了自动填充可能会
失真，这时候可以取消自动修复设置自定义修复，将比较小的图片过滤出来，将其它的图片自动修复即可。

注意，如果img标签中没有宽高的话onFix方法会在图片加载完成前后调用两次，可以通过imageReady来判断

如下：
```
        RichText.from(text).autoFix(false).fix(new ImageFixCallback() {
            @Override
            public void onFix(ImageHolder holder, boolean imageReady) {
                if (holder.getWidth() > 500 && holder.getHeight() > 500) {
                    holder.setAutoFix(true);
                }
            }
        }).into(textView);
```

### 注意

* gif图片只能显示第一幁
* 只支持Html.fromHtml能够解析的标签，自定义标签的支持后续会跟上的

### 后续计划

* 添加自定义标签的支持
* 添加MarkDown语法的支持

### 具体使用请查看demo

_by zzhoujay_
