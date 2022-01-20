package com.github.p03w.modifold.core

import com.github.p03w.modifold.Global
import com.github.p03w.modifold.util.log
import com.github.p03w.modifold.networking.curseforge.CurseforgeAPI
import com.github.p03w.modifold.networking.curseforge.CurseforgeProject
import com.github.p03w.modifold.util.warn
import com.github.p03w.modifold.util.withSpinner

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
        } else if (!projectData.authors.any { it.name.lowercase() == Global.args.curseforgeUsername.lowercase() }) {
            warn("Skipping project id $id (${projectData.name}) because none of its authors are ${Global.args.curseforgeUsername}")
        } else {
            curseforgeProjects.add(projectData)
        }
    }
    return curseforgeProjects
}
