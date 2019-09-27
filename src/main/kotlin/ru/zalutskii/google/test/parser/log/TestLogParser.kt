package ru.zalutskii.google.test.parser.log

import java.io.BufferedReader

interface TestLogParser {
    fun parseToken(stream: BufferedReader): Token?
}
