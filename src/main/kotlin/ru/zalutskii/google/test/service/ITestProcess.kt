package ru.zalutskii.google.test.service

import java.io.BufferedReader
import java.io.File

interface ITestProcess {
    fun open(file: File)
    fun readTestCases(): BufferedReader
    fun runTestCases(): BufferedReader
    fun stop()
}