package dev.jstorts.consistenthash

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class ConsistentHashRouter<T : Node>(
    pNodes: Collection<T>,
    private var vNodeCount: Int,
    private var hashFunction: HashFunction? = null
) {
    private val ring: SortedMap<Long, VirtualNode<T>> = TreeMap()

    init {
        hashFunction = hashFunction ?: MD5Hash()
        pNodes.forEach { addNode(it, vNodeCount) }
    }

    private fun addNode(pNode: T, vNodeCount: Int) {
        if (vNodeCount < 0) {
            throw IllegalArgumentException("illegal virtual node count: $vNodeCount")
        }
        val existingReplicas = getExistingReplicas(pNode)
        for (i in 0..vNodeCount) {
            val vNode = VirtualNode(pNode, i + existingReplicas)
            ring[hashFunction?.hash(vNode.getKey())] = vNode
        }
    }

    fun removeNode(pNode: T) {
        val iterator: Iterator<Long> = ring.keys.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            val virtualNode = ring[key]
            if (virtualNode!!.isVirtualNodeOf(pNode)) {
                ring.remove(key)
            }
        }
    }

    fun routeNode(objectKey: String?): T? {
        if (ring.isEmpty()) {
            return null
        }
        val hashVal = hashFunction!!.hash(objectKey!!)
        val tailMap: SortedMap<Long, VirtualNode<T>> = ring.tailMap(hashVal)
        val nodeHashVal: Long = if (!tailMap.isEmpty()) tailMap.firstKey() else ring.firstKey()
        return ring[nodeHashVal]?.getPhysicalNode()
    }


    private fun getExistingReplicas(pNode: T): Int {
        var replicas = 0
        for (vNode in ring.values) {
            if (vNode.isVirtualNodeOf(pNode)) {
                replicas++
            }
        }
        return replicas
    }


    private class MD5Hash : HashFunction {
        var instance: MessageDigest? = null

        init {
            try {
                instance = MessageDigest.getInstance("MD5")
            } catch (_: NoSuchAlgorithmException) {
            }
        }

        override fun hash(key: String): Long {
            instance!!.reset()
            instance!!.update(key.toByteArray())
            val digest = instance!!.digest()
            var h: Long = 0
            for (i in 0..3) {
                h = h shl 8
                h = h or (digest[i].toInt() and 0xFF).toLong()
            }
            return h
        }
    }
}