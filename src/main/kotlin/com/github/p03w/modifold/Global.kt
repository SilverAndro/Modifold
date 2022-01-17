package com.github.p03w.modifold

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

object Global {
    lateinit var args: ModifoldArgs
    val scope = CoroutineScope(Dispatchers.Default)

    val helpMenuPrologue = "Modifold is a Kotlin CLI program for moving curseforge mods to modrinth almost completely autonomously thanks to " +
            "the incredible modrinth API work by the modrinth team, " +
            "as well as the internal curseforge API proxy created and maintained by NikkyAI on Github, much thanks to both <3"
}
