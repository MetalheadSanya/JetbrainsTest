package ru.zalutskii.google.test.parser.testList

import java.io.BufferedReader

interface ITestListParser {
    fun parseTree(reader: BufferedReader): TestTree
}