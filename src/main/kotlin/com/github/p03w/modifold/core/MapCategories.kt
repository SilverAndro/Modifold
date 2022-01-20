package com.github.p03w.modifold.core

import com.github.p03w.modifold.Global
import com.github.p03w.modifold.ModifoldArgs.DONT
import com.github.p03w.modifold.util.debug
import com.github.p03w.modifold.networking.curseforge.CurseforgeCategory
import com.github.p03w.modifold.networking.curseforge.CurseforgeFile
import com.github.p03w.modifold.networking.modrinth.ModrinthAPI
import com.github.p03w.modifold.util.warn

fun mapCategories(curseforgeCategories: List<CurseforgeCategory>): List<String> {
    val out = mutableSetOf<String>()

    val mapping = mapOf(
        "World Gen" to "worldgen",

        "Armor, Tools, and Weapons" to "equipment",

        "Technology" to "technology",
        "Redstone" to "technology",

        "Adventure and RPG" to "adventure",

        "Magic" to "magic",

        "Miscellaneous" to "misc",
        "Twitch Integration" to "misc",

        "Storage" to "storage",

        "Server Utility" to "utility",
        "Utility & QoL" to "utility",
        "Map and Information" to "utility",

        "API and Library" to "library",

        "Cosmetic" to "decoration",

        "Food" to "food",

        if (!Global.args.donts.contains(DONT.CURSE_MCREATOR)) "MCreator" to "cursed" else "MCreator" to "misc"
    )

    curseforgeCategories.forEach {
        val name = it.name
        if (mapping.containsKey(name)) {
            out.add(mapping[name]!!)
        } else {
            debug("No category mapping found for \"$name\"")
        }
    }

    return out.toList()
}

fun getLoaders(file: CurseforgeFile): List<String> {
    return file.gameVersion.filterNot { ModrinthAPI.MC_SEMVER.matches(it) || it.lowercase().contains("java") }.map { it.lowercase() }
}

fun checkForUnknownCategories() {
    val expected = setOf(
        "technology",
        "adventure",
        "magic",
        "utility",
        "decoration",
        "library",
        "cursed",
        "worldgen",
        "storage",
        "food",
        "equipment",
        "misc"
    )

    val found = ModrinthAPI.getPossibleCategories().toSet()

    val missing = found subtract expected

    if (missing.isNotEmpty()) {
        warn("Unknown modrinth categories: $missing")
        warn("These cannot be converted to with this version of the tool, make sure to apply them yourself if relevant")
    }
}
