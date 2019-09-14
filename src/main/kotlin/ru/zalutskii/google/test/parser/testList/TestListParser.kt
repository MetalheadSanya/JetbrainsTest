package ru.zalutskii.google.test.parser.testList

import java.io.BufferedReader

class TestListParser(private val lexer: ITestListLexer) : ITestListParser {
    override fun parseTree(reader: BufferedReader): TestTree {
        val cases = mutableListOf<TestTree.Case>()
        var functions = mutableListOf<TestTree.Case.Function>()

        var testCaseName: String? = null

        while (true) {
            val token = lexer.parseToken(reader) ?: break

            if (token.type == Token.Type.TEST_CASE) {
                if (testCaseName != null && functions.isNotEmpty()) {
                    cases.add(TestTree.Case(testCaseName, functions.toList()))
                    functions = mutableListOf()
                }
                testCaseName = token.literal
            } else if (token.type == Token.Type.TEST_FUNCTION) {
                functions.add(TestTree.Case.Function(token.literal))
            }
        }

        if (testCaseName != null && functions.isNotEmpty()) {
            cases.add(TestTree.Case(testCaseName, functions.toList()))
        }

        return TestTree(cases)
    }
}
