package ru.zalutskii.google.test.parser.list

import java.io.BufferedReader

class TestListLexerMock(tokens: List<Token>) :
    TestListLexer {
    var iterator = tokens.iterator()

    override fun parseToken(stream: BufferedReader): Token? {
        return if (iterator.hasNext()) {
            iterator.next()
        } else {
            null
        }
    }
}