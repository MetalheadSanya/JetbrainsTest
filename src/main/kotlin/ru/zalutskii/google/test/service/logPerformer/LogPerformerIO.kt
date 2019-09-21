package ru.zalutskii.google.test.service.logPerformer

import java.io.BufferedReader

interface LogPerformerInput {
    suspend fun reset()
    suspend fun perform(reader: BufferedReader)
    suspend fun getLogForTest(suite: String, test: String): String
    suspend fun getLogForSuite(suite: String): String
    suspend fun getFullLog(): String
}

interface LogPerformerOutput {
    suspend fun didProcessLog(log: String)
    suspend fun didStartTest()
    suspend fun didEndTest()
    suspend fun didStartTestSuite(suiteName: String)
    suspend fun didEndTestSuite(suiteName: String, milliseconds: Int)
    suspend fun didStartTest(suiteName: String, testName: String)
    suspend fun didPassTest(suiteName: String, testName: String, milliseconds: Int)
    suspend fun didFailTest(suiteName: String, testName: String, milliseconds: Int)
}