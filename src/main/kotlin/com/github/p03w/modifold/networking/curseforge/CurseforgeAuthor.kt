package com.github.p03w.modifold.networking.curseforge

data class CurseforgeAuthor(
    val id: Int,
    val name: String,
    val projectId: Int,
    val projectTitleId: Int?,
    val projectTitleTitle: String?,
    val twitchId: Int?,
    val url: String,
    val userId: Int
)
