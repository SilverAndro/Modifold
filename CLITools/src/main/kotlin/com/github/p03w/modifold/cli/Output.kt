package com.github.p03w.modifold.cli

import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.ansi

fun String.highlight(): Ansi = ansi().bold().fgCyan().a(this).reset()
fun String.debug(): Ansi = ansi().fgGreen().a(this).reset()
fun String.warn(): Ansi = ansi().fgBrightYellow().a(this).reset()
fun String.error(): Ansi = ansi().fgBrightRed().a(this).reset()
fun String.bold(): Ansi = ansi().bold().a(this).reset()
