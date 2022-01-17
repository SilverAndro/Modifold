package com.github.p03w.modifold.networking.modrinth

import com.github.p03w.modifold.networking.core.APIInterface
import com.github.p03w.modifold.networking.curseforge.CurseforgeFile
import com.github.p03w.modifold.networking.curseforge.CurseforgeProject
import com.google.gson.GsonBuilder
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.runBlocking
import java.net.URL
import kotlin.time.Duration.Companion.milliseconds

object ModrinthAPI : APIInterface(380.milliseconds) {
    lateinit var AuthToken: String

    override fun HttpRequestBuilder.attachAuth() {
        headers {
            append("Authorization", AuthToken)
        }
    }

    fun getModInfo(id: String): ModrinthMod {
        return get("https://api.modrinth.com/api/v1/mod/$id")
    }

    fun getUser(): ModrinthUser {
        return get("https://api.modrinth.com/api/v1/user")
    }

    fun getUserMods(user: ModrinthUser): List<String> {
        return get("https://api.modrinth.com/api/v1/user/${user.id}/mods")
    }

    fun makeMod(create: ModrinthModCreate, project: CurseforgeProject): ModrinthMod {
        return runBlocking {
            waitUntilCanSend()
            return@runBlocking client.submitForm("https://api.modrinth.com/api/v1/mod") {
                attachAuth()

                body = MultiPartFormDataContent(
                    formData {
                        append("data", GsonBuilder().serializeNulls().create().toJson(create))

                        appendInput("icon", headersOf(HttpHeaders.ContentDisposition, "filename=icon.png")) {
                            buildPacket {
                                writeFully(URL(project.attachments[0].url).openStream().readAllBytes())
                            }
                        }
                    }
                )
            }
        }
    }

    fun makeModVersion(mod: ModrinthMod, file: CurseforgeFile, project: CurseforgeProject) {
        runBlocking {
            waitUntilCanSend()
            client.submitForm<HttpResponse>(
                "https://api.modrinth.com/api/v1/version"
            ) {
                attachAuth()

                body = MultiPartFormDataContent(
                    formData {
                        val upload = ModrinthVersionUpload(
                            mod.id,
                            listOf("${file.fileName}-0"),
                            SEMVER.find(file.fileName.removeSuffix(".jar"))?.value ?: file.fileName.removeSuffix(".jar"),
                            file.displayName,
                            "Transferred automatically from https://www.curseforge.com/minecraft/mc-mods/${project.slug}/files/${file.id}",
                            file.gameVersion.filter { MC_SEMVER.matches(it) }
                        )
                        append("data", GsonBuilder().serializeNulls().create().toJson(upload))

                        appendInput("${file.fileName}-0", headersOf(HttpHeaders.ContentDisposition, "filename=${file.fileName}"), file.fileLength) {
                            buildPacket {
                                writeFully(URL(file.downloadUrl).openStream().readAllBytes())
                            }
                        }
                    }
                )
            }
        }
    }

    private val SEMVER = Regex("(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?")
    private val MC_SEMVER = Regex("(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:\\.(0|[1-9]\\d*))?")
}
