package com.github.p03w.modifold.core

import com.github.p03w.modifold.cli.ModifoldArgs
import com.github.p03w.modifold.cli.debug
import com.github.p03w.modifold.cli.error
import com.github.p03w.modifold.modrinth_api.ModrinthAPI

fun verifyDefaultArgs() {
    debug("Getting supported modrinth licenses")
    val possibleLicenses = ModrinthAPI.getPossibleLicenses()
    if (!possibleLicenses.contains(ModifoldArgs.args.defaultLicense)) {
        error(buildString {
            appendLine("Unsupported default license \"${ModifoldArgs.args.defaultLicense}\"")
            appendLine("If you want to use a license not on the following list, you must set it yourself per project")
            appendLine("Available licenses:")
            possibleLicenses.forEach {
                appendLine("- $it")
            }
        })
    }

    debug("Getting supported modrinth loaders")
    val possibleLoaders = ModrinthAPI.getPossibleLoaders().map { it.name }
    ModifoldArgs.args.defaultLoaders.forEach { loader ->
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
