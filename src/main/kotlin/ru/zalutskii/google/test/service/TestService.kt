package ru.zalutskii.google.test.service

import ru.zalutskii.google.test.parser.list.TestListParser
import ru.zalutskii.google.test.parser.list.TestTree
import ru.zalutskii.google.test.service.log.performer.LogPerformerInput
import ru.zalutskii.google.test.service.log.performer.LogPerformerOutput
import ru.zalutskii.google.test.service.process.TestProcess
import java.io.File

class TestService(
    private val parserImpl: TestListParser,
    private val process: TestProcess
) : TestServiceInput, LogPerformerOutput {

    var output: TestServiceOutput? = null
    var logPerformer: LogPerformerInput? = null

    private var tree: TestTree? = null

    override fun open(file: File) {
        process.open(file)
        logPerformer?.reset()
        val reader = process.readTestCases()
        val parsedTree = parserImpl.parseTree(reader)

        tree = parsedTree

        output?.didLoadTestTree(parsedTree)
        output?.didProcessOutput("")
    }

    override fun run() {
        try {
            val logPerformer = this@TestService.logPerformer ?: return

            logPerformer.reset()

            output?.didProcessOutput("")

            setLogStatus(TestTree.Status.QUEUE)
            tree?.let { output?.didUpdateTestTree(it) }

            val reader = process.runTestCases()
            logPerformer.perform(reader)
        } finally {
            output?.didFinishRun(getTreeStatus())
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

    override fun rerunFailedTests() {
        try {
            val logPerformer = this@TestService.logPerformer ?: return

            logPerformer.reset()

            output?.didProcessOutput("")

            val failedTests = getFailedTests()
            if (failedTests.none()) {
                return
            }

            setFailedTestStatus(TestTree.Status.QUEUE)
            tree?.let { output?.didUpdateTestTree(it) }

            val reader = process.runTests(failedTests)
            logPerformer.perform(reader)
        } finally {
            output?.didFinishRun(getTreeStatus())
        }
    }

    private fun getTreeStatus(): TestTree.Status =
        tree?.status ?: TestTree.Status.SUCCESS

    private fun getFailedTests(): Iterable<String> {
        val tree = this.tree ?: return emptyList()
        return tree.suites
            .filter { it.status == TestTree.Status.FAIL }
            .flatMap {
                it.functions
                    .filter { func -> func.status == TestTree.Status.FAIL }
                    .map { func -> "${it.name}.${func.name}" }

            }
    }

    private fun setTestStatus(to: TestTree.Status, condition: (TestTree.Status) -> Boolean) {
        var tree = this.tree ?: return
        val suites = tree.suites
            .toMutableList()
            .map { suite ->
                if (!condition(suite.status)) {
                    suite
                } else {
                    val functions = suite.functions
                        .toMutableList()
                        .map { function ->
                            if (!condition(function.status)) {
                                function
                            } else {
                                function.copy(status = to)
                            }
                        }
                    suite.copy(status = to, functions = functions)
                }
            }
        val status = if (condition(tree.status)) {
            to
        } else {
            tree.status
        }
        tree = tree.copy(suites = suites, status = status)
        this.tree = tree
    }

    private fun setFailedTestStatus(status: TestTree.Status) {
        setTestStatus(status) {
            it == TestTree.Status.FAIL
        }
    }

    private fun setUnfinishedTestsToReady() {
        setTestStatus(TestTree.Status.READY) {
            it != TestTree.Status.FAIL && it != TestTree.Status.SUCCESS
        }
    }

    override fun stop() {
        process.stop()
        setUnfinishedTestsToReady()
        tree?.let { output?.didUpdateTestTree(it) }
    }

    override fun changeLogTo(suite: String, test: String) {
        val log = logPerformer?.setCurrentLogTo(suite, test) ?: return
        output?.didProcessOutput(log)
    }

    override fun changeLogTo(suite: String) {
        val log = logPerformer?.setCurrentLogTo(suite) ?: return
        output?.didProcessOutput(log)
    }

    override fun changeLogTo() {
        val log = logPerformer?.setCurrentLogToRoot() ?: ""
        output?.didProcessOutput(log)
    }

    override fun didProcessLog(log: String) {
        output?.didProcessOutput(log)
    }

    override fun didStartTestSuite(suiteName: String) {
        updateSuiteStatus(suiteName, TestTree.Status.RUN)
    }

    private fun updateSuiteStatus(
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

        output?.didUpdateTestTree(tree)
    }

    override fun didEndTestSuite(suiteName: String, milliseconds: Int) {
        val tree = this@TestService.tree ?: return
        val suite = tree.suites.firstOrNull { it.name == suiteName } ?: return
        val failed = suite.functions.map { it.status == TestTree.Status.FAIL }
            .firstOrNull { it } ?: false
        val status = when (failed) {
            true -> TestTree.Status.FAIL
            false -> TestTree.Status.SUCCESS
        }
        updateSuiteStatus(suiteName, status, "$milliseconds")
    }

    override fun didStartTest(suiteName: String, testName: String) {
        updateTestStatus(suiteName, testName, TestTree.Status.RUN)
    }

    private fun updateTestStatus(
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

        output?.didUpdateTestTree(tree)
        return
    }

    override fun didPassTest(suiteName: String, testName: String, milliseconds: Int) {
        updateTestStatus(suiteName, testName, TestTree.Status.SUCCESS, "$milliseconds")
    }

    override fun didFailTest(suiteName: String, testName: String, milliseconds: Int) {
        updateTestStatus(suiteName, testName, TestTree.Status.FAIL, "$milliseconds")
    }

    override fun didStartTest() {
        updateRootStatus(TestTree.Status.RUN)
    }

    override fun didEndTest() {
        val tree = this@TestService.tree ?: return
        val failed = tree.suites.map { it.status == TestTree.Status.FAIL }
            .firstOrNull { it } ?: false
        val status = when (failed) {
            true -> TestTree.Status.FAIL
            false -> TestTree.Status.SUCCESS
        }
        updateRootStatus(status)
    }

    private fun updateRootStatus(status: TestTree.Status) {
        var tree = this.tree ?: return
        tree = tree.copy(status = status)
        this.tree = tree

        output?.didUpdateTestTree(tree)
    }
}
