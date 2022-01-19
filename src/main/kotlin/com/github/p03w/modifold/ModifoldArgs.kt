package com.github.p03w.modifold

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.SystemExitException
import com.xenomachina.argparser.default
import java.util.*

class ModifoldArgs(parser: ArgParser) {
    //
    // Optional Args
    //

    val debug by parser.flagging(
        "-v", "--verbose",
        help = "Enable debug/verbose mode"
    )

    val noVerifyEndUser by parser.flagging(
        "--no_verify_end_user",
        help = "Skip the startup verify/message that makes sure these are your mods and that you're absolutely sure those are the right params"
    )

    val noVerifyExisting by parser.flagging(
        "--no_verify_existing",
        help = "If the initial setup of checking for possibly matching projects should be skipped. " +
                "Use this if you know theres nothing there that would match"
    )

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
        help = "What loader to add to mods by default, can be repeated",
        argName = "DEFAULT_LOADER"
    ) { lowercase(Locale.getDefault()) }

    //
    // Required args
    //

    val curseforgeUsername by parser.positional(
        "CURSEFORGE_USERNAME",
        help = "The curseforge username so the program only moves projects you own"
    )

    val curseforgeIDs by parser.positionalList(
        "CURSEFORGE_PROJECT_ID",
        help = "Adds a curseforge project to transfer by numerical ID"
    ) { toInt() }.addValidator {
        if (value.isEmpty()) throw SystemExitException("Must specify at least one curseforge project ID", 1)
    }
}
