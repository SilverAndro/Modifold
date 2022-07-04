package com.github.p03w.modifold.curseforge_api

import com.github.p03w.modifold.api_core.APIInterface
import com.github.p03w.modifold.api_core.Ratelimit
import com.github.p03w.modifold.cli.ModifoldArgs
import com.github.p03w.modifold.curseforge_schema.*
import java.io.InputStream
import java.net.URL
import kotlin.time.Duration.Companion.milliseconds

object CurseforgeAPI : APIInterface() {
    override val ratelimit = Ratelimit(ModifoldArgs.args.curseforgeSpeed.milliseconds, true)
    const val root = "https://cfproxy.fly.dev/v1"
    fun getProjectData(id: Int): CurseforgeProject? {
        return try {
            getWithoutAuth<ProjectWrapper>("$root/mods/$id").data
        } catch (ignored: Exception) {
            ignored.printStackTrace()
            null
        }
    }

    fun getProjectFiles(id: Int, allFiles: Boolean, onFailed: () -> Unit = {}): List<CurseforgeFile> {
        return try {
            if (allFiles) {
                getWithoutAuth<FilesWrapper>("$root/mods/$id/files").data
            } else {
                getProjectData(id)!!.latestFiles
            }
        } catch (err: Exception) {
            err.printStackTrace()
            onFailed()
            emptyList()
        }
    }

    fun getProjectDescription(id: Int): String? {
        return try {
            getWithoutAuth<CurseforgeDescription>("$root/mods/$id/description").data
        } catch (ignored: Exception) {
            ignored.printStackTrace()
            null
        }
    }

    fun getFileStream(file: CurseforgeFile): InputStream {
        waitUntilCanSend()
        return URL(file.downloadUrl).openStream()
    }
}
