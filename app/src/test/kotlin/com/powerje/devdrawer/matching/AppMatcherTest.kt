package com.powerje.devdrawer.matching

import com.powerje.devdrawer.data.Pattern
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppMatcherTest {
    @Test
    fun `empty patterns returns empty list`() {
        val apps =
            listOf(
                InstalledApp("com.example.app", "Example App"),
            )
        val result = AppMatcher.match(apps, emptyList())
        assertTrue(result.isEmpty())
    }

    @Test
    fun `matches app by exact package pattern`() {
        val apps =
            listOf(
                InstalledApp("com.example.app", "Example App"),
                InstalledApp("com.other.app", "Other App"),
            )
        val patterns = listOf(Pattern("Test", "com\\.example\\.app"))
        val result = AppMatcher.match(apps, patterns)
        assertEquals(1, result.size)
        assertEquals("com.example.app", result[0].packageName)
    }

    @Test
    fun `matches app by wildcard pattern`() {
        val apps =
            listOf(
                InstalledApp("com.example.one", "One"),
                InstalledApp("com.example.two", "Two"),
                InstalledApp("com.other.app", "Other"),
            )
        val patterns = listOf(Pattern("Example", "com\\.example\\..*"))
        val result = AppMatcher.match(apps, patterns)
        assertEquals(2, result.size)
    }

    @Test
    fun `multiple patterns combine matches`() {
        val apps =
            listOf(
                InstalledApp("com.work.app", "Work"),
                InstalledApp("com.personal.app", "Personal"),
                InstalledApp("com.other.app", "Other"),
            )
        val patterns =
            listOf(
                Pattern("Work", "com\\.work\\..*"),
                Pattern("Personal", "com\\.personal\\..*"),
            )
        val result = AppMatcher.match(apps, patterns)
        assertEquals(2, result.size)
    }

    @Test
    fun `no duplicate matches with overlapping patterns`() {
        val apps =
            listOf(
                InstalledApp("com.example.app", "Example"),
            )
        val patterns =
            listOf(
                Pattern("One", "com\\.example\\..*"),
                Pattern("Two", "com\\..*"),
            )
        val result = AppMatcher.match(apps, patterns)
        assertEquals(1, result.size)
    }

    @Test
    fun `invalid regex is skipped`() {
        val apps =
            listOf(
                InstalledApp("com.example.app", "Example"),
            )
        val patterns =
            listOf(
                Pattern("Invalid", "[invalid"),
                Pattern("Valid", "com\\.example\\..*"),
            )
        val result = AppMatcher.match(apps, patterns)
        assertEquals(1, result.size)
    }

    @Test
    fun `results sorted alphabetically by app name`() {
        val apps =
            listOf(
                InstalledApp("com.c.app", "Charlie"),
                InstalledApp("com.a.app", "Alpha"),
                InstalledApp("com.b.app", "Bravo"),
            )
        val patterns = listOf(Pattern("All", "com\\..*"))
        val result = AppMatcher.match(apps, patterns)
        assertEquals(listOf("Alpha", "Bravo", "Charlie"), result.map { it.appName })
    }
}
