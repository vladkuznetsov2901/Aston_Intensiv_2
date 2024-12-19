package com.example.aston_intensiv_2

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class MainViewModel {

    fun getRandomImage(url: String, key: String, callback: (Bitmap) -> Unit) {
        Thread {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .addHeader("X-Api-Key", key)
                .addHeader("Accept", "image/jpg")
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                val imageBytes = response.body?.bytes()
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes?.size ?: 0)
                callback(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

}