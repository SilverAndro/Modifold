package com.github.p03w.modifold.core

import com.github.p03w.modifold.cli.*
import com.github.p03w.modifold.curseforge_api.CurseforgeAPI
import com.github.p03w.modifold.curseforge_schema.CurseforgeProject
import com.github.p03w.modifold.modrinth_api.ModrinthAPI
import com.github.p03w.modifold.modrinth_schema.ModrinthProject
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.Instant
import java.util.*

private fun File.make(): File {
    mkdirs()
    return this
}

fun transferProjectFiles(
    mapping: MutableMap<CurseforgeProject, ModrinthProject>,
    toSave: EnumSet<InformationToSave>,
    toTransfer: FileSet
) {
    mapping.keys.forEach { project ->
        val files = withSpinner("Collecting files for ${project.display()})") {
            CurseforgeAPI.getProjectFiles(project.id, toTransfer == FileSet.ALL) {
                error("Could not get curseforge files for project ${project.display()}")
            }.sortedBy { Instant.parse(it.fileDate) }
        }.let {
            if (ModifoldArgs.args.fileLimit > -1) {
                it.takeLast(ModifoldArgs.args.fileLimit)
            } else {
                it
            }
        }

        val modrinthProject = ModrinthAPI.getProjectInfo(mapping[project]!!.id)

        // Save logo
        if (toSave.contains(InformationToSave.IMAGES)) {
            debug("Saving ${project.logo.url} as project_icon.png")
            val localCopy = File("ModifoldSaved/${project.display()}/images/project_icon.png").make()
            Files.copy(URL(project.logo.url).openStream(), localCopy.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }

        // Transfer screenshots
        project.screenshots.forEach nextAttach@{
            withSpinner("Transferring ${it.title} to gallery") { spinner ->
                if (toSave.contains(InformationToSave.IMAGES)) {
                    debug("Saving ${it.url} as ${it.title}.${it.getExt()}")
                    val localCopy = File("ModifoldSaved/${project.display()}/images/${it.title}.${it.getExt()}").make()
                    Files.copy(URL(it.url).openStream(), localCopy.toPath(), StandardCopyOption.REPLACE_EXISTING)
                }
                try {
                    ModrinthAPI.addProjectImage(modrinthProject, it)
                } catch (err: Throwable) {
                    log("Failed to add ${it.title} to gallery! ${err.localizedMessage}".error().toString())
                    spinner.fail()
                }
            }

        }

        files.forEach { file ->
            withSpinner("Transferring ${file.fileName}") { spinner ->
                val buffered = CurseforgeAPI.getFileStream(file).buffered()

                val stream = if (toSave.contains(InformationToSave.VERSIONS)) {
                    debug("Saving version to disk")
                    val localCopy = File("ModifoldSaved/${project.display()}/versions/${file.fileName}").make()
                    Files.copy(buffered, localCopy.toPath(), StandardCopyOption.REPLACE_EXISTING)
                    localCopy.inputStream().buffered()
                } else {
                    buffered
                }

                try {
                    ModrinthAPI.makeProjectVersion(
                        modrinthProject,
                        file,
                        stream,
                        project
                    )
                } catch (err: Throwable) {
                    log("Failed to upload ${file.fileName}! ${err.localizedMessage}".error().toString())
                    spinner.fail()
                }
            }
        }
        println()
    }
}

fun backupProjectFiles(
    projects: List<CurseforgeProject>,
    toSave: EnumSet<InformationToSave>,
    toTransfer: FileSet
) {
    projects.forEach { project ->
        val files = withSpinner("Collecting files for ${project.display()})") {
            CurseforgeAPI.getProjectFiles(project.id, toTransfer == FileSet.ALL) {
                error("Could not get curseforge files for project ${project.display()}")
            }.sortedBy { Instant.parse(it.fileDate) }
        }.let {
            if (ModifoldArgs.args.fileLimit > -1) {
                it.takeLast(ModifoldArgs.args.fileLimit)
            } else {
                it
            }
        }

        // Save logo
        if (toSave.contains(InformationToSave.IMAGES)) {
            debug("Saving ${project.logo.url} as project_icon.png")
            val localCopy = File("ModifoldSaved/${project.display()}/images/project_icon.png").make()
            Files.copy(URL(project.logo.url).openStream(), localCopy.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }

        // Transfer screenshots
        project.screenshots.forEach nextAttach@{
            if (toSave.contains(InformationToSave.IMAGES)) {
                withSpinner("Saving gallery image ${it.title}") { _ ->
                    debug("Saving ${it.url} as ${it.title}.${it.getExt()}")
                    val localCopy = File("ModifoldSaved/${project.display()}/images/${it.title}.${it.getExt()}").make()
                    Files.copy(URL(it.url).openStream(), localCopy.toPath(), StandardCopyOption.REPLACE_EXISTING)
                }
            }
        }

        files.forEach { file ->
            if (toSave.contains(InformationToSave.VERSIONS)) {
                withSpinner("Saving ${file.fileName}") { _ ->
                    debug("Saving version ${file.fileName} to disk")
                    val buffered = CurseforgeAPI.getFileStream(file).buffered()
                    val localCopy = File("ModifoldSaved/${project.display()}/versions/${file.fileName}").make()
                    Files.copy(buffered, localCopy.toPath(), StandardCopyOption.REPLACE_EXISTING)
                    localCopy.inputStream().buffered()
                }
            }
        }
        println()
    }
}