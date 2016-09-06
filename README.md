# RichText

> Android平台下的富文本解析器

* 流式操作
* 低侵入性
* 支持Html和Markdown格式文本
* 支持图片点击和长按事件
* 链接点击事件和长按事件
* 支持设置加载中和加载错误时的图片
* 支持自定义超链接的点击回调
* 支持修正图片宽高
* 支持GIF图片

### 效果

![演示](image/image.jpg "演示")


### gradle中引用的方法

```
compile 'com.zzhoujay.richtext:richtext:2.0.11'
```


### 使用方式

```
RichText.from(text).into(textView);
```

Markdown：

```
RichText.fromMarkdown(markdown).into(textView);
```

高级

```
RichText
       .from(text) // 数据源
       .type(RichText.TYPE_MARKDOWN) // 数据格式,不设置默认是Html,使用fromMarkdown的默认是Markdown格式
       .autoFix(true) // 是否自动修复，默认true
       .async(true) // 是否异步，默认false
       .fix(imageFixCallback) // 设置自定义修复图片宽高
       .noImage(true) // 不显示并且不加载图片
       .clickable(true) // 是否可点击，默认只有设置了点击监听才可点击
       .imageClick(onImageClickListener) // 设置图片点击回调
       .imageLongClick(onImageLongClickListener) // 设置图片长按回调
       .urlClick(onURLClickListener) // 设置链接点击回调
       .urlLongClick(onUrlLongClickListener) // 设置链接长按回调
       .placeHolder(placeHolder) // 设置加载中显示的占位图
       .error(errorImage) // 设置加载失败的错误图
       .into(textView); // 设置目标TextView
```

### 关于ImageHolder

ImageHolder是在设置了ImageFixCallback后回调方法中的一个参数,代表了每张图片

其属性有:

* `width` : 图片宽度
* `height` : 图片高度
* `scaleType` : 缩放方式
* `imageType` : 图片类型,JPG/PNG
* `autoFix` : 自动修复宽高
* `autoPlay` : 自动播放Gif图,在图片类型是Gif时有效
* `autoStop` : 自动停止Gif图片的播放,在图片是Gif时有效
* `show` : 是否显示

通过调用对应的getter和setter方法可以获取和设置ImageHolder的状态,并达到相应的功能

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

注意，如果img标签中没有宽高的话onFix方法会在图片加载完成前后调用两次，可以通过imageReady来判断

### 注意

* gif图片播放在API12以下需要手动调用recycler，不然直到应用退出gif图片都会一直刷新
* 目前只能自动通过src的后缀识别是否为gif图，可以通过设置ImageFixCallback对某个特点的图片设置为GIF
，例如：`holder.setImageType(ImageHolder.GIF)`
* 解析Html文本时只支持Html.fromHtml能够解析的标签，自定义标签的支持后续会跟上的
* 在RecyclerView和ListView中使用Gif图目前存在bug,如需使用可以把Gif的autoPlay关闭

### 后续计划

* 添加自定义标签的支持

### 关于Markdown

Markdown源于子项目：[Markdown](https://github.com/zzhoujay/Markdown)

若在markdown解析过程中发现什么问题可以在该项目中反馈

### 具体使用请查看demo

[ListView Demo](https://github.com/zzhoujay/RichText/blob/master/app/src/main/java/zhou/demo/ListViewActivity.java)、
[RecyclerView Demo](https://github.com/zzhoujay/RichText/blob/master/app/src/main/java/zhou/demo/RecyclerViewActivity.java)、
[Gif Demo](https://github.com/zzhoujay/RichText/blob/master/app/src/main/java/zhou/demo/GifActivity.java)

_by zzhoujay_
