package com.powerje.devdrawer.matching

import com.powerje.devdrawer.data.Pattern
import java.util.regex.PatternSyntaxException

data class InstalledApp(
    val packageName: String,
    val appName: String,
)

object AppMatcher {
    fun match(
        apps: List<InstalledApp>,
        patterns: List<Pattern>,
    ): List<MatchedApp> {
        if (patterns.isEmpty()) return emptyList()

        val regexes =
            patterns.mapNotNull { pattern ->
                try {
                    pattern.regex.toRegex()
                } catch (_: PatternSyntaxException) {
                    null // Invalid regex patterns are silently skipped
                }
            }

        return apps
            .filter { app -> regexes.any { it.matches(app.packageName) } }
            .map { MatchedApp(it.packageName, it.appName) }
            .distinctBy { it.packageName }
            .sortedBy { it.appName.lowercase() }
    }
}
