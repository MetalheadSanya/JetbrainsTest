package ru.zalutskii.google.test.parser.list

data class Token(val type: Type, val literal: String) {
    enum class Type {
        TEST_CASE, TEST_FUNCTION
    }
}