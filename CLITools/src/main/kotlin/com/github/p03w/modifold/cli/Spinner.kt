package com.github.p03w.modifold.cli

import kotlinx.coroutines.*
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds

class Spinner(private val message: String) {
    private val delay by Countdown(70.milliseconds)
    private var spinner = "|"
    private val job: Job

    private val startInstant = Instant.now()

    init {
        spinnerActive = true
        job = scope.launch {
            try {
                while (true) {
                    spin()
                    delay(7)
                }
            } catch (cancel: CancellationException) {
                if (cancel !is BadCancellationException) {
                    finish()
                }
                spinnerActive = false
            }
        }
    }

    fun done() {
        runBlocking {
            job.cancelAndJoin()
        }
    }

    fun fail() {
        runBlocking {
            job.cancel(BadCancellationException())
            job.join()
        }
        val now = Instant.now()
        val change = now.toEpochMilli() - startInstant.toEpochMilli()
        synchronized(Companion) {
            println("\r$message [${"FAIL".error()}] (${change}ms)")
        }
    }

    private fun spin() {
        tickSpinner()
        val now = Instant.now()
        val change = now.toEpochMilli() - startInstant.toEpochMilli()
        synchronized(Companion) {
            print("\r$message [${spinner.highlight()}] (${change}ms)")
        }
    }


    private fun finish() {
        val now = Instant.now()
        val change = now.toEpochMilli() - startInstant.toEpochMilli()

        synchronized(Companion) {
            println("\r$message [${"DONE".highlight()}] (${change}ms)")
        }
    }

    private fun tickSpinner() {
        if (delay) {
            spinner = when (spinner) {
                "|" -> "/"
                "/" -> "-"
                "-" -> "\\"
                "\\" -> "|"
                else -> "|"
            }
        }
    }

    companion object {
        val scope = CoroutineScope(Dispatchers.Default)

        @Volatile
        var spinnerActive = false
    }
}
