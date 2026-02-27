package com.litelauncher.data

import android.content.SharedPreferences
import com.litelauncher.model.AppInfo

class PreferencesManagerImpl(private val prefs: SharedPreferences) : PreferencesManager {

    companion object {
        private const val KEY_FAV_PREFIX = "fav_"
        private const val KEY_ACCENT = "accent_color_index"
        private const val KEY_BG = "background_index"
        private const val KEY_SYSTEM_APPS = "show_system_apps"
        private const val SEPARATOR = "||"
        const val MAX_FAVORITES = 6
    }

    override fun getFavorite(slot: Int): AppInfo? {
        val raw = prefs.getString("$KEY_FAV_PREFIX$slot", null) ?: return null
        val parts = raw.split(SEPARATOR)
        if (parts.size < 3) return null
        return AppInfo(
            label = parts[0],
            packageName = parts[1],
            activityName = parts[2]
        )
    }

    override fun setFavorite(slot: Int, appInfo: AppInfo) {
        val value = "${appInfo.label}$SEPARATOR${appInfo.packageName}$SEPARATOR${appInfo.activityName}"
        prefs.edit().putString("$KEY_FAV_PREFIX$slot", value).apply()
    }

    override fun removeFavorite(slot: Int) {
        prefs.edit().remove("$KEY_FAV_PREFIX$slot").apply()
    }

    override fun getAccentColorIndex(): Int = prefs.getInt(KEY_ACCENT, 0)

    override fun setAccentColorIndex(index: Int) {
        prefs.edit().putInt(KEY_ACCENT, index).apply()
    }

    override fun getBackgroundIndex(): Int = prefs.getInt(KEY_BG, -1)

    override fun setBackgroundIndex(index: Int) {
        prefs.edit().putInt(KEY_BG, index).apply()
    }

    override fun getShowSystemApps(): Boolean = prefs.getBoolean(KEY_SYSTEM_APPS, true)

    override fun setShowSystemApps(show: Boolean) {
        prefs.edit().putBoolean(KEY_SYSTEM_APPS, show).apply()
    }
}
