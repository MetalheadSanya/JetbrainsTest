package ru.zalutskii.google.test.parser.list

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.io.BufferedReader
import java.io.Reader

class TestListParserImplSpec : StringSpec() {
    init {
        "should parse tree with one case" {
            val mock = TestListLexerMock(
                listOf(
                    Token(
                        Token.Type.TEST_CASE,
                        "CustomOutputTest"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "PrintsMessage"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "Succeeds"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "Fails"
                    )
                )
            )

            val parser = TestListParserImpl(mock)

            parser.parseTree(BufferedReader(Reader.nullReader())).shouldBe(
                TestTree(
                    listOf(
                        TestTree.Case(
                            name = "CustomOutputTest",
                            functions = listOf(
                                TestTree.Case.Function("PrintsMessage"),
                                TestTree.Case.Function("Succeeds"),
                                TestTree.Case.Function("Fails")
                            )
                        )
                    )
                )
            )
        }

        "should parse tree with two test case" {
            val mock = TestListLexerMock(
                listOf(
                    Token(
                        Token.Type.TEST_CASE,
                        "FactorialTest"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "Negative"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "Zero"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "Positive"
                    ),
                    Token(
                        Token.Type.TEST_CASE,
                        "IsPrimeTest"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "Negative"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "Trivial"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "Positive"
                    )
                )
            )

            val parser = TestListParserImpl(mock)

            parser.parseTree(BufferedReader(Reader.nullReader())).shouldBe(
                TestTree(
                    listOf(
                        TestTree.Case(
                            name = "FactorialTest",
                            functions = listOf(
                                TestTree.Case.Function("Negative"),
                                TestTree.Case.Function("Zero"),
                                TestTree.Case.Function("Positive")
                            )
                        ),
                        TestTree.Case(
                            name = "IsPrimeTest",
                            functions = listOf(
                                TestTree.Case.Function("Negative"),
                                TestTree.Case.Function("Trivial"),
                                TestTree.Case.Function("Positive")
                            )
                        )
                    )
                )
            )
        }

        "should parse tree with TypeParam" {
            val mock = TestListLexerMock(
                listOf(
                    Token(
                        Token.Type.TEST_CASE,
                        "PrimeTableTest/0.  # TypeParam = OnTheFlyPrimeTable"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "ReturnsFalseForNonPrimes"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "ReturnsTrueForPrimes"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "CanGetNextPrime"
                    ),
                    Token(
                        Token.Type.TEST_CASE,
                        "PrimeTableTest/1.  # TypeParam = PreCalculatedPrimeTable"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "ReturnsFalseForNonPrimes"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "ReturnsTrueForPrimes"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "CanGetNextPrime"
                    ),
                    Token(
                        Token.Type.TEST_CASE,
                        "OnTheFlyAndPreCalculated/PrimeTableTest2/0.  # TypeParam = OnTheFlyPrimeTable"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "ReturnsFalseForNonPrimes"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "ReturnsTrueForPrimes"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "CanGetNextPrime"
                    ),
                    Token(
                        Token.Type.TEST_CASE,
                        "OnTheFlyAndPreCalculated/PrimeTableTest2/1.  # TypeParam = PreCalculatedPrimeTable"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "ReturnsFalseForNonPrimes"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "ReturnsTrueForPrimes"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "CanGetNextPrime"
                    )
                )
            )

            val parser = TestListParserImpl(mock)

            parser.parseTree(BufferedReader(Reader.nullReader())).shouldBe(
                TestTree(
                    listOf(
                        TestTree.Case(
                            name = "PrimeTableTest/0.  # TypeParam = OnTheFlyPrimeTable",
                            functions = listOf(
                                TestTree.Case.Function("ReturnsFalseForNonPrimes"),
                                TestTree.Case.Function("ReturnsTrueForPrimes"),
                                TestTree.Case.Function("CanGetNextPrime")
                            )
                        ),
                        TestTree.Case(
                            name = "PrimeTableTest/1.  # TypeParam = PreCalculatedPrimeTable",
                            functions = listOf(
                                TestTree.Case.Function("ReturnsFalseForNonPrimes"),
                                TestTree.Case.Function("ReturnsTrueForPrimes"),
                                TestTree.Case.Function("CanGetNextPrime")
                            )
                        ),
                        TestTree.Case(
                            name = "OnTheFlyAndPreCalculated/PrimeTableTest2/0.  # TypeParam = OnTheFlyPrimeTable",
                            functions = listOf(
                                TestTree.Case.Function("ReturnsFalseForNonPrimes"),
                                TestTree.Case.Function("ReturnsTrueForPrimes"),
                                TestTree.Case.Function("CanGetNextPrime")
                            )
                        ),
                        TestTree.Case(
                            name = "OnTheFlyAndPreCalculated/PrimeTableTest2/1.  # TypeParam = PreCalculatedPrimeTable",
                            functions = listOf(
                                TestTree.Case.Function("ReturnsFalseForNonPrimes"),
                                TestTree.Case.Function("ReturnsTrueForPrimes"),
                                TestTree.Case.Function("CanGetNextPrime")
                            )
                        )
                    )
                )
            )
        }

        "should parse tree with GetParam" {
            val mock = TestListLexerMock(
                listOf(
                    Token(
                        Token.Type.TEST_CASE,
                        "OnTheFlyAndPreCalculated/PrimeTableTestSmpl7"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "ReturnsFalseForNonPrimes/0  # GetParam() = 0x10f894dd0"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "ReturnsFalseForNonPrimes/1  # GetParam() = 0x10f894e10"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "ReturnsTrueForPrimes/0  # GetParam() = 0x10f894dd0"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "ReturnsTrueForPrimes/1  # GetParam() = 0x10f894e10"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "CanGetNextPrime/0  # GetParam() = 0x10f894dd0"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "CanGetNextPrime/1  # GetParam() = 0x10f894e10"
                    )
                )
            )

            val parser = TestListParserImpl(mock)

            parser.parseTree(BufferedReader(Reader.nullReader())).shouldBe(
                TestTree(
                    listOf(
                        TestTree.Case(
                            name = "OnTheFlyAndPreCalculated/PrimeTableTestSmpl7",
                            functions = listOf(
                                TestTree.Case.Function("ReturnsFalseForNonPrimes/0  # GetParam() = 0x10f894dd0"),
                                TestTree.Case.Function("ReturnsFalseForNonPrimes/1  # GetParam() = 0x10f894e10"),
                                TestTree.Case.Function("ReturnsTrueForPrimes/0  # GetParam() = 0x10f894dd0"),
                                TestTree.Case.Function("ReturnsTrueForPrimes/1  # GetParam() = 0x10f894e10"),
                                TestTree.Case.Function("CanGetNextPrime/0  # GetParam() = 0x10f894dd0"),
                                TestTree.Case.Function("CanGetNextPrime/1  # GetParam() = 0x10f894e10")
                            )
                        )
                    )
                )
            )
        }

        "should parse tree with GetParam with typed values" {
            val mock = TestListLexerMock(
                listOf(
                    Token(
                        Token.Type.TEST_CASE,
                        "MeaningfulTestParameters/PrimeTableTest"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "ReturnsFalseForNonPrimes/0  # GetParam() = (false, 1)"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "ReturnsFalseForNonPrimes/1  # GetParam() = (false, 10)"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "ReturnsFalseForNonPrimes/2  # GetParam() = (true, 1)"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "ReturnsFalseForNonPrimes/3  # GetParam() = (true, 10)"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "ReturnsTrueForPrimes/0  # GetParam() = (false, 1)"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "ReturnsTrueForPrimes/1  # GetParam() = (false, 10)"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "ReturnsTrueForPrimes/2  # GetParam() = (true, 1)"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "ReturnsTrueForPrimes/3  # GetParam() = (true, 10)"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "CanGetNextPrime/0  # GetParam() = (false, 1)"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "CanGetNextPrime/1  # GetParam() = (false, 10)"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "CanGetNextPrime/2  # GetParam() = (true, 1)"
                    ),
                    Token(
                        Token.Type.TEST_FUNCTION,
                        "CanGetNextPrime/3  # GetParam() = (true, 10)"
                    )
                )
            )

            val parser = TestListParserImpl(mock)

            parser.parseTree(BufferedReader(Reader.nullReader())).shouldBe(
                TestTree(
                    listOf(
                        TestTree.Case(
                            name = "MeaningfulTestParameters/PrimeTableTest",
                            functions = listOf(
                                TestTree.Case.Function("ReturnsFalseForNonPrimes/0  # GetParam() = (false, 1)"),
                                TestTree.Case.Function("ReturnsFalseForNonPrimes/1  # GetParam() = (false, 10)"),
                                TestTree.Case.Function("ReturnsFalseForNonPrimes/2  # GetParam() = (true, 1)"),
                                TestTree.Case.Function("ReturnsFalseForNonPrimes/3  # GetParam() = (true, 10)"),
                                TestTree.Case.Function("ReturnsTrueForPrimes/0  # GetParam() = (false, 1)"),
                                TestTree.Case.Function("ReturnsTrueForPrimes/1  # GetParam() = (false, 10)"),
                                TestTree.Case.Function("ReturnsTrueForPrimes/2  # GetParam() = (true, 1)"),
                                TestTree.Case.Function("ReturnsTrueForPrimes/3  # GetParam() = (true, 10)"),
                                TestTree.Case.Function("CanGetNextPrime/0  # GetParam() = (false, 1)"),
                                TestTree.Case.Function("CanGetNextPrime/1  # GetParam() = (false, 10)"),
                                TestTree.Case.Function("CanGetNextPrime/2  # GetParam() = (true, 1)"),
                                TestTree.Case.Function("CanGetNextPrime/3  # GetParam() = (true, 10)")
                            )
                        )
                    )
                )
            )
        }
    }
}