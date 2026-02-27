package com.litelauncher.data

import com.litelauncher.model.AppInfo
import com.litelauncher.model.FavoriteSlot

interface AppRepository {
    fun getInstalledApps(includeSystem: Boolean): List<AppInfo>
    fun getFavorites(): List<FavoriteSlot>
    fun setFavorite(slot: Int, appInfo: AppInfo)
    fun removeFavorite(slot: Int)
}
