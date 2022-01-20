package com.github.p03w.modifold.core

import com.github.p03w.modifold.networking.curseforge.CurseforgeAPI
import com.github.p03w.modifold.networking.curseforge.CurseforgeProject
import com.github.p03w.modifold.networking.modrinth.ModrinthAPI
import com.github.p03w.modifold.networking.modrinth.ModrinthMod
import com.github.p03w.modifold.util.error
import com.github.p03w.modifold.util.withSpinner
import java.time.Instant

fun transferProjectFiles(mapping: MutableMap<CurseforgeProject, ModrinthMod>) {
    mapping.keys.forEach { project ->
        val files = withSpinner("Collecting files for ${project.display()})") {
            CurseforgeAPI.getProjectFiles(project.id) {
                error("Could not get curseforge files for project ${project.display()}")
            }.sortedBy { Instant.parse(it.fileDate + "Z") }
        }

        files.forEach { file ->
            withSpinner("Transferring ${file.fileName}") {
                ModrinthAPI.makeModVersion(
                    ModrinthAPI.getModInfo(mapping[project]!!.id),
                    file,
                    project
                )
            }
        }
        println()
    }
}
