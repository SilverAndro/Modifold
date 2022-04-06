package com.github.p03w.modifold.core

import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.promptInputPassword
import com.github.kinquirer.components.promptList
import com.github.p03w.modifold.cli.*
import com.github.p03w.modifold.modrinth_api.ModrinthAPI
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.awt.Desktop
import java.net.URI
import kotlin.system.exitProcess

fun loginToModrinth(): String {
    if (ModifoldArgs.args.modrinthToken != null) {
        log("Token was passed through CLI, using that")
        return ModifoldArgs.args.modrinthToken!!
    }

    val option = KInquirer.promptList(
        "How do you want to login to modrinth?",
        listOf("Web Flow", "Manual Token Entry", "CLI Arg")
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

fun doWebFlow(): String {
    if (!Desktop.getDesktop().isSupported(Desktop.Action.BROWSE) && ModifoldArgs.args.modrinthToken == null) {
        error("Your system does not support starting a web browser! Cannot do web flow.")
    }

    debug("Starting local webserver")

    var code: String? = null
    val server = embeddedServer(Netty, port = 2822) {
        routing {
            get("/") {
                code = this.context.parameters["code"]
                if (code != null) {
                    call.respondText("Thanks! You can close this tab and go back to modifold now")
                } else {
                    call.respondText(
                        status = HttpStatusCode(400, "No code parameter in URL"),
                        text = "No code parameter in URL"
                    )
                }
            }
        }
    }

    server.start()

    debug("Opening login flow")
    Desktop.getDesktop().browse(URI.create("${ModrinthAPI.root}/auth/init?url=http://127.0.0.1:2822"))

    await("Press enter to continue")

    debug("Code is $code")

    server.stop(200, 500)

    if (code == null) {
        error("No authorization code was provided!")
    } else {
        return code!!
    }
}

fun doManualEntry(): String {
    return KInquirer.promptInputPassword("Enter your modrinth access token", hint = "Go to account > Settings > Security > Copy token to clipboard")
}