package ru.zalutskii.google.test.parser.testLog

import java.io.BufferedReader

interface ITestLogLexer {
    fun parseToken(stream: BufferedReader): Token?
}
