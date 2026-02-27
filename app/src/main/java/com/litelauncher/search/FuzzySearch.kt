package com.litelauncher.search

import com.litelauncher.model.AppInfo

object FuzzySearch {

    data class ScoredApp(val app: AppInfo, val score: Int)

    /**
     * Filters and ranks apps by fuzzy-matching query against app labels.
     * Returns apps sorted by descending score (best match first).
     */
    fun filter(apps: List<AppInfo>, query: String): List<AppInfo> {
        if (query.isBlank()) return apps
        val lowerQuery = query.lowercase()
        return apps.mapNotNull { app ->
            val score = score(app.label, lowerQuery)
            if (score > 0) ScoredApp(app, score) else null
        }
            .sortedByDescending { it.score }
            .map { it.app }
    }

    /**
     * Scores how well [text] matches [query] using subsequence matching.
     * Returns 0 if query is not a subsequence of text.
     *
     * Bonuses:
     *  - Consecutive character matches: +5
     *  - Match at word boundary (after space/punctuation): +10
     *  - Match at start of string: +15
     *  - Exact case match: +1
     */
    fun score(text: String, query: String): Int {
        if (query.isEmpty()) return 1
        if (text.isEmpty()) return 0

        val lowerText = text.lowercase()
        val lowerQuery = query.lowercase()

        var totalScore = 0
        var queryIdx = 0
        var prevMatchIdx = -2
        var firstMatch = true

        for (textIdx in lowerText.indices) {
            if (queryIdx >= lowerQuery.length) break
            if (lowerText[textIdx] != lowerQuery[queryIdx]) continue

            // Base match score
            totalScore += 1

            // Exact case bonus
            if (text[textIdx] == query[queryIdx]) {
                totalScore += 1
            }

            // Consecutive match bonus
            if (textIdx == prevMatchIdx + 1) {
                totalScore += 5
            }

            // Word boundary bonus (start of word)
            if (textIdx == 0) {
                totalScore += 15
            } else if (lowerText[textIdx - 1] == ' ' || lowerText[textIdx - 1] == '-' || lowerText[textIdx - 1] == '_') {
                totalScore += 10
            }

            // First match position penalty (prefer earlier matches)
            if (firstMatch) {
                totalScore += maxOf(0, 10 - textIdx)
                firstMatch = false
            }

            prevMatchIdx = textIdx
            queryIdx++
        }

        // All query characters must be matched
        return if (queryIdx == lowerQuery.length) totalScore else 0
    }
}
