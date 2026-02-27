package com.litelauncher.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.litelauncher.data.AppRepository
import com.litelauncher.data.PreferencesManager
import com.litelauncher.model.AppInfo
import com.litelauncher.model.FavoriteSlot
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class LauncherViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: AppRepository
    private lateinit var prefs: PreferencesManager
    private lateinit var viewModel: LauncherViewModel

    private val testApps = listOf(
        AppInfo("Netflix", "com.netflix", ".Main"),
        AppInfo("YouTube", "com.youtube", ".Main"),
        AppInfo("Settings", "com.settings", ".Main")
    )

    private val testFavorites = listOf(
        FavoriteSlot(0, testApps[0]),
        FavoriteSlot(1, null),
        FavoriteSlot(2, null),
        FavoriteSlot(3, null),
        FavoriteSlot(4, null),
        FavoriteSlot(5, null)
    )

    @Before
    fun setup() {
        repository = mock()
        prefs = mock()
        whenever(prefs.getShowSystemApps()).thenReturn(false)
        whenever(repository.getInstalledApps(false)).thenReturn(testApps)
        whenever(repository.getFavorites()).thenReturn(testFavorites)
        viewModel = LauncherViewModel(repository, prefs)
        // Activate MediatorLiveData by observing
        viewModel.filteredApps.observeForever {}
    }

    @Test
    fun `loadApps populates allApps and favorites`() {
        viewModel.loadApps()
        assertEquals(3, viewModel.allApps.value?.size)
        assertEquals(6, viewModel.favorites.value?.size)
    }

    @Test
    fun `filteredApps returns all when no search query`() {
        viewModel.loadApps()
        assertEquals(3, viewModel.filteredApps.value?.size)
    }

    @Test
    fun `search filters apps by query`() {
        viewModel.loadApps()
        viewModel.search("net")
        val filtered = viewModel.filteredApps.value
        assertEquals(1, filtered?.size)
        assertEquals("Netflix", filtered?.first()?.label)
    }

    @Test
    fun `clearSearch resets filter`() {
        viewModel.loadApps()
        viewModel.search("net")
        viewModel.clearSearch()
        assertEquals(3, viewModel.filteredApps.value?.size)
    }

    @Test
    fun `setFavorite delegates to repository`() {
        val app = testApps[1]
        viewModel.setFavorite(1, app)
        verify(repository).setFavorite(1, app)
    }

    @Test
    fun `removeFavorite delegates to repository`() {
        viewModel.removeFavorite(0)
        verify(repository).removeFavorite(0)
    }
}
