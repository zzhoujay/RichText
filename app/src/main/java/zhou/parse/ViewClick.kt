package zhou.parse

import android.view.View

interface ViewClick {

    fun ItemClick(item: Any, clickTag: String?, paras: String?, view: View?)


}

const val TAG_CLICK_URL = "clickUrl"
const val TAG_LONG_CLICK_URL = "longClickUrl"
const val TAG_CLICK_IMAGE = "clickImage"
