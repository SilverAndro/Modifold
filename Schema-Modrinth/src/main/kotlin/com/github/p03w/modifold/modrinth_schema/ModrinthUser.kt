package com.github.p03w.modifold.modrinth_schema

data class ModrinthUser(
    val id: String,
    val username: String,
    val name: String,
    val bio: String,
    val role: String
)
