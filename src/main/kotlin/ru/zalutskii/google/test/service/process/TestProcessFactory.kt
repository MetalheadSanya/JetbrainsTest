package ru.zalutskii.google.test.service.process

import java.io.File

interface TestProcessFactory {
    fun createFromFile(file: File): TestProcess
}

object TestProcessFactoryImpl : TestProcessFactory {
    override fun createFromFile(file: File): TestProcess = TestProcessImpl(file)
}
