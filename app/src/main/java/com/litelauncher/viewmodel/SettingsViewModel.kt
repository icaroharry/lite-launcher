package com.litelauncher.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.litelauncher.data.PreferencesManager

class SettingsViewModel(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _accentColorIndex = MutableLiveData<Int>()
    val accentColorIndex: LiveData<Int> = _accentColorIndex

    private val _backgroundIndex = MutableLiveData<Int>()
    val backgroundIndex: LiveData<Int> = _backgroundIndex

    private val _showSystemApps = MutableLiveData<Boolean>()
    val showSystemApps: LiveData<Boolean> = _showSystemApps

    init {
        load()
    }

    private fun load() {
        _accentColorIndex.value = preferencesManager.getAccentColorIndex()
        _backgroundIndex.value = preferencesManager.getBackgroundIndex()
        _showSystemApps.value = preferencesManager.getShowSystemApps()
    }

    fun setAccentColor(index: Int) {
        preferencesManager.setAccentColorIndex(index)
        _accentColorIndex.value = index
    }

    fun setBackground(index: Int) {
        preferencesManager.setBackgroundIndex(index)
        _backgroundIndex.value = index
    }

    fun setShowSystemApps(show: Boolean) {
        preferencesManager.setShowSystemApps(show)
        _showSystemApps.value = show
    }
}
