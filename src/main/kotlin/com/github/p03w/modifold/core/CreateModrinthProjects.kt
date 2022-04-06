package com.github.p03w.modifold.core

import com.github.p03w.modifold.cli.log
import com.github.p03w.modifold.cli.withSpinner
import com.github.p03w.modifold.curseforge_schema.CurseforgeProject
import com.github.p03w.modifold.curseforge_schema.ModrinthProject
import com.github.p03w.modifold.modrinth_api.ModrinthAPI
import com.github.p03w.modifold.modrinth_api.ModrinthModCreate

fun createModrinthProjects(curseforgeProjects: List<CurseforgeProject>): MutableMap<CurseforgeProject, ModrinthProject> {
    log("Creating modrinth projects from curseforge projects")
    val out = mutableMapOf<CurseforgeProject, ModrinthProject>()

    curseforgeProjects.forEach { project ->
        withSpinner("Making modrinth project for ${project.display()}") {
            val mod = ModrinthAPI.makeMod(ModrinthModCreate.of(project), project)
            out[project] = mod
        }
    }

    return out
}
