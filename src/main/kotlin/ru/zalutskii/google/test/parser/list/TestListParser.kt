package ru.zalutskii.google.test.parser.list

import java.io.BufferedReader

interface TestListParser {
    fun parseTree(reader: BufferedReader): TestTree
}