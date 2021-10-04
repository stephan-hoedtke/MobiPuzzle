package com.stho.mobipuzzle.engine

class ThreadSafeBoolean(value: Boolean = false) {

    private var backingValue: Boolean = value
    private val lock = Object()

    val isSet: Boolean
        get() {
            synchronized(lock) {
                return backingValue
            }
        }

    val isUnset: Boolean
        get() {
            synchronized(lock) {
                return !backingValue
            }
        }

    fun set() {
        synchronized(lock) {
            backingValue = true
        }
    }

    fun unset() {
        synchronized(lock) {
            backingValue = false
        }
    }
}

