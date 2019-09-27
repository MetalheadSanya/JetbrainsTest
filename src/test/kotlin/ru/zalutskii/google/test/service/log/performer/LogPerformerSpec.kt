package ru.zalutskii.google.test.service.log.performer

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturnConsecutively
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import io.kotlintest.specs.StringSpec
import ru.zalutskii.google.test.parser.log.*
import java.io.BufferedReader
import java.io.StringReader

class LogPerformerSpec : StringSpec() {
    init {
        "tesst" {
            val parser = mock<TestLogParser> {
                on { parseToken(any()) }.doReturnConsecutively(
                    listOf(
                        UnknownToken("Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc"),
                        RunToken("[==========] Running 6 tests from 2 test suites."),
                        UnknownToken("[----------] Global test environment set-up."),
                        SuiteStartToken("FactorialTest", 3, "[----------] 3 tests from FactorialTest"),
                        RunTestToken("FactorialTest", "Negative", "[ RUN      ] FactorialTest.Negative"),
                        OkTestToken("FactorialTest", "Negative", 0, "[       OK ] FactorialTest.Negative (0 ms)"),
                        RunTestToken("FactorialTest", "Zero", "[ RUN      ] FactorialTest.Zero"),
                        OkTestToken("FactorialTest", "Zero", 0, "[       OK ] FactorialTest.Zero (0 ms)"),
                        RunTestToken("FactorialTest", "Positive", "[ RUN      ] FactorialTest.Positive"),
                        OkTestToken("FactorialTest", "Positive", 0, "[       OK ] FactorialTest.Positive (0 ms)"),
                        SuiteEndToken("FactorialTest", 3, 0, "[----------] 3 tests from FactorialTest (0 ms total)"),
                        UnknownToken(""),
                        SuiteStartToken("IsPrimeTest", 1, "[----------] 1 test from IsPrimeTest"),
                        RunTestToken("IsPrimeTest", "Negative", "[ RUN      ] IsPrimeTest.Negative"),
                        OkTestToken("IsPrimeTest", "Negative", 0, "[       OK ] IsPrimeTest.Negative (0 ms)"),
                        SuiteEndToken("IsPrimeTest", 1, 0, "[----------] 1 test from IsPrimeTest (0 ms total)"),
                        UnknownToken(""),
                        UnknownToken("[----------] Global test environment tear-down"),
                        StopToken("[==========] 6 tests from 2 test suites ran. (0 ms total)"),
                        PassedToken(6, "[  PASSED  ] 6 tests."),
                        null
                    )
                )
            }

            val outputMock = mock<LogPerformerOutput>()

            val buffer = BufferedReader(StringReader(""))
            val performer = LogPerformer(parser)
            performer.output = outputMock
            performer.perform(buffer)

            val order = inOrder(outputMock)

            order.verify(outputMock).didProcessLog(
                "Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc"
            )
            order.verify(outputMock).didProcessLog(
                "Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc\n" +
                        "[==========] Running 6 tests from 2 test suites."
            )
            order.verify(outputMock).didStartTest()
            order.verify(outputMock).didProcessLog(
                "Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc\n" +
                        "[==========] Running 6 tests from 2 test suites.\n" +
                        "[----------] Global test environment set-up."
            )
            order.verify(outputMock).didProcessLog(
                "Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc\n" +
                        "[==========] Running 6 tests from 2 test suites.\n" +
                        "[----------] Global test environment set-up.\n" +
                        "[----------] 3 tests from FactorialTest"
            )
            order.verify(outputMock).didStartTestSuite("FactorialTest")
            order.verify(outputMock).didProcessLog(
                "Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc\n" +
                        "[==========] Running 6 tests from 2 test suites.\n" +
                        "[----------] Global test environment set-up.\n" +
                        "[----------] 3 tests from FactorialTest\n" +
                        "[ RUN      ] FactorialTest.Negative"
            )
            order.verify(outputMock).didStartTest("FactorialTest", "Negative")
            order.verify(outputMock).didProcessLog(
                "Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc\n" +
                        "[==========] Running 6 tests from 2 test suites.\n" +
                        "[----------] Global test environment set-up.\n" +
                        "[----------] 3 tests from FactorialTest\n" +
                        "[ RUN      ] FactorialTest.Negative\n" +
                        "[       OK ] FactorialTest.Negative (0 ms)"
            )
            order.verify(outputMock).didPassTest("FactorialTest", "Negative", 0)
            order.verify(outputMock).didProcessLog(
                "Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc\n" +
                        "[==========] Running 6 tests from 2 test suites.\n" +
                        "[----------] Global test environment set-up.\n" +
                        "[----------] 3 tests from FactorialTest\n" +
                        "[ RUN      ] FactorialTest.Negative\n" +
                        "[       OK ] FactorialTest.Negative (0 ms)\n" +
                        "[ RUN      ] FactorialTest.Zero"
            )
            order.verify(outputMock).didStartTest("FactorialTest", "Zero")
            order.verify(outputMock).didProcessLog(
                "Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc\n" +
                        "[==========] Running 6 tests from 2 test suites.\n" +
                        "[----------] Global test environment set-up.\n" +
                        "[----------] 3 tests from FactorialTest\n" +
                        "[ RUN      ] FactorialTest.Negative\n" +
                        "[       OK ] FactorialTest.Negative (0 ms)\n" +
                        "[ RUN      ] FactorialTest.Zero\n" +
                        "[       OK ] FactorialTest.Zero (0 ms)"
            )
            order.verify(outputMock).didPassTest("FactorialTest", "Zero", 0)
            order.verify(outputMock).didProcessLog(
                "Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc\n" +
                        "[==========] Running 6 tests from 2 test suites.\n" +
                        "[----------] Global test environment set-up.\n" +
                        "[----------] 3 tests from FactorialTest\n" +
                        "[ RUN      ] FactorialTest.Negative\n" +
                        "[       OK ] FactorialTest.Negative (0 ms)\n" +
                        "[ RUN      ] FactorialTest.Zero\n" +
                        "[       OK ] FactorialTest.Zero (0 ms)\n" +
                        "[ RUN      ] FactorialTest.Positive"
            )
            order.verify(outputMock).didStartTest("FactorialTest", "Positive")
            order.verify(outputMock).didProcessLog(
                "Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc\n" +
                        "[==========] Running 6 tests from 2 test suites.\n" +
                        "[----------] Global test environment set-up.\n" +
                        "[----------] 3 tests from FactorialTest\n" +
                        "[ RUN      ] FactorialTest.Negative\n" +
                        "[       OK ] FactorialTest.Negative (0 ms)\n" +
                        "[ RUN      ] FactorialTest.Zero\n" +
                        "[       OK ] FactorialTest.Zero (0 ms)\n" +
                        "[ RUN      ] FactorialTest.Positive\n" +
                        "[       OK ] FactorialTest.Positive (0 ms)"
            )
            order.verify(outputMock).didPassTest("FactorialTest", "Positive", 0)
            order.verify(outputMock).didProcessLog(
                "Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc\n" +
                        "[==========] Running 6 tests from 2 test suites.\n" +
                        "[----------] Global test environment set-up.\n" +
                        "[----------] 3 tests from FactorialTest\n" +
                        "[ RUN      ] FactorialTest.Negative\n" +
                        "[       OK ] FactorialTest.Negative (0 ms)\n" +
                        "[ RUN      ] FactorialTest.Zero\n" +
                        "[       OK ] FactorialTest.Zero (0 ms)\n" +
                        "[ RUN      ] FactorialTest.Positive\n" +
                        "[       OK ] FactorialTest.Positive (0 ms)\n" +
                        "[----------] 3 tests from FactorialTest (0 ms total)"
            )
            order.verify(outputMock).didEndTestSuite("FactorialTest", 0)
            order.verify(outputMock).didProcessLog(
                "Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc\n" +
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
                        ""
            )
            order.verify(outputMock).didProcessLog(
                "Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc\n" +
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
                        "[----------] 1 test from IsPrimeTest"
            )
            order.verify(outputMock).didStartTestSuite("IsPrimeTest")
            order.verify(outputMock).didProcessLog(
                "Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc\n" +
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
                        "[ RUN      ] IsPrimeTest.Negative"
            )
            order.verify(outputMock).didStartTest("IsPrimeTest", "Negative")
            order.verify(outputMock).didProcessLog(
                "Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc\n" +
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
                        "[       OK ] IsPrimeTest.Negative (0 ms)"
            )
            order.verify(outputMock).didPassTest("IsPrimeTest", "Negative", 0)
            order.verify(outputMock).didProcessLog(
                "Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc\n" +
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
                        "[----------] 1 test from IsPrimeTest (0 ms total)"
            )
            order.verify(outputMock).didEndTestSuite("IsPrimeTest", 0)
            order.verify(outputMock).didProcessLog(
                "Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc\n" +
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
                        ""
            )
            order.verify(outputMock).didProcessLog(
                "Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc\n" +
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
                        "[----------] Global test environment tear-down"
            )
            order.verify(outputMock).didProcessLog(
                "Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc\n" +
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
                        "[==========] 6 tests from 2 test suites ran. (0 ms total)"

            )
            order.verify(outputMock).didEndTest()
            order.verify(outputMock).didProcessLog(
                "Running main() from /Users/zalutskii/Work/googleTest/googletest/src/gtest_main.cc\n" +
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
            )
        }

        "te" {
            val parser = mock<TestLogParser> {
                on { parseToken(any()) }.doReturnConsecutively(
                    listOf(
                        UnknownToken("Run this program with --terse_output to change the way it prints its output."),
                        RunToken("[==========] Running 2 tests from 1 test suite."),
                        UnknownToken("[----------] Global test environment set-up."),
                        SuiteStartToken("CustomOutputTest", 2, "[----------] 2 tests from CustomOutputTest"),
                        RunTestToken("CustomOutputTest", "Succeeds", "[ RUN      ] CustomOutputTest.Succeeds"),
                        OkTestToken("CustomOutputTest", "Succeeds", 0, "[       OK ] CustomOutputTest.Succeeds (0 ms)"),
                        RunTestToken("CustomOutputTest", "Fails", "[ RUN      ] CustomOutputTest.Fails"),
                        UnknownToken("/Users/zalutskii/Work/googleTest/googletest/samples/sample9_unittest.cc:99: Failure"),
                        UnknownToken("Expected equality of these values:"),
                        UnknownToken("  1"),
                        UnknownToken("  2"),
                        UnknownToken("This test fails in order to demonstrate alternative failure messages"),
                        FailedTestToken("CustomOutputTest", "Fails", 0, "[  FAILED  ] CustomOutputTest.Fails (0 ms)"),
                        SuiteEndToken(
                            "CustomOutputTest",
                            2,
                            0,
                            "[----------] 2 tests from CustomOutputTest (0 ms total)"
                        ),
                        UnknownToken(""),
                        UnknownToken("[----------] Global test environment tear-down"),
                        StopToken("[==========] 2 tests from 1 test suite ran. (0 ms total)"),
                        PassedToken(1, "[  PASSED  ] 1 test."),
                        UnknownToken("[  FAILED  ] 1 test, listed below:"),
                        UnknownToken("[  FAILED  ] CustomOutputTest.Fails"),
                        UnknownToken(""),
                        UnknownToken(" 1 FAILED TEST"),
                        null
                    )
                )
            }

            val outputMock = mock<LogPerformerOutput>()

            val buffer = BufferedReader(StringReader(""))
            val performer = LogPerformer(parser)
            performer.output = outputMock
            performer.perform(buffer)

            val order = inOrder(outputMock)

            order.verify(outputMock).didProcessLog(
                "Run this program with --terse_output to change the way it prints its output."
            )
            order.verify(outputMock).didProcessLog(
                "Run this program with --terse_output to change the way it prints its output.\n" +
                        "[==========] Running 2 tests from 1 test suite."
            )
            order.verify(outputMock).didStartTest()
            order.verify(outputMock).didProcessLog(
                "Run this program with --terse_output to change the way it prints its output.\n" +
                        "[==========] Running 2 tests from 1 test suite.\n" +
                        "[----------] Global test environment set-up."
            )

            order.verify(outputMock).didProcessLog(
                "Run this program with --terse_output to change the way it prints its output.\n" +
                        "[==========] Running 2 tests from 1 test suite.\n" +
                        "[----------] Global test environment set-up.\n" +
                        "[----------] 2 tests from CustomOutputTest"
            )
            order.verify(outputMock).didStartTestSuite("CustomOutputTest")
            order.verify(outputMock).didProcessLog(
                "Run this program with --terse_output to change the way it prints its output.\n" +
                        "[==========] Running 2 tests from 1 test suite.\n" +
                        "[----------] Global test environment set-up.\n" +
                        "[----------] 2 tests from CustomOutputTest\n" +
                        "[ RUN      ] CustomOutputTest.Succeeds"
            )
            order.verify(outputMock).didStartTest("CustomOutputTest", "Succeeds")
            order.verify(outputMock).didProcessLog(
                "Run this program with --terse_output to change the way it prints its output.\n" +
                        "[==========] Running 2 tests from 1 test suite.\n" +
                        "[----------] Global test environment set-up.\n" +
                        "[----------] 2 tests from CustomOutputTest\n" +
                        "[ RUN      ] CustomOutputTest.Succeeds\n" +
                        "[       OK ] CustomOutputTest.Succeeds (0 ms)"
            )
            order.verify(outputMock).didPassTest("CustomOutputTest", "Succeeds", 0)
            order.verify(outputMock).didProcessLog(
                "Run this program with --terse_output to change the way it prints its output.\n" +
                        "[==========] Running 2 tests from 1 test suite.\n" +
                        "[----------] Global test environment set-up.\n" +
                        "[----------] 2 tests from CustomOutputTest\n" +
                        "[ RUN      ] CustomOutputTest.Succeeds\n" +
                        "[       OK ] CustomOutputTest.Succeeds (0 ms)\n" +
                        "[ RUN      ] CustomOutputTest.Fails"
            )
            order.verify(outputMock).didStartTest("CustomOutputTest", "Fails")
            order.verify(outputMock).didProcessLog(
                "Run this program with --terse_output to change the way it prints its output.\n" +
                        "[==========] Running 2 tests from 1 test suite.\n" +
                        "[----------] Global test environment set-up.\n" +
                        "[----------] 2 tests from CustomOutputTest\n" +
                        "[ RUN      ] CustomOutputTest.Succeeds\n" +
                        "[       OK ] CustomOutputTest.Succeeds (0 ms)\n" +
                        "[ RUN      ] CustomOutputTest.Fails\n" +
                        "/Users/zalutskii/Work/googleTest/googletest/samples/sample9_unittest.cc:99: Failure"
            )
            order.verify(outputMock).didProcessLog(
                "Run this program with --terse_output to change the way it prints its output.\n" +
                        "[==========] Running 2 tests from 1 test suite.\n" +
                        "[----------] Global test environment set-up.\n" +
                        "[----------] 2 tests from CustomOutputTest\n" +
                        "[ RUN      ] CustomOutputTest.Succeeds\n" +
                        "[       OK ] CustomOutputTest.Succeeds (0 ms)\n" +
                        "[ RUN      ] CustomOutputTest.Fails\n" +
                        "/Users/zalutskii/Work/googleTest/googletest/samples/sample9_unittest.cc:99: Failure\n" +
                        "Expected equality of these values:"
            )
            order.verify(outputMock).didProcessLog(
                "Run this program with --terse_output to change the way it prints its output.\n" +
                        "[==========] Running 2 tests from 1 test suite.\n" +
                        "[----------] Global test environment set-up.\n" +
                        "[----------] 2 tests from CustomOutputTest\n" +
                        "[ RUN      ] CustomOutputTest.Succeeds\n" +
                        "[       OK ] CustomOutputTest.Succeeds (0 ms)\n" +
                        "[ RUN      ] CustomOutputTest.Fails\n" +
                        "/Users/zalutskii/Work/googleTest/googletest/samples/sample9_unittest.cc:99: Failure\n" +
                        "Expected equality of these values:\n" +
                        "  1"
            )
            order.verify(outputMock).didProcessLog(
                "Run this program with --terse_output to change the way it prints its output.\n" +
                        "[==========] Running 2 tests from 1 test suite.\n" +
                        "[----------] Global test environment set-up.\n" +
                        "[----------] 2 tests from CustomOutputTest\n" +
                        "[ RUN      ] CustomOutputTest.Succeeds\n" +
                        "[       OK ] CustomOutputTest.Succeeds (0 ms)\n" +
                        "[ RUN      ] CustomOutputTest.Fails\n" +
                        "/Users/zalutskii/Work/googleTest/googletest/samples/sample9_unittest.cc:99: Failure\n" +
                        "Expected equality of these values:\n" +
                        "  1\n" +
                        "  2"
            )
            order.verify(outputMock).didProcessLog(
                "Run this program with --terse_output to change the way it prints its output.\n" +
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
                        "This test fails in order to demonstrate alternative failure messages"
            )
            order.verify(outputMock).didProcessLog(
                "Run this program with --terse_output to change the way it prints its output.\n" +
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
                        "[  FAILED  ] CustomOutputTest.Fails (0 ms)"
            )
            order.verify(outputMock).didFailTest("CustomOutputTest", "Fails", 0)
            order.verify(outputMock).didProcessLog(
                "Run this program with --terse_output to change the way it prints its output.\n" +
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
                        "[----------] 2 tests from CustomOutputTest (0 ms total)"
            )
            order.verify(outputMock).didEndTestSuite("CustomOutputTest", 0)
            order.verify(outputMock).didProcessLog(
                "Run this program with --terse_output to change the way it prints its output.\n" +
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
                        ""
            )
            order.verify(outputMock).didProcessLog(
                "Run this program with --terse_output to change the way it prints its output.\n" +
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
                        "[----------] Global test environment tear-down"
            )
            order.verify(outputMock).didProcessLog(
                "Run this program with --terse_output to change the way it prints its output.\n" +
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
                        "[==========] 2 tests from 1 test suite ran. (0 ms total)"
            )
            order.verify(outputMock).didEndTest()
            order.verify(outputMock).didProcessLog(
                "Run this program with --terse_output to change the way it prints its output.\n" +
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
                        "[  PASSED  ] 1 test."
            )
            order.verify(outputMock).didProcessLog(
                "Run this program with --terse_output to change the way it prints its output.\n" +
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
                        "[  FAILED  ] 1 test, listed below:"
            )
            order.verify(outputMock).didProcessLog(
                "Run this program with --terse_output to change the way it prints its output.\n" +
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
                        "[  FAILED  ] CustomOutputTest.Fails"
            )
            order.verify(outputMock).didProcessLog(
                "Run this program with --terse_output to change the way it prints its output.\n" +
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
                        ""
            )
            order.verify(outputMock).didProcessLog(
                "Run this program with --terse_output to change the way it prints its output.\n" +
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
            )
        }

    }
}