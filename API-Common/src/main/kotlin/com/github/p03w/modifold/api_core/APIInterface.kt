package com.github.p03w.modifold.api_core

import com.github.p03w.modifold.cli.debug
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.runBlocking
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Suppress("DEPRECATION")
abstract class APIInterface {
    abstract val ratelimit: Ratelimit
    open val ratelimitRemainingHeader = ""
    open val ratelimitResetHeader = ""

    val client = HttpClient(CIO) {
        install(JsonFeature)
    }
    open fun HttpRequestBuilder.attachAuth() {}

    fun waitUntilCanSend() {
        @Suppress("ControlFlowWithEmptyBody")
        while (!ratelimit.canSend) {

        }
        ratelimit.makeRequest()
    }

    @Deprecated("Internal only, please")
    suspend inline fun <reified T: Any> HttpResponse.extractRatelimit(): T {
        val ratelimitRemaining = response.headers[ratelimitRemainingHeader]
        val ratelimitReset = response.headers[ratelimitResetHeader]
        if (ratelimitRemaining != null) {
            debug("Got ratelimit remaining header of ${ratelimitRemaining.toInt()}")
            ratelimit.remainingRequests = ratelimitRemaining.toInt()
        }
        if (ratelimitReset != null) {
            debug("Got ratelimit reset header of ${ratelimitReset.toInt()}")
            // +200ms to make sure we don't undershoot
            ratelimit.secondsUntilReset = ratelimitReset.toInt().seconds + 200.milliseconds
            ratelimit.resetClock = Instant.now()
        }
        return call.receive(typeInfo<T>()) as T
    }

    inline fun <reified T : Any> getWithoutAuth(url: String): T {
        return runBlocking {
            waitUntilCanSend()
            debug("GET | $url")
            return@runBlocking client.get<HttpResponse>(url).extractRatelimit()
        }
    }

    inline fun <reified T : Any> get(url: String): T {
        return runBlocking {
            waitUntilCanSend()
            debug("GET(AUTHED) | $url")
            return@runBlocking client.get<HttpResponse>(url) { attachAuth() }.extractRatelimit()
        }
    }

    inline fun <reified T : Any> postForm(url: String, crossinline action: FormBuilder.() -> Unit): T {
        return runBlocking {
            waitUntilCanSend()
            debug("SUBMIT FORM | $url")
            return@runBlocking client.submitForm<HttpResponse>(url) {
                attachAuth()

                body = MultiPartFormDataContent(
                    formData {
                        action()
                    }
                )
            }.extractRatelimit()
        }
    }
}
