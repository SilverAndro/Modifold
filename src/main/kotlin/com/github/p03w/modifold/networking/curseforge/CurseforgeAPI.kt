package com.github.p03w.modifold.networking.curseforge

import com.github.p03w.modifold.Global
import com.github.p03w.modifold.networking.core.APIInterface
import java.io.InputStream
import java.net.URL
import kotlin.time.Duration.Companion.milliseconds

object CurseforgeAPI: APIInterface(Global.args.curseforgeSpeed.milliseconds) {
    fun getProjectData(id: Int): CurseforgeProject? {
        return try {
            get("https://curse.nikky.moe/api/addon/$id")
        } catch (ignored: Exception) {
            null
        }
    }

    fun getProjectFiles(id: Int, onFailed: ()->Unit = {}): List<CurseforgeFile> {
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