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
            val tokenSectionRegex = "^\\[.*?] ".toRegex()
            val tokenSection = tokenSectionRegex.find(line) ?: return UnknownToken(line)
            val tokenType = tokenSection.value
                .replace("^\\[\\s*".toRegex(), "")
                .replace("\\s*] $".toRegex(), "")

            val text = line.replace(tokenSectionRegex, "")

            when (tokenType) {
                "==========" -> {
                    val startRegex = "^Running \\d+ tests? from \\d+ test suites?.".toRegex()
                    val endRegex = "^\\d+ tests? from \\d+ test suites? ran.".toRegex()
                    when {
                        startRegex.containsMatchIn(text) -> RunToken(line)
                        endRegex.containsMatchIn(text) -> StopToken(line)
                        else -> UnknownToken(line)
                    }
                }
                "----------" -> {
                    val suiteStartRegex = "^\\d+ tests? from ".toRegex()
                    val suiteEndRegex = "^\\d+ tests? from .*? \\(\\d+ ms total\\)$".toRegex()
                    when {
                        suiteEndRegex.containsMatchIn(text) -> {
                            val count = text.substringBefore(" ").toIntOrNull() ?: 0
                            val suite = text.substringAfter("from ").substringBefore(" ")
                            val time = text.substringAfter("(").substringBefore(" ").toIntOrNull() ?: 0

                            text.substring(1, 2)
                            return SuiteEndToken(suite, count, time, line)
                        }
                        suiteStartRegex.containsMatchIn(text) -> {
                            val count = text.substringBefore(" ").toIntOrNull() ?: 0
                            val suite = text.substringAfter("from ").substringBefore(",")
                            SuiteStartToken(suite, count, line)
                        }
                        else -> UnknownToken(line)
                    }

                }
                "RUN" -> {
                    val suite = text.substringBefore(".")
                    val test = text.substringAfter(".")
                    RunTestToken(suite, test, line)
                }
                "OK" -> {
                    val suite = text.substringBefore(".")
                    val test = text.substringAfter(".").substringBefore(" ")
                    val time = text.substringAfter("(").substringBefore(" ").toIntOrNull() ?: 0
                    OkTestToken(suite, test, time, line)
                }
                "FAILED" -> {
                    val failedTestTokenRegex = "\\(\\d+ ms\\)$".toRegex()
                    when {
                        failedTestTokenRegex.containsMatchIn(text) -> {
                            val suite = text.substringBefore(".")
                            val test = text.substringAfter(".").substringBefore(" ")
                            val time = text.substringAfter("(").substringBefore(" ").toIntOrNull() ?: 0
                            FailedTestToken(suite, test, time, line)
                        }
                        else -> UnknownToken(line)
                    }
                }
                "PASSED" -> {
                    val count = text.substringBefore(" ").toIntOrNull() ?: 0
                    PassedToken(count, line)
                }
                else -> UnknownToken(line)
            }
        }
    }
}