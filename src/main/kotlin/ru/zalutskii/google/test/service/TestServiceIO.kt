package ru.zalutskii.google.test.service

import ru.zalutskii.google.test.parser.list.TestTree
import java.io.File

interface TestServiceInput {
    suspend fun open(file: File)
    suspend fun run()
    suspend fun rerunFailedTests()
    suspend fun stop()

    suspend fun showLog(suite: String, test: String)
    suspend fun showLog(suite: String)
    suspend fun showLog()
}

interface TestServiceOutput {
    suspend fun didLoadTestTree(tree: TestTree)
    suspend fun didUpdateTestTree(tree: TestTree)
    suspend fun didProcessOutput(log: String)
    suspend fun didFinishRun(status: TestTree.Status)
}