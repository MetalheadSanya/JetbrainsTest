package ru.zalutskii.google.test.ui.openFile

import java.io.File

interface OpenFileModuleInput {
    fun show()
}

interface OpenFileModuleOutput {
    fun didOpenFile(file: File)
}