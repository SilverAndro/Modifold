package com.github.p03w.modifold.api_core

import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class Ratelimit(
    private val delay: Duration,
    private val ignoreRequestCounter: Boolean
) {
    private var clock = -1L
    var resetClock = Instant.now()
    var remainingRequests = 10 // Better get a ratelimit in 10 requests lol
    var secondsUntilReset = 1000.seconds

    fun makeRequest() {
        remainingRequests--
    }

    val canSend: Boolean get() {
        if (resetClock.toEpochMilli().milliseconds + secondsUntilReset <= Instant.now().toEpochMilli().milliseconds) {
            remainingRequests = 10
        }
        if (Instant.now().toEpochMilli() >= clock) {
            clock = Instant.now().toEpochMilli() + delay.inWholeMilliseconds
            return remainingRequests > 0 || ignoreRequestCounter
        }
        return false
    }
}
