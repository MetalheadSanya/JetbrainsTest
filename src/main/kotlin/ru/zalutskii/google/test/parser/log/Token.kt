package ru.zalutskii.google.test.parser.log

sealed class Token {
    abstract val literal: String
}

data class RunToken(override val literal: String) : Token()

data class StopToken(override val literal: String) : Token()

data class SuiteStartToken(val suite: String, val testCount: Int, override val literal: String) : Token()

data class SuiteEndToken(val suite: String, val testCount: Int, val milliseconds: Int, override val literal: String) :
    Token()

data class RunTestToken(val suite: String, val test: String, override val literal: String) : Token()

data class OkTestToken(val suite: String, val test: String, val milliseconds: Int, override val literal: String) :
    Token()

data class FailedTestToken(val suite: String, val test: String, val milliseconds: Int, override val literal: String) :
    Token()

data class PassedToken(val testCount: Int, override val literal: String) : Token()

data class UnknownToken(override val literal: String) : Token()
