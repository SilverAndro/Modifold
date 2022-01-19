package com.github.p03w.modifold.core

import com.github.p03w.modifold.log
import com.github.p03w.modifold.networking.curseforge.CurseforgeProject
import com.github.p03w.modifold.networking.modrinth.ModrinthAPI
import com.github.p03w.modifold.networking.modrinth.ModrinthMod
import com.github.p03w.modifold.networking.modrinth.ModrinthModCreate
import com.github.p03w.modifold.withSpinner

fun createModrinthProjects(curseforgeProjects: List<CurseforgeProject>): MutableMap<CurseforgeProject, ModrinthMod> {
    log("Creating modrinth projects from curseforge projects")
    val out = mutableMapOf<CurseforgeProject, ModrinthMod>()

    curseforgeProjects.forEach {
        withSpinner("Making modrinth project for ${it.display}") {
            val mod = ModrinthAPI.makeMod(ModrinthModCreate.of(it), it)
            out[it] = mod
        }
    }

    return out
}
