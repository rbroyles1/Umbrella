package com.example.rxbro.umbrella.utils

import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley

/**
 * Created by rxbro on 9/7/2017.
 */

class NetworkControllerSingleton private constructor(private val context: Context) {
    private var requestQueue: RequestQueue? = null

    init {
        requestQueue = getRequestQueue()
        imageLoader = ImageLoader(requestQueue, object : ImageLoader.ImageCache {
            private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            private val availableMemoryInBytes = activityManager.memoryClass * 1024 * 1024
            private val bitmapCache = LruCache<String, Bitmap>(availableMemoryInBytes / 4)

            override fun getBitmap(url: String): Bitmap {
                return bitmapCache.get(url)

            }

            override fun putBitmap(url: String, bitmap: Bitmap) {
                bitmapCache.put(url, bitmap)
            }
        })
    }

    fun getRequestQueue(): RequestQueue? {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.applicationContext)
        }
        return requestQueue
    }

    fun <T> addToRequestQueue(request: Request<T>) {
        getRequestQueue()?.add(request)
    }

    companion object {
        private var networkControllerSingleton: NetworkControllerSingleton? = null
        @Synchronized
        fun getInstance(context: Context): NetworkControllerSingleton {
            if (networkControllerSingleton == null) {
                networkControllerSingleton = NetworkControllerSingleton(context)
            }
            return networkControllerSingleton as NetworkControllerSingleton
        }

        var imageLoader: ImageLoader? = null
    }

    fun getImageLoader(): ImageLoader? {
        return imageLoader
    }
}
