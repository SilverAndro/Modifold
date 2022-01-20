package com.github.p03w.modifold.core

import com.github.p03w.modifold.Global
import com.github.p03w.modifold.networking.modrinth.ModrinthAPI
import com.github.p03w.modifold.util.debug
import com.github.p03w.modifold.util.error

fun verifyDefaultArgs() {
    debug("Getting supported modrinth licenses")
    val possibleLicenses = ModrinthAPI.getPossibleLicenses()
    if (!possibleLicenses.contains(Global.args.defaultLicense)) {
        error(buildString {
            appendLine("Unsupported default license \"${Global.args.defaultLicense}\"")
            appendLine("If you want to use a license not on the following list, you must set it yourself per project")
            appendLine("Available licenses:")
            possibleLicenses.forEach {
                appendLine("- $it")
            }
        })
    }

    debug("Getting supported modrinth loaders")
    val possibleLoaders = ModrinthAPI.getPossibleLoaders()
    Global.args.defaultLoaders.forEach { loader ->
        if (!possibleLoaders.contains(loader)) {
            error(buildString {
                appendLine("Unsupported default loader \"$loader\"")
                appendLine("Available loaders:")
                possibleLoaders.forEach {
                    appendLine("- $it")
                }
            })
        }
    }
}
