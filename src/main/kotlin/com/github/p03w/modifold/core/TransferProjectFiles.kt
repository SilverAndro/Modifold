package com.github.p03w.modifold.core

import com.github.p03w.modifold.cli.debug
import com.github.p03w.modifold.cli.error
import com.github.p03w.modifold.cli.withSpinner
import com.github.p03w.modifold.curseforge_api.CurseforgeAPI
import com.github.p03w.modifold.curseforge_schema.CurseforgeProject
import com.github.p03w.modifold.curseforge_schema.ModrinthProject
import com.github.p03w.modifold.modrinth_api.ModrinthAPI
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.Instant
import java.util.*

fun transferProjectFiles(mapping: MutableMap<CurseforgeProject, ModrinthProject>, toSave: EnumSet<InformationToSave>) {
    fun File.make(): File {
        mkdirs()
        return this
    }

    mapping.keys.forEach { project ->
        val files = withSpinner("Collecting files for ${project.display()})") {
            CurseforgeAPI.getProjectFiles(project.id) {
                error("Could not get curseforge files for project ${project.display()}")
            }.sortedBy { Instant.parse(it.fileDate + "Z") }
        }

        if (toSave.contains(InformationToSave.IMAGES)) {
            debug("Saving attachments for project")
            project.attachments.forEach {
                debug("Saving ${it.url} as ${it.url.hashCode()}")
                val localCopy = File("ModifoldSaved/${project.display()}/images/${it.url.hashCode()}").make()
                Files.copy(URL(it.url).openStream(), localCopy.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        }

        files.forEach { file ->
            withSpinner("Transferring ${file.fileName}") {
                val buffered = CurseforgeAPI.getFileStream(file).buffered()

                val stream = if (toSave.contains(InformationToSave.VERSIONS)) {
                    debug("Saving version to disk")
                    val localCopy = File("ModifoldSaved/${project.display()}/versions/${file.fileName}").make()
                    Files.copy(buffered, localCopy.toPath(), StandardCopyOption.REPLACE_EXISTING)
                    localCopy.inputStream().buffered()
                } else { buffered }

                ModrinthAPI.makeProjectVersion(
                    ModrinthAPI.getProjectInfo(mapping[project]!!.id),
                    file,
                    stream,
                    project
                )
            }
        }
        println()
    }
}
