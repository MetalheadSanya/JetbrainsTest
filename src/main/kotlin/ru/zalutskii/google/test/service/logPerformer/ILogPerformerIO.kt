package ru.zalutskii.google.test.service.logPerformer

import java.io.BufferedReader

interface ILogPerformerInput {
    suspend fun reset()
    suspend fun perform(reader: BufferedReader)
    suspend fun getLogForTest(suite: String, test: String): String
    suspend fun getLogForSuite(suite: String): String
    suspend fun getFullLog(): String
}

interface ILogPerformerOutput {
    suspend fun didProcessLog(log: String)
    suspend fun didStartTestSuite(suite: String)
    suspend fun didEndTestSuite(suite: String, milliseconds: Int)
    suspend fun didStartTest(suite: String, test: String)
    suspend fun didPassTest(suite: String, test: String, milliseconds: Int)
    suspend fun didFailTest(suite: String, test: String, milliseconds: Int)
}