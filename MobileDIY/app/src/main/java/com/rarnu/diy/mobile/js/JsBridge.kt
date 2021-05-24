package com.rarnu.diy.mobile.js

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.rarnu.diy.mobile.ImageUtil
import com.rarnu.diy.mobile.common.CardData
import com.rarnu.diy.mobile.util.runOnMainThread

class DIYJsBridge(private val ctx: Context, private val webview: WebView) {

    var onInitCardData: () -> CardData = { CardData() }
    var onExportImage: (data: String) -> Unit = { }
    var onOpenJsonFile: () -> Unit = { }
    var onSaveJsonFile: (data: String) -> Unit = { }
    var onOpenShareCard: (cardName: String) -> Unit = { }
    var onEditTextKana: () -> Unit = { }

    /* exportImage 回调此函数 */
    @JavascriptInterface
    fun receiveImageData(data: String) {
        runOnMainThread { onExportImage(data) }
    }

    /* 进入卡片时回调此函数 */
    @JavascriptInterface
    fun initCard() {
        assignCardData(onInitCardData())
    }

    @JavascriptInterface
    fun openJsonFile() {
        runOnMainThread {
            onOpenJsonFile()
        }
    }

    @JavascriptInterface
    fun saveJsonFile(data: String) {
        runOnMainThread {
            onSaveJsonFile(data)
        }
    }

    /* 从相册选卡图 */
    @JavascriptInterface
    fun uploadImage() {
        runOnMainThread {
            ImageUtil.getImageFromAlbum {
                setCardImage(it)
            }
        }
    }

    /* 拍照作为卡图 */
    @JavascriptInterface
    fun takePhotoImage() {
        runOnMainThread {
            ImageUtil.getImageFromCamera {
                setCardImage(it)
            }
        }
    }

    /* 打开分享页 */
    @JavascriptInterface
    fun openShareCard(cardName: String) {
        runOnMainThread { onOpenShareCard(cardName) }
    }

    /* 注音文本编辑 */
    @JavascriptInterface
    fun editTextKana() {
        runOnMainThread { onEditTextKana() }
    }

    /**********************************/
    /** JS 方法                        */
    /**********************************/
    fun setCardScaleByScreenWidth(w: Float) {
        webview.evaluateJavascript("setScale($w)") { }
    }

    fun setCardScale(scale: Double) {
        webview.evaluateJavascript("setScaleEx($scale)") { }
    }

    fun assignCardData(data: CardData) {
        runOnMainThread {
            val obj = data.toJsObj()
            webview.evaluateJavascript("assignCardData(${obj})") { }
        }
    }

    fun setCardImage(data: String) {
        runOnMainThread {
            webview.evaluateJavascript("setImage('$data')") { }
        }
    }

    /**
     * 导出卡图，这个方法将回调 receiveImageData
     */
    fun exportImage() {
        webview.evaluateJavascript("exportImage()") { }
    }

    /* 获取页面上编辑着的卡片信息 */
    fun getFormData(callback: (data: CardData) -> Unit) {
        webview.evaluateJavascript("getFormData()") {
            val c = CardData.fromJson(it) ?: return@evaluateJavascript
            callback(c)
        }
    }
}