package com.github.p03w.modifold.core

import com.github.p03w.modifold.networking.curseforge.CurseforgeProject
import com.github.p03w.modifold.networking.modrinth.ModrinthMod
import com.github.p03w.modifold.withSpinner
import com.kennethlange.nlp.similarity.TextSimilarity
import com.kennethlange.nlp.similarity.TokenizerImpl

class SimilarProjectFinder(val modrinthProjects: List<ModrinthMod>, curseforgeProjects: List<CurseforgeProject>) {
    private val ts = TextSimilarity(TokenizerImpl(IGNORED_WORDS))

    val ignoredIDs: MutableSet<String> = mutableSetOf()

    init {
        withSpinner("Pre-calculating project tf-idf weight") {
            modrinthProjects.forEach {
                ts.addDocument("$MODRINTH_PREFIX:${it.id}", it.slug + it.description + it.body)
            }
            curseforgeProjects.forEach {
                ts.addDocument("$CURSEFORGE_PREFIX:${it.id}", it.slug + it.name + it.summary)
            }

            ts.calculate()
        }
    }

    fun findSimilar(cfProject: CurseforgeProject): ModrinthMod? {
        val closest = ts.getSimilarDocuments("$CURSEFORGE_PREFIX:${cfProject.id}").firstOrNull {
            it.startsWith(MODRINTH_PREFIX) && !ignoredIDs.contains(it.split(":")[1])
        } ?: return null

        val id = closest.split(":")[1]
        return modrinthProjects.first { it.id == id }
    }

    companion object {
        const val MODRINTH_PREFIX = "MODRINTH"
        const val CURSEFORGE_PREFIX = "CURSEFORGE"

        val IGNORED_WORDS = hashSetOf(
            // Default ones, have to copy here
            "a","able","about","across","after","all","almost","also","am","among","an","and","any","are","as","at","be","because","been","but","by","can","cannot","could","dear","did","do","does","either","else","ever","every","for","from","get","got","had","has","have","he","her","hers","him","his","how","however","i","if","in","into","is","it","its","just","least","let","like","likely","may","me","might","most","must","my","neither","no","nor","not","of","off","often","on","only","or","other","our","own","rather","said","say","says","she","should","since","so","some","than","that","the","their","them","then","there","these","they","this","tis","to","too","twas","us","wants","was","we","were","what","when","where","which","while","who","whom","why","will","with","would","yet","you","your",
            // Custom words
            "minecraft",
            "mod",
            "allow", "allows",
            "add", "adds",
            "make", "makes",
            "recipe", "recipes",
            "requires",
            "discord",
            "config", "configuration",
            "github",
            "curseforge",
            "wiki",
            "data",
            "features",
            "optional",
            "crafting",
            "crash",
            "bug",
            "report"
        )
    }
}
