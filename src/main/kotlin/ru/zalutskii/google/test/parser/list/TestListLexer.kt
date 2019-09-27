package ru.zalutskii.google.test.parser.list

import java.io.BufferedReader

interface TestListLexer {
    fun parseToken(stream: BufferedReader): Token?
}