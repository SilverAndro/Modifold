package com.github.p03w.modifold.api_core

import com.github.p03w.modifold.cli.debug
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking

@Suppress("DEPRECATION")
abstract class APIInterface {
    abstract val ratelimit: Ratelimit
    open val ratelimitRemainingHeader = ""

    val client = HttpClient(CIO) {
        install(JsonFeature)
    }
    open fun HttpRequestBuilder.attachAuth() {}

    fun waitUntilCanSend() {
        @Suppress("ControlFlowWithEmptyBody")
        while (!ratelimit.canSend) { }
        ratelimit.makeRequest()
    }

    @Deprecated("Internal only, please")
    suspend inline fun <reified T: Any> HttpStatement.extractRatelimit(): T {
        val response = execute()
        val ratelimitHeader = response.headers[ratelimitRemainingHeader]
        if (ratelimitHeader != null) {
            debug("Got ratelimit remaining header of ${ratelimitHeader.toInt()}")
            ratelimit.remainingRequests = ratelimitHeader.toInt()
        }
        return receive()
    }

    inline fun <reified T : Any> getWithoutAuth(url: String): T {
        return runBlocking {
            waitUntilCanSend()
            debug("GET | $url")
            return@runBlocking client.get<HttpStatement>(url).extractRatelimit()
        }
    }

    inline fun <reified T : Any> get(url: String): T {
        return runBlocking {
            waitUntilCanSend()
            debug("GET(AUTHED) | $url")
            return@runBlocking client.get<HttpStatement>(url) { attachAuth() }.extractRatelimit()
        }
    }

    inline fun <reified T : Any> postForm(url: String, crossinline action: FormBuilder.() -> Unit): T {
        return runBlocking {
            waitUntilCanSend()
            debug("SUBMIT FORM | $url")
            return@runBlocking client.submitForm<HttpStatement>(url) {
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
