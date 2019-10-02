package ru.zalutskii.google.test.service.log.performer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import ru.zalutskii.google.test.parser.log.*
import java.io.BufferedReader
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class LogPerformer(private val parser: TestLogParser) :
    LogPerformerInput,
    CoroutineScope {

    override val coroutineContext: CoroutineContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    var output: LogPerformerOutput? = null

    private var log = ""
    private var logBySuite = mapOf<String, String>()
    private var logByTest = mapOf<String, String>()

    override suspend fun reset() = coroutineScope {
        log = ""
        logBySuite = mapOf()
        logByTest = mapOf()
    }

    override suspend fun perform(reader: BufferedReader) = coroutineScope {
        var currentTest: String? = null
        var currentSuite: String? = null

        val log = StringBuilder()
        val logBySuite = mutableMapOf<String, StringBuilder>()
        val logByTest = mutableMapOf<String, StringBuilder>()

        loop@ while (true) {

            when (val token = parser.parseToken(reader)) {
                is RunToken -> {
                    if (currentTest != null) {
                        logByTest[currentTest]?.append(token.literal)
                    }
                    if (currentSuite != null) {
                        logBySuite[currentSuite]?.append(token.literal)
                    }
                    log.append(token.literal)
                    output?.didProcessLog(log.toString())
                    output?.didStartTest()
                }
                is StopToken -> {
                    if (currentTest != null) {
                        logByTest[currentTest]?.append(token.literal)
                    }
                    if (currentSuite != null) {
                        logBySuite[currentSuite]?.append(token.literal)
                    }
                    log.append(token.literal)
                    output?.didProcessLog(log.toString())
                    output?.didEndTest()
                }
                is SuiteStartToken -> {
                    currentSuite = token.suite
                    logBySuite[token.suite] = StringBuilder(token.literal)
                    log.append(token.literal)
                    output?.didProcessLog(log.toString())
                    output?.didStartTestSuite(token.suite)
                }
                is SuiteEndToken -> {
                    currentSuite = null
                    logBySuite[token.suite]?.append(token.literal)
                    log.append(token.literal)
                    output?.didProcessLog(log.toString())
                    output?.didEndTestSuite(token.suite, token.milliseconds)
                }
                is RunTestToken -> {
                    currentTest = "${token.suite}.${token.test}"
                    logBySuite[token.suite]?.append(token.literal)
                    logByTest[currentTest] = StringBuilder(token.literal)
                    log.append(token.literal)
                    output?.didProcessLog(log.toString())
                    output?.didStartTest(token.suite, token.test)
                }
                is OkTestToken -> {
                    currentTest = "${token.suite}.${token.test}"
                    logBySuite[token.suite]?.append(token.literal)
                    logByTest[currentTest]?.append(token.literal)
                    log.append(token.literal)
                    currentTest = null
                    output?.didProcessLog(log.toString())
                    output?.didPassTest(token.suite, token.test, token.milliseconds)
                }
                is FailedTestToken -> {
                    currentTest = "${token.suite}.${token.test}"
                    logBySuite[token.suite]?.append(token.literal)
                    logByTest[currentTest]?.append(token.literal)
                    log.append(token.literal)
                    currentTest = null
                    output?.didProcessLog(log.toString())
                    output?.didFailTest(token.suite, token.test, token.milliseconds)
                }
                is PassedToken -> {
                    log.append(token.literal)
                    output?.didProcessLog(log.toString())
                }
                is UnknownToken -> {
                    if (currentTest != null) {
                        logByTest[currentTest]?.append(token.literal)
                    }
                    if (currentSuite != null) {
                        logBySuite[currentSuite]?.append(token.literal)
                    }
                    log.append(token.literal)
                    output?.didProcessLog(log.toString())
                }
                null -> break@loop
            }

            log.append("\n")
            if (currentTest != null) {
                logByTest[currentTest]?.append("\n")
            }
            if (currentSuite != null) {
                logBySuite[currentSuite]?.append("\n")
            }
        }

        this@LogPerformer.log = log.toString()
        this@LogPerformer.logBySuite = logBySuite.mapValues { builder -> builder.toString() }
        this@LogPerformer.logByTest = logByTest.mapValues { builder -> builder.toString() }
    }


    override suspend fun getLogForTest(suite: String, test: String): String = coroutineScope {
        logByTest["$suite.$test"] ?: ""
    }

    override suspend fun getLogForSuite(suite: String): String = coroutineScope {
        logBySuite[suite] ?: ""
    }

    override suspend fun getFullLog(): String = coroutineScope {
        log
    }
}

