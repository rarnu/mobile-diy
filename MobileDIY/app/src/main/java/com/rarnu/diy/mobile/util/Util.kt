package com.rarnu.diy.mobile.util

import android.os.Handler
import android.os.Looper
import com.rarnu.diy.mobile.common.CardData

const val BASE_URL = "http://127.0.0.1:8010/#"

fun runOnMainThread(runnable: () -> Unit) = Handler(Looper.getMainLooper()).post { runnable() }

fun String.removeKana(): String =
    replace("\\[.*?\\(.*?\\)]".toRegex()) { s ->
        s.value.replace("[", "").run { substring(0, indexOf("(")) }
    }

object CardDataHolder {
    private var tmpCard: CardData? = null
    fun setCard(c: CardData) {
        tmpCard = c
    }

    fun getCard(): CardData? = tmpCard
}