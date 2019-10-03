package ru.zalutskii.google.test.parser.log

import java.io.BufferedReader

interface TestLogParser {
    /**
     * Parse next log token from stream.
     *
     * @param stream log stream.
     *
     * @return log token.
     */
    fun parseToken(stream: BufferedReader): Token?
}
