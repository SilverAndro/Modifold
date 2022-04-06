package com.github.p03w.modifold.curseforge_schema

data class ModrinthProject(
    val id: String,
    val slug: String,

    val title: String,
    val description: String,
    val categories: List<String>,

    val client_side: String,
    val server_side: String,

    val body: String,

    val issues_url: String?,
    val source_url: String?,
    val wiki_url: String?,
    val discord_url: String?,
    val donation_urls: List<ModrinthDonationURL>?,

    val project_type: String,
    val downloads: Int,

    val icon_url: String?,

    val team: String,

    @Deprecated("Only for old projects")
    val body_url: String?,

    val license: ModrinthLicense,
    val versions: List<String>,
) {
    fun display() = "$title ($id)"
}
