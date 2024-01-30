package dev.jstorts.consistenthash

interface HashFunction {
    fun hash(key: String): Long
}