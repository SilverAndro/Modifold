package com.github.p03w.modifold.core

import com.github.p03w.modifold.util.log
import com.github.p03w.modifold.networking.curseforge.CurseforgeProject
import com.github.p03w.modifold.networking.modrinth.ModrinthAPI
import com.github.p03w.modifold.networking.modrinth.ModrinthMod
import com.github.p03w.modifold.networking.modrinth.ModrinthModCreate
import com.github.p03w.modifold.util.withSpinner

fun createModrinthProjects(curseforgeProjects: List<CurseforgeProject>): MutableMap<CurseforgeProject, ModrinthMod> {
    log("Creating modrinth projects from curseforge projects")
    val out = mutableMapOf<CurseforgeProject, ModrinthMod>()

    curseforgeProjects.forEach { project ->
        withSpinner("Making modrinth project for ${project.display()}") {
            val mod = ModrinthAPI.makeMod(ModrinthModCreate.of(project), project)
            out[project] = mod
        }
    }

    return out
}
