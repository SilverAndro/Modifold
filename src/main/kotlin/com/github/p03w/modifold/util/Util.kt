package com.github.p03w.modifold.util

import com.github.p03w.modifold.Global
import com.github.p03w.modifold.console.Spinner
import com.github.p03w.modifold.console.debug
import com.github.p03w.modifold.console.error
import com.github.p03w.modifold.console.warn

inline fun <T> withSpinner(message: String, action: () -> T): T {
    val spinner = Spinner(message)
    val result: T
    try {
        result = action()
    } catch (err: Exception) {
        spinner.fail()
        throw err
    }
    spinner.done()
    return result
}

fun debug(text: String) {
    if (Global.args.debug) {
        println("DEBUG: ".debug().a(text))
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

fun error(text: String): Nothing {
    throw IllegalStateException("ERROR: $text".error().toString())
}

fun requireInputOf(vararg possible: String): String {
    while (true) {
        val input = readln().lowercase()
        if (possible.contains(input)) {
            return input
        }
    }
}
