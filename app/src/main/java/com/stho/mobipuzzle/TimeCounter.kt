package com.stho.mobipuzzle

class TimeCounter {

    private var startTimeMillis: Long = 0L

    fun stop() {
        startTimeMillis = 0L
    }

    val isAlive: Boolean
        get() = (startTimeMillis > 0L)

    fun getSeconds(): Long {
        if (startTimeMillis == 0L) {
            startTimeMillis = currentMillis
        }
        return (currentMillis - startTimeMillis) / MILLIS_PER_SECOND
    }

    companion object {

        private val currentMillis: Long
            get() = System.nanoTime() / NANOS_PER_MILLIS

        private const val NANOS_PER_MILLIS: Long = 1000000L
        private const val MILLIS_PER_SECOND: Long = 1000L

    }
}
