package com.litelauncher.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.util.Log
import com.litelauncher.model.AppInfo
import com.litelauncher.model.FavoriteSlot

class AppRepositoryImpl(
    private val context: Context,
    private val preferencesManager: PreferencesManager
) : AppRepository {

    override fun getInstalledApps(includeSystem: Boolean): List<AppInfo> {
        val pm = context.packageManager
        val apps = mutableMapOf<String, AppInfo>()

        // Standard launcher apps
        val mainIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        addResolvedApps(pm, mainIntent, apps)

        // Leanback launcher apps (TV-specific)
        val leanbackIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        addResolvedApps(pm, leanbackIntent, apps)

        Log.d("AppRepo", "Found ${apps.size} total apps (includeSystem=$includeSystem)")

        return apps.values
            .filter { it.packageName != context.packageName }
            .filter { includeSystem || !it.isSystem }
            .sortedBy { it.label.lowercase() }
    }

    private fun addResolvedApps(
        pm: PackageManager,
        intent: Intent,
        apps: MutableMap<String, AppInfo>
    ) {
        val resolveInfos: List<ResolveInfo> = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        Log.d("AppRepo", "Query ${intent.categories}: found ${resolveInfos.size} activities")
        for (ri in resolveInfos) {
            val pkg = ri.activityInfo.packageName
            if (apps.containsKey(pkg)) continue

            val isSystem = ri.activityInfo.applicationInfo.flags and
                    android.content.pm.ApplicationInfo.FLAG_SYSTEM != 0
            val label = ri.loadLabel(pm).toString()
            val activityName = ri.activityInfo.name
            apps[pkg] = AppInfo(label, pkg, activityName, isSystem)
        }
    }

    override fun getFavorites(): List<FavoriteSlot> {
        return (0 until PreferencesManagerImpl.MAX_FAVORITES).map { index ->
            FavoriteSlot(index, preferencesManager.getFavorite(index))
        }
    }

    override fun setFavorite(slot: Int, appInfo: AppInfo) {
        preferencesManager.setFavorite(slot, appInfo)
    }

    override fun removeFavorite(slot: Int) {
        preferencesManager.removeFavorite(slot)
    }
}
