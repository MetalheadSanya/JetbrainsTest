package ru.zalutskii.google.test.parser.testLog

sealed class Token

data class SuiteStartToken(val suite: String, val testCount: Int) : Token()

data class SuiteEndToken(val suite: String, val testCount: Int, val milliseconds: Int) : Token()

data class RunTestToken(val suite: String, val test: String) : Token()

data class OkTestToken(val suite: String, val test: String, val milliseconds: Int) : Token()

data class FailedTestToken(val suite: String, val test: String, val milliseconds: Int) : Token()

data class PassedToken(val testCount: Int) : Token()

data class UnknownToken(val literal: String) : Token()
