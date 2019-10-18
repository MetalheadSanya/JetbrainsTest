package ru.zalutskii.google.test.service.process

import java.io.BufferedReader

interface TestProcess {
    /**
     * Read test cases and functions from opened file.
     *
     * @return buffer with program output.
     */
    fun readTestCases(): BufferedReader

    /**
     * Run tests from opened executable file.
     *
     * @return buffer with program output.
     */
    fun runTestCases(): BufferedReader

    /**
     * Run tests from opened executable file
     *
     * @param list list of all test function in format: "TestSuite.TestFunction"
     *
     * @return buffer with program output.
     */
    fun runTests(list: Iterable<String>): BufferedReader

    /**
     * Break current process if exist.
     */
    fun stop()
}