package com.github.p03w.modifold.curseforge_schema

data class CurseforgeProject(
    val id: Int,
    val name: String,
    val slug: String,
    val links: CurseforgeLinks,
    val authors: List<CurseforgeAuthor>,
    val summary: String,
    val categories: List<CurseforgeCategory>,
    val logo: CurseforgeAsset,
    val screenshots: List<CurseforgeAsset>,
    val latestFiles: List<CurseforgeFile>,
    val allowModDistribution: Boolean
) {
    fun display() = "$name ($id)"
}
