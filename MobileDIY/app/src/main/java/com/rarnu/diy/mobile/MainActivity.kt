package com.rarnu.diy.mobile

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pakdata.xwalk.refactor.XWalkSettings
import com.pakdata.xwalk.refactor.XWalkView
import com.rarnu.diy.mobile.common.CardData
import com.rarnu.diy.mobile.databinding.ActivityMainBinding
import com.rarnu.diy.mobile.js.DIYJsBridge
import com.rarnu.diy.mobile.js.XUIClient
import com.rarnu.diy.mobile.server.PreviewServer
import com.rarnu.diy.mobile.util.FileUtil
import com.rarnu.diy.mobile.util.ShareUtil
import com.rarnu.diy.mobile.util.globalFileUtil
import org.json.JSONObject
import org.xwalk.core.XWalkInitializer
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), XWalkInitializer.XWalkInitListener {

    companion object {
        private const val MENU_ID_PREVIEW = Menu.FIRST + 1
        private const val MENU_ID_SETTINGS = Menu.FIRST + 2
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var xView: XWalkView
    private lateinit var xInitializer: XWalkInitializer
    private lateinit var xSettings: XWalkSettings

    private lateinit var server: PreviewServer
    private var cardData = CardData()
    private lateinit var bridge: DIYJsBridge

    private var initCompleted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ImageUtil.register(this)
        globalFileUtil = FileUtil(this)
        xInitializer = XWalkInitializer(this, this)
        xInitializer.initAsync()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initX() {
        xView.setUIClient(XUIClient(xView) { })

        with(xSettings) {
            setAppCacheEnabled(true)
            javaScriptEnabled = true
            allowContentAccess = true
            allowFileAccess = true
            domStorageEnabled = true
        }
    }

    private fun initGlobal() {
        globalFileUtil.prepareServerFiles(this, { current, total ->
            binding.pLoading.max = total
            binding.pLoading.progress = current
        }) { done ->
            if (!done) {
                finish()
                return@prepareServerFiles
            }
            cardData.image = globalFileUtil.rawImageToBase64(this, R.raw.blueeyes)
            binding.layLoading.visibility = View.GONE
            /* 启动服务 */
            startServer()
            initWebView()
            initCompleted = true
        }
    }

    private fun initWebView() {
        bridge = DIYJsBridge(this, xView)
        bridge.onInitCardData = { cardData }
        bridge.onOpenJsonFile = {
            ImageUtil.openJsonFile { path ->
                val jsonstr = File(path).readText()
                val card = CardData.fromJson(jsonstr) ?: return@openJsonFile
                bridge.assignCardData(card)
            }
        }
        bridge.onSaveJsonFile = { data ->
            val cardName = JSONObject(data).getString("name").replace("\\[.*?\\(.*?\\)]".toRegex()) { s ->
                s.value.replace("[", "").run { substring(0, indexOf("(")) }
            }
            val destFile = File(globalFileUtil.cardFilePath, "${cardName}_${SimpleDateFormat("yyyyMMdd_hhmmss", Locale.getDefault()).format(Date())}.json")
            destFile.writeText(data)
            Toast.makeText(this, R.string.str_json_saved, Toast.LENGTH_LONG).show()
        }
        bridge.onOpenShareCard = { cardName ->
            val file = globalFileUtil.cardFilePath.listFiles()?.firstOrNull { it.name.startsWith(cardName) }
            if (file != null) {
                ShareUtil.share(this, file)
            } else {
                Toast.makeText(this, R.string.str_not_saved, Toast.LENGTH_LONG).show()
            }
        }
        bridge.onEditTextKana = {
            startActivity(Intent(this, EditKanaActivity::class.java))
        }
        xView.addJavascriptInterface(bridge, "diy")
        xView.loadUrl("${BASE_URL}/dataview")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, MENU_ID_PREVIEW, 1, R.string.menu_preview).apply {
            setIcon(R.drawable.outline_preview_24)
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        menu.add(0, MENU_ID_SETTINGS, 0, R.string.menu_settings).apply {
            setIcon(R.drawable.outline_settings_suggest_24)
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (!initCompleted) return true
        when (item.itemId) {
            MENU_ID_PREVIEW -> {
                // getFormData
                bridge.getFormData {
                    cardData = it
                    CardDataHolder.setCard(cardData)
                    startActivity(Intent(this, PreviewActivity::class.java))
                }
            }
            MENU_ID_SETTINGS -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        stopServer()
        super.onDestroy()
    }

    private fun startServer() {
        if (!this::server.isInitialized) {
            server = PreviewServer(this, 8010)
        }
        if (!server.isAlive) {
            server.start()
        }
    }

    private fun stopServer() {
        if (this::server.isInitialized) {
            server.stop()
        }
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
        binding.layWebview.addView(xView)
        initGlobal()
    }
}