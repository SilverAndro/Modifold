package com.github.p03w.modifold.conversion

import com.github.p03w.modifold.cli.ModifoldArgs
import com.github.p03w.modifold.cli.ModifoldArgsContainer.DONT
import com.github.p03w.modifold.cli.debug
import com.github.p03w.modifold.cli.warn
import com.github.p03w.modifold.curseforge_schema.CurseforgeCategory

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

        if (!ModifoldArgs.args.donts.contains(DONT.CURSE_MCREATOR)) "MCreator" to "cursed" else "MCreator" to "misc"
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

fun checkForUnknownCategories(known: Set<String>) {
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
        "misc",
        "optimization"
    )

    val missing = known subtract expected

    if (missing.isNotEmpty()) {
        warn("Unknown modrinth categories: $missing")
        warn("These cannot be converted to with this version of the tool, make sure to apply them yourself if relevant")
    }
}
