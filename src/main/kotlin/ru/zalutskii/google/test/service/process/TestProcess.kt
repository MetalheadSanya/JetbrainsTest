package ru.zalutskii.google.test.service.process

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.lang.NullPointerException

class TestProcess: ITestProcess {

    private lateinit var path: String

    private lateinit var process: Process

    override fun open(file: File) {
        path = file.absolutePath
    }

    override fun readTestCases(): BufferedReader {
        if (!::path.isInitialized) {
            throw NullPointerException()
        }

        val command = listOf(path, "--gtest_list_tests")
        val process = ProcessBuilder()
            .command(command)
            .start()
        val inputStream = process
            .inputStream

        val inputStreamReader = InputStreamReader(inputStream)
        return BufferedReader(inputStreamReader)
    }

    override fun runTestCases(): BufferedReader {
        if (!::path.isInitialized) {
            throw NullPointerException()
        }

        val command = listOf(path)
        process = ProcessBuilder()
            .command(command)
            .start()
        val inputStream = process
            .inputStream

        val inputStreamReader = InputStreamReader(inputStream)
        return BufferedReader(inputStreamReader)
    }

    override fun stop() {
        if (!::process.isInitialized) {
            throw NullPointerException()
        }

        process.destroyForcibly()
    }
}