package com.github.p03w.modifold.networking.core

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration

abstract class APIInterface(delay: Duration) {
    val canSend by Countdown(delay)
    val client = HttpClient(CIO) {
        install(JsonFeature)
    }

    fun waitUntilCanSend() { @Suppress("ControlFlowWithEmptyBody") while (!canSend) {} }

    open fun HttpRequestBuilder.attachAuth() {}

    inline fun <reified T: Any> get(url: String): T {
        return runBlocking {
            waitUntilCanSend()
            return@runBlocking client.get(url) { attachAuth() }
        }
    }
}
