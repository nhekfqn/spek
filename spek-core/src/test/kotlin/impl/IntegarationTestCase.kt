package org.spek.impl

import kotlin.test.*
import org.spek.impl.*
import org.spek.api.*
import org.junit.*

public open class IntegrationTestCase {

    fun runTest(case: TestSpekAction, vararg expected: String) {
        val list = arrayListOf<String>()
        executeSpek(case, TestLogger(list))
        if (expected.size == 0) return
        val actualDump = list.map { it + "\n" }.fold("") { r, i -> r + i }
        val expectedLog = expected
                .flatMap { it
                    .trim()
                    .split("[\r\n]+")
                    .map { it.trim() }
                    .filter { it.length > 0 }
        } . filter { it.length > 0 }  . toList()

        Assert.assertEquals(
                actualDump,
                expectedLog,
                list
                )
    }

    public fun data(f: Specification.() -> Unit) : TestSpekAction {
        val d = Data()
        d.f()
        return d
    }

    public open class Data : SpekImpl(), TestSpekAction {
        override fun description(): String = "42"
    }

    public class TestLogger(val output: MutableList<String>): Listener {
        private fun step(prefix:String) : ExecutionReporter = object : ExecutionReporter {
            override fun started() {
                output add prefix + " START"
            }
            override fun completed() {
                output add prefix + " FINISH"
            }
            override fun skipped(why: String) {
                output add prefix + " SKIP:" + why
            }
            override fun pending(why: String) {
                output add prefix + " PEND:" + why
            }
            override fun failed(error: Throwable) {
                output add prefix + " FAIL:" + error.getMessage()
            }
        }

        override fun spek(spek: String): ExecutionReporter = step("SPEK: $spek")
        override fun given(spek: String, given: String): ExecutionReporter = step("SPEK: $spek GIVEN: $given")
        override fun on(spek: String, given: String, on: String)= step("SPEK: $spek GIVEN: $given ON: $on")
        override fun it(spek: String, given: String, on: String, it: String) = step("SPEK: $spek GIVEN: $given ON: $on IT: $it")
    }

}