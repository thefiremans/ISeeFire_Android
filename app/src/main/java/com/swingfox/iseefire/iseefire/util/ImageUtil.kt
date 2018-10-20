package com.swingfox.iseefire.iseefire.util

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.support.v4.content.FileProvider
import com.swingfox.iseefire.iseefire.BuildConfig
import org.joda.time.DateTime
import java.io.File
import java.util.*

class ImageUtil(private val context: Context) {

    fun readToLocal(uri: Uri, public: Boolean = false): String {
        val fileCopyUri = if (public) saveToGallery(uri) else saveToCache(uri)
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }.also { options ->
            context.contentResolver.openInputStream(fileCopyUri).use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }
        }
        return fileCopyUri.path
    }

    private fun saveToCache(uri: Uri): Uri {
        val tempFile = File(context.cacheDir, UUID.randomUUID().toString())
        val stream = context.contentResolver.openInputStream(uri)
        val length = DEFAULT_BUFFER_SIZE * 16
        val bytes = ByteArray(length)
        var size: Int
        stream.buffered(length).use { from ->
            tempFile.outputStream().buffered(length).use { to ->
                do {
                    size = from.read(bytes)
                    if (size > 0) {
                        to.write(bytes, 0, size)
                    }
                } while (size > 0)
            }
        }
        return Uri.fromFile(tempFile)
    }

    private fun saveToGallery(uri: Uri): Uri {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            DateTime.now().toString("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZZ") + ".jpg"
        )
        val stream = context.contentResolver.openInputStream(uri)
        val length = DEFAULT_BUFFER_SIZE * 16
        val bytes = ByteArray(length)
        var size: Int
        stream.buffered(length).use { from ->
            file.outputStream().buffered(length).use { to ->
                do {
                    size = from.read(bytes)
                    if (size > 0) {
                        to.write(bytes, 0, size)
                    }
                } while (size > 0)
            }
        }
        return Uri.fromFile(file)
    }
}