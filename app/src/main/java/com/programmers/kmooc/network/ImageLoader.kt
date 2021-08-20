package com.programmers.kmooc.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import java.net.URL


object ImageLoader {
    suspend fun loadImage(url: String) : Bitmap? {
        return getBitmap(url)
    }

      suspend fun getBitmap(uri: String) : Bitmap? {
        val bmp : Bitmap? = null
        try {
            val url = URL(uri)
            val stream = url.openStream()
            return BitmapFactory.decodeStream(stream)

        }catch (e: IOException) {

        }
        return bmp
    }
}