package com.github.p03w.modifold

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.SystemExitException
import com.xenomachina.argparser.default

class ModifoldArgs(parser: ArgParser) {
    //
    // Optional Args
    //

    val debug by parser.flagging(
        "-d", "--debug", "-v", "--verbose",
        help = "Enable debug/verbose mode"
    )

    val noVerifyEndUser by parser.flagging(
        "--no_verify_end_user",
        help = "Skip the startup verify/message that makes sure these are your mods and that you're absolutely sure those are the right params"
    )

    val noVerifyExisting by parser.flagging(
        "--no_verify_existing",
        help = "If the initial setup of checking for possibly matching projects should be skipped. Use this if you're new to modrinth and know theres nothing there"
    )

    val curseforgeSpeed by parser.storing(
        "-s", "--speed",
        help = "Speed at which to make requests to curseforge, in ms delay between requests. " +
                "Curseforge doesn't document their rate-limits so this is my solution. " +
                "Defaults to 2000ms (2 seconds)"
    ) { toInt() }.default(2000)

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
