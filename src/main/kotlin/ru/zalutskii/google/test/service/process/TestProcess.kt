package ru.zalutskii.google.test.service.process

import java.io.BufferedReader
import java.io.File

interface TestProcess {
    fun open(file: File)
    fun readTestCases(): BufferedReader
    fun runTestCases(): BufferedReader
    fun runTests(list: Iterable<String>): BufferedReader
    fun stop()
}