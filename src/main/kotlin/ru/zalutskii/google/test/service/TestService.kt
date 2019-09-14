package ru.zalutskii.google.test.service

import ru.zalutskii.google.test.parser.testList.TestListParser
import java.io.File

class TestService(
    private val parser: TestListParser,
    private val process: ITestProcess
) : TestServiceInput {

    var output: TestServiceOutput? = null

    private var log: String = ""

    override suspend fun open(file: File) {
        process.open(file)
        val reader = process.readTestCases()
        val tree = parser.parseTree(reader)

        output?.didLoadTestTree(tree)
    }

    override suspend fun run() {
        log = ""

        output?.didProcessOutput(log)

        val reader = process.runTestCases()
        while (true) {
            val line = reader.readLine() ?: break
            log += line
            log += "\n"
            output?.didProcessOutput(log)
        }

        output?.didFinishRun()
    }

    override suspend fun stop() {
        process.stop()
    }
}