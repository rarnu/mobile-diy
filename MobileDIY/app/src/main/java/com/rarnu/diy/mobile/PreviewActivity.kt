@file:Suppress("DuplicatedCode")

package com.rarnu.diy.mobile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pakdata.xwalk.refactor.XWalkSettings
import com.pakdata.xwalk.refactor.XWalkView
import com.rarnu.diy.mobile.common.CardData
import com.rarnu.diy.mobile.databinding.ActivityPreviewBinding
import com.rarnu.diy.mobile.js.DIYJsBridge
import com.rarnu.diy.mobile.js.XUIClient
import com.rarnu.diy.mobile.util.globalFileUtil
import org.xwalk.core.XWalkInitializer

class PreviewActivity : AppCompatActivity(), XWalkInitializer.XWalkInitListener {

    companion object {
        private const val MENU_ID_SAVE = Menu.FIRST + 1
    }

    private lateinit var binding: ActivityPreviewBinding
    private lateinit var xView: XWalkView
    private lateinit var xInitializer: XWalkInitializer
    private lateinit var xSettings: XWalkSettings
    private lateinit var cardData: CardData
    private lateinit var bridge: DIYJsBridge

    private var scaled = false

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
        xInitializer = XWalkInitializer(this, this)
        xInitializer.initAsync()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initX() {
        xView.setUIClient(XUIClient(xView) {
            if (!scaled) {
                scaled = true
                val w = resources.displayMetrics.widthPixels / resources.displayMetrics.density
                bridge.setCardScaleByScreenWidth(w)
            }
        })
        with(xSettings) {
            javaScriptEnabled = true
            allowContentAccess = true
            allowFileAccess = true
            domStorageEnabled = true
        }
    }

    private fun initWebView() {
        bridge = DIYJsBridge(this, xView)
        bridge.onInitCardData = { cardData }
        bridge.onExportImage = { data -> globalFileUtil.base64ToImage(this, data, cardData.name) }

        xView.addJavascriptInterface(bridge, "diy")
        xView.loadUrl("${BASE_URL}/yugioh")
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

    override fun onXWalkInitStarted() {}

    override fun onXWalkInitCancelled() {}

    override fun onXWalkInitFailed() {}

    override fun onXWalkInitCompleted() {
        xView = XWalkView(this)
        xView.isHorizontalScrollBarEnabled = false
        xView.isVerticalScrollBarEnabled = false
        xView.enableSwipeRefresh(false)
        xSettings = xView.settings
        initX()
        xView.clearCache(true)
        binding.layPreview.addView(xView)
        initWebView()
    }
}