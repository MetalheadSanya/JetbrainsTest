package ru.zalutskii.google.test.parser.list

import java.io.BufferedReader

interface TestListParser {
    /**
     * Parse test tree from buffer.
     *
     * @param reader reader of test tree.
     *
     * @return test tree.
     */
    fun parseTree(reader: BufferedReader): TestTree
}