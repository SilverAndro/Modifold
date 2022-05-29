package com.github.p03w.modifold.core

import com.github.p03w.modifold.cli.log
import com.github.p03w.modifold.cli.withSpinner
import com.github.p03w.modifold.modrinth_schema.ModrinthProject
import com.github.p03w.modifold.modrinth_schema.ModrinthUser
import com.github.p03w.modifold.modrinth_api.ModrinthAPI

fun collectModrinthProjects(modrinthUser: ModrinthUser): MutableList<ModrinthProject> {
    log("Collecting existing modrinth projects")
    val modrinthProjects = mutableListOf<ModrinthProject>()
    ModrinthAPI.getUserProjects(modrinthUser).forEach { project ->
        val projectData = withSpinner("Collecting project info for project ${project.display()}") {
            ModrinthAPI.getProjectInfo(project.id)
        }
        modrinthProjects.add(projectData)
    }

    return modrinthProjects
}
