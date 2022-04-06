package com.github.p03w.modifold.core

import com.github.p03w.modifold.cli.ModifoldArgs
import com.github.p03w.modifold.cli.log
import com.github.p03w.modifold.cli.warn
import com.github.p03w.modifold.cli.withSpinner
import com.github.p03w.modifold.curseforge_api.CurseforgeAPI
import com.github.p03w.modifold.curseforge_schema.CurseforgeProject

fun collectCurseforgeProjects(ids: List<Int>): MutableList<CurseforgeProject> {
    log("Collecting curseforge projects")
    val curseforgeProjects = mutableListOf<CurseforgeProject>()
    ids.forEach { id ->
        val projectData = withSpinner("Collecting curseforge project info for project $id") {
            CurseforgeAPI.getProjectData(id)
        } ?: run {
            warn("Could not get curseforge project info for project $id")
            return@forEach
        }

        if (projectData.categorySection.name != "Mods") {
            warn("Skipping project id $id (${projectData.name}) because its category is \"${projectData.categorySection.name}\" not \"Mods\"")
        } else if (!projectData.authors.any { it.name.lowercase() == ModifoldArgs.args.curseforgeUsername.lowercase() }) {
            warn("Skipping project id $id (${projectData.name}) because none of its authors are ${ModifoldArgs.args.curseforgeUsername}")
        } else {
            curseforgeProjects.add(projectData)
        }
    }
    return curseforgeProjects
}
