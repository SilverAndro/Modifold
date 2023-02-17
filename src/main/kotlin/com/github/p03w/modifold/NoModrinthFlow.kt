package com.github.p03w.modifold

import com.github.p03w.modifold.cli.ModifoldArgs
import com.github.p03w.modifold.cli.ModifoldArgsContainer.*
import com.github.p03w.modifold.cli.log
import com.github.p03w.modifold.cli.userUnderstandsUsageAlternative
import com.github.p03w.modifold.core.collectCurseforgeProjects
import com.github.p03w.modifold.core.getFilesToTransfer
import com.github.p03w.modifold.core.getInfoToSaveLocally
import com.github.p03w.modifold.core.transferProjectFiles
import kotlin.system.exitProcess

fun noModrinthFlow() {
    if (!ModifoldArgs.args.donts.contains(DONT.VERIFY_END_USER)) {
        if (!userUnderstandsUsageAlternative()) {
            println("Quiting")
            exitProcess(1)
        }
    }

    val curseforgeProjects = collectCurseforgeProjects(ModifoldArgs.args.curseforgeIDs)

    if (curseforgeProjects.isEmpty()) {
        error("No curseforge projects")
    }

    log("Done getting projects")

    val toSave = getInfoToSaveLocally()
    val toTransfer = getFilesToTransfer()

    log("Beginning file \"transfer\"")
    transferProjectFiles(mutableMapOf(), toSave, toTransfer, true)

    log("Done!")
}