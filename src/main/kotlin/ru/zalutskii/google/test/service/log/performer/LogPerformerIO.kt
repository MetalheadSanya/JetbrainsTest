package ru.zalutskii.google.test.service.log.performer

import java.io.BufferedReader

interface LogPerformerInput {
    /**
     * Reset state of [LogPerformerInput].
     *
     * This method clean all saved log in performer.
     */
    fun reset()

    /**
     * Perform log from reader.
     *
     * This method start perform buffer. And call [LogPerformerOutput] methods
     * when something happened.
     *
     * @param reader log buffered reader.
     */
    fun perform(reader: BufferedReader)

    /**
     * Get log for concrete test function.
     *
     * Required complexity O(1).
     *
     * @param suite test suite name.
     * @param test test function name.
     *
     * @return log for test function.
     */
    fun setCurrentLogTo(suite: String, test: String): String

    /**
     * Get log for concrete test suite.
     *
     * Required complexity O(1).
     *
     * @param suite test suite name.
     *
     * @return log for test suite.
     */
    fun setCurrentLogTo(suite: String): String

    /**
     * Get full log of test run.
     *
     * Required complexity O(1).
     *
     * @return full log of test run.
     */
    fun setCurrentLogToRoot(): String
}

interface LogPerformerOutput {
    /**
     * New part of log was processed.
     *
     * [LogPerformerInput] must call this method on each processed line
     * and pass full processed log as argument.
     *
     * @param log processed log.
     */
    fun didProcessLog(log: String)

    /**
     * Test run has been started.
     *
     * [LogPerformerInput] must call this method when found in log line
     * that test has been started.
     */
    fun didStartTest()

    /**
     * Test run has been ended.
     *
     * [LogPerformerInput] must call this method when found in log line
     * that test has been ended.
     */
    fun didEndTest()

    /**
     * Test suite has been started.
     *
     * [LogPerformerInput] must call this method when found in log line
     * that test suite has been started.
     *
     * @param suiteName name of started suite.
     */
    fun didStartTestSuite(suiteName: String)

    /**
     * Test suite has been ended.
     *
     * [LogPerformerInput] must call this method when found in log line
     * that test suite has been ended.
     *
     * @param suiteName name of ended suite.
     * @param milliseconds test suite execution time.
     */
    fun didEndTestSuite(suiteName: String, milliseconds: Int)

    /**
     * Test function has been started.
     *
     * [LogPerformerInput] must call this method when found in log line
     * that test function has been started.
     *
     * @param suiteName name of test function suite.
     * @param testName name of test function.
     */
    fun didStartTest(suiteName: String, testName: String)

    /**
     * Test function has been passed.
     *
     * [LogPerformerInput] must call this method when found in log line
     * that test function has been passed.
     *
     * @param suiteName name of passed suite.
     * @param milliseconds test function execution time.
     */
    fun didPassTest(suiteName: String, testName: String, milliseconds: Int)

    /**
     * Test function has been failed.
     *
     * [LogPerformerInput] must call this method when found in log line
     * that test function has been failed.
     *
     * @param suiteName name of failed suite.
     * @param milliseconds test function execution time.
     */
    fun didFailTest(suiteName: String, testName: String, milliseconds: Int)
}
