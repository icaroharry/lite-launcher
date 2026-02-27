package com.litelauncher.data

import android.content.SharedPreferences
import com.litelauncher.model.AppInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class PreferencesManagerImplTest {

    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var manager: PreferencesManagerImpl

    @Before
    fun setup() {
        prefs = mock()
        editor = mock()
        whenever(prefs.edit()).thenReturn(editor)
        whenever(editor.putString(any(), any())).thenReturn(editor)
        whenever(editor.putInt(any(), any())).thenReturn(editor)
        whenever(editor.putBoolean(any(), any())).thenReturn(editor)
        whenever(editor.remove(any())).thenReturn(editor)
        manager = PreferencesManagerImpl(prefs)
    }

    @Test
    fun `getFavorite returns null when not set`() {
        whenever(prefs.getString(eq("fav_0"), anyOrNull())).thenReturn(null)
        assertNull(manager.getFavorite(0))
    }

    @Test
    fun `getFavorite returns AppInfo when set`() {
        whenever(prefs.getString(eq("fav_0"), anyOrNull())).thenReturn("Netflix||com.netflix||.Main")
        val result = manager.getFavorite(0)
        assertEquals("Netflix", result?.label)
        assertEquals("com.netflix", result?.packageName)
        assertEquals(".Main", result?.activityName)
    }

    @Test
    fun `getFavorite returns null for malformed data`() {
        whenever(prefs.getString(eq("fav_0"), anyOrNull())).thenReturn("incomplete")
        assertNull(manager.getFavorite(0))
    }

    @Test
    fun `setFavorite writes serialized AppInfo`() {
        val app = AppInfo("Netflix", "com.netflix", ".Main")
        manager.setFavorite(0, app)
        verify(editor).putString("fav_0", "Netflix||com.netflix||.Main")
        verify(editor).apply()
    }

    @Test
    fun `removeFavorite removes the key`() {
        manager.removeFavorite(2)
        verify(editor).remove("fav_2")
        verify(editor).apply()
    }

    @Test
    fun `getAccentColorIndex returns stored value`() {
        whenever(prefs.getInt(eq("accent_color_index"), any())).thenReturn(5)
        assertEquals(5, manager.getAccentColorIndex())
    }

    @Test
    fun `setAccentColorIndex persists value`() {
        manager.setAccentColorIndex(3)
        verify(editor).putInt("accent_color_index", 3)
    }

    @Test
    fun `getBackgroundIndex defaults to -1`() {
        whenever(prefs.getInt(eq("background_index"), eq(-1))).thenReturn(-1)
        assertEquals(-1, manager.getBackgroundIndex())
    }

    @Test
    fun `getShowSystemApps defaults to false`() {
        whenever(prefs.getBoolean(eq("show_system_apps"), eq(false))).thenReturn(false)
        assertEquals(false, manager.getShowSystemApps())
    }
}
