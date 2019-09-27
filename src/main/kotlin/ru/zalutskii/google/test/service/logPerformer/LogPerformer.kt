package ru.zalutskii.google.test.service.logPerformer

import ru.zalutskii.google.test.parser.log.*
import java.io.BufferedReader

class LogPerformer(private val parser: TestLogParser) : LogPerformerInput {

    var output: LogPerformerOutput? = null

    private var log = ""

    private var logBySuite = mutableMapOf<String, String>()
    private var logByTest = mutableMapOf<String, String>()

    override suspend fun reset() {
        log = ""
        logByTest = mutableMapOf()
    }

    override suspend fun perform(reader: BufferedReader) {
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
                    output?.didStartTestSuite(token.suite)
                    log += token.literal
                    output?.didProcessLog(log)
                }
                is SuiteEndToken -> {
                    currentSuite = null
                    logBySuite[token.suite] += token.literal
                    output?.didEndTestSuite(token.suite, token.milliseconds)
                    log += token.literal
                    output?.didProcessLog(log)
                }
                is RunTestToken -> {
                    currentTest = "${token.suite}.${token.test}"
                    logBySuite[token.suite] += token.literal
                    logByTest[currentTest] = token.literal
                    log += token.literal
                    output?.didStartTest(token.suite, token.test)
                    output?.didProcessLog(log)
                }
                is OkTestToken -> {
                    currentTest = "${token.suite}.${token.test}"
                    logBySuite[token.suite] += token.literal
                    logByTest[currentTest] += token.literal
                    log += token.literal
                    currentTest = null
                    output?.didPassTest(token.suite, token.test, token.milliseconds)
                    output?.didProcessLog(log)
                }
                is FailedTestToken -> {
                    currentTest = "${token.suite}.${token.test}"
                    logBySuite[token.suite] += token.literal
                    logByTest[currentTest] += token.literal
                    log += token.literal
                    currentTest = null
                    output?.didFailTest(token.suite, token.test, token.milliseconds)
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

    override suspend fun getLogForTest(suite: String, test: String): String = logByTest["$suite.$test"] ?: ""

    override suspend fun getLogForSuite(suite: String): String = logBySuite[suite] ?: ""

    override suspend fun getFullLog(): String = log
}
