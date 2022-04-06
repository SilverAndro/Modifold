package com.github.p03w.modifold.core

import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.ListViewOptions
import com.github.kinquirer.components.promptList
import com.github.p03w.modifold.cli.*
import com.github.p03w.modifold.cli.ModifoldArgsContainer.DONT
import com.github.p03w.modifold.curseforge_schema.CurseforgeProject
import com.github.p03w.modifold.curseforge_schema.ModrinthUser

fun matchExistingProjects(modrinthUser: ModrinthUser, curseforgeProjects: MutableList<CurseforgeProject>) {
    if (!ModifoldArgs.args.donts.contains(DONT.VERIFY_EXISTING)) {
        val modrinthProjects = collectModrinthProjects(modrinthUser)
        if (modrinthProjects.isNotEmpty()) {
            val finder = SimilarProjectFinder(modrinthProjects, curseforgeProjects)

            log("\nYou will now be given the closest match on modrinth for each curseforge project.\n")

            val existing = mutableListOf<CurseforgeProject>()
            curseforgeProjects.forEach {
                val similar = finder.findSimilar(it)
                if (similar != null) {
                    log("Possible existing modrinth project found for curseforge project ${it.display()}:")
                    log("${similar.display()}: ${similar.description}")
                    val option = KInquirer.promptList(
                        "Is this a match?",
                        listOf("Yes", "No", "Ignore"),
                        "Ignore makes modifold not consider this modrinth project again for matches",
                        viewOptions = ListViewOptions(questionMarkPrefix = "")
                    )
                    when (option) {
                        "Yes" -> {
                            existing.add(it); finder.ignoredIDs.add(similar.id)
                        }
                        "No" -> {}
                        "Ignore" -> finder.ignoredIDs.add(similar.id)
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
