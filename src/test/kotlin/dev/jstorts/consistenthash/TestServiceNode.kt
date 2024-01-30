package dev.jstorts.consistenthash


class TestServiceNode(private val idc: String, private val ip: String, private val port: Int) : Node {
    override fun getKey(): String {
        return "$idc-$ip:$port"
    }

    override fun toString(): String {
        return getKey()
    }
}