package ru.zalutskii.google.test.service

import ru.zalutskii.google.test.parser.testList.TestListParser
import ru.zalutskii.google.test.service.logPerformer.ILogPerformerInput
import ru.zalutskii.google.test.service.logPerformer.ILogPerformerOutput
import ru.zalutskii.google.test.service.process.ITestProcess
import java.io.File

class TestService(
    private val parser: TestListParser,
    private val process: ITestProcess
) : TestServiceInput, ILogPerformerOutput {

    var output: TestServiceOutput? = null
    var logPerformer: ILogPerformerInput? = null

    private var log: String = ""

    override suspend fun open(file: File) {
        process.open(file)
        logPerformer?.reset()
        val reader = process.readTestCases()
        val tree = parser.parseTree(reader)

        output?.didLoadTestTree(tree)
        output?.didProcessOutput("")
    }

    override suspend fun run() {
        try {
            if (logPerformer == null) {
                return
            }

            logPerformer?.reset()
            output?.didProcessOutput("")

            val reader = process.runTestCases()
            logPerformer?.perform(reader)
        } finally {
            output?.didFinishRun()
        }
    }

    override suspend fun stop() {
        process.stop()
    }

    override suspend fun showLog(suite: String, test: String) {
        val log = logPerformer?.getLogForTest(suite, test) ?: return
        output?.didProcessOutput(log)
    }

    override suspend fun showLog(suite: String) {
        val log = logPerformer?.getLogForSuite(suite) ?: return
        output?.didProcessOutput(log)
    }

    override suspend fun showLog() {
        val log = logPerformer?.getFullLog() ?: ""
        output?.didProcessOutput(log)
    }

    override suspend fun didProcessLog(log: String) {
        output?.didProcessOutput(log)
    }

    override suspend fun didStartTestSuite(suite: String) {
    }

    override suspend fun didEndTestSuite(suite: String, milliseconds: Int) {
    }

    override suspend fun didStartTest(suite: String, test: String) {
    }

    override suspend fun didPassTest(suite: String, test: String, milliseconds: Int) {
    }

    override suspend fun didFailTest(suite: String, test: String, milliseconds: Int) {
    }
}