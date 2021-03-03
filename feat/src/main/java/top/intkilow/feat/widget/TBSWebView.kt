package top.intkilow.feat.widget

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.tencent.smtt.export.external.interfaces.WebResourceError
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import org.json.JSONException
import org.json.JSONObject
import top.intkilow.architecture.utils.GradientDrawableUtils
import top.intkilow.architecture.utils.ViewUtils
import top.intkilow.feat.R
import java.lang.ref.WeakReference
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class TBSWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    private var mProgressBar: ProgressBar? = null
    private var mTBSWebViewCallback: TBSWebViewCallback? = null
    private val mTBSWebViewImages: LinkedList<String> = LinkedList()
    private var mRect: Rect? = null
    private var mCoverView: View? = null
    private lateinit var mContextWeakReference: WeakReference<Context>
    private var mTBSContentHeight = 0
    private var mMinHeight = 0
    private var mCollapsing = false // 是否展开

    private var mFixViewHeight = true
    private var mFixImageScreenWidth = true // 默认打开修复图片

    private var mCollapsingLayout: LinearLayout? = null
    private val mDownStr = "查看更多"
    private var mUpStr: String = "收起"
    private val mDownDrawable: Int = R.drawable.feat_down
    private var mUpDrawable: Int = R.drawable.feat_up

    init {
        init()
    }

    private fun init() {
        mContextWeakReference = WeakReference(context)
        val webSettings: WebSettings = settings

        webSettings.javaScriptEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.useWideViewPort = true //关键点
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webSettings.allowFileAccess = true // 允许访问文件
        webSettings.builtInZoomControls = false // 设置显示缩放按钮
        webSettings.setSupportZoom(false) // 支持缩放
        webSettings.loadWithOverviewMode = true
        addJavascriptInterface(Android(), "android")
        webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(
                webView: WebView?,
                newProgress: Int
            ) {
                super.onProgressChanged(webView, newProgress)
                if (null != mProgressBar) {
                    mProgressBar!!.progress = newProgress
                }
            }

            override fun onReceivedTitle(
                webView: WebView?,
                title: String
            ) {
                super.onReceivedTitle(webView, title)
                if (null != mTBSWebViewCallback) {
                    var result = "结果"
                    if (title != "about:blank") {
                        result = title
                    }
                    mTBSWebViewCallback!!.onReceivedTitle(result)
                }
            }
        }
        webViewClient = object : WebViewClient() {
            override fun onPageStarted(
                webView: WebView?,
                s: String?,
                bitmap: Bitmap?
            ) {
                super.onPageStarted(webView, s, bitmap)
                if (null != mProgressBar) {
                    mProgressBar!!.visibility = View.VISIBLE
                }
                if (null != mTBSWebViewCallback) {
                    mTBSWebViewCallback!!.onPageStarted()
                }
            }

            override fun onPageFinished(
                webView: WebView,
                s: String?
            ) {
                super.onPageFinished(webView, s)
                if (mFixImageScreenWidth) {
                    webView.loadUrl("javascript:imgAutoFit(document.body.clientWidth)")
                }
                if (mFixViewHeight) {
                    webView.loadUrl("javascript:android.setHeight(document.body.scrollHeight)")
                }
                val context = mContextWeakReference.get()
                if (null != context) {
                    (context as Activity).runOnUiThread {
                        if (null != mProgressBar) {
                            mProgressBar!!.visibility = View.GONE
                        }
                    }
                }
                if (null != mTBSWebViewCallback) {
                    mTBSWebViewCallback!!.onPageFinished()
                }
            }

            // 新版本，只会在Android6及以上调用
            @TargetApi(Build.VERSION_CODES.M)
            override fun onReceivedError(
                webView: WebView?,
                webResourceRequest: WebResourceRequest?,
                webResourceError: WebResourceError?
            ) {
                super.onReceivedError(webView, webResourceRequest, webResourceError)
                if (null != mProgressBar) {
                    mProgressBar!!.visibility = View.VISIBLE
                }
                if (null != mTBSWebViewCallback) {
                    mTBSWebViewCallback!!.onReceivedError(Exception("Error"))
                }
            }

            /**
             * App里面使用webview控件的时候遇到了诸如404这类的错误的时候，若也显示浏览器里面的那种错误提示页面就显得
             * 很丑陋了，那么这个时候我们的app就需要加载一个本地的错误提示页面，即webview如何加载一个本地的页面
             * 步骤1：写一个html文件（error_handle.html），用于出错时展示给用户看的提示页面
             * 步骤2：将该html文件放置到代码根目录的assets文件夹下
             * 步骤3：复写WebViewClient的onRecievedError方法 //该方法传回了错误码，根据错误类型可以进行不同的错误分类处理
             */
            override fun onReceivedError(
                webView: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                super.onReceivedError(webView, errorCode, description, failingUrl)
                if (null != mProgressBar) {
                    mProgressBar!!.visibility = View.VISIBLE
                }
                if (null != mTBSWebViewCallback) {
                    mTBSWebViewCallback!!.onReceivedError(Exception("Error"))
                }
            }

            override fun onReceivedHttpError(
                webView: WebView?,
                webResourceRequest: WebResourceRequest?,
                webResourceResponse: WebResourceResponse?
            ) {
                super.onReceivedHttpError(webView, webResourceRequest, webResourceResponse)
                if (null != mProgressBar) {
                    mProgressBar!!.visibility = View.VISIBLE
                }
                if (null != mTBSWebViewCallback) {
                    mTBSWebViewCallback!!.onReceivedError(Exception("HttpError"))
                }
            }

            override fun shouldOverrideUrlLoading(
                webView: WebView,
                url: String
            ): Boolean {
                if (TextUtils.isEmpty(url)) {
                    return false
                }
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    // 此处需要产品确认是否尝试跳转到第三方的app，默认是不跳转，在自己的app里处理
                    //                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    //                startActivity(intent);
                    return true
                }
                webView.loadUrl(url)
                return true
            }
        }
    }


    fun loadRichText(content: String): TBSWebView? {
        loadDataWithBaseURL(
            null, getHtmlData(content), "text/html", "UTF-8",
            null
        )
        return this
    }

    fun enableFixViewHeight(): TBSWebView? {
        return enableFixViewHeight(true)
    }

    fun enableFixViewHeight(enable: Boolean): TBSWebView? {
        mFixViewHeight = enable
        return this
    }

    fun enableFixImageScreenW(): TBSWebView? {
        return enableFixImageScreenW(true)
    }

    fun enableFixImageScreenW(enable: Boolean): TBSWebView {
        mFixImageScreenWidth = enable
        return this
    }

    fun enableProgress(): TBSWebView {
        if (null == mProgressBar) {
            mProgressBar =
                ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal)
            mProgressBar!!.max = 100
            val bgDrawable = GradientDrawableUtils.getDrawable(
                Color.WHITE,
                GradientDrawable.RECTANGLE,
                0f
            )
            val progressDrawable = GradientDrawableUtils.getDrawable(
                ViewUtils.colorPrimary,
                GradientDrawable.RECTANGLE,
                0f
            )
            val layers = arrayOfNulls<Drawable>(2)
            layers[0] = bgDrawable
            layers[1] = ClipDrawable(progressDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL)
            val newDrawable = LayerDrawable(layers)




            mProgressBar!!.progressDrawable = newDrawable
            val layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(1))
            mProgressBar!!.layoutParams = layoutParams
            addView(mProgressBar, childCount)
        }
        mProgressBar!!.progress = 0
        return this
    }

    fun enableCollapsing(h: Int): TBSWebView? {
        return enableCollapsing(h, true)
    }

    fun enableCollapsing(h: Int, collapsing: Boolean): TBSWebView? {

        mCollapsing = collapsing
        mMinHeight = h
        return this
    }

    fun enableScroll(enable: Boolean): TBSWebView? {
        if (null == mCoverView) {
            return this
        }
        if (enable) {
            mCoverView!!.visibility = View.GONE
        } else {
            mCoverView!!.visibility = View.VISIBLE
        }
        return this
    }


    fun setTBSWebViewCallback(tbsWebViewCallback: TBSWebViewCallback): TBSWebView {
        mTBSWebViewCallback = tbsWebViewCallback
        return this
    }


    fun getTBSWebViewCallback(): TBSWebViewCallback? {
        return mTBSWebViewCallback
    }

    /**
     * 展开收起
     */
    private fun collapsing() {
        if (null == mCollapsingLayout) {
            return
        }
        val img: ImageView = mCollapsingLayout!!.getChildAt(0) as ImageView
        val text = mCollapsingLayout!!.getChildAt(1) as TextView
        val drawable: Int
        val str: String
        enableScroll(!mCollapsing)
        if (mCollapsing) {
            drawable = mDownDrawable
            str = mDownStr
        } else {
            drawable = mUpDrawable
            str = mUpStr
        }
        if (null != mTBSWebViewCallback) {
            mTBSWebViewCallback!!.collapsing(mCollapsing, mCollapsingLayout)
        }
        img.setImageResource(drawable)
        text.text = str
        val layoutParams = layoutParams
        layoutParams.height = if (mCollapsing) mMinHeight else mTBSContentHeight
        setLayoutParams(layoutParams)
    }

    /**
     * 设置高度
     *
     * @param height
     */
    private fun setWebHeight(height: Int) {
//        Log.e("weblib", "setHeight:" + height);
        mTBSContentHeight = height

        val context = mContextWeakReference.get()
        if (null != context) {
            (context as Activity).runOnUiThread {
                if (null == mCoverView) {
                    mCoverView = View(getContext())
                    mCoverView!!.visibility = View.GONE
                    mCoverView!!.setOnClickListener { }
                    addView(mCoverView, childCount)
                }


                // 渲染是否折叠
                if (mMinHeight > 0 && mMinHeight < mTBSContentHeight) {
                    mTBSContentHeight = mTBSContentHeight + dpToPx(48)
                    if (null == mCollapsingLayout) {
                        mCollapsingLayout = LinearLayout(getContext())
                        mCollapsingLayout!!.orientation = LinearLayout.HORIZONTAL
                        mCollapsingLayout!!.gravity = Gravity.CENTER
                        val imageView = ImageView(getContext())
                        imageView.setImageResource(R.drawable.feat_down)
                        val wh = dpToPx(24)
                        imageView.layoutParams = LinearLayout.LayoutParams(wh, wh)
                        mCollapsingLayout!!.addView(imageView)
                        val textView = TextView(getContext())
                        textView.setTextColor(-0x707071)
                        textView.textSize = 14f
                        mCollapsingLayout!!.addView(textView)
                        val l = LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            dpToPx(48)
                        )
                        l.gravity = Gravity.BOTTOM or Gravity.CENTER
                        mCollapsingLayout!!.layoutParams = l
                        mCollapsingLayout!!.setOnClickListener {
                            mCollapsing = !mCollapsing
                            collapsing()
                        }
                        mCollapsingLayout!!.setBackgroundColor(Color.WHITE)
                        addView(mCollapsingLayout, childCount)
                    }
                    // 设置当前状态
                    collapsing()
                } else {
                    val layoutParams = layoutParams
                    layoutParams.height = mTBSContentHeight
                    setLayoutParams(layoutParams)
                }
            }
        }
    }

    fun fixImageClick(url: String): String {
        val pattern: Pattern = Pattern.compile("(<img[^>]*/>)")
        val m: Matcher = pattern.matcher(url)
        val positionEndList: LinkedList<Int> = LinkedList()
        while (m.find()) {
            val group: String = m.group()
            val end: Int = m.end()
            positionEndList.add(end)
            var imgSrc = ""
            val imgSrcMatcher: Matcher =
                Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(group)
            while (imgSrcMatcher.find()) {
                imgSrc = imgSrcMatcher.group(1)
            }

            // 没有判断null情况
            mTBSWebViewImages.add(imgSrc)
        }
        var afterURL: StringBuffer? = null
        if (positionEndList.size > 0) {
            var index = 0
            var currentInsertLen = 0
            afterURL = StringBuffer(url)
            for (integer in positionEndList) {
                val rect =
                    "JSON.stringify(document.getElementById('img$index').getBoundingClientRect())"
                val insertStr =
                    "id=\"img$index\" onclick=\"javascript:image.click($index,$rect)\""
                afterURL.insert(integer + currentInsertLen - 2, insertStr)
                index += 1
                currentInsertLen += insertStr.length
            }
        }
        addJavascriptInterface(ImageClick(), "image")
        return afterURL?.toString() ?: url
    }

    fun onDestroy() {
        mTBSWebViewImages.clear()
        mRect = null
        loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
        clearHistory()
        removeAllViews()
        destroy()
    }

    fun getHtmlData(bodyHTML: String): String? {
        val head = ("<head>"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> "
                + "<style>img{max-width: 100%; width:auto; height:auto;}</style>"
                + "<script>"
                + "function imgAutoFit(width){ "
                + "var imgs = document.getElementsByTagName('img');"
                + "for (var i = 0; i < imgs.length; i++) {"
                + "var img = imgs[i]; "
                + "img.style.maxWidth = width;"
                + "img.style.height = 'auto';"
                + "}"
                + "}"
                + "</script>"
                + "</head>")
        return "<html>$head<body style='margin:0;'>$bodyHTML</body></html>"
    }

    private fun dpToPx(dp: Int): Int {
        return mContextWeakReference.get()?.let {
            ViewUtils.dp2px(it, dp * 1.0f)
        } ?: 0
    }

    /**
     * 回调
     */
    interface TBSWebViewCallback {
        fun onPageStarted()
        fun onPageFinished()
        fun collapsing(
            collapsing: Boolean,
            collapsingLayout: LinearLayout?
        )

        fun onImageClick(p: Int, imgs: LinkedList<String>, rect: Rect)
        fun onReceivedTitle(title: String?)
        fun onReceivedError(e: Exception?)
    }


    inner class ImageClick {
        @JavascriptInterface
        fun click(position: Int, obj: String) {
            if (null == mRect) {
                mRect = Rect()
                getGlobalVisibleRect(mRect)
            }
            var rect: Rect? = null
            try {
                val jsonObject = JSONObject(obj)
                rect = Rect()
                rect.top = jsonObject.getInt("top") + mRect!!.top
                rect.right = jsonObject.getInt("right")
                rect.bottom = jsonObject.getInt("bottom") + mRect!!.top
                rect.left = jsonObject.getInt("left")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            mTBSWebViewCallback?.let {
                val context = mContextWeakReference.get()
                if (null != context && null != rect) {
                    (context as Activity).runOnUiThread {
                        it.onImageClick(
                            position,
                            mTBSWebViewImages,
                            rect
                        )
                    }
                }
            }
        }
    }

    inner class Android {
        @JavascriptInterface
        fun setHeight(height: Int) {
            mContextWeakReference.get()?.let {
                setWebHeight(ViewUtils.dp2px(it, height * 1.0f))
            }
        }
    }


}