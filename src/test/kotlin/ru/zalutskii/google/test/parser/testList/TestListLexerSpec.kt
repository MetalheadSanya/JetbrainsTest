package ru.zalutskii.google.test.parser.testList

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.io.BufferedReader
import java.io.StringReader

class TestListLexerSpec : StringSpec() {
    init {
        "should parse simple tree" {
            val output = "CustomOutputTest.\n" +
                    "  PrintsMessage\n" +
                    "  Succeeds\n" +
                    "  Fails\n"
            val reader = BufferedReader(StringReader(output))
            val lexer = TestListLexer()
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_CASE, "CustomOutputTest"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "PrintsMessage"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "Succeeds"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "Fails"))
            lexer.parseToken(reader).shouldBe(null)
        }

        "should parse tree with two test case" {
            val output = "FactorialTest.\n" +
                    "  Negative\n" +
                    "  Zero\n" +
                    "  Positive\n" +
                    "IsPrimeTest.\n" +
                    "  Negative\n" +
                    "  Trivial\n" +
                    "  Positive"
            val reader = BufferedReader(StringReader(output))
            val lexer = TestListLexer()
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_CASE, "FactorialTest"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "Negative"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "Zero"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "Positive"))

            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_CASE, "IsPrimeTest"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "Negative"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "Trivial"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "Positive"))
            lexer.parseToken(reader).shouldBe(null)
        }

        "should parse tree with TypeParam" {
            val output = "PrimeTableTest/0.  # TypeParam = OnTheFlyPrimeTable\n" +
                    "  ReturnsFalseForNonPrimes\n" +
                    "  ReturnsTrueForPrimes\n" +
                    "  CanGetNextPrime\n" +
                    "PrimeTableTest/1.  # TypeParam = PreCalculatedPrimeTable\n" +
                    "  ReturnsFalseForNonPrimes\n" +
                    "  ReturnsTrueForPrimes\n" +
                    "  CanGetNextPrime\n" +
                    "OnTheFlyAndPreCalculated/PrimeTableTest2/0.  # TypeParam = OnTheFlyPrimeTable\n" +
                    "  ReturnsFalseForNonPrimes\n" +
                    "  ReturnsTrueForPrimes\n" +
                    "  CanGetNextPrime\n" +
                    "OnTheFlyAndPreCalculated/PrimeTableTest2/1.  # TypeParam = PreCalculatedPrimeTable\n" +
                    "  ReturnsFalseForNonPrimes\n" +
                    "  ReturnsTrueForPrimes\n" +
                    "  CanGetNextPrime"

            val reader = BufferedReader(StringReader(output))
            val lexer = TestListLexer()
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_CASE, "PrimeTableTest/0.  # TypeParam = OnTheFlyPrimeTable"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "ReturnsFalseForNonPrimes"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "ReturnsTrueForPrimes"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "CanGetNextPrime"))

            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_CASE, "PrimeTableTest/1.  # TypeParam = PreCalculatedPrimeTable"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "ReturnsFalseForNonPrimes"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "ReturnsTrueForPrimes"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "CanGetNextPrime"))

            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_CASE, "OnTheFlyAndPreCalculated/PrimeTableTest2/0.  # TypeParam = OnTheFlyPrimeTable"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "ReturnsFalseForNonPrimes"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "ReturnsTrueForPrimes"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "CanGetNextPrime"))

            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_CASE, "OnTheFlyAndPreCalculated/PrimeTableTest2/1.  # TypeParam = PreCalculatedPrimeTable"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "ReturnsFalseForNonPrimes"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "ReturnsTrueForPrimes"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "CanGetNextPrime"))
            lexer.parseToken(reader).shouldBe(null)
        }

        "should parse tree with GetParam" {
            val output = "OnTheFlyAndPreCalculated/PrimeTableTestSmpl7.\n" +
                    "  ReturnsFalseForNonPrimes/0  # GetParam() = 0x10f894dd0\n" +
                    "  ReturnsFalseForNonPrimes/1  # GetParam() = 0x10f894e10\n" +
                    "  ReturnsTrueForPrimes/0  # GetParam() = 0x10f894dd0\n" +
                    "  ReturnsTrueForPrimes/1  # GetParam() = 0x10f894e10\n" +
                    "  CanGetNextPrime/0  # GetParam() = 0x10f894dd0\n" +
                    "  CanGetNextPrime/1  # GetParam() = 0x10f894e10"

            val reader = BufferedReader(StringReader(output))
            val lexer = TestListLexer()
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_CASE, "OnTheFlyAndPreCalculated/PrimeTableTestSmpl7"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "ReturnsFalseForNonPrimes/0  # GetParam() = 0x10f894dd0"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "ReturnsFalseForNonPrimes/1  # GetParam() = 0x10f894e10"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "ReturnsTrueForPrimes/0  # GetParam() = 0x10f894dd0"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "ReturnsTrueForPrimes/1  # GetParam() = 0x10f894e10"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "CanGetNextPrime/0  # GetParam() = 0x10f894dd0"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "CanGetNextPrime/1  # GetParam() = 0x10f894e10"))
            lexer.parseToken(reader).shouldBe(null)
        }

        "should parse tree with GetParam with typed values" {
            val output = "MeaningfulTestParameters/PrimeTableTest.\n" +
                    "  ReturnsFalseForNonPrimes/0  # GetParam() = (false, 1)\n" +
                    "  ReturnsFalseForNonPrimes/1  # GetParam() = (false, 10)\n" +
                    "  ReturnsFalseForNonPrimes/2  # GetParam() = (true, 1)\n" +
                    "  ReturnsFalseForNonPrimes/3  # GetParam() = (true, 10)\n" +
                    "  ReturnsTrueForPrimes/0  # GetParam() = (false, 1)\n" +
                    "  ReturnsTrueForPrimes/1  # GetParam() = (false, 10)\n" +
                    "  ReturnsTrueForPrimes/2  # GetParam() = (true, 1)\n" +
                    "  ReturnsTrueForPrimes/3  # GetParam() = (true, 10)\n" +
                    "  CanGetNextPrime/0  # GetParam() = (false, 1)\n" +
                    "  CanGetNextPrime/1  # GetParam() = (false, 10)\n" +
                    "  CanGetNextPrime/2  # GetParam() = (true, 1)\n" +
                    "  CanGetNextPrime/3  # GetParam() = (true, 10)"

            val reader = BufferedReader(StringReader(output))
            val lexer = TestListLexer()
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_CASE, "MeaningfulTestParameters/PrimeTableTest"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "ReturnsFalseForNonPrimes/0  # GetParam() = (false, 1)"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "ReturnsFalseForNonPrimes/1  # GetParam() = (false, 10)"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "ReturnsFalseForNonPrimes/2  # GetParam() = (true, 1)"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "ReturnsFalseForNonPrimes/3  # GetParam() = (true, 10)"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "ReturnsTrueForPrimes/0  # GetParam() = (false, 1)"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "ReturnsTrueForPrimes/1  # GetParam() = (false, 10)"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "ReturnsTrueForPrimes/2  # GetParam() = (true, 1)"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "ReturnsTrueForPrimes/3  # GetParam() = (true, 10)"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "CanGetNextPrime/0  # GetParam() = (false, 1)"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "CanGetNextPrime/1  # GetParam() = (false, 10)"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "CanGetNextPrime/2  # GetParam() = (true, 1)"))
            lexer.parseToken(reader).shouldBe(Token(Token.Type.TEST_FUNCTION, "CanGetNextPrime/3  # GetParam() = (true, 10)"))
            lexer.parseToken(reader).shouldBe(null)
        }
    }
}