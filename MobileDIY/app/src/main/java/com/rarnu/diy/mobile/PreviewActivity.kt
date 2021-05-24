@file:Suppress("DuplicatedCode")

package com.rarnu.diy.mobile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.rarnu.diy.mobile.base.BaseActivity
import com.rarnu.diy.mobile.common.CardData
import com.rarnu.diy.mobile.databinding.ActivityPreviewBinding
import com.rarnu.diy.mobile.js.DIYJsBridge
import com.rarnu.diy.mobile.util.BASE_URL
import com.rarnu.diy.mobile.util.CardDataHolder
import com.rarnu.diy.mobile.util.globalFileUtil
import com.rarnu.diy.mobile.util.removeKana

class PreviewActivity : BaseActivity() {

    companion object {
        private const val MENU_ID_SAVE = Menu.FIRST + 1
    }

    private lateinit var binding: ActivityPreviewBinding
    private lateinit var xView: WebView
    private lateinit var cardData: CardData
    private lateinit var bridge: DIYJsBridge

    private var scaleValue = 1.0

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val c = CardDataHolder.getCard()
        if (c == null) {
            finish()
            return
        }
        cardData = c
        scaleValue = resources.displayMetrics.widthPixels / resources.displayMetrics.density / 1393.0

        xView = binding.wvPreview
        with(xView.settings) {
            javaScriptEnabled = true
            allowContentAccess = true
            allowFileAccess = true
            domStorageEnabled = true
        }
        initWebView()
    }

    private fun initWebView() {
        bridge = DIYJsBridge(this, xView)
        bridge.onInitCardData = { cardData }
        bridge.onExportImage = { data -> globalFileUtil.base64ToImage(this, data, cardData.name.removeKana()) }
        xView.addJavascriptInterface(bridge, "diy")
        xView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                bridge.setCardScale(scaleValue)
                super.onPageFinished(view, url)
            }
        }
        xView.loadUrl("$BASE_URL/yugioh")
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, MENU_ID_SAVE, 0, R.string.menu_save).apply {
            setIcon(R.drawable.outline_save_alt_24)
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            MENU_ID_SAVE -> {
                Toast.makeText(this, R.string.str_exporting, Toast.LENGTH_LONG).show()
                bridge.exportImage()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}