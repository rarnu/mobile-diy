package com.rarnu.diy.mobile

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.isyscore.kotlin.common.HttpMethod
import com.isyscore.kotlin.common.http
import com.isyscore.kotlin.common.toJson
import com.isyscore.kotlin.common.toObj
import com.rarnu.diy.mobile.base.BaseActivity
import com.rarnu.diy.mobile.databinding.ActivityEditkanaBinding
import com.rarnu.diy.mobile.util.runOnMainThread
import kotlin.concurrent.thread

class EditKanaActivity: BaseActivity() {

    companion object {
        private const val MENU_ID_KANA_CARD = Menu.FIRST + 1
        private const val MENU_ID_KANA_TEXT = Menu.FIRST + 2
    }

    private lateinit var binding: ActivityEditkanaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditkanaBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, MENU_ID_KANA_CARD, 0, R.string.menu_kana_card).apply {
            setIcon(R.drawable.outline_post_add_24)
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        menu.add(0, MENU_ID_KANA_TEXT, 0, R.string.menu_kana_text).apply {
            setIcon(R.drawable.outline_textsms_24)
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            MENU_ID_KANA_CARD -> doKanjiKana(false)
            MENU_ID_KANA_TEXT -> doKanjiKana(true)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun doKanjiKana(isNormal: Boolean) {
        val txt = binding.edtText.text.toString().trim()
        if (txt == "") return

        thread {
            val jsonret = http {
                url = "http://rarnu.xyz:9987/kk/${if (isNormal) "normal" else "effect"}"
                method = HttpMethod.POST
                mimeType = "application/json"
                data = ReqCardName(txt).toJson()
            } ?: """{"found":false, "kk":""}"""
            val o = jsonret.toObj<RespCardName>()
            runOnMainThread {
                if (o.found) {
                    binding.edtKana.setText(o.kk)
                } else {
                    binding.edtKana.setText(R.string.str_no_kk_found)
                }
            }
        }
    }

    data class ReqCardName(val name: String)
    data class RespCardName(val found: Boolean, val kk: String)
}