@file:Suppress("PropertyName", "SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    application
}

group = "com.github.p03w"
version = "2.2.2"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

// Dependency versions
val ktor_version = "1.6.8"
val argparse_version = "2.0.7"
val slf4j_nop_version = "1.7.36"
val jansi_version = "2.4.0"
val inquirer_version = "0.1.0"

val commonImplementaions = setOf(
    "io.ktor:ktor-client-core:$ktor_version",
    "io.ktor:ktor-client-cio:$ktor_version",
    "io.ktor:ktor-client-gson:$ktor_version",
    "io.ktor:ktor-server-netty:$ktor_version",
    "org.slf4j:slf4j-nop:$slf4j_nop_version",

    "org.fusesource.jansi:jansi:$jansi_version",
    "com.xenomachina:kotlin-argparser:$argparse_version",
    "com.github.kotlin-inquirer:kotlin-inquirer:$inquirer_version",
    "com.github.kenneth-lange:java-nlp-text-similarity:-SNAPSHOT",
)

dependencies {
    commonImplementaions.forEach {
        implementation(it)
    }

    subprojects.forEach {
        implementation(it)
    }
}

subprojects {
    pluginManager.apply("org.jetbrains.kotlin.jvm")
    repositories {
        mavenCentral()
        maven("https://jitpack.io")
    }
    dependencies {
        commonImplementaions.forEach {
            implementation(it)
        }
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "9"
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "9"
}

application {
    mainClass.set("com.github.p03w.modifold.MainKt")
}

task("embedVersion") {
    doLast {
        val file = File("src/main/resources/version.txt")
        file.delete()
        file.parentFile.mkdirs()
        file.createNewFile()
        file.writer().use {
            it.write(version.toString())
        }
    }
}

tasks.classes {
    dependsOn("embedVersion")
}