package com.example.myapplication.ui

import android.app.Application

class MyApplication : Application() {
    companion object {
        private lateinit var instance: MyApplication
        fun getInstance(): MyApplication = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
