package com.github.p03w.modifold.cli

import kotlinx.coroutines.cancel
import org.fusesource.jansi.Ansi

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

private fun clearLine() {
    synchronized(Spinner.Companion) {
        print("\r${Ansi.ansi().eraseLine()}\r")
        System.out.flush()
    }
}

fun debug(text: String) {
    if (ModifoldArgs.args.debug) {
        if (Spinner.spinnerActive) {
            clearLine()
        }
        synchronized(Spinner.Companion) {
            println("DEBUG: ".debug().a(text))
        }
    }
}

fun log(text: String) {
    if (Spinner.spinnerActive) {
        clearLine()
    }
    synchronized(Spinner.Companion) {
        println(text)
    }
}

fun await(text: String) {
    print(text)
    readln()
}

fun warn(text: String) {
    if (Spinner.spinnerActive) {
        clearLine()
    }
    synchronized(Spinner.Companion) {
        println("WARN: $text".warn())
    }
}

fun error(text: String, err: Throwable? = null): Nothing {
    if (Spinner.spinnerActive) Spinner.scope.cancel(BadCancellationException())
    synchronized(Spinner.Companion) {
        println()
        println("ERROR: $text".error().toString())
    }
    if (err != null) {
        throw IllegalStateException("ERROR: $text").initCause(err)
    } else {
        throw IllegalStateException("ERROR: $text")
    }
}