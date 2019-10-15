package ru.zalutskii.google.test.service.log.performer

import ru.zalutskii.google.test.parser.log.*
import java.io.BufferedReader

class LogPerformer(private val parser: TestLogParser) : LogPerformerInput {

    var output: LogPerformerOutput? = null

    private var log = ""
    private var logByTest = mutableMapOf<String, String>()

    private var currentLog: String? = null

    override fun reset() {
        synchronized(this) {
            log = ""
            logByTest = mutableMapOf()
        }
    }

    override fun perform(reader: BufferedReader) {
        var currentTest: String? = null
        var currentSuite: String? = null

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

            synchronized(this) {
                log += token.literal
                writeToLog(logByTest, currentSuite, token.literal)
                writeToLog(logByTest, currentTest, token.literal)

                if (currentLog == null) {
                    output?.didProcessLog(log)
                } else {
                    val log = logByTest[currentLog ?: ""]
                    if (log != null) {
                        output?.didProcessLog(log)
                    }
                }

                log += "\n"
                writeToLog(logByTest, currentSuite, "\n")
                writeToLog(logByTest, currentTest, "\n")
            }


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
    }

    private fun writeToLog(
        logBuilders: MutableMap<String, String>,
        key: String?,
        literal: String
    ) {
        if (key != null) {
            if (logBuilders[key] != null) {
                logBuilders[key] += literal
            } else {
                logBuilders[key] = literal
            }
        }
    }


    override fun setCurrentLogTo(suite: String, test: String): String =
        synchronized(this) {
            currentLog = "$suite.$test"
            logByTest["$suite.$test"] ?: ""
        }

    override fun setCurrentLogTo(suite: String): String =
        synchronized(this) {
            currentLog = suite
            logByTest[suite] ?: ""
        }

    override fun setCurrentLogToRoot(): String =
        synchronized(this) {
            currentLog = null
            log
        }
}

