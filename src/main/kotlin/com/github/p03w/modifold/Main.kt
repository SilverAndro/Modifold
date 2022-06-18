package com.github.p03w.modifold

import com.github.p03w.modifold.cli.*
import com.github.p03w.modifold.cli.ModifoldArgsContainer.DONT
import com.github.p03w.modifold.conversion.checkForUnknownCategories
import com.github.p03w.modifold.core.*
import com.github.p03w.modifold.modrinth_api.ModrinthAPI
import com.xenomachina.argparser.*
import org.fusesource.jansi.AnsiConsole
import kotlin.system.exitProcess

suspend fun main(args: Array<String>) {
    AnsiConsole.systemInstall()

    try {
        ModifoldArgs.args = ArgParser(args, helpFormatter = DefaultHelpFormatter(prologue = Global.helpMenuPrologue)).parseInto(::ModifoldArgsContainer)
    } catch (err: SystemExitException) {
        log("See the help menu (-h or --help) for usage")
        mainBody {
            throw err
        }
    }

    if (!ModifoldArgs.args.donts.contains(DONT.MAP_CATEGORIES)) {
        checkForUnknownCategories(ModrinthAPI.getPossibleCategories().mapTo(mutableSetOf()) {it.name})
    }
    verifyDefaultArgs()

    if (!ModifoldArgs.args.donts.contains(DONT.VERIFY_END_USER)) {
        if (!userUnderstandsUsage()) {
            println("Quiting")
            exitProcess(1)
        }
    }


    // Start actual flow with login
    ModrinthAPI.AuthToken = loginToModrinth()

    debug("Verifying and standardizing modrinth user")
    val modrinthUser = try {
        ModrinthAPI.getUser()
    } catch (err: Throwable) {
        error("Failed to login! Invalid access token?", err)
    }
    log("Modrinth login successful, user is ${modrinthUser.username} (${modrinthUser.id})")

    val curseforgeProjects = collectCurseforgeProjects(ModifoldArgs.args.curseforgeIDs)

    if (curseforgeProjects.isEmpty()) {
        error("No projects to transfer")
    }
    matchExistingProjects(modrinthUser, curseforgeProjects)
    if (curseforgeProjects.isEmpty()) {
        error("No projects to transfer")
    }

    log("Done matching projects")

    val toSave = getInfoToSaveLocally()
    val toTransfer = getFilesToTransfer()

    val projectMapping = createModrinthProjects(curseforgeProjects)

    log("Beginning file transfer")
    transferProjectFiles(projectMapping, toSave, toTransfer)

    log("Done! Don't forget to fix up the created mods and submit for approval!")

    AnsiConsole.systemUninstall()
}
