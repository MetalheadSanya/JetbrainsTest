package ru.zalutskii.google.test.parser.log

import java.io.BufferedReader

class TestLogParserImpl : TestLogParser {
    override fun parseToken(stream: BufferedReader): Token? {
        val line = stream.readLine()

        if (line == null) {
            stream.close()
            return null
        }

        if (line.isEmpty()) {
            return UnknownToken(line)
        }

        return if (!line.startsWith("[")) {
            UnknownToken(line)
        } else {
            val tokenType = extractTokenType(line)
            val message = extractMessage(line)

            when (tokenType) {
                "==========" -> {
                    val startRegex = "^Running \\d+ tests? from \\d+ test suites?.".toRegex()
                    val endRegex = "^\\d+ tests? from \\d+ test suites? ran.".toRegex()
                    when {
                        startRegex.containsMatchIn(message) -> RunToken(line)
                        endRegex.containsMatchIn(message) -> StopToken(line)
                        else -> UnknownToken(line)
                    }
                }
                "----------" -> {
                    val suiteStartRegex = "^\\d+ tests? from ".toRegex()
                    val suiteEndRegex = "^\\d+ tests? from .*? \\(\\d+ ms total\\)$".toRegex()
                    when {
                        suiteEndRegex.containsMatchIn(message) -> parseSuiteEndToken(message, line)
                        suiteStartRegex.containsMatchIn(message) -> parseSuiteStartToken(message, line)
                        else -> UnknownToken(line)
                    }

                }
                "RUN" -> parseRunTestToken(message, line)
                "OK" -> parseOkToken(message, line)
                "FAILED" -> {
                    val failedTestTokenRegex = "\\(\\d+ ms\\)$".toRegex()
                    when {
                        failedTestTokenRegex.containsMatchIn(message) -> parseFailedTestToken(message, line)
                        else -> UnknownToken(line)
                    }
                }
                "PASSED" -> parsePassedToken(message, line)
                else -> UnknownToken(line)
            }
        }
    }

    private fun parsePassedToken(
        message: String,
        line: String
    ): PassedToken {
        val count = parseIntFromStart(message)
        return PassedToken(count, line)
    }

    private fun parseFailedTestToken(
        message: String,
        line: String
    ): FailedTestToken {
        val suite = parseSuiteNameFromBegin(message)
        val test = parseFunctionNameFromBegin(message)
        val time = parseTimeAfterParenthesis(message)
        return FailedTestToken(suite, test, time, line)
    }

    private fun parseOkToken(message: String, line: String): OkTestToken {
        val suite = parseSuiteNameFromBegin(message)
        val test = parseFunctionNameFromBegin(message)
        val time = parseTimeAfterParenthesis(message)
        return OkTestToken(suite, test, time, line)
    }

    private fun parseRunTestToken(
        message: String,
        line: String
    ): RunTestToken {
        val suite = parseSuiteNameFromBegin(message)
        val test = parseFunctionNameFromBegin(message)
        return RunTestToken(suite, test, line)
    }

    private fun parseSuiteStartToken(
        message: String,
        line: String
    ): SuiteStartToken {
        val count = parseIntFromStart(message)
        val suite = parseSuiteNameAfterFromKeyword(message)
        return SuiteStartToken(suite, count, line)
    }

    private fun parseSuiteEndToken(
        message: String,
        line: String
    ): SuiteEndToken {
        val count = parseIntFromStart(message)
        val suite = parseSuiteNameAfterFromKeyword(message)
        val time = parseTimeAfterParenthesis(message)

        return SuiteEndToken(suite, count, time, line)
    }

    private fun extractTokenType(line: String) =
        line.substringAfter("[")
            .substringBefore("]")
            .trim()

    private fun extractMessage(line: String) =
        line.substringAfter("]")
            .trim()

    private fun parseSuiteNameFromBegin(message: String) =
        message.substringBefore(".")

    private fun parseFunctionNameFromBegin(message: String) =
        message.substringAfter(".")
            .substringBefore(" ")

    private fun parseTimeAfterParenthesis(message: String) =
        message.substringAfter("(")
            .substringBefore(" ")
            .toIntOrNull() ?: 0

    private fun parseIntFromStart(message: String) =
        message.substringBefore(" ")
            .toIntOrNull() ?: 0

    private fun parseSuiteNameAfterFromKeyword(message: String) =
        message.substringAfter("from ")
            .substringBefore(" ")
            .substringBefore(",")
}
