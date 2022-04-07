package com.github.p03w.modifold.curseforge_schema

data class CurseforgeAttachment(
    val url: String,
    val isDefault: Boolean,
    val title: String,
    val description: String
) {
    fun getExt() = url.split(".").last()
}
