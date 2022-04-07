package com.github.p03w.modifold.core

import com.github.p03w.modifold.cli.*
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

        val modrinthProject = ModrinthAPI.getProjectInfo(mapping[project]!!.id)

        project.attachments.forEach nextAttach@{
            if (toSave.contains(InformationToSave.IMAGES) && it.isDefault) {
                debug("Saving ${it.url} as project_icon.png")
                val localCopy = File("ModifoldSaved/${project.display()}/images/project_icon.png").make()
                Files.copy(URL(it.url).openStream(), localCopy.toPath(), StandardCopyOption.REPLACE_EXISTING)
                return@nextAttach
            }

            withSpinner("Transferring ${it.title} to gallery") { _ ->
                if (toSave.contains(InformationToSave.IMAGES)) {
                    debug("Saving ${it.url} as ${it.title}.${it.getExt()}")
                    val localCopy = File("ModifoldSaved/${project.display()}/images/${it.title}.${it.getExt()}").make()
                    Files.copy(URL(it.url).openStream(), localCopy.toPath(), StandardCopyOption.REPLACE_EXISTING)
                }
                ModrinthAPI.addProjectImage(modrinthProject, it)
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

                try {
                    ModrinthAPI.makeProjectVersion(
                        modrinthProject,
                        file,
                        stream,
                        project
                    )
                } catch (err: Throwable) {
                    log("Failed to upload ${file.fileName}! ${err.localizedMessage}".error().toString())
                    it.fail()
                }
            }
        }
        println()
    }
}
