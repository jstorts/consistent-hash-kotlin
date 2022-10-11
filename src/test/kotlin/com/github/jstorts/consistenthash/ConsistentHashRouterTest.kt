package com.github.jstorts.consistenthash

import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


class ConsistentHashRouterTest {

    @Test
    fun testOutputDistribution() {
        val node1 = TestServiceNode("IDC1", "10.8.1.11", 8080)
        val node2 = TestServiceNode("IDC1", "10.8.3.99", 8080)
        val node3 = TestServiceNode("IDC1", "10.9.11.105", 8080)
        val node4 = TestServiceNode("IDC1", "10.10.9.210", 8080)

        val consistentHashRouter: ConsistentHashRouter<TestServiceNode> =
            ConsistentHashRouter(listOf(node1, node2, node3, node4), 3)
        val requestIps: MutableList<String> = ArrayList()
        for (i in 0..9999) {
            requestIps.add(randomIp)
        }
        println(goRoute(consistentHashRouter, *requestIps.toTypedArray()).toString())
    }

    private fun goRoute(
        consistentHashRouter: ConsistentHashRouter<TestServiceNode>,
        vararg requestIps: String
    ): TreeMap<String, AtomicInteger> {
        val res = TreeMap<String, AtomicInteger>()
        for (requestIp in requestIps) {
            val testServiceNode: TestServiceNode? = consistentHashRouter.routeNode(requestIp)
            res.putIfAbsent(testServiceNode!!.getKey(), AtomicInteger())
            res[testServiceNode.getKey()]!!.incrementAndGet()
            println("$requestIp is routed to $testServiceNode")
        }
        return res
    }

    private val randomIp: String
        get() {
            val range = arrayOf(
                intArrayOf(607649792, 608174079),
                intArrayOf(1038614528, 1039007743),
                intArrayOf(1783627776, 1784676351),
                intArrayOf(2035023872, 2035154943),
                intArrayOf(2078801920, 2079064063),
                intArrayOf(-1950089216, -1948778497),
                intArrayOf(-1425539072, -1425014785),
                intArrayOf(-1236271104, -1235419137),
                intArrayOf(-770113536, -768606209),
                intArrayOf(-569376768, -564133889)
            )
            val rdint = Random()
            val index = rdint.nextInt(10)
            return num2ip(range[index][0] + Random().nextInt(range[index][1] - range[index][0]))
        }

    private fun num2ip(ip: Int): String {
        val b = IntArray(4)
        b[0] = (ip shr 24 and 0xff)
        b[1] = (ip shr 16 and 0xff)
        b[2] = (ip shr 8 and 0xff)
        b[3] = (ip and 0xff)
        return "${b[0]}.${b[1]}.${b[2]}.${b[3]}"
    }
}