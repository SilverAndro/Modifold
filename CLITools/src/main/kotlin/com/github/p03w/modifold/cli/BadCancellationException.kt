package com.github.p03w.modifold.cli

import kotlinx.coroutines.CancellationException

// Used to cancel a job in a "bad" way
class BadCancellationException : CancellationException()
