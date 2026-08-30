package com.autojournal.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageUtils {

    fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val contentResolver: ContentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)

            val directory = File(context.filesDir, "car_photos")
            if (!directory.exists()) {
                directory.mkdirs()
            }

            val fileName = "${UUID.randomUUID()}.jpg"
            val file = File(directory, fileName)

            inputStream?.use { input ->
                val bitmap = BitmapFactory.decodeStream(input)
                FileOutputStream(file).use { output ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, output)
                }
            }

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun loadImageFromStorage(path: String): Bitmap? {
        return try {
            val file = File(path)
            if (file.exists()) {
                BitmapFactory.decodeFile(file.absolutePath)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteImage(path: String): Boolean {
        return try {
            val file = File(path)
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getCacheDirectory(context: Context): File {
        return context.cacheDir
    }

    fun getCropOutputUri(context: Context): Uri {
        val fileName = "cropped_${UUID.randomUUID()}.jpg"
        val file = File(getCacheDirectory(context), fileName)
        return Uri.fromFile(file)
    }
}