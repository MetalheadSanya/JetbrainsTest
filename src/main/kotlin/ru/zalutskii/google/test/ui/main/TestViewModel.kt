package ru.zalutskii.google.test.ui.main

data class TestViewModel(val name: String, val status: Status?, val time: String?) {
    enum class Status {
        RUN,
        QUEUED,
        FAIL,
        SUCCESS
    }
}
