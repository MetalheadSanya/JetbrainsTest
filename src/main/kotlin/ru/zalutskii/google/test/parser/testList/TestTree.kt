package ru.zalutskii.google.test.parser.testList

data class TestTree(val cases: List<Case>) {

    data class Case(val name: String, val functions: List<Function>) {

        data class Function(val name: String, val status: Status = Status.READY) {

            enum class Status {
                READY
            }

        }
    }
}