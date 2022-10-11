package com.github.jstorts.consistenthash

interface HashFunction {

    fun hash(key: String): Long
}