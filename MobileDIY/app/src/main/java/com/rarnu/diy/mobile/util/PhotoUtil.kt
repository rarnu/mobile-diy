package com.rarnu.diy.mobile

import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import java.util.*

object ImageUtil {

    private lateinit var context: ComponentActivity
    private lateinit var launcher: ActivityResultLauncher<Intent>

    private var onCallback: (data: Intent?) -> Unit = { }

    fun register(ctx: ComponentActivity) {
        context = ctx
        launcher = ctx.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                onCallback(result.data)
            }
        }
    }

    fun openJsonFile(callback: (path: String) -> Unit) {
        val inOpen = Intent(context, FileListActivity::class.java)
        onCallback = { data ->
            callback(data?.getStringExtra("path") ?: "")
        }
        launcher.launch(inOpen)
    }

    fun getImageFromCamera(callback: (data: String) -> Unit) {

        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        val cv = ContentValues().apply {
            put(MediaStore.Images.Media.DESCRIPTION, filename)
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.TITLE, filename)
            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/IMAGE")
        }
        val external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val resolver = context.contentResolver
        val cUri = resolver.insert(external, cv) ?: return

        val inCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            putExtra(MediaStore.EXTRA_OUTPUT, cUri)
        }
        onCallback = { _ ->
            cropImage(cUri, callback)
        }
        launcher.launch(inCamera)
    }

    fun getImageFromAlbum(callback: (data: String) -> Unit) {
        val inAlbum = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        onCallback = { data ->
            val contentUri = data?.data
            if (contentUri != null) cropImage(contentUri, callback)
        }
        launcher.launch(inAlbum)
    }

    fun cropImage(uri: Uri, callback: (data: String) -> Unit) {
        val filename = "CIMG_${System.currentTimeMillis()}.jpg"
        val cv = ContentValues().apply {
            put(MediaStore.Images.Media.DESCRIPTION, filename)
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.TITLE, filename)
            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/CROP")
        }
        val external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val resolver = context.contentResolver
        val cUri = resolver.insert(external, cv) ?: return

        val inCrop = Intent("com.android.camera.action.CROP").apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            setDataAndType(uri, "image/*")
            putExtra("crop", "true")
            putExtra("scale", true)
            putExtra("aspectX", 1)
            putExtra("aspectY", 1)
            putExtra("return-data", false)
            putExtra(MediaStore.EXTRA_OUTPUT, cUri)
            putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        }

        val list = context.packageManager.queryIntentActivities(inCrop, PackageManager.MATCH_DEFAULT_ONLY)
        list.forEach {
            val pkg = it.activityInfo.packageName
            context.grantUriPermission(pkg, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }

        onCallback = {
            resolver.openInputStream(cUri).use { input ->
                callback("data:image/png;base64," + Base64.getEncoder().encodeToString(input?.readBytes()))
            }
        }
        launcher.launch(inCrop)
    }
}