package com.github.p03w.modifold.core

import com.github.p03w.modifold.cli.error
import com.github.p03w.modifold.cli.withSpinner
import com.github.p03w.modifold.curseforge_api.CurseforgeAPI
import com.github.p03w.modifold.curseforge_schema.CurseforgeProject
import com.github.p03w.modifold.curseforge_schema.ModrinthProject
import com.github.p03w.modifold.modrinth_api.ModrinthAPI
import java.time.Instant

fun transferProjectFiles(mapping: MutableMap<CurseforgeProject, ModrinthProject>) {
    mapping.keys.forEach { project ->
        val files = withSpinner("Collecting files for ${project.display()})") {
            CurseforgeAPI.getProjectFiles(project.id) {
                error("Could not get curseforge files for project ${project.display()}")
            }.sortedBy { Instant.parse(it.fileDate + "Z") }
        }

        files.forEach { file ->
            withSpinner("Transferring ${file.fileName}") {
                ModrinthAPI.makeModVersion(
                    ModrinthAPI.getProjectInfo(mapping[project]!!.id),
                    file,
                    project
                )
            }
        }
        println()
    }
}
