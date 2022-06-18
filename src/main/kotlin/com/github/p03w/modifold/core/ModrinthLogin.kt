package com.github.p03w.modifold.core

import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.ListViewOptions
import com.github.kinquirer.components.promptInputPassword
import com.github.kinquirer.components.promptList
import com.github.p03w.modifold.cli.*
import com.github.p03w.modifold.core.github_schema.AuthToken
import com.github.p03w.modifold.core.github_schema.DeviceCode
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import java.awt.Desktop
import java.net.URI
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

suspend fun loginToModrinth(): String {
    if (ModifoldArgs.args.modrinthToken != null) {
        log("Token was passed through CLI, using that")
        return ModifoldArgs.args.modrinthToken!!
    }

    val option = KInquirer.promptList(
        "How do you want to login to modrinth?",
        listOf("Web Flow", "Manual Token Entry", "CLI Arg"),
        viewOptions = ListViewOptions(questionMarkPrefix = "")
    )

    return when (option) {
        "Web Flow" -> doWebFlow()
        "Manual Token Entry" -> doManualEntry()
        "CLI Arg" -> showCLIExplainer()
        else -> throw IllegalStateException()
    }
}

fun showCLIExplainer(): Nothing {
    log("To pass the access token through CLI, pass --token <TOKEN>")
    exitProcess(0)
}

@OptIn(ExperimentalTime::class)
suspend fun doWebFlow(): String {
    if (!Desktop.getDesktop().isSupported(Desktop.Action.BROWSE) && ModifoldArgs.args.modrinthToken == null) {
        error("Your system does not support starting a web browser! Cannot do web flow.")
    }

    debug("Starting github request client")
    val client = HttpClient(CIO) {
        install(JsonFeature)
    }

    debug("POSTing github for device code")
    val deviceCode: DeviceCode = client.post("https://github.com/login/device/code") {
        accept(ContentType("application", "json"))
        parameter("client_id", "7eacdcb00a21e6d6a847")
    }

    debug("Device code is ${deviceCode.device_code}")
    debug("User code is ${deviceCode.user_code}")

    await("Enter the code ${deviceCode.user_code.bold()} on ${deviceCode.verification_uri} (press enter to open)")
    withContext(Dispatchers.IO) {
        Desktop.getDesktop().browse(URI.create(deviceCode.verification_uri))
    }

    log("Waiting for approval...")
    lateinit var authToken: String
    withContext(Dispatchers.IO) {
        launch {
            while (isActive) {
                delay(deviceCode.interval.seconds + 100.milliseconds)
                val response: AuthToken = client.post("https://github.com/login/oauth/access_token") {
                    accept(ContentType("application", "json"))
                    parameter("client_id", "7eacdcb00a21e6d6a847")
                    parameter("device_code", deviceCode.device_code)
                    parameter("grant_type", "urn:ietf:params:oauth:grant-type:device_code")
                }
                if (response.access_token != null) {
                    log("Got access token!")
                    authToken = response.access_token
                    cancel()
                }
            }
        }
    }
    client.close()
    return authToken
}

fun doManualEntry(): String {
    return KInquirer.promptInputPassword(
        "Enter your modrinth access token",
        hint = "Go to account > Settings > Security > Copy token to clipboard"
    )
}
