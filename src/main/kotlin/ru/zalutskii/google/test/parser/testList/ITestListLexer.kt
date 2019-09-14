package ru.zalutskii.google.test.parser.testList

import java.io.BufferedReader

interface ITestListLexer {
    fun parseToken(stream: BufferedReader): Token?
}