package ru.zalutskii.google.test.parser.log

sealed class Token

data class RunToken(val literal: String) : Token()

data class StopToken(val literal: String) : Token()

data class SuiteStartToken(val suite: String, val testCount: Int, val literal: String) : Token()

data class SuiteEndToken(val suite: String, val testCount: Int, val milliseconds: Int, val literal: String) : Token()

data class RunTestToken(val suite: String, val test: String, val literal: String) : Token()

data class OkTestToken(val suite: String, val test: String, val milliseconds: Int, val literal: String) : Token()

data class FailedTestToken(val suite: String, val test: String, val milliseconds: Int, val literal: String) : Token()

data class PassedToken(val testCount: Int, val literal: String) : Token()

data class UnknownToken(val literal: String) : Token()
