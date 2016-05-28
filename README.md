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

![演示](http://git.oschina.net/uploads/images/2015/0721/172827_3339b62f_141009.png "演示")


### gradle中引用的方法

```
compile 'com.zzhoujay.richtext:richtext:1.1.0'
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
richText.fix(new ImageFixCallback() {
                @Override
                public void onFix(ImageHolder holder) {
                     f(holder.getImageType()==ImageHolder.GIF){
                          holder.setWidth(400);
                          holder.setHeight(400);
                     }else {
                          holder.setAutoFix(true);
                     }
                }
             })
```

通过设置`holder.setAutoFix(true)`设置该图片为自动修复

### 注意

* gif图片只能显示第一幁
* 只支持Html.fromHtml能够解析的标签，自定义标签的支持后续会跟上的

### 后续计划

* 添加自定义标签的支持
* 添加MarkDown语法的支持

### 具体使用请查看demo

_by zzhoujay_
