// SentimentAnalyzer.kt
package com.example.musicplayer.data.analyzer

import com.google.mlkit.nl.sentiment.SentimentAnalysis

import kotlinx.coroutines.tasks.await

class MLKitSentimentAnalyzer {
    private val analyzer: SentimentAnalyzer = SentimentAnalysis.getClient()

    suspend fun analyze(text: String): String {
        return try {
            val result = analyzer.analyze(text).await()
            when {
                result.score < 0 -> "negative"   // -1.0 to -0.1
                result.score == 0f -> "neutral"  // 0
                result.score > 0 -> "positive"   // 0.1 to +1.0
                else -> "unknown"
            }
        } catch (e: Exception) {
            "error: ${e.message}"
        }
    }
}