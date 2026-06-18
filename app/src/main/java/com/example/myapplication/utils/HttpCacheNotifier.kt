package com.example.myapplication.utils

import android.os.Handler
import android.os.Looper

object HttpCacheNotifier {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var listener: (() -> Unit)? = null

    fun setListener(listener: (() -> Unit)?) {
        this.listener = listener
    }

    internal fun onResponse(fromCache: Boolean) {
        if (fromCache) {
            mainHandler.post {
                listener?.invoke()
            }
        }
    }
}
