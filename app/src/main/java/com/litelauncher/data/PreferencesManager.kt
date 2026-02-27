package com.litelauncher.data

import com.litelauncher.model.AppInfo

interface PreferencesManager {
    fun getFavorite(slot: Int): AppInfo?
    fun setFavorite(slot: Int, appInfo: AppInfo)
    fun removeFavorite(slot: Int)
    fun getAccentColorIndex(): Int
    fun setAccentColorIndex(index: Int)
    fun getBackgroundIndex(): Int
    fun setBackgroundIndex(index: Int)
    fun getShowSystemApps(): Boolean
    fun setShowSystemApps(show: Boolean)
}
