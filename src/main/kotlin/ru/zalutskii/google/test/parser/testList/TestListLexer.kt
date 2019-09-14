package ru.zalutskii.google.test.parser.testList

import java.io.BufferedReader

class TestListLexer() : ITestListLexer {

    override fun parseToken(stream: BufferedReader): Token? {

        val line = stream.readLine()

        if (line == null) {
            stream.close()
            return null
        }

        return if (line.startsWith("  ")) {
            Token(Token.Type.TEST_FUNCTION, line.removePrefix("  "))
        } else {
            Token(Token.Type.TEST_CASE, line.removeSuffix("."))
        }
    }
}