package com.github.p03w.modifold

import com.github.p03w.modifold.core.collectCurseforgeProjects
import com.github.p03w.modifold.core.createModrinthProjects
import com.github.p03w.modifold.core.matchExistingProjects
import com.github.p03w.modifold.core.transferProjectFiles
import com.github.p03w.modifold.networking.modrinth.ModrinthAPI
import com.github.p03w.modifold.core.loginToModrinth
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.DefaultHelpFormatter
import com.xenomachina.argparser.mainBody
import org.fusesource.jansi.AnsiConsole
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    Global.args = mainBody {
        ArgParser(args, helpFormatter = DefaultHelpFormatter(prologue = Global.helpMenuPrologue))
            .parseInto(::ModifoldArgs)
    }

    debug("Getting supported modrinth licenses")
    val possibleLicenses = ModrinthAPI.getPossibleLicenses()
    if (!possibleLicenses.contains(Global.args.defaultLicense)) {
        error(buildString {
            appendLine("Unsupported default license \"${Global.args.defaultLicense}\"")
            appendLine("If you want to use a license not on the following list, you must set it yourself per project")
            appendLine("Available licenses:")
            possibleLicenses.forEach {
                appendLine("- $it")
            }
        })
    }

    AnsiConsole.systemInstall()

    if (!Global.args.noVerifyEndUser) {
        println("ONLY USE THIS TOOL ON PROJECTS YOU OWN, PLEASE")
        println("I built this for honest users who want to move off curseforge, I don't want to have to deal with people blaming me because someone stole their mods.")
        println("That would probably hurt modrinth's reputation as well.")
        println("Type \"yes\" if you understand this should only be used on your own projects to continue execution")
        if (readln() != "yes") {
            println("Quiting")
            exitProcess(1)
        } else {
            println("Continuing")
        }
    }

    ModrinthAPI.AuthToken = loginToModrinth()

    debug("Verifying and standardizing modrinth user")
    val modrinthUser = ModrinthAPI.getUser()
    log("Modrinth login successful, user is ${modrinthUser.username} (${modrinthUser.id})")

    val curseforgeProjects = collectCurseforgeProjects(Global.args.curseforgeIDs)

    matchExistingProjects(modrinthUser, curseforgeProjects)
    if (curseforgeProjects.isEmpty()) {
        error("No projects to transfer")
    }
    log("Done matching projects, beginning transfer")

    val projectMapping = createModrinthProjects(curseforgeProjects)

    log("Beginning file transfer")
    transferProjectFiles(projectMapping)

    AnsiConsole.systemUninstall()
}