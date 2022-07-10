package com.github.p03w.modifold.core

import com.github.p03w.modifold.cli.ModifoldArgs
import com.github.p03w.modifold.cli.ModifoldArgsContainer
import com.github.p03w.modifold.cli.log
import com.github.p03w.modifold.cli.withSpinner
import com.github.p03w.modifold.curseforge_api.CurseforgeAPI
import com.github.p03w.modifold.curseforge_schema.CurseforgeProject
import com.github.p03w.modifold.modrinth_api.ModrinthAPI
import com.github.p03w.modifold.modrinth_api.ModrinthProjectCreate
import com.github.p03w.modifold.modrinth_schema.ModrinthProject

fun createModrinthProjects(curseforgeProjects: List<CurseforgeProject>): MutableMap<CurseforgeProject, ModrinthProject> {
    log("Creating modrinth projects from curseforge projects")
    val out = mutableMapOf<CurseforgeProject, ModrinthProject>()

    curseforgeProjects.forEach { project ->
        withSpinner("Making modrinth project for ${project.display()}") {
            val description = if (ModifoldArgs.args.donts.contains(ModifoldArgsContainer.DONT.MIGRATE_DESCRIPTION)) {
                null
            } else {
                CurseforgeAPI.getProjectDescription(project.id)
            }

            val mod = ModrinthAPI.makeProject(
                ModrinthProjectCreate.of(
                    project,
                    description
                ), project
            )
            out[project] = mod
        }
    }

    return out
}
