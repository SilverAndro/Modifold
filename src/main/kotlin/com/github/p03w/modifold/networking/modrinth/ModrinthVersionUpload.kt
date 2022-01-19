package com.github.p03w.modifold.networking.modrinth

import com.github.p03w.modifold.Global

data class ModrinthVersionUpload(
    val mod_id: String,
    val file_parts: List<String>,
    val version_number: String,
    val version_title: String,
    val version_body: String,
    val game_versions: List<String>,
    val release_channel: String = "release",
    val loaders: List<String> = Global.args.loaders,
    val featured: Boolean = false,
    val dependencies: List<String> = emptyList()
)
