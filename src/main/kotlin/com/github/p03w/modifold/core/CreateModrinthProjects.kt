package com.github.p03w.modifold.core

import com.github.p03w.modifold.log
import com.github.p03w.modifold.networking.curseforge.CurseforgeProject
import com.github.p03w.modifold.networking.modrinth.ModrinthAPI
import com.github.p03w.modifold.networking.modrinth.ModrinthMod
import com.github.p03w.modifold.networking.modrinth.ModrinthModCreate
import com.github.p03w.modifold.networking.modrinth.ModrinthTeamMember
import com.github.p03w.modifold.withSpinner

fun createModrinthProjects(curseforgeProjects: List<CurseforgeProject>): MutableMap<CurseforgeProject, ModrinthMod> {
    log("Creating modrinth projects from curseforge projects")
    val user = ModrinthAPI.getUser()

    val out = mutableMapOf<CurseforgeProject, ModrinthMod>()

    curseforgeProjects.forEach {
        withSpinner("Making modrinth project for ${it.name} (${it.id})") {
            val mod = ModrinthAPI.makeMod(ModrinthModCreate.of(it, user), it)
            out[it] = mod
        }
    }

    return out
}
