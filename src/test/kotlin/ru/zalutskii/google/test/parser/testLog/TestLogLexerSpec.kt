package ru.zalutskii.google.test.parser.testLog

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.io.BufferedReader
import java.io.StringReader

class TestLogLexerSpec : StringSpec() {
    init {
        "should parse simple log" {
            val output = "Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc\n" +
                    "[==========] Running 6 tests from 2 test suites.\n" +
                    "[----------] Global test environment set-up.\n" +
                    "[----------] 3 tests from FactorialTest\n" +
                    "[ RUN      ] FactorialTest.Negative\n" +
                    "[       OK ] FactorialTest.Negative (0 ms)\n" +
                    "[ RUN      ] FactorialTest.Zero\n" +
                    "[       OK ] FactorialTest.Zero (0 ms)\n" +
                    "[ RUN      ] FactorialTest.Positive\n" +
                    "[       OK ] FactorialTest.Positive (0 ms)\n" +
                    "[----------] 3 tests from FactorialTest (0 ms total)\n" +
                    "\n" +
                    "[----------] 1 test from IsPrimeTest\n" +
                    "[ RUN      ] IsPrimeTest.Negative\n" +
                    "[       OK ] IsPrimeTest.Negative (0 ms)\n" +
                    "[----------] 1 test from IsPrimeTest (0 ms total)\n" +
                    "\n" +
                    "[----------] Global test environment tear-down\n" +
                    "[==========] 6 tests from 2 test suites ran. (0 ms total)\n" +
                    "[  PASSED  ] 6 tests."

            val reader = BufferedReader(StringReader(output))
            val lexer = TestLogLexer()

            lexer.parseToken(reader).shouldBe(UnknownToken("Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc"))
            lexer.parseToken(reader).shouldBe(UnknownToken("[==========] Running 6 tests from 2 test suites."))
            lexer.parseToken(reader).shouldBe(UnknownToken("[----------] Global test environment set-up."))

            lexer.parseToken(reader).shouldBe(SuiteStartToken("FactorialTest", 3))
            lexer.parseToken(reader).shouldBe(RunTestToken("FactorialTest", "Negative"))
            lexer.parseToken(reader).shouldBe(OkTestToken("FactorialTest" , "Negative", 0))
            lexer.parseToken(reader).shouldBe(RunTestToken("FactorialTest", "Zero"))
            lexer.parseToken(reader).shouldBe(OkTestToken("FactorialTest" , "Zero", 0))
            lexer.parseToken(reader).shouldBe(RunTestToken("FactorialTest", "Positive"))
            lexer.parseToken(reader).shouldBe(OkTestToken("FactorialTest" , "Positive", 0))
            lexer.parseToken(reader).shouldBe(SuiteEndToken("FactorialTest", 3, 0))

            lexer.parseToken(reader).shouldBe(SuiteStartToken("IsPrimeTest", 1))
            lexer.parseToken(reader).shouldBe(RunTestToken("IsPrimeTest", "Negative"))
            lexer.parseToken(reader).shouldBe(OkTestToken("IsPrimeTest" , "Negative", 0))
            lexer.parseToken(reader).shouldBe(SuiteEndToken("IsPrimeTest", 1, 0))

            lexer.parseToken(reader).shouldBe(UnknownToken("[----------] Global test environment tear-down"))
            lexer.parseToken(reader).shouldBe(UnknownToken("[==========] 6 tests from 2 test suites ran. (0 ms total)"))
            lexer.parseToken(reader).shouldBe(PassedToken(6))
            lexer.parseToken(reader).shouldBe(null)
        }

        "should parse log with custom output" {
            val output = "Run this program with --terse_output to change the way it prints its output.\n" +
                    "[==========] Running 1 test from 1 test suite.\n" +
                    "[----------] Global test environment set-up.\n" +
                    "[----------] 1 test from CustomOutputTest\n" +
                    "[ RUN      ] CustomOutputTest.PrintsMessage\n" +
                    "Printing something from the test body...\n" +
                    "[       OK ] CustomOutputTest.PrintsMessage (0 ms)\n" +
                    "[----------] 1 test from CustomOutputTest (0 ms total)\n" +
                    "\n" +
                    "[----------] Global test environment tear-down\n" +
                    "[==========] 1 test from 1 test suite ran. (0 ms total)\n" +
                    "[  PASSED  ] 1 test.\n"

            val reader = BufferedReader(StringReader(output))
            val lexer = TestLogLexer()

            lexer.parseToken(reader).shouldBe(UnknownToken("Run this program with --terse_output to change the way it prints its output."))
            lexer.parseToken(reader).shouldBe(UnknownToken("[==========] Running 1 test from 1 test suite."))
            lexer.parseToken(reader).shouldBe(UnknownToken("[----------] Global test environment set-up."))

            lexer.parseToken(reader).shouldBe(SuiteStartToken("CustomOutputTest", 1))
            lexer.parseToken(reader).shouldBe(RunTestToken("CustomOutputTest", "PrintsMessage"))
            lexer.parseToken(reader).shouldBe(UnknownToken("Printing something from the test body..."))
            lexer.parseToken(reader).shouldBe(OkTestToken("CustomOutputTest" , "PrintsMessage", 0))
            lexer.parseToken(reader).shouldBe(SuiteEndToken("CustomOutputTest", 1, 0))

            lexer.parseToken(reader).shouldBe(UnknownToken("[----------] Global test environment tear-down"))
            lexer.parseToken(reader).shouldBe(UnknownToken("[==========] 1 test from 1 test suite ran. (0 ms total)"))
            lexer.parseToken(reader).shouldBe(PassedToken(1))
            lexer.parseToken(reader).shouldBe(null)
        }

        "should parse log with errors" {
            val output = "Run this program with --terse_output to change the way it prints its output.\n" +
                    "[==========] Running 2 tests from 1 test suite.\n" +
                    "[----------] Global test environment set-up.\n" +
                    "[----------] 2 tests from CustomOutputTest\n" +
                    "[ RUN      ] CustomOutputTest.Succeeds\n" +
                    "[       OK ] CustomOutputTest.Succeeds (0 ms)\n" +
                    "[ RUN      ] CustomOutputTest.Fails\n" +
                    "/Users/zalutskii/Work/googleTest/googletest/samples/sample9_unittest.cc:99: Failure\n" +
                    "Expected equality of these values:\n" +
                    "  1\n" +
                    "  2\n" +
                    "This test fails in order to demonstrate alternative failure messages\n" +
                    "[  FAILED  ] CustomOutputTest.Fails (0 ms)\n" +
                    "[----------] 2 tests from CustomOutputTest (0 ms total)\n" +
                    "\n" +
                    "[----------] Global test environment tear-down\n" +
                    "[==========] 2 tests from 1 test suite ran. (0 ms total)\n" +
                    "[  PASSED  ] 1 test.\n" +
                    "[  FAILED  ] 1 test, listed below:\n" +
                    "[  FAILED  ] CustomOutputTest.Fails\n" +
                    "\n" +
                    " 1 FAILED TEST"

            val reader = BufferedReader(StringReader(output))
            val lexer = TestLogLexer()

            lexer.parseToken(reader).shouldBe(UnknownToken("Run this program with --terse_output to change the way it prints its output."))
            lexer.parseToken(reader).shouldBe(UnknownToken("[==========] Running 2 tests from 1 test suite."))
            lexer.parseToken(reader).shouldBe(UnknownToken("[----------] Global test environment set-up."))

            lexer.parseToken(reader).shouldBe(SuiteStartToken("CustomOutputTest", 2))
            lexer.parseToken(reader).shouldBe(RunTestToken("CustomOutputTest", "Succeeds"))
            lexer.parseToken(reader).shouldBe(OkTestToken("CustomOutputTest" , "Succeeds", 0))
            lexer.parseToken(reader).shouldBe(RunTestToken("CustomOutputTest", "Fails"))
            lexer.parseToken(reader).shouldBe(UnknownToken("/Users/zalutskii/Work/googleTest/googletest/samples/sample9_unittest.cc:99: Failure"))
            lexer.parseToken(reader).shouldBe(UnknownToken("Expected equality of these values:"))
            lexer.parseToken(reader).shouldBe(UnknownToken("  1"))
            lexer.parseToken(reader).shouldBe(UnknownToken("  2"))
            lexer.parseToken(reader).shouldBe(UnknownToken("This test fails in order to demonstrate alternative failure messages"))
            lexer.parseToken(reader).shouldBe(FailedTestToken("CustomOutputTest", "Fails", 0))
            lexer.parseToken(reader).shouldBe(SuiteEndToken("CustomOutputTest", 2, 0))

            lexer.parseToken(reader).shouldBe(UnknownToken("[----------] Global test environment tear-down"))
            lexer.parseToken(reader).shouldBe(UnknownToken("[==========] 2 tests from 1 test suite ran. (0 ms total)"))
            lexer.parseToken(reader).shouldBe(PassedToken(1))
            lexer.parseToken(reader).shouldBe(UnknownToken("[  FAILED  ] 1 test, listed below:"))
            lexer.parseToken(reader).shouldBe(UnknownToken("[  FAILED  ] CustomOutputTest.Fails"))

            lexer.parseToken(reader).shouldBe(UnknownToken(" 1 FAILED TEST"))
            lexer.parseToken(reader).shouldBe(null)
        }

        "should parse log with GetParam test" {
            val output = "Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc\n" +
                    "[==========] Running 1 test from 1 test suite.\n" +
                    "[----------] Global test environment set-up.\n" +
                    "[----------] 1 test from MeaningfulTestParameters/PrimeTableTest\n" +
                    "[ RUN      ] MeaningfulTestParameters/PrimeTableTest.ReturnsFalseForNonPrimes/0\n" +
                    "[       OK ] MeaningfulTestParameters/PrimeTableTest.ReturnsFalseForNonPrimes/0 (0 ms)\n" +
                    "[----------] 1 test from MeaningfulTestParameters/PrimeTableTest (0 ms total)\n" +
                    "\n" +
                    "[----------] Global test environment tear-down\n" +
                    "[==========] 1 test from 1 test suite ran. (0 ms total)\n" +
                    "[  PASSED  ] 1 test.\n"

            val reader = BufferedReader(StringReader(output))
            val lexer = TestLogLexer()

            lexer.parseToken(reader).shouldBe(UnknownToken("Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc"))
            lexer.parseToken(reader).shouldBe(UnknownToken("[==========] Running 1 test from 1 test suite."))
            lexer.parseToken(reader).shouldBe(UnknownToken("[----------] Global test environment set-up."))

            lexer.parseToken(reader).shouldBe(SuiteStartToken("MeaningfulTestParameters/PrimeTableTest", 1))
            lexer.parseToken(reader).shouldBe(RunTestToken("MeaningfulTestParameters/PrimeTableTest", "ReturnsFalseForNonPrimes/0"))
            lexer.parseToken(reader).shouldBe(OkTestToken("MeaningfulTestParameters/PrimeTableTest" , "ReturnsFalseForNonPrimes/0", 0))
            lexer.parseToken(reader).shouldBe(SuiteEndToken("MeaningfulTestParameters/PrimeTableTest", 1, 0))

            lexer.parseToken(reader).shouldBe(UnknownToken("[----------] Global test environment tear-down"))
            lexer.parseToken(reader).shouldBe(UnknownToken("[==========] 1 test from 1 test suite ran. (0 ms total)"))
            lexer.parseToken(reader).shouldBe(PassedToken(1))
            lexer.parseToken(reader).shouldBe(null)
        }

        "should parse test log with TypeParam" {
            val output = "Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc\n" +
                    "[==========] Running 1 test from 1 test suite.\n" +
                    "[----------] Global test environment set-up.\n" +
                    "[----------] 1 tests from PrimeTableTest/0, where TypeParam = OnTheFlyPrimeTable\n" +
                    "[ RUN      ] PrimeTableTest/0.ReturnsFalseForNonPrimes\n" +
                    "[       OK ] PrimeTableTest/0.ReturnsFalseForNonPrimes (0 ms)\n" +
                    "[----------] 1 test from PrimeTableTest/0 (0 ms total)\n" +
                    "[----------] Global test environment tear-down\n" +
                    "[==========] 1 test from 1 test suite ran. (0 ms total)\n" +
                    "[  PASSED  ] 1 test."

            val reader = BufferedReader(StringReader(output))
            val lexer = TestLogLexer()

            lexer.parseToken(reader).shouldBe(UnknownToken("Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc"))
            lexer.parseToken(reader).shouldBe(UnknownToken("[==========] Running 1 test from 1 test suite."))
            lexer.parseToken(reader).shouldBe(UnknownToken("[----------] Global test environment set-up."))

            lexer.parseToken(reader).shouldBe(SuiteStartToken("PrimeTableTest/0", 1))
            lexer.parseToken(reader).shouldBe(RunTestToken("PrimeTableTest/0", "ReturnsFalseForNonPrimes"))
            lexer.parseToken(reader).shouldBe(OkTestToken("PrimeTableTest/0" , "ReturnsFalseForNonPrimes", 0))
            lexer.parseToken(reader).shouldBe(SuiteEndToken("PrimeTableTest/0", 1, 0))

            lexer.parseToken(reader).shouldBe(UnknownToken("[----------] Global test environment tear-down"))
            lexer.parseToken(reader).shouldBe(UnknownToken("[==========] 1 test from 1 test suite ran. (0 ms total)"))
            lexer.parseToken(reader).shouldBe(PassedToken(1))
            lexer.parseToken(reader).shouldBe(null)
        }
    }
}
