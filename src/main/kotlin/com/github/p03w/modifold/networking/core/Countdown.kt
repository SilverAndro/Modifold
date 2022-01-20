package com.github.p03w.modifold.networking.core

import java.time.Instant
import kotlin.reflect.KProperty
import kotlin.time.Duration

class Countdown(private val delay: Duration) {
    private var clock = -1L

    operator fun getValue(thisRef: Any, kParameter: KProperty<*>): Boolean {
        if (Instant.now().toEpochMilli() >= clock) {
            clock = Instant.now().toEpochMilli() + delay.inWholeMilliseconds
            return true
        }
        return false
    }
}
