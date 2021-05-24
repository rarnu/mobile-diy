package com.rarnu.diy.mobile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.rarnu.diy.mobile.base.BaseActivity
import com.rarnu.diy.mobile.databinding.ActivitySettingsBinding
import com.rarnu.diy.mobile.util.globalFileUtil

class SettingsActivity: BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnReinstall.setOnClickListener {
            confirmReinstall()
        }
        binding.tvMyGithub.setOnClickListener {
            openBrowser("https://github.com/rarnu/mobile-diy")
        }
        binding.tvToolsVue.setOnClickListener {
            openBrowser("https://github.com/kooriookami/tools-vue")
        }
        binding.tvXwalkView.setOnClickListener {
            openBrowser("https://github.com/ks32/CrosswalkNative")
        }

    }

    private fun confirmReinstall() {
        AlertDialog.Builder(this)
            .setTitle(R.string.str_confirm)
            .setMessage(R.string.str_confirm_reinstall)
            .setPositiveButton(R.string.str_ok) { _, _ ->
                globalFileUtil.removeServerFile()
            }
            .setNegativeButton(R.string.str_cancel, null)
            .show()
    }

    private fun openBrowser(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}