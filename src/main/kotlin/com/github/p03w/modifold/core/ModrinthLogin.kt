package com.github.p03w.modifold.core

import com.github.p03w.modifold.Global
import com.github.p03w.modifold.util.await
import com.github.p03w.modifold.util.debug
import com.github.p03w.modifold.util.error
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.awt.Desktop
import java.net.URI

fun loginToModrinth(): String {
    if (!Desktop.getDesktop().isSupported(Desktop.Action.BROWSE) && Global.args.modrinthToken == null) {
        error("Your system does not support starting a web browser, but is missing an access token passed through the --token argument")
    }
    if (Global.args.modrinthToken != null) {
        debug("Token was passed manually, using that")
        return Global.args.modrinthToken!!
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

    await("Your web browser will open a webpage to login to modrinth, please complete the flow and come back here (enter to continue)")

    debug("Opening login flow")
    Desktop.getDesktop().browse(URI.create("https://api.modrinth.com/api/v1/auth/init?url=http://127.0.0.1:2822"))

    await("Press enter here when you return")

    debug("Code is $code")

    server.stop(200, 500)

    if (code == null) {
        error("No authorization code was provided!")
    } else {
        return code!!
    }
    throw IllegalStateException("Invalid logic flow, modrinth login should throw error or return, not reach here.")
}
