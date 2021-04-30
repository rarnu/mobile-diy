package com.rarnu.diy.mobile.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.isyscore.kotlin.common.unzip
import com.rarnu.diy.mobile.R
import com.rarnu.diy.mobile.runOnMainThread
import java.io.File
import java.util.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess

lateinit var globalFileUtil: FileUtil

class FileUtil(ctx: Context) {
    val serverPath = File(ctx.getExternalFilesDir(""), "Server").apply { if (!exists()) mkdirs() }
    private val indexHtmlPath = File(serverPath, "index.html")
    val cardFilePath = File(ctx.getExternalFilesDir(""), "Cards").apply { if (!exists()) mkdirs() }

    /* 设置，重新安装服务器数据，升级服务器端数据 */
    fun removeServerFile() {
        if (indexHtmlPath.exists()) {
            if (serverPath.exists()) {
                serverPath.deleteRecursively()
                exitProcess(0)
            }
        }
    }

    fun prepareServerFiles(ctx: Context, p: (current: Int, total: Int) -> Unit, callback: (done: Boolean) -> Unit) {
        if (indexHtmlPath.exists()) {
            callback(true)
            return
        }
        thread {
            val fZip = File(serverPath, "server.zip")
            fZip.outputStream().use { output ->
                ctx.assets.open("server.zip").use { input ->
                    input.copyTo(output)
                }
            }
            unzip {
                zipPath = fZip.absolutePath
                destPath = serverPath.absolutePath
                progress { _, current, total ->
                    runOnMainThread { p(current, total) }
                }
                success {
                    runOnMainThread {
                        callback(true)
                    }
                }
                error {
                    Log.e("DIY", "extract error: $it")
                    runOnMainThread {
                        callback(false)
                    }
                }
            }
        }
    }

    fun rawImageToBase64(ctx: Context, id: Int): String {
        val data = ctx.resources.openRawResource(id).readBytes()
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(data)
    }

    fun base64ToImage(ctx: Context, data: String, name: String) {
        val imgData = data.substring(data.indexOf(",") + 1)
        val ba = Base64.getDecoder().decode(imgData)
        val bmp = BitmapFactory.decodeByteArray(ba, 0, ba.size)
        saveImageToDCIM(ctx, bmp, name)
    }

    private fun saveImageToDCIM(ctx: Context, bitmap: Bitmap, name: String) {
        val cv = ContentValues().apply {
            put(MediaStore.Images.Media.DESCRIPTION, name)
            put(MediaStore.Images.Media.DISPLAY_NAME, "$name.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.TITLE, "${name}_${System.currentTimeMillis()}.png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/YGODIY")
        }
        val external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val resolver = ctx.contentResolver
        val insertUri = resolver.insert(external, cv) ?: return
        resolver.openOutputStream(insertUri).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        Toast.makeText(ctx, R.string.str_export_image, Toast.LENGTH_LONG).show()
    }

}