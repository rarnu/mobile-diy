package com.rarnu.diy.mobile.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

object ShareUtil {

    fun share(ctx: Context, file: File) {

        val inShare = Intent().apply {
            `package` = "com.tencent.mm"
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(ctx, ctx.applicationContext.packageName + ".fileprovider", file))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            ctx.startActivity(Intent.createChooser(inShare, "分享卡片数据"))
        } catch (th: Throwable) {
            Toast.makeText(ctx, "没有可以分享的应用", Toast.LENGTH_LONG).show()
        }
    }
}