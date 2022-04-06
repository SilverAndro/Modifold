package com.github.p03w.modifold.curseforge_api

import com.github.p03w.modifold.api_core.APIInterface
import com.github.p03w.modifold.api_core.Ratelimit
import com.github.p03w.modifold.cli.ModifoldArgs
import com.github.p03w.modifold.curseforge_schema.CurseforgeFile
import com.github.p03w.modifold.curseforge_schema.CurseforgeProject
import java.io.InputStream
import java.net.URL
import kotlin.time.Duration.Companion.milliseconds

object CurseforgeAPI : APIInterface() {
    override val ratelimit = Ratelimit(ModifoldArgs.args.curseforgeSpeed.milliseconds, true)
    const val root = "https://curse.nikky.moe/api"
    fun getProjectData(id: Int): CurseforgeProject? {
        return try {
            getWithoutAuth("$root/addon/$id")
        } catch (ignored: Exception) {
            null
        }
    }

    fun getProjectFiles(id: Int, onFailed: () -> Unit = {}): List<CurseforgeFile> {
        return try {
            getWithoutAuth("$root/addon/$id/files")
        } catch (err: Exception) {
            err.printStackTrace()
            onFailed()
            emptyList()
        }
    }

    fun getFileStream(file: CurseforgeFile): InputStream {
        waitUntilCanSend()
        return URL(file.downloadUrl).openStream()
    }
}
