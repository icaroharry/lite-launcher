package com.litelauncher.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.litelauncher.data.AppRepository
import com.litelauncher.data.PreferencesManager
import com.litelauncher.model.AppInfo
import com.litelauncher.model.FavoriteSlot
import com.litelauncher.search.FuzzySearch

class LauncherViewModel(
    private val appRepository: AppRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _allApps = MutableLiveData<List<AppInfo>>(emptyList())
    val allApps: LiveData<List<AppInfo>> = _allApps

    private val _favorites = MutableLiveData<List<FavoriteSlot>>(emptyList())
    val favorites: LiveData<List<FavoriteSlot>> = _favorites

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    val filteredApps: LiveData<List<AppInfo>> = MediatorLiveData<List<AppInfo>>().apply {
        fun update() {
            val apps = _allApps.value.orEmpty()
            val query = _searchQuery.value.orEmpty()
            value = if (query.isBlank()) apps else FuzzySearch.filter(apps, query)
        }
        addSource(_allApps) { update() }
        addSource(_searchQuery) { update() }
    }

    fun loadApps() {
        val showSystem = preferencesManager.getShowSystemApps()
        _allApps.value = appRepository.getInstalledApps(showSystem)
        _favorites.value = appRepository.getFavorites()
    }

    fun search(query: String) {
        _searchQuery.value = query
    }

    fun setFavorite(slot: Int, appInfo: AppInfo) {
        appRepository.setFavorite(slot, appInfo)
        _favorites.value = appRepository.getFavorites()
    }

    fun removeFavorite(slot: Int) {
        appRepository.removeFavorite(slot)
        _favorites.value = appRepository.getFavorites()
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }
}
