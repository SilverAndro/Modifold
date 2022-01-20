package com.github.p03w.modifold.core

import com.github.p03w.modifold.util.log
import com.github.p03w.modifold.networking.modrinth.ModrinthAPI
import com.github.p03w.modifold.networking.modrinth.ModrinthMod
import com.github.p03w.modifold.networking.modrinth.ModrinthUser
import com.github.p03w.modifold.util.withSpinner

fun collectModrinthProjects(modrinthUser: ModrinthUser): MutableList<ModrinthMod> {
    log("Collecting existing modrinth projects")
    val modrinthProjects = mutableListOf<ModrinthMod>()
    ModrinthAPI.getUserMods(modrinthUser).forEach { id ->
        val projectData = withSpinner("Collecting project info for project $id") {
            ModrinthAPI.getModInfo(id)
        }
        modrinthProjects.add(projectData)
    }

    return modrinthProjects
}
