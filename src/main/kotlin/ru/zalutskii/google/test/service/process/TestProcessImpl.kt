package ru.zalutskii.google.test.service.process

import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

class TestProcessImpl : TestProcess {

    object FileHadNotBeenOpenedException : IOException("File had not been opened")

    private lateinit var path: String

    private lateinit var process: Process

    override fun open(file: File) {
        path = file.absolutePath
    }

    override fun readTestCases(): BufferedReader {
        if (!::path.isInitialized) {
            FileHadNotBeenOpenedException
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
            throw FileHadNotBeenOpenedException
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

    override fun runTests(list: Iterable<String>): BufferedReader {
        if (!::path.isInitialized) {
            FileHadNotBeenOpenedException
        }

        val filter = "--gtest_filter=" + list.joinToString(":")

        val command = listOf(path, filter)
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
            FileHadNotBeenOpenedException
        }

        process.destroyForcibly()
    }
}