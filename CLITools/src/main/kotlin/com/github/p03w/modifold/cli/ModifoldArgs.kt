package com.github.p03w.modifold.cli

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import java.util.*

object ModifoldArgs {
    lateinit var args: ModifoldArgsContainer
}

class ModifoldArgsContainer(parser: ArgParser) {
    //
    // Optional Args
    //

    val debug by parser.flagging(
        "-v", "--verbose",
        help = "Enable debug/verbose mode"
    )

    val modrinthToken by parser.storing(
        "--token",
        help = "Sets the modrinth access token manually, bypassing the web-auth flow"
    ).default(null)

    @Suppress("SpellCheckingInspection")
    val donts by parser.adding(
        "--dont",
        help = "Things to not do (can be repeated), " +
                "pass 0 to disable startup confirm, " +
                "1 to disable checking existing modrinth mods, " +
                "2 to change the mcreator->cursed mapping to mcreator->misc, " +
                "3 to disable category mapping entirely, " +
                "4 to disable copying links",
        argName = "DONT_INDEX"
    ) { DONT.values()[toInt()] }

    val curseforgeSpeed by parser.storing(
        "-s", "--speed",
        help = "Speed at which to make requests to curseforge, in minimum ms delay between requests. " +
                "Curseforge doesn't document their rate-limits so this is my solution. " +
                "Defaults to 2000ms (2 seconds)"
    ) { toInt() }.default(2000)

    val defaultLicense by parser.storing(
        "-l", "--license",
        help = "The default license for newly created projects, i.e mpl, lgpl-3, apache, cc0. Defaults to ARR"
    ) { lowercase(Locale.getDefault()) }.default("arr")

    val discordServer by parser.storing(
        "-d", "--discord",
        help = "The discord server link to add to each mod page"
    ).default("")

    val defaultLoaders by parser.adding(
        "-L", "--loader",
        help = "What loader to add to mods by default if no loader is specified, defaults to forge",
        argName = "DEFAULT_LOADER"
    ) { lowercase(Locale.getDefault()) }.default(listOf("forge"))

    val fileLimit by parser.storing(
        "-f", "--file-limit",
        help = "Limits how many files to transfer (recent first), -1 (default) to disable/transfer all"
    ) { toInt() }.default(-1)

    //
    // Required args
    //

    val curseforgeUsername by parser.positional(
        "CURSEFORGE_USERNAME",
        help = "The curseforge username so the program only moves projects you own"
    )

    val curseforgeIDs by parser.positionalList(
        "CURSEFORGE_PROJECT_ID",
        help = "Adds a curseforge project to transfer by numerical ID",
        sizeRange = 1..Int.MAX_VALUE
    ) { toInt() }

    enum class DONT {
        VERIFY_END_USER,
        VERIFY_EXISTING,
        CURSE_MCREATOR,
        MAP_CATEGORIES,
        COPY_LINKS
    }
}
