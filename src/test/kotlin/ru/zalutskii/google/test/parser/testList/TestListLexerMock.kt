package ru.zalutskii.google.test.parser.testList

import java.io.BufferedReader

class TestListLexerMock(tokens: List<Token>): ITestListLexer {
    var iterator = tokens.iterator()

    override fun parseToken(stream: BufferedReader): Token? {
        return if (iterator.hasNext()) {
            iterator.next()
        } else {
            null
        }
    }
}