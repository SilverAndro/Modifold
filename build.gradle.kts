import org.jetbrains.kotlin.gradle.plugin.statistics.ReportStatisticsToElasticSearch.url
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    application
}

group = "com.github.p03w"
version = "0.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

// Dependency versions
val ktor_version = "1.6.7"
val argparse_version = "2.0.7"

dependencies {
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-gson:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("org.slf4j:slf4j-nop:1.7.33")

    implementation("com.xenomachina:kotlin-argparser:$argparse_version")
    implementation("org.fusesource.jansi:jansi:2.4.0")
    implementation("com.github.kenneth-lange:java-nlp-text-similarity:-SNAPSHOT")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("com.github.p03w.modifold.MainKt")
}
