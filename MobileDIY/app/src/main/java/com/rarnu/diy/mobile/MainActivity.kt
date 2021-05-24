package com.rarnu.diy.mobile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import com.rarnu.diy.mobile.base.BaseActivity
import com.rarnu.diy.mobile.common.CardData
import com.rarnu.diy.mobile.databinding.ActivityMainBinding
import com.rarnu.diy.mobile.js.DIYJsBridge
import com.rarnu.diy.mobile.server.PreviewServer
import com.rarnu.diy.mobile.util.*
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseActivity() {

    companion object {
        private const val MENU_ID_PREVIEW = Menu.FIRST + 1
        private const val MENU_ID_SETTINGS = Menu.FIRST + 2
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var xView: WebView

    private lateinit var server: PreviewServer
    private var cardData = CardData()
    private lateinit var bridge: DIYJsBridge

    private var initCompleted = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ImageUtil.register(this)
        globalFileUtil = FileUtil(this)
        xView = binding.wvMain
        with(xView.settings) {
            javaScriptEnabled = true
            allowContentAccess = true
            allowFileAccess = true
            domStorageEnabled = true
        }
        initGlobal()
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
        xView.loadUrl("$BASE_URL/dataview")
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
}