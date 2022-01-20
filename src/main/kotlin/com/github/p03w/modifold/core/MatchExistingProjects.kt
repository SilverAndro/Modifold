package com.github.p03w.modifold.core

import com.github.p03w.modifold.Global
import com.github.p03w.modifold.ModifoldArgs.DONT
import com.github.p03w.modifold.console.highlight
import com.github.p03w.modifold.util.debug
import com.github.p03w.modifold.util.log
import com.github.p03w.modifold.networking.curseforge.CurseforgeProject
import com.github.p03w.modifold.networking.modrinth.ModrinthUser
import com.github.p03w.modifold.util.requireInputOf

fun matchExistingProjects(modrinthUser: ModrinthUser, curseforgeProjects: MutableList<CurseforgeProject>) {
    if (!Global.args.donts.contains(DONT.VERIFY_EXISTING)) {
        val modrinthProjects = collectModrinthProjects(modrinthUser)
        if (modrinthProjects.isNotEmpty()) {
            val finder = SimilarProjectFinder(modrinthProjects, curseforgeProjects)

            log(
                "\n${"---".highlight()}\n" +
                        "You will now be given the closest match on modrinth for each curseforge project.\n" +
                        "If its a match, enter y\n" +
                        "If not, enter n\n" +
                        "To stop considering this modrinth project as a possibility for matches, enter i\n" +
                        "${"---".highlight()}\n"
            )

            val existing = mutableListOf<CurseforgeProject>()
            curseforgeProjects.forEach {
                val similar = finder.findSimilar(it)
                if (similar != null) {
                    log("Possible existing modrinth project found for curseforge project ${it.display()}):")
                    log("${similar.display()}: ${similar.description}")
                    log("Is this a match? (${"[y]".highlight()}es/${"[n]".highlight()}o/${"[i]".highlight()}gnore)")
                    when (requireInputOf("y", "n", "i")) {
                        "y" -> {
                            existing.add(it); finder.ignoredIDs.add(similar.id)
                        }
                        "no" -> {}
                        "i" -> finder.ignoredIDs.add(similar.id)
                    }
                    println()
                } else {
                    debug("Found no similar mods for ${it.name}")
                }
            }
            curseforgeProjects.removeAll { existing.contains(it) }
        }
    }
}
