package com.github.p03w.modifold

import com.github.p03w.modifold.ModifoldArgs.DONT
import com.github.p03w.modifold.core.*
import com.github.p03w.modifold.networking.modrinth.ModrinthAPI
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

    if (!Global.args.donts.contains(DONT.MAP_CATEGORIES)) checkForUnknownCategories()
    verifyDefaultArgs()

    AnsiConsole.systemInstall()

    if (!Global.args.donts.contains(DONT.VERIFY_END_USER)) {
        println("ONLY USE THIS TOOL ON PROJECTS YOU OWN")
        println("I built this for honest users who want to move off curseforge, I don't want to have to deal with people blaming me because someone stole their mods.")
        println("Modrinth moderation also checks for ownership anyways, so you're unlikely to get anywhere")
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

    log("Done! Don't forget to fix up the created mods and submit for approval!")

    AnsiConsole.systemUninstall()
}
