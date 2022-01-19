package com.github.p03w.modifold.networking.modrinth

data class ModrinthMod(
    val id: String,
    val slug: String,
    val title: String,
    val license: ModrinthLicense,
    val description: String,
    val body: String,
    val versions: List<String>
) {
    val display: String = "$title ($id)"
}
