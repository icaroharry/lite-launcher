package com.litelauncher.search

import com.litelauncher.model.AppInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FuzzySearchTest {

    private fun app(label: String) = AppInfo(label, "com.test.$label", ".Main")

    @Test
    fun `empty query returns all apps`() {
        val apps = listOf(app("Netflix"), app("YouTube"))
        val result = FuzzySearch.filter(apps, "")
        assertEquals(2, result.size)
    }

    @Test
    fun `exact match returns the app`() {
        val apps = listOf(app("Netflix"), app("YouTube"))
        val result = FuzzySearch.filter(apps, "Netflix")
        assertEquals(1, result.size)
        assertEquals("Netflix", result[0].label)
    }

    @Test
    fun `case insensitive matching`() {
        val apps = listOf(app("Netflix"))
        val result = FuzzySearch.filter(apps, "netflix")
        assertEquals(1, result.size)
    }

    @Test
    fun `partial subsequence match`() {
        val apps = listOf(app("Netflix"), app("Notes"))
        val result = FuzzySearch.filter(apps, "ntf")
        assertEquals(1, result.size)
        assertEquals("Netflix", result[0].label)
    }

    @Test
    fun `no match returns empty`() {
        val apps = listOf(app("Netflix"))
        val result = FuzzySearch.filter(apps, "xyz")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `prefix match scores higher than mid-word match`() {
        val apps = listOf(app("XSettings"), app("Settings"))
        val result = FuzzySearch.filter(apps, "set")
        assertEquals(2, result.size)
        assertEquals("Settings", result[0].label)
    }

    @Test
    fun `consecutive matches score higher`() {
        val apps = listOf(app("App Store"), app("A Puzzle"))
        val result = FuzzySearch.filter(apps, "app")
        assertTrue(result.isNotEmpty())
        assertEquals("App Store", result[0].label)
    }

    @Test
    fun `score returns zero for non-subsequence`() {
        assertEquals(0, FuzzySearch.score("abc", "xyz"))
    }

    @Test
    fun `score handles empty text`() {
        assertEquals(0, FuzzySearch.score("", "a"))
    }

    @Test
    fun `score handles empty query`() {
        assertTrue(FuzzySearch.score("anything", "") > 0)
    }

    @Test
    fun `special characters in query`() {
        val apps = listOf(app("Wi-Fi Settings"))
        val result = FuzzySearch.filter(apps, "wi-fi")
        assertEquals(1, result.size)
    }
}
