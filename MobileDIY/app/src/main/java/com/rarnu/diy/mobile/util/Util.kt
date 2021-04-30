package com.rarnu.diy.mobile

import android.os.Handler
import android.os.Looper
import com.rarnu.diy.mobile.common.CardData
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import org.apache.commons.io.IOUtils
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipFile

//const val BASE_URL = "http://192.168.31.165:8010/#"

const val BASE_URL = "http://127.0.0.1:8010/#"

fun runOnMainThread(runnable: () -> Unit) = Handler(Looper.getMainLooper()).post { runnable() }

object CardDataHolder {
    private var tmpCard: CardData? = null
    fun setCard(c: CardData) {
        tmpCard = c
    }

    fun getCard(): CardData? = tmpCard
}