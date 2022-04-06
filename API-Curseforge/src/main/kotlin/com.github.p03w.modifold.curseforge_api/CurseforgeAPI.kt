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
    fun getProjectData(id: Int): CurseforgeProject? {
        return try {
            get("https://curse.nikky.moe/api/addon/$id")
        } catch (ignored: Exception) {
            null
        }
    }

    fun getProjectFiles(id: Int, onFailed: () -> Unit = {}): List<CurseforgeFile> {
        return try {
            get("https://curse.nikky.moe/api/addon/$id/files")
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
