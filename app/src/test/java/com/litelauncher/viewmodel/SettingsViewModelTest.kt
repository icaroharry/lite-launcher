package com.litelauncher.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.litelauncher.data.PreferencesManager
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SettingsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var prefs: PreferencesManager
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        prefs = mock()
        whenever(prefs.getAccentColorIndex()).thenReturn(1)
        whenever(prefs.getBackgroundIndex()).thenReturn(3)
        whenever(prefs.getShowSystemApps()).thenReturn(false)
        viewModel = SettingsViewModel(prefs)
    }

    @Test
    fun `init loads values from preferences`() {
        assertEquals(1, viewModel.accentColorIndex.value)
        assertEquals(3, viewModel.backgroundIndex.value)
        assertEquals(false, viewModel.showSystemApps.value)
    }

    @Test
    fun `setAccentColor persists and updates LiveData`() {
        viewModel.setAccentColor(5)
        verify(prefs).setAccentColorIndex(5)
        assertEquals(5, viewModel.accentColorIndex.value)
    }

    @Test
    fun `setBackground persists and updates LiveData`() {
        viewModel.setBackground(7)
        verify(prefs).setBackgroundIndex(7)
        assertEquals(7, viewModel.backgroundIndex.value)
    }

    @Test
    fun `setShowSystemApps persists and updates LiveData`() {
        viewModel.setShowSystemApps(true)
        verify(prefs).setShowSystemApps(true)
        assertEquals(true, viewModel.showSystemApps.value)
    }
}
