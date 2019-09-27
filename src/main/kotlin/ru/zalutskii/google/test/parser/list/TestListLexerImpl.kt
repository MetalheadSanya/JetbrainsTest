package ru.zalutskii.google.test.parser.list

import java.io.BufferedReader

class TestListLexerImpl : TestListLexer {

    override fun parseToken(stream: BufferedReader): Token? {

        val line = stream.readLine()

        if (line == null) {
            stream.close()
            return null
        }

        return if (line.startsWith("  ")) {
            Token(
                Token.Type.TEST_FUNCTION,
                line.removePrefix("  ").substringBefore("  ")
            )
        } else {
            Token(
                Token.Type.TEST_CASE,
                line.substringBefore(".")
            )
        }
    }
}