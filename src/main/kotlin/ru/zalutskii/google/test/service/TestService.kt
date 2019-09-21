package ru.zalutskii.google.test.service

import ru.zalutskii.google.test.parser.testList.TestListParser
import ru.zalutskii.google.test.parser.testList.TestTree
import ru.zalutskii.google.test.service.logPerformer.LogPerformerInput
import ru.zalutskii.google.test.service.logPerformer.LogPerformerOutput
import ru.zalutskii.google.test.service.process.ITestProcess
import java.io.File

class TestService(
    private val parser: TestListParser,
    private val process: ITestProcess
) : TestServiceInput, LogPerformerOutput {

    var output: TestServiceOutput? = null
    var logPerformer: LogPerformerInput? = null

    private var tree: TestTree? = null

    override suspend fun open(file: File) {
        process.open(file)
        logPerformer?.reset()
        val reader = process.readTestCases()
        val tree = parser.parseTree(reader)

        this.tree = tree

        output?.didLoadTestTree(tree)
        output?.didProcessOutput("")
    }

    override suspend fun run() {
        try {
            val logPerformer = this.logPerformer ?: return

            logPerformer.reset()

            output?.didProcessOutput("")

            setLogStatus(TestTree.Status.QUEUE)
            tree?.let { output?.didLoadTestTree(it) }

            val reader = process.runTestCases()
            logPerformer.perform(reader)
        } finally {
            output?.didFinishRun()
        }
    }

    private fun setLogStatus(status: TestTree.Status) {
        var tree = this.tree ?: return
        val suites = tree.suites.toMutableList()
        suites.replaceAll { suite ->
            val functions = suite.functions.toMutableList()
            functions.replaceAll { function ->
                function.copy(status = status)
            }
            suite.copy(status = status, functions = functions)
        }
        tree = tree.copy(suites = suites, status = status)
        this.tree = tree
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

    override suspend fun didStartTestSuite(suiteName: String) {
        updateSuiteStatus(suiteName, TestTree.Status.RUN)
    }

    private suspend fun updateSuiteStatus(
        suiteName: String,
        status: TestTree.Status,
        time: String? = null
    ) {
        var tree = this.tree ?: return
        val suites = tree.suites.toMutableList()
        suites.replaceAll {
            when (it.name) {
                suiteName -> it.copy(status = status, time = time)
                else -> it
            }
        }
        tree = tree.copy(suites = suites)
        this.tree = tree

        output?.didLoadTestTree(tree)
    }

    override suspend fun didEndTestSuite(suiteName: String, milliseconds: Int) {
        val tree = this.tree ?: return
        val suite = tree.suites.firstOrNull { it.name == suiteName } ?: return
        val failed = suite.functions.map { it.status == TestTree.Status.FAIL }
            .firstOrNull { it } ?: false
        val status = when (failed) {
            true -> TestTree.Status.FAIL
            false -> TestTree.Status.SUCCESS
        }
        updateSuiteStatus(suiteName, status, "$milliseconds")
    }

    override suspend fun didStartTest(suiteName: String, testName: String) {
        updateTestStatus(suiteName, testName, TestTree.Status.RUN)
    }

    private suspend fun updateTestStatus(
        suiteName: String,
        testName: String,
        status: TestTree.Status,
        time: String? = null
    ) {
        var tree = this.tree ?: return
        val suites = tree.suites.toMutableList()
        val suite = suites.firstOrNull { it.name == suiteName } ?: return
        val tests = suite.functions.toMutableList()
        tests.replaceAll {
            when (it.name) {
                testName -> it.copy(status = status, time = time)
                else -> it
            }
        }
        suites.replaceAll {
            when (it.name) {
                suiteName -> it.copy(functions = tests)
                else -> it
            }
        }
        tree = tree.copy(suites = suites)
        this.tree = tree

        output?.didLoadTestTree(tree)
        return
    }

    override suspend fun didPassTest(suiteName: String, testName: String, milliseconds: Int) {
        updateTestStatus(suiteName, testName, TestTree.Status.SUCCESS, "$milliseconds")
    }

    override suspend fun didFailTest(suiteName: String, testName: String, milliseconds: Int) {
        updateTestStatus(suiteName, testName, TestTree.Status.FAIL, "$milliseconds")
    }

    override suspend fun didStartTest() {
        updateRootStatus(TestTree.Status.RUN)
    }

    override suspend fun didEndTest() {
        val tree = this.tree ?: return
        val failed = tree.suites.map { it.status == TestTree.Status.FAIL }
            .firstOrNull { it } ?: false
        val status = when (failed) {
            true -> TestTree.Status.FAIL
            false -> TestTree.Status.SUCCESS
        }
        updateRootStatus(status)
    }

    private suspend fun updateRootStatus(status: TestTree.Status) {
        var tree = this.tree ?: return
        tree = tree.copy(status = status)
        this.tree = tree

        output?.didLoadTestTree(tree)
    }

}