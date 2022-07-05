package com.github.p03w.modifold.modrinth_api

import com.github.p03w.modifold.api_core.APIInterface
import com.github.p03w.modifold.api_core.Ratelimit
import com.github.p03w.modifold.cli.ModifoldArgs
import com.github.p03w.modifold.cli.warn
import com.github.p03w.modifold.curseforge_schema.CurseforgeAsset
import com.github.p03w.modifold.curseforge_schema.CurseforgeFile
import com.github.p03w.modifold.curseforge_schema.CurseforgeProject
import com.github.p03w.modifold.modrinth_schema.*
import com.google.gson.GsonBuilder
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import java.io.BufferedInputStream
import java.net.URL
import kotlin.time.Duration.Companion.milliseconds


object ModrinthAPI : APIInterface() {
    override val ratelimit = Ratelimit(150.milliseconds, false)
    override val ratelimitRemainingHeader = "X-Ratelimit-Remaining"
    override val ratelimitResetHeader = "X-Ratelimit-Reset"
    lateinit var AuthToken: String

    fun getUserAgent(): String {
        return "SilverAndro/Modifold/${getFileVersion()} (Silver <3#0955)"
    }

    private fun getFileVersion(): String {
        return this.javaClass.getResourceAsStream("/version.txt")
            ?.bufferedReader()?.readText() ?: "Unknown Version"
    }

    override val client = HttpClient(CIO) {
        install(JsonFeature)
        install(UserAgent) {
            agent = getUserAgent()
        }
    }

    override fun HttpRequestBuilder.attachAuth() {
        headers {
            append(HttpHeaders.Authorization, AuthToken)
        }
    }

    const val root = "https://api.modrinth.com/v2"

    fun getPossibleLicenses(): List<String> {
        val shortLicenses = getWithoutAuth<List<ModrinthShortLicense>>("$root/tag/license")
        return shortLicenses.map { it.short }.filterNot { it == "custom" }
    }

    fun getPossibleLoaders(): List<ModrinthLoader> {
        return getWithoutAuth("$root/tag/loader")
    }

    fun getPossibleCategories(): List<ModrinthCategory> {
        return getWithoutAuth("$root/tag/category")
    }

    fun getProjectInfo(id: String): ModrinthProject {
        return get("$root/project/$id")
    }

    fun getUser(): ModrinthUser {
        return get("$root/user")
    }

    fun getUserProjects(user: ModrinthUser): List<ModrinthProject> {
        return get<List<ModrinthProject>>("$root/user/${user.id}/projects").filter { it.project_type == "mod" }
    }

    fun makeProject(create: ModrinthProjectCreate, project: CurseforgeProject): ModrinthProject {
        return postForm("$root/project") {
            append("data", GsonBuilder().serializeNulls().disableHtmlEscaping().create().toJson(create))

            appendInput("icon", headersOf(HttpHeaders.ContentDisposition, "filename=icon.png")) {
                buildPacket {
                    writeFully(
                        URL(project.logo.url).openStream().readAllBytes()
                    )
                }
            }
        }
    }

    private fun getLoaders(file: CurseforgeFile): List<String> {
        return file.gameVersions.filterNot { MC_SEMVER.matches(it) || it.lowercase().contains("java") }
            .map { it.lowercase() }
    }

    fun addProjectImage(project: ModrinthProject, attachment: CurseforgeAsset) {
        post<HttpResponse>("$root/project/${project.id}/gallery") {
            contentType(ContentType("image", attachment.getExt()))

            parameter("ext", attachment.getExt())
            parameter("featured", false)
            parameter("title", attachment.title)
            parameter("description", attachment.description)

            body = URL(attachment.url).openStream().readAllBytes()
        }
    }

    fun makeProjectVersion(mod: ModrinthProject, file: CurseforgeFile, stream: BufferedInputStream, project: CurseforgeProject) {
        postForm<HttpResponse>("$root/version") {
            val upload = ModrinthVersionUpload(
                mod_id = mod.id,
                file_parts = listOf("${file.fileName}-0"),
                version_number = SEMVER.find(file.fileName.removeSuffix(".jar"))?.value
                    ?: file.fileName.removeSuffix(".jar"),
                version_title = file.displayName,
                version_body = "Transferred automatically from https://www.curseforge.com/minecraft/mc-mods/${project.slug}/files/${file.id}",
                game_versions = getGameVersions(file),
                release_channel = when (file.releaseType) {
                    3 -> "alpha"
                    2 -> "beta"
                    1 -> "release"
                    else -> throw IllegalArgumentException("Unknown release type ${file.releaseType} on file https://www.curseforge.com/minecraft/mc-mods/${project.slug}/files/${file.id}")
                },
                loaders = getLoaders(file).takeUnless { it.isEmpty() } ?: run {
                    warn("${file.fileName} has no specified loaders, using default loader(s) ${ModifoldArgs.args.defaultLoaders}")
                    ModifoldArgs.args.defaultLoaders
                }
            )
            append("data", GsonBuilder().serializeNulls().create().toJson(upload))

            appendInput(
                "${file.fileName}-0",
                headersOf(HttpHeaders.ContentDisposition, "filename=${file.fileName}"),
                file.fileLength
            ) {
                buildPacket {
                    var next = stream.read()
                    while (next != -1) {
                        writeByte(next.toByte())
                        next = stream.read()
                    }
                }
            }
        }
    }

    private fun getGameVersions(file: CurseforgeFile): List<String> {
        val out = mutableSetOf<String>()
        file.gameVersions.forEach {
            if (MC_SEMVER.matches(it)) {
                if (SNAPSHOT_REGEX.containsMatchIn(it)) {
                    warn(
                        "Dropping snapshot version $it because curseforge" +
                                " snapshots are not as precise as modrinth's and cannot" +
                                "be accurately represented!"
                    )
                } else {
                    out.add(it)
                }
            }
        }
        return out.toList()
    }

    @Suppress("SpellCheckingInspection")
    private val SEMVER =
        Regex("(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?")

    @Suppress("SpellCheckingInspection")
    val MC_SEMVER = Regex("(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:\\.(0|[1-9]\\d*))?(?:-[sS]napshots?)?")

    val SNAPSHOT_REGEX = Regex("-[sS]napshots?")
}
