package com.github.p03w.modifold.api_core

import java.time.Instant
import kotlin.time.Duration

class Ratelimit(
    private val delay: Duration,
    private val ignoreRequestCounter: Boolean
) {
    private var clock = -1L
    var remainingRequests = 10 // Better get a ratelimit in 10 requests lol

    fun makeRequest() {
        remainingRequests--
    }

    val canSend: Boolean get() {
        if (Instant.now().toEpochMilli() >= clock) {
            clock = Instant.now().toEpochMilli() + delay.inWholeMilliseconds
            return remainingRequests > 0 || ignoreRequestCounter
        }
        return false
    }
}