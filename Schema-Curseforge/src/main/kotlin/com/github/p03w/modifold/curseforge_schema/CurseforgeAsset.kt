package com.github.p03w.modifold.curseforge_schema

data class CurseforgeAsset(
    val id: Int,
    val title: String,
    val description: String,
    val url: String,
) {
    fun getExt() = url.split(".").last()
}
