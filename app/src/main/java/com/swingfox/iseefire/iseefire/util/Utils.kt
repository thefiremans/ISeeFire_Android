package com.swingfox.iseefire.iseefire.util

inline fun <T> tryOrNull(f: () -> T): T? {
    return try {
        f()
    } catch (ex: Exception) {
        ex.printStackTrace()
        null
    }
}