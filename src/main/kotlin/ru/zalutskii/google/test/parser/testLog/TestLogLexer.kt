package ru.zalutskii.google.test.parser.testLog

import java.io.BufferedReader

class TestLogLexer : ITestLogLexer {
    override fun parseToken(stream: BufferedReader): Token? {
        val line = stream.readLine()

        if (line == null) {
            stream.close()
            return null
        }

        if (line.isEmpty()) {
            return parseToken(stream)
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
                "----------" -> {
                    val suiteStartRegex = "^\\d+ tests? from ".toRegex()
                    val suiteEndToken = "^\\d+ tests? from .*? \\(\\d+ ms total\\)$".toRegex()
                    when {
                        suiteEndToken.containsMatchIn(text) -> {
                            val count = text.substringBefore(" ").toIntOrNull() ?: 0
                            val suite = text.substringAfter("from ").substringBefore(" ")
                            val time = text.substringAfter("(").substringBefore(" ").toIntOrNull() ?: 0
                            return SuiteEndToken(suite, count, time)
                        }
                        suiteStartRegex.containsMatchIn(text) -> {
                            val count = text.substringBefore(" ").toIntOrNull() ?: 0
                            val suite = text.substringAfter("from ").substringBefore(",")
                            SuiteStartToken(suite, count)
                        }
                        else -> UnknownToken(line)
                    }

                }
                "RUN" -> {
                    val suite = text.substringBefore(".")
                    val test = text.substringAfter(".")
                    RunTestToken(suite, test)
                }
                "OK" -> {
                    val suite = text.substringBefore(".")
                    val test = text.substringAfter(".").substringBefore(" ")
                    val time = text.substringAfter("(").substringBefore(" ").toIntOrNull() ?: 0
                    OkTestToken(suite, test, time)
                }
                "FAILED" -> {
                    val failedTestTokenRegex = "\\(\\d+ ms\\)$".toRegex()
                    when {
                        failedTestTokenRegex.containsMatchIn(text) -> {
                            val suite = text.substringBefore(".")
                            val test = text.substringAfter(".").substringBefore(" ")
                            val time = text.substringAfter("(").substringBefore(" ").toIntOrNull() ?: 0
                            FailedTestToken(suite, test, time)
                        }
                        else -> UnknownToken(line)
                    }
                }
                "PASSED" -> {
                    val count = text.substringBefore(" ").toIntOrNull() ?: 0
                    PassedToken(count)
                }
                else -> UnknownToken(line)
            }
        }
    }
}
