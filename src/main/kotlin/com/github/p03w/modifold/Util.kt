package com.github.p03w.modifold

import com.github.p03w.modifold.console.Spinner
import com.github.p03w.modifold.console.debug
import com.github.p03w.modifold.console.error
import com.github.p03w.modifold.console.warn
import org.fusesource.jansi.Ansi
import kotlin.system.exitProcess

operator fun Ansi.plus(other: String) = this.toString() + other

inline fun <T> withSpinner(message: String, action: () -> T): T {
    val spinner = Spinner(message)
    val result = action()
    spinner.done()
    return result
}

fun debug(text: String) {
    if (Global.args.debug) {
        println("DEBUG: ".debug() + text)
    }
}

fun log(text: String) {
    println(text)
}

fun await(text: String) {
    print(text)
    readln()
}

fun warn(text: String) {
    println("WARN: $text".warn())
}

fun error(text: String) {
    println("ERROR: $text".error())
    exitProcess(1)
}

fun requireInputOf(vararg possible: String): String {
    while (true) {
        val input = readln().lowercase()
        if (possible.contains(input)) {
            return input
        }
    }
}
