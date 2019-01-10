package zhou.parse


import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.text.method.LinkMovementMethod
import android.widget.TextView
import com.qmuiteam.qmui.widget.textview.QMUISpanTouchFixTextView
import com.zzhoujay.richtext.LinkHolder
import com.zzhoujay.richtext.RichText
import com.zzhoujay.richtext.callback.*
import com.zzhoujay.richtext.parser.CommonParser
import zhou.demo.R


/**
 * 设置富文本内容，可以解析图片、表情、"作者"标签、@用户链接
 * @param textView      TextView控件
 * @param string        富文本字符串
 * @param imageUrlPrefix    图片URL前缀(看项目是否需要)
 * @param viewClick     点击回调
 */
fun setRichText(textView: TextView?, string: String, imageUrlPrefix: String?, viewClick: ViewClick?) {
    textView?.apply {
        RichText.fromHtml(string)
                .imageClick(genImageClickListener(imageUrlPrefix, viewClick))
                .linkFix(genLinkFixCallback(this.context))
                .urlClick(genUrlClickListener(viewClick))
                .urlLongClick(genUrlLongClickListener(viewClick))
                .into(this, genEmotionGetterImpl(context), true)
    }
}

/**
 * 设置简单的富文本内容，只会解析表情、"作者"标签、@用户链接，没有解析图片
 * @param textView      TextView控件
 * @param string        富文本字符串
 * @param parseAuthor   是否需要解析"作者"标签
 * @param viewClick     点击回调
 */
fun setSimpleRichText(textView: TextView?, string: String?, parseAuthor: Boolean, viewClick: ViewClick?) {
    textView?.apply {
        val ssb = CommonParser.parseString(context, string, textSize, parseAuthor,
                genEmotionGetterImpl(context), genLinkFixCallback(context), genUrlClickListener(viewClick),
                genUrlLongClickListener(viewClick))
        if (this is QMUISpanTouchFixTextView) {
            setMovementMethodDefault()
            setNeedForceEventToParent(true)
        } else {
            movementMethod = LinkMovementMethod.getInstance()
        }
        textView.text = ssb
    }
}

/**
 * 生成获取表情接口的实例
 */
fun genEmotionGetterImpl(context: Context): EmotionGetter {
    return EmotionGetterImpl(context)
}

private class EmotionGetterImpl(val context: Context) : EmotionGetter {

    override fun getDrawable(emotionKey: String): Drawable {
        context.apply {
            //这里模拟获取表情Drawable，项目自己做Cache吧
            return ContextCompat.getDrawable(this, R.drawable.qq6)!!
        }
    }
}


/**
 * 生成点击图片监听器
 */
fun genImageClickListener(imageUrlPrefix: String?, viewClick: ViewClick?): OnImageClickListener {
    return ImageClickListener(imageUrlPrefix, viewClick)
}

class ImageClickListener(private val imageUrlPrefix: String?, val viewClick: ViewClick?) : OnImageClickListener {

    override fun imageClicked(imageUrls: List<String>, position: Int) {
        viewClick?.apply {
            if (imageUrls.isEmpty()) {
                return
            }
            ItemClick(imageUrls, TAG_CLICK_IMAGE, position.toString(), null)
        }
    }
}

/**
 * 生成链接文字调整实例
 */
fun genLinkFixCallback(context: Context?): LinkFixCallback {
    return PrimaryLinkFixCallback(context)
}

class PrimaryLinkFixCallback(val context: Context?) : LinkFixCallback {

    override fun fix(holder: LinkHolder) {
        //这里可以自定义链接文字的样式
        holder.isUnderLine = false
        context?.apply {
            holder.normalTextColor = ContextCompat.getColor(this, R.color.normal_text_color)
            holder.pressedTextColor = ContextCompat.getColor(this, R.color.pressed_text_color)
            holder.normalBackgroundColor = ContextCompat.getColor(this, R.color.normal_bg_color)
            holder.pressedBackgroundColor = ContextCompat.getColor(this, R.color.pressed_bg_color)
        }
    }
}

/**
 * 生成点击链接监听器
 */
fun genUrlClickListener(viewClick: ViewClick?): OnUrlClickListener {
    return UrlClickListener(viewClick)
}

class UrlClickListener(val viewClick: ViewClick?) : OnUrlClickListener {

    override fun urlClicked(url: String): Boolean {
        viewClick?.apply {
            if (!url.isEmpty()) {
                val userId = parseInt(url)
                if (userId != 0) {
                    ItemClick(url, TAG_CLICK_URL, userId.toString(), null)
                    return true
                }
            }
        }
        return false
    }
}

/**
 * 生成长按链接监听器
 */
fun genUrlLongClickListener(viewClick: ViewClick?): OnUrlLongClickListener {
    return UrlLongClickListener(viewClick)
}

class UrlLongClickListener(val viewClick: ViewClick?) : OnUrlLongClickListener {

    override fun urlLongClick(url: String): Boolean {
        viewClick?.apply {
            if (!url.isEmpty()) {
                val userId = parseInt(url)
                if (userId != 0) {
                    ItemClick(url, TAG_LONG_CLICK_URL, userId.toString(), null)
                    return true
                }
            }
        }
        return false
    }
}

fun parseInt(s: String?): Int {
    var i = 0
    if (s == null) return i
    try {
        i = Integer.parseInt(s.trim { it <= ' ' })
    } catch (e: Exception) {
        return 0
    }

    return i
}
