package com.example.sophieaianalyst

import android.app.Application
import com.example.sophieaianalyst.data.AppContainer
import com.example.sophieaianalyst.data.DefaultAppContainer

class SophieApplication : Application() {
    lateinit var container: AppContainer
    
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(applicationContext)
    }
} 