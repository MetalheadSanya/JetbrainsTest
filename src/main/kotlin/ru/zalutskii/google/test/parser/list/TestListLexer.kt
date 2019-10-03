package ru.zalutskii.google.test.parser.list

import java.io.BufferedReader

interface TestListLexer {
    /**
     * Parse next test list token from buffer.
     *
     * @param stream buffer with tokens.
     *
     * @return next token from buffer or nil if buffer ended.
     */
    fun parseToken(stream: BufferedReader): Token?
}