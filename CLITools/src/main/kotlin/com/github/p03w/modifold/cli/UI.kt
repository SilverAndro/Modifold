package com.github.p03w.modifold.cli

import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.promptConfirm

fun userUnderstandsUsage(): Boolean {
    println(
        """
        ONLY USE THIS TOOL ON PROJECTS YOU OWN
        I built this for honest users who want to move off curseforge, I don't want to have to deal with people blaming me because someone stole their mods.
        Modrinth moderation also checks for ownership anyways, so you're unlikely to get anywhere
    """.trimIndent()
    )
    return KInquirer.promptConfirm("I understand this tool should only be used on my own projects")
}

fun userUnderstandsUsageAlternative(): Boolean {
    println(
        """
        ONLY USE THIS TOOL ON PROJECTS YOU OWN
        I built this for honest users who want to move off curseforge, and not for anyone else, as that has significant legal complications.
    """.trimIndent()
    )
    return KInquirer.promptConfirm("I understand this tool should only be used on my own projects")
}