package com.github.p03w.modifold.curseforge_schema

data class CurseforgeFile(
    val id: Int,
    val displayName: String,
    val fileName: String,
    val gameVersion: List<String>,
    val releaseType: Int,
    val downloadUrl: String,
    val fileLength: Long,
    val fileDate: String
)
