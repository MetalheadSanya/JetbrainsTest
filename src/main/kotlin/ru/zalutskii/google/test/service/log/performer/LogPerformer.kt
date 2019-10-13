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
            val token = parser.parseToken(reader)

            when (token) {
                is RunToken -> {
                    output?.didStartTest()
                }
                is StopToken -> {
                    output?.didEndTest()
                }
                is SuiteStartToken -> {
                    currentSuite = token.suite
                    output?.didStartTestSuite(token.suite)
                }
                is SuiteEndToken -> {
                    output?.didEndTestSuite(token.suite, token.milliseconds)
                }
                is RunTestToken -> {
                    currentTest = "${token.suite}.${token.test}"
                    output?.didStartTest(token.suite, token.test)
                }
                is OkTestToken -> {
                    output?.didPassTest(token.suite, token.test, token.milliseconds)
                }
                is FailedTestToken -> {
                    output?.didFailTest(token.suite, token.test, token.milliseconds)
                }
                is PassedToken -> {
                }
                is UnknownToken -> {
                }
                null -> break@loop
            }

            log.append(token.literal)
            output?.didProcessLog(log.toString())
            log.append("\n")

            writeToLog(logBySuite, currentSuite, token)
            writeToLog(logByTest, currentTest, token)

            when (token) {
                is SuiteEndToken -> {
                    currentSuite = null
                }
                is OkTestToken -> {
                    currentTest = null
                }
                is FailedTestToken -> {
                    currentTest = null
                }
            }
        }

        this@LogPerformer.log = log.toString()
        this@LogPerformer.logBySuite = logBySuite.mapValues { builder -> builder.toString() }
        this@LogPerformer.logByTest = logByTest.mapValues { builder -> builder.toString() }
    }

    private fun writeToLog(
        logBuilders: MutableMap<String, StringBuilder>,
        key: String?,
        token: Token
    ) {
        if (key != null) {
            if (logBuilders[key] != null) {
                logBuilders[key]?.append(token.literal)
            } else {
                logBuilders[key] = StringBuilder(token.literal)
            }
            logBuilders[key]?.append("\n")
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

