package com.github.p03w.modifold.networking.curseforge

data class CurseforgeProject(
    val id: Int,
    val name: String,
    val slug: String,
    val authors: List<CurseforgeAuthor>,
    val summary: String,
    val categories: List<CurseforgeCategory>,
    val categorySection: CurseforgeCategorySection,
    val attachments: List<CurseforgeAttachment>
) {
    fun display() = "$name ($id)"
}
