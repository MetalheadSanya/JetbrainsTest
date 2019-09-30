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

    private var logBySuite = mutableMapOf<String, String>()
    private var logByTest = mutableMapOf<String, String>()

    override suspend fun reset() = coroutineScope {
        log = ""
        logByTest = mutableMapOf()
    }

    override suspend fun perform(reader: BufferedReader) = coroutineScope {
        var currentTest: String? = null
        var currentSuite: String? = null

        loop@ while (true) {

            when (val token = parser.parseToken(reader)) {
                is RunToken -> {
                    if (currentTest != null) {
                        logByTest[currentTest] += token.literal
                    }
                    if (currentSuite != null) {
                        logBySuite[currentSuite] += token.literal
                    }
                    log += token.literal
                    output?.didProcessLog(log)
                    output?.didStartTest()
                }
                is StopToken -> {
                    if (currentTest != null) {
                        logByTest[currentTest] += token.literal
                    }
                    if (currentSuite != null) {
                        logBySuite[currentSuite] += token.literal
                    }
                    log += token.literal
                    output?.didProcessLog(log)
                    output?.didEndTest()
                }
                is SuiteStartToken -> {
                    currentSuite = token.suite
                    logBySuite[token.suite] = token.literal
                    log += token.literal
                    output?.didProcessLog(log)
                    output?.didStartTestSuite(token.suite)
                }
                is SuiteEndToken -> {
                    currentSuite = null
                    logBySuite[token.suite] += token.literal
                    log += token.literal
                    output?.didProcessLog(log)
                    output?.didEndTestSuite(token.suite, token.milliseconds)
                }
                is RunTestToken -> {
                    currentTest = "${token.suite}.${token.test}"
                    logBySuite[token.suite] += token.literal
                    logByTest[currentTest] = token.literal
                    log += token.literal
                    output?.didProcessLog(log)
                    output?.didStartTest(token.suite, token.test)
                }
                is OkTestToken -> {
                    currentTest = "${token.suite}.${token.test}"
                    logBySuite[token.suite] += token.literal
                    logByTest[currentTest] += token.literal
                    log += token.literal
                    currentTest = null
                    output?.didProcessLog(log)
                    output?.didPassTest(token.suite, token.test, token.milliseconds)
                }
                is FailedTestToken -> {
                    currentTest = "${token.suite}.${token.test}"
                    logBySuite[token.suite] += token.literal
                    logByTest[currentTest] += token.literal
                    log += token.literal
                    currentTest = null
                    output?.didProcessLog(log)
                    output?.didFailTest(token.suite, token.test, token.milliseconds)
                }
                is PassedToken -> {
                    log += token.literal
                    output?.didProcessLog(log)
                }
                is UnknownToken -> {
                    if (currentTest != null) {
                        logByTest[currentTest] += token.literal
                    }
                    if (currentSuite != null) {
                        logBySuite[currentSuite] += token.literal
                    }
                    log += token.literal
                    output?.didProcessLog(log)
                }
                null -> break@loop
            }

            log += "\n"
            if (currentTest != null) {
                logByTest[currentTest] += "\n"
            }
            if (currentSuite != null) {
                logBySuite[currentSuite] += "\n"
            }
        }
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

