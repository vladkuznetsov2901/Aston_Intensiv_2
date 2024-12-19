package com.example.aston_intensiv_2

import android.content.Context
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
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
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

    fun saveBitmapToFile(context: Context, bitmap: Bitmap, filename: String): File? {
        val directory = context.filesDir
        val file = File(directory, filename)

        try {
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        return file
    }

    fun loadImageFromFile(context: Context, fileName: String): Bitmap? {
        val directory = context.filesDir
        val file = File(directory, fileName)

        return try {
            val fileInputStream = FileInputStream(file)
            BitmapFactory.decodeStream(fileInputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


}