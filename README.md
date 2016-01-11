# RichText

> Android平台下的富文本显示TextView

* 支持Html格式文本
* 支持图片点击事件
* 链接自动回调
* 支持设置加载中和加载错误时的图片
* 支持自定义超链接的点击回调
* 加入了修正图片宽高的方法（感谢[foolchen](https://github.com/foolchen)）

### 加入了自定义超链接点击回调

```
text.setOnURLClickListener(new RichText.OnURLClickListener() {
   @Override
   public boolean urlClicked(String url) {
        Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
        return true;// 返回true表示已处理，false会继续调用默认得处理方法
   }
});
```
注意此方法需在setRichText方法之前调用

### 修正图片宽高的方法

```
text.setImageFixListener(new RichText.ImageFixListener() {
     @Override
     public void onFix(RichText.ImageHolder holder) {
           if (holder.getWidth() > 100 || holder.getHeight() > 100) {
                int width = getScreenWidth(getApplicationContext());
                int height = (int) (holder.getHeight() * 1f * width / holder.getWidth()) - 300;
                holder.setWidth(width);
                holder.setHeight(height);
                holder.setScaleType(RichText.ImageHolder.CENTER_INSIDE);
           }
     }
});
```
也需要在setRichText方法之前调用

### gradle中引用的方法

```
compile 'zhou.widget:richtext:1.0.4'
```

### 具体使用请查看demo

![演示](http://git.oschina.net/uploads/images/2015/0721/172827_3339b62f_141009.png "演示")

_by zzhoujay_
