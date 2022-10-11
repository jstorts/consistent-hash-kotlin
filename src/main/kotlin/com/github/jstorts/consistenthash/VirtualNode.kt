package com.github.jstorts.consistenthash

class VirtualNode<T : Node>(private val physicalNode: T, private val replicaIndex: Int) : Node {

    override fun getKey(): String {
        return "${physicalNode.getKey()}-$replicaIndex"
    }

    fun isVirtualNodeOf(pNode: T): Boolean = physicalNode.getKey().equals(pNode)

    fun getPhysicalNode() = physicalNode
}