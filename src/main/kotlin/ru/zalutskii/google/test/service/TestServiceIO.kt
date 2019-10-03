package ru.zalutskii.google.test.service

import ru.zalutskii.google.test.parser.list.TestTree
import java.io.File

interface TestServiceInput {
    /**
     * Open file.
     *
     * This method should call [TestServiceOutput.didLoadTestTree] with test tree
     * from file.
     *
     * @param file file to open.
     */
    suspend fun open(file: File)

    /**
     * Run tests from opened file.
     */
    suspend fun run()

    /**
     * Rerun failed tests.
     *
     * If test run hasn't failed test this method must call [TestServiceOutput.didFinishRun]
     * with current status of current tree.
     */
    suspend fun rerunFailedTests()

    /**
     * Break current test run.
     */
    suspend fun stop()

    /**
     * Request log for test function.
     *
     * Should call [TestServiceOutput.didProcessOutput].
     *
     * @param suite test suite name
     * @param test test function name
     */
    suspend fun showLog(suite: String, test: String)

    /**
     * Request log for test suite.
     *
     * Should call [TestServiceOutput.didProcessOutput].
     *
     * @param suite test suite name
     */
    suspend fun showLog(suite: String)

    /**
     * Request log for test run.
     *
     * Should call [TestServiceOutput.didProcessOutput].
     */
    suspend fun showLog()
}

interface TestServiceOutput {
    /**
     * Has been load new test function tree.
     *
     * @param tree new test function tree.
     */
    suspend fun didLoadTestTree(tree: TestTree)

    /**
     * Test tree has been updated.
     *
     * @param tree tree with updated statuses.
     */
    suspend fun didUpdateTestTree(tree: TestTree)

    /**
     * New test run log has been processed.
     *
     * @param log new log.
     */
    suspend fun didProcessOutput(log: String)

    /**
     * Test run has been finished.
     *
     * @param status of finished test run.
     */
    suspend fun didFinishRun(status: TestTree.Status)
}