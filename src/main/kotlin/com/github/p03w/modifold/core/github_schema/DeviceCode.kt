package com.github.p03w.modifold.core.github_schema

data class DeviceCode(
    val device_code: String,
    val user_code: String,
    val verification_uri: String,
    val interval: Int
)
