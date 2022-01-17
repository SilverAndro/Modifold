package com.github.p03w.modifold.core

import com.github.p03w.modifold.log
import com.github.p03w.modifold.networking.curseforge.CurseforgeAPI
import com.github.p03w.modifold.networking.curseforge.CurseforgeProject
import com.github.p03w.modifold.networking.modrinth.ModrinthAPI
import com.github.p03w.modifold.networking.modrinth.ModrinthMod
import com.github.p03w.modifold.withSpinner
import java.time.Instant

fun transferProjectFiles(mapping: MutableMap<CurseforgeProject, ModrinthMod>) {
    mapping.keys.forEach { curseforgeProject ->
        log("Transferring files for project ${curseforgeProject.name} (${curseforgeProject.id})")
        CurseforgeAPI.getProjectFiles(curseforgeProject.id) {
            com.github.p03w.modifold.error("Could not get curseforge files for project ${curseforgeProject.name} (${curseforgeProject.id})")
        }
            .sortedBy { Instant.parse(it.fileDate + "Z") }
            .forEach { file ->
                withSpinner("Transferring ${file.fileName}") {
                    ModrinthAPI.makeModVersion(
                        ModrinthAPI.getModInfo(mapping[curseforgeProject]!!.id),
                        file,
                        curseforgeProject
                    )
                }
            }
        println()
    }
}
