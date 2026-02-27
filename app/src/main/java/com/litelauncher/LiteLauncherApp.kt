package com.litelauncher

import android.app.Application
import android.content.Context
import com.litelauncher.data.AppRepository
import com.litelauncher.data.AppRepositoryImpl
import com.litelauncher.data.PreferencesManager
import com.litelauncher.data.PreferencesManagerImpl

class LiteLauncherApp : Application() {

    lateinit var preferencesManager: PreferencesManager
        private set
    lateinit var appRepository: AppRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences("lite_launcher_prefs", Context.MODE_PRIVATE)
        preferencesManager = PreferencesManagerImpl(prefs)
        appRepository = AppRepositoryImpl(this, preferencesManager)
    }
}
