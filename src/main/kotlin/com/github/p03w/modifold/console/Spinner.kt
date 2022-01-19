package com.github.p03w.modifold.console

import com.github.p03w.modifold.Global
import com.github.p03w.modifold.networking.core.Countdown
import kotlinx.coroutines.*
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds

class Spinner(private val message: String) {
    private val delay by Countdown(70.milliseconds)
    private var spinner = "|"
    private val job: Job

    private val startInstant = Instant.now()

    init {
        job = Global.scope.launch {
            try {
                while (true) {
                    spin()
                    delay(7)
                }
            } finally {
                finish()
            }
        }
    }

    fun done() {
        runBlocking {
            job.cancelAndJoin()
        }
    }

    private fun spin() {
        tickSpinner()
        val now = Instant.now()
        val change = now.toEpochMilli() - startInstant.toEpochMilli()
        print("\r$message [${spinner.highlight()}] (${change}ms)")
    }

    private fun finish() {
        val now = Instant.now()
        val change = now.toEpochMilli() - startInstant.toEpochMilli()
        println("\r$message [${"DONE".highlight()}] (${change}ms)")
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
}
