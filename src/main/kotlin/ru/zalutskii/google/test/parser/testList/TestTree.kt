package ru.zalutskii.google.test.parser.testList

data class TestTree(val suites: List<Case>, val status: Status = Status.READY) {
    enum class Status {
        READY,
        QUEUE,
        RUN,
        SUCCESS,
        FAIL
    }

    data class Case(
        val name: String,
        val time: String? = null,
        val status: Status = Status.READY,
        val functions: List<Function>
    ) {

        data class Function(val name: String, val time: String? = null, val status: Status = Status.READY)
    }
}