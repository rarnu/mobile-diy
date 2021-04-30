package com.rarnu.diy.mobile.server

import android.content.Context
import android.util.Log
import com.rarnu.diy.mobile.util.globalFileUtil
import fi.iki.elonen.NanoHTTPD
import java.io.File

class PreviewServer(val ctx: Context, port: Int) : NanoHTTPD(port) {
    override fun serve(session: IHTTPSession): Response {
        val uri = session.uri
        var filename = uri.substring(1)
        if (uri == "/") filename = "index.html"

        val mimetype = when {
            filename.contains(".html") -> "text/html"
            filename.contains(".js") -> "text/javascript"
            filename.contains(".css") -> "text/css"
            filename.contains(".jpg") || filename.contains(".jpeg") -> "image/jpeg"
            filename.contains(".png") -> "image/png"
            filename.contains(".svg") -> "image/svg+xml"
            filename.contains(".ttf") -> "application/x-font-ttf"
            filename.contains(".woff2") -> "application/font-woff2"
            filename.contains(".woff") -> "application/font-woff"
            filename.contains(".eot") -> "application/vnd.ms-fontobject"
            filename.contains(".gz") -> "application/gzip; charset=UTF-8"
            filename.contains(".dat") -> "application/octet-stream"
            filename.contains(".ico") -> "image/x-icon"
            else -> "text/html"
        }
        try {
            val file = File(globalFileUtil.serverPath, filename).inputStream().buffered()
            return newChunkedResponse(Response.Status.OK, mimetype, file)
        } catch (e: Throwable) {
            Log.e("DIY", "Serve Error: $e")
        }
        return newFixedLengthResponse("404: $uri")
    }
}