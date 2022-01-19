package com.github.p03w.modifold.core

import com.github.p03w.modifold.Global
import com.github.p03w.modifold.debug
import com.github.p03w.modifold.networking.modrinth.ModrinthAPI

fun verifyDefaultLicense() {
    debug("Getting supported modrinth licenses")
    val possibleLicenses = ModrinthAPI.getPossibleLicenses()
    if (!possibleLicenses.contains(Global.args.defaultLicense)) {
        com.github.p03w.modifold.error(buildString {
            appendLine("Unsupported default license \"${Global.args.defaultLicense}\"")
            appendLine("If you want to use a license not on the following list, you must set it yourself per project")
            appendLine("Available licenses:")
            possibleLicenses.forEach {
                appendLine("- $it")
            }
        })
    }
}
