# RichText [ ![Download](https://api.bintray.com/packages/zzhoujay/maven/richtext/images/download.svg) ](https://bintray.com/zzhoujay/maven/richtext/_latestVersion)

### 注意：此项目已不再维护

> Android平台下的富文本解析器

* 流式操作
* 低侵入性
* 依赖少，只依赖了`disklrucache`和`support v4`
* 支持Html和Markdown格式文本
* 支持图片点击和长按事件
* 链接点击事件和长按事件
* 支持设置加载中和加载错误时的图片
* 支持自定义超链接的点击回调
* 支持修正图片宽高
* 支持GIF图片
* 支持Base64编码、本地图片和Assets目录图片
* 自持自定义图片加载器、图片加载器
* 支持内存和磁盘双缓存
* 已经加入对自定义Html解析器的支持

### 效果

![演示](image/image.jpg "演示")


### gradle中引用的方法

```
compile 'com.zzhoujay.richtext:richtext:latest-version'
```

### 使用新的Html解析器

只需加入此依赖即可，无须其他操作，新Html解析器对原生Html解析器的功能做了补充
```
compile 'com.zzhoujay:html:latest-version'
```

新Html解析器增加了对代码块的支持，代码块可以触发点击事件，通过`urlClick`设置，
代码块回调的参数由`code://`开头

使用新Html解析器遇到问题请在[https://github.com/zzhoujay/Htm](https://github.com/zzhoujay/Html)提issue

### 关于issue

最近一段时间会比较忙，issue不能及时处理，一般会定时抽空集中解决issue，但时间有限解决速度上不敢保证。

欢迎提交pull request帮助完善这个项目

### 注意

在第一次调用RichText之前先调用`RichText.initCacheDir()`方法设置缓存目录

ImageFixCallback的回调方法不一定是在主线程回调，注意不要进行UI操作

本地图片由根路径`\`开头，Assets目录图片由`file:///android_asset/`开头

Gif图片播放不支持硬件加速，若要使用Gif图片请先关闭TextView的硬件加速
```
textView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
```

### 使用方式

[多看wiki](https://github.com/zzhoujay/RichText/wiki)、[多看wiki](https://github.com/zzhoujay/RichText/wiki)、[多看wiki](https://github.com/zzhoujay/RichText/wiki)，重要的事情说三遍

### 关于自定义的Html解析器

Html解析器子项目：[Html](https://github.com/zzhoujay/Htm)

### 关于Markdown

Markdown源于子项目：[Markdown](https://github.com/zzhoujay/Markdown)

若在markdown解析过程中发现什么问题可以在该项目中反馈

### 关于富文本编辑器

编辑器开发已暂停，[RichEditor](https://github.com/zzhoujay/RichEditor)

### 具体使用请查看demo

[ListView Demo](https://github.com/zzhoujay/RichText/blob/master/app/src/main/java/zhou/demo/ListViewActivity.java)、
[RecyclerView Demo](https://github.com/zzhoujay/RichText/blob/master/app/src/main/java/zhou/demo/RecyclerViewActivity.java)、
[Gif Demo](https://github.com/zzhoujay/RichText/blob/master/app/src/main/java/zhou/demo/GifActivity.java)


### License

```
The MIT License (MIT)

Copyright (c) 2016 zzhoujay

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR

IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

_by zzhoujay_
